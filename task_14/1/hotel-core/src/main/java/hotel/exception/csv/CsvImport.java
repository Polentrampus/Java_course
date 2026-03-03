package hotel.exception.csv;

public class CsvImport extends RuntimeException {

    public CsvImport(String message) {
        super(message);
    }

    public CsvImport(String message, Throwable cause) {
        super(message);
    }
}
