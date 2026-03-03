package hotel.view.action.room;

import hotel.model.room.Room;
import hotel.model.room.RoomCsvExport;
import hotel.service.CsvExportService;
import hotel.service.IRoomService;
import hotel.view.action.BaseAction;

import java.util.List;
import java.util.Scanner;

public class CsvExportRoomsAction extends BaseAction {
    private final IRoomService roomService;
    private final CsvExportService csvExportService;

    public CsvExportRoomsAction(IRoomService roomService, Scanner scanner) {
        super(scanner);
        this.roomService = roomService;
        this.csvExportService = new CsvExportService();
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ЭКСПОРТ КОМНАТ ===");

            List<Room> rooms = roomService.findAll();

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
