Консольная система управления отелем.
Для работы необходимо:
1) создать БД: create database hotel_management;
2) Настроить resource/hotel.properties файл
    2.1) настроить пользователя и заменить на валидный пароль
    2.2) вставить свой url
3) Liquibase - заполнит таблицами бд, необходимо выполнить второй файл для заполнения бд данными

Система имеет архитектуру:
http request->controller->->repository->database->repository->service->controller->http response

hotel-app/
├── src/
│   └── main/
│       └── java/
│           └── hotel/
│               ├── config/           # Конфигурационные классы @Configuration
│               └── Main.java         # Класс с main метод
│       └── resources/                # Используемые в приложение ресурсы (настройки для логера и конфигурации)
│           └── db.changelog/         # Настройки и контроля версий БД (Liquibase) а так же сами миграции
hotel-business/
├── src/
│   └── main/
│       └── java/
│           └── hotel/
│               ├── config/       # Конфигурация
│               ├── controller/       # Слой контроллеров
│               ├── dto/          # Data Transfer Objects
│               ├── repository/   # Репозитории
│               ├── service/      # Сервисы
│   └── test/
│       └── java/
│           └── repository/       # Интеграционные тесты для репозиториев
hotel-core/
├── src/
│   └── main/
│       └── java/
│           └── hotel/
│               ├── exception/    # Исключения
│               ├── model/        # Доменные модели

17) Для прослушивания порта 5432 докер контейнера, необходимо отключить временно сервис postgres
и после заверешния работы контейнера вновь подключить его.
sudo systemctl stop postgresql

sudo systemctl start postgresql

и необходимо освободить порт 8080
