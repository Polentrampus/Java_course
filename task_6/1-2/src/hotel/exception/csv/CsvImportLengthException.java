package hotel.exception.csv;

public class CsvImportLengthException extends CsvImport{
    public CsvImportLengthException(int size, int realSize) {
        super("Недостаточно данных в строке. Ожидается " + size + " полей, получено: " + realSize);
    }
}
