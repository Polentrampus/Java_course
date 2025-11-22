package hotel.view.action.room;

import hotel.controller.AdminController;
import hotel.controller.manager.CsvImportManager;
import hotel.model.room.Room;
import hotel.model.room.RoomCsvImport;
import hotel.view.action.BaseAction;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class CsvImportRoomsActon extends BaseAction {
    private final AdminController adminController;
    private final CsvImportManager importManager;

    public CsvImportRoomsActon(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
        this.importManager = new  CsvImportManager();
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ИМПОРТ КОМНАТ ===");

            String filePath = readString("Введите путь к CSV файлу: " +
                    "( C:\\my_program\\Java_course\\task_6\\1\\exports\\roomsExport.csv )");
            File file = new File(filePath);

            if (!file.exists()) {
                System.out.println("Файл не найден: " + filePath);
                return;
            }

            System.out.println("Импорт из файла: " + file.getName());
            RoomCsvImport importer = new RoomCsvImport();
            List<Room> importedRooms = importManager.importFromFile(file, importer);

            if (importedRooms.isEmpty()) {
                System.out.println("Не удалось импортировать комнаты из файла");
                return;
            }

            System.out.println("Успешно импортированы комнаты: " + importedRooms.size());
            saveRoomToSystem(importedRooms);
        } catch (Exception e) {
            System.out.println("Ошибка при импорте комнат: " + e.getMessage());
        }
    }
    public  void saveRoomToSystem(List<Room> importedServices) {
        for (Room room : importedServices) {
            adminController.addRoom(
                    room.getCategory(),
                    room.getStatus(),
                    room.getType(),
                    room.getNumber(),
                    room.getPrice(),
                    room.getCapacity()
            );
        }
    }
}
