# AGENTS.md - Bank Transaction Processing System

## Architecture Overview

**Project Type:** Distributed Java/Spring application with Kafka streaming and Hibernate ORM
- **Structure:** Maven multi-module project (`producer` + `consumer`)
- **Deployment:** Docker Compose with PostgreSQL (5432), Kafka cluster (3 brokers), and 3 consumer replicas
- **Purpose:** Process bank money transfers with exactly-once semantics and ACID transactions

### Data Flow
```
Producer App → Kafka (bank-transactions topic) → Consumer Apps (1,2,3) → PostgreSQL
                                ↓
                         Offset management (read_committed)
```

## Key Components & Responsibilities

### Consumer Module Architecture

| Component | Purpose | Critical Details |
|-----------|---------|-----------------|
| **AppContextListener** | Lifecycle management (Servlet) | Starts/stops KafkaConsumerService on app startup/shutdown. Calls HibernateUtil.shutdown() |
| **KafkaConsumerService** | Kafka consumer loop | Manual offset commit (not auto), reads batches (100 records), spawns thread for polling |
| **TransactionProcessor** | Business logic | Deserializes JSON → validates accounts → checks balance → executes DB transaction → saves transfer record |
| **AccountDao** | Account persistence | Uses pessimistic write locks (LockMode.PESSIMISTIC_WRITE) for concurrent updates |
| **MoneyTransferDAO** | Transfer persistence | Saves with SUCCESS/FAILED status based on validation result |
| **HibernateUtil** | Session factory | Configured with PostgreSQL, HikariCP connection pool, environment variables |

### Models (Jakarta Persistence)
- **Account:** `id` (PK), `currentBalance` (BigDecimal), `version` (for optimistic locking fallback)
- **MoneyTransfer:** `id` (PK), `fromAccountId`, `toAccountId`, `summ`, `status` (ENUM), `createdAt`
- **TransferStatus:** Enum (SUCCESS, FAILED)

## Database Configuration

### Environment Variables (Docker-provided)
```
DB_HOST=postgres      # Container hostname
DB_PORT=5432          # PostgreSQL port
DB_NAME=bankdb
DB_USER=bankuser
DB_PASSWORD=bankpass
KAFKA_BOOTSTRAP_SERVERS=kafka1:9092,kafka2:9093,kafka3:9094
CONSUMER_ID=1|2|3     # Set per consumer instance
```

### Database Schema
- **accounts:** `(id INT PRIMARY KEY, current_balance DECIMAL, version BIGINT)`
- **money_transfers:** `(id INT PRIMARY KEY, id_account_from INT, id_account_to INT, summ DECIMAL, status VARCHAR(20), created_at TIMESTAMP)`

## Critical Workflow & Patterns

### Transaction Processing Flow
1. **KafkaConsumerService.pollLoop()** polls batch of 100 messages max (Duration.ofMillis(1000))
2. For each message:
   - **TransactionProcessor.processMessage()** deserializes with Jackson ObjectMapper
   - Validates both accounts exist (findById)
   - Checks sender has sufficient balance
   - **executeTransaction()** opens Hibernate session with:
     - Pessimistic write locks on both accounts (prevents dirty reads in concurrent scenario)
     - Deducts from fromAccount, adds to toAccount
     - Creates new MoneyTransfer record with SUCCESS status
     - Commits transaction
   - On error: **saveFailedTransaction()** logs FAILED status
3. Manual **commitSync()** after processing batch (exactly-once semantics)

### Concurrency Strategy
- **Pessimistic Locking:** Accounts locked with `LockMode.PESSIMISTIC_WRITE` during updates
- **Kafka:** 3-broker cluster, replication factor 3, min_insync_replicas 2
- **Consumer Groups:** Single group "bank-consumer-group", 3 instances split 3 partitions
- **Isolation Level:** read_committed (no dirty reads from uncommitted transfers)

### Error Handling
- JSON deserialization fails → caught, logged, continue next message
- Account validation fails → saveFailedTransaction()
- Insufficient balance → saveFailedTransaction()
- DB transaction fails → rollback, saveFailedTransaction() (best effort)

## Build & Deployment

### Maven Commands
- Build consumer module: `mvn -pl consumer clean package`
- Build all modules: `mvn clean package`
- Output: `consumer/target/consumer.war`

### Docker Deployment
```bash
docker-compose up
# Launches: postgres, zookeeper, kafka1/2/3, producer-app, consumer-app-1/2/3
# Consumer logs: docker logs bank-consumer-1
```

### Local Development Without Docker
Set environment variables and run:
```bash
export DB_HOST=localhost DB_PORT=5432 DB_NAME=bankdb DB_USER=bankuser DB_PASSWORD=bankpass
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092,localhost:9093,localhost:9094
mvn -pl consumer tomcat:run
```

## Spring Configuration Status
- **AppConfig.java** is empty (@Configuration only, no beans)
- Spring is imported but **NOT** used for dependency injection (manual instantiation in TransactionProcessor)
- **TODO:** Migrate to Spring DI if needed (add @Bean methods, inject via constructor)

## Common Pitfalls for AI Agents

1. **AccountDao.getBalance()** doesn't exist - use `getCurrentBalance()` instead
2. **MoneyTransfer.amount** is field `summ`, not `amount` - check model before coding
3. **Spring is configured but not used** - all DAOs created with `new` keyword, not @Autowired
4. **Kafka offset commits are manual** - don't add ENABLE_AUTO_COMMIT_CONFIG
5. **No database initialization** - assumes tables exist; producer module has SQL scripts in resources
6. **Consumer module should NOT depend on producer** - they are separate microservices
7. **HealthServlet differs between modules** - consumer checks DB + Kafka, producer checks cache + Kafka

## Testing Strategy
- Check consumer logs: `docker logs bank-consumer-1` for processing details
- Verify database: connect to postgres:5432 with bankuser/bankpass
- Monitor Kafka: use kafka-console-consumer in zookeeper container
- Health check servlet exists (**HealthServlet**) - endpoint available at consumer app

## Performance Considerations
- Batch size: 100 records per poll
- Fetch wait: 1000ms (balance between latency and throughput)
- Session factory: Configured with HikariCP pool (10 max, 5 min idle)
- Pessimistic locks: May cause contention under high concurrency - monitor for deadlocks

---
**Last Updated:** 2026-03-23 | **Framework:** Spring 6.2.17, Hibernate 6.3.0, Kafka 7.5.0
