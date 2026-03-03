package hotel.exception.csv;

public class CsvImportParsingException extends CsvImport{
    public CsvImportParsingException(String[] row) {
        super("Ошибка парсинга строки: " + String.join(";", row));
    }
}
