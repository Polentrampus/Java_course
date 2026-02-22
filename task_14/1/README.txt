Консольная система управления отелем.
Для работы необходимо:
1) создать БД: create database hotel_management;
2) Настроить resource/hotel.properties файл
    2.1) настроить пользователя и заменить на валидный пароль
    2.2) вставить свой url
3) выполнить скрипт: psql -h localhost -U postgres -d hotelManager -f "C:\my_program\Java_course\task_11\4\hotel.sql"

Система имеет архитектуру:
UI->service->repository->database

hotel-app/
├── src/
│   └── main/
│       └── java/
│           └── hotel/
│               ├── util/         # Утилиты
│               └── view/         # Представление
hotel-business/
├── src/
│   └── main/
│       └── java/
│           └── hotel/
│               ├── config/       # Конфигурация
│               ├── dto/          # Data Transfer Objects
│               ├── repository/   # Репозитории
│               ├── service/      # Сервисы
│   └── test/
│       └── java/
│           └── repository/       # Интеграционные тесты для репозиториев
hotel-hibernate/
├── src/
│   └── main/
│       └── java/
│           └── hotel/
│               ├── HibernateUtil/            # Утилитный класс для подключеня к БД
│               ├── SessionContext/           # Контекст сессий
│               ├── TransactionManager/       # Обертка для проведения транзакций
hotel-core/
├── src/
│   └── main/
│       └── java/
│           └── hotel/
│               ├── exception/    # Исключения
│               ├── model/        # Доменные модели
