package hotel.config;

public interface HotelConfiguration {
    /// Возможность изменения статуса комнаты
    boolean isRoomStatusModifiable();

    ///  Получить историю бронирования комнаты
    int getNumberOfGuestsInRoomHistory(Integer idRoom);

    /// Разрешено ли удаление бронирований
    boolean isBookingDeletionAllowed();

    /// Включена ли история бронирований
    boolean isBookingHistoryEnabled();
}
