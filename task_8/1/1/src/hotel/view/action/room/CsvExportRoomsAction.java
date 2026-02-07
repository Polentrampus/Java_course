package hotel.view.action.room;

import hotel.controller.AdminController;
import hotel.service.CsvExportService;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomCsvExport;
import hotel.view.action.BaseAction;

import java.util.List;
import java.util.Scanner;

public class CsvExportRoomsAction extends BaseAction {
    private final AdminController adminController;
    private final CsvExportService csvExportService;

    public CsvExportRoomsAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
        this.csvExportService = new CsvExportService();
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ЭКСПОРТ КОМНАТ ===");

            List<Room> rooms = adminController.requestListRoom(RoomFilter.ID);

            if (rooms.isEmpty()) {
                System.out.println("Нет комнат для экспорта!");
                return;
            }

            System.out.println("Найдено комнат для экспорта: " + rooms.size());
            RoomCsvExport exporter = new RoomCsvExport();
            csvExportService.exportToFile("exports", rooms, exporter);

        } catch (Exception e) {
            System.out.println("Ошибка при экспорте комнат: " + e.getMessage());
        }
    }
}
