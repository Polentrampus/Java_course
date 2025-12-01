package hotel.model.room;

import hotel.controller.export_import.CsvImporter;
import hotel.exception.csv.CsvImportLengthException;
import hotel.exception.csv.CsvImportParsingException;

import java.util.ArrayList;
import java.util.List;

public class RoomCsvImport implements CsvImporter<Room> {
    @Override
    public List<Room> importFromCsv(List<String[]> data) {
        List<Room> rooms = new ArrayList<>();
        for (String[] row : data) {
            try {
                Room room = createEntity(row);
                rooms.add(room);
            }
            catch (Exception e) {
                throw new CsvImportParsingException(row);
            }
        }
        return rooms;
    }

    @Override
    public Room createEntity(String[] data) {
        if (data.length < 6) {
            throw new CsvImportLengthException(getHeader().length, data.length);
        }
        Room room = new Room();
        room.setId(Integer.parseInt(data[0]));
        room.setCategory(RoomCategory.valueOf(data[1]));
        room.setStatus(RoomStatus.valueOf(data[2]));
        room.setType(RoomType.valueOf(data[3]));
        room.setPrice(Integer.parseInt(data[4]));
        room.setCapacity(Integer.parseInt(data[5]));
        return room;
    }

    @Override
    public String[] getHeader() {
        return new String[]{"id", "category", "status", "type", "price", "capacity"};
    }
}
