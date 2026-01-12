package hotel.model.room;

import hotel.service.export_import.CsvExporter;

import java.util.ArrayList;
import java.util.List;

public class RoomCsvExport implements CsvExporter<Room> {
    @Override
    public List<String[]> exportCSV(List<Room> rooms) {
        List<String[]> csvData = new ArrayList<>();
        for (Room room : rooms) {
            String[] csvRow = {
                    String.valueOf(room.getId()),
                    room.getCategory().name(),
                    room.getStatus().name(),
                    room.getType().name(),
                    String.valueOf(room.getPrice()),
                    String.valueOf(room.getCapacity()),
            };
            csvData.add(csvRow);
        }
        return csvData;
    }

    @Override
    public String[] getHeader() {
        return new String[]{"id", "category", "status", "type", "price", "capacity"};
    }

    @Override
    public String fileName() {
        return "roomsExport.csv";
    }
}
