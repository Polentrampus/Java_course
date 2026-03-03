package hotel.service.export_import;

import java.util.List;

public interface CsvImporter<T> {
    List<T> importFromCsv(List<String[]> data); //преобразование(парсинг) в сущности
    T createEntity(String[] data);
    String[] getHeader();
}
