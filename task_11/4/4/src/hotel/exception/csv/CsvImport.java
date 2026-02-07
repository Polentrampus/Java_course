package hotel.exception.csv;

import hotel.exception.room.RoomException;

public class CsvImport extends RuntimeException{

    public CsvImport(String message) {
        super(message);
    }

    public CsvImport(String message, Throwable cause) {
        super(message);
    }
}
