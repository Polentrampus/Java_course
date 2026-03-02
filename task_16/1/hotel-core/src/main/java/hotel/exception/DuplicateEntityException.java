package hotel.exception;

/**
 * Исключение выбрасывается при попытке создать дублирующую сущность
 * (клиент с таким же телефоном, комната с таким же номером и т.д.)
 */
public class DuplicateEntityException extends HotelException {

    public DuplicateEntityException(String message) {
        super(ErrorCode.DUPLICATE_ENTRY, message);
    }

    public DuplicateEntityException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public DuplicateEntityException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * Фабричный метод для создания исключения при дублировании клиента
     */
    public static DuplicateEntityException forClient(String phone, String passport) {
        return new DuplicateEntityException(
                ErrorCode.CLIENT_DUPLICATE,
                String.format("Клиент с телефоном '%s' или паспортом '%s' уже существует",
                        phone, passport)
        );
    }

    /**
     * Фабричный метод для создания исключения при дублировании комнаты
     */
    public static DuplicateEntityException forRoom(Integer roomNumber) {
        return new DuplicateEntityException(
                ErrorCode.ROOM_ALREADY_EXISTS,
                String.format("Комната с номером %d уже существует", roomNumber)
        );
    }

    /**
     * Фабричный метод для создания исключения при дублировании услуги
     */
    public static DuplicateEntityException forService(String serviceName) {
        return new DuplicateEntityException(
                ErrorCode.SERVICE_ALREADY_EXISTS,
                String.format("Услуга с названием '%s' уже существует", serviceName)
        );
    }
}