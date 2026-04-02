Консольная система управления отелем.
Для работы необходимо:
1) создать БД: create database hotel_management;
2) Настроить resource/hotel.properties файл
    2.1) настроить пользователя и заменить на валидный пароль
    2.2) вставить свой url
3) выполнить скрипт: psql -h localhost -U postgres -d hotelManager -f "C:\my_program\Java_course\task_11\4\hotel.sql"

Система имеет архитектуру:
UI->service->repository->(DAO, если репозиторий jdbc)->database

hotel-project/
├── src/
│   └── main/
│       └── java/
│           └── hotel/
│               ├── annotation/   # Аннотации
│               ├── config/       # Конфигурация
│               ├── dao/          # Data Access Objects
│               ├── dto/          # Data Transfer Objects
│               ├── di/           # Dependency Injection
│               ├── exception/    # Исключения
│               ├── model/        # Доменные модели
│               ├── repository/   # Репозитории
│               ├── service/      # Сервисы
│               ├── util/         # Утилиты
│               └── view/         # Представление