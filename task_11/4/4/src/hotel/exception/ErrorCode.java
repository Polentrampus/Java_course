package hotel.exception;

public enum ErrorCode {
    CLIENT_NOT_FOUND("CLT_001", "Клиент не найден"),
    CLIENT_DUPLICATE("CLT_002", "Клиент уже существует"),
    CLIENT_INVALID_DATA("CLT_003", "Некорректные данные клиента"),
    CLIENT("CLT_004", "Ошибка в работе с client"),

    ROOM_NOT_FOUND("ROM_001", "Комната не найдена"),
    ROOM_NOT_AVAILABLE("ROM_002", "Комната недоступна"),
    ROOM_ALREADY_EXISTS("ROM_003", "Комната уже существует"),
    ROOM_INVALID_DATA("ROM_004", "Некорректные данные комнаты"),

    BOOKING_NOT_FOUND("BKG_001", "Бронирование не найдено"),
    BOOKING_DATE_CONFLICT("BKG_002", "Конфликт дат бронирования"),
    BOOKING_INVALID_DATES("BKG_003", "Некорректные даты бронирования"),
    BOOKING_ROOM_UNAVAILABLE("BKG_004", "Комната недоступна для бронирования"),
    BOOKING("BKG_004", "Данных не достаточно для создания брони"),

    SERVICE_NOT_FOUND("SRV_001", "Услуга не найдена"),
    SERVICE_ALREADY_EXISTS("SRV_002", "Услуга уже существует"),
    SERVICE_INVALID_PRICE("SRV_003", "Некорректная цена услуги"),

    EMPLOYEE_NOT_FOUND("EMP_001", "Сотрудник не найден"),
    EMPLOYEE_INVALID_ROLE("EMP_002", "Некорректная роль сотрудника"),

    DATA_INTEGRITY_VIOLATION("DAT_001", "Нарушение целостности данных"),
    DUPLICATE_ENTRY("DAT_002", "Дублирующая запись"),
    DATA_VALIDATION_FAILED("DAT_003", "Ошибка валидации данных"),

    DATABASE_CONNECTION_ERROR("DB_001", "Ошибка подключения к БД"),
    DATABASE_QUERY_ERROR("DB_002", "Ошибка выполнения запроса"),
    DATABASE_TRANSACTION_ERROR("DB_003", "Ошибка транзакции"),

    CONFIG_ERROR("CFG_001", "Ошибка конфигурации"),
    CONFIG_PROPERTY_MISSING("CFG_002", "Отсутствует свойство конфигурации"),
    
    VALIDATION_ERROR("VALID_001", "Ошибка валидации"),
    UNEXPECTED_ERROR("UNXP_001","Непредвиденная ошибка" );

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
}
