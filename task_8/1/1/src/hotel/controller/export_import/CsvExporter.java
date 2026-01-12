package hotel.controller.export_import;

import java.util.List;

public interface CsvExporter<T> {
    List<String[]> exportCSV(List<T> entity);
    String[] getHeader();
    String fileName();
}
