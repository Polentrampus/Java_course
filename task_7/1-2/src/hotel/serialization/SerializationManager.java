package hotel.serialization;
import hotel.model.room.Room;
import java.io.IOException;

public class SerializationManager {
    private static final String SAVE_FILE = "library_state.json";
    private final JsonSerializer<Room> serializer_room;

    public SerializationManager() {
        this.serializer_room = new JsonSerializer<>(Room.class);
    }

    // Сохраняем состояние при завершении программы
    public void saveState(Class<T> library) {
        try {
            serializer.serialize(library, SAVE_FILE);
            System.out.println("✓ Состояние библиотеки сохранено");
        } catch (IOException e) {
            System.err.println("✗ Ошибка при сохранении: " + e.getMessage());
            // Можно выбросить исключение или просто залогировать
        }
    }

    // Восстанавливаем состояние при старте программы
    public Library loadState() {
        try {
            Library library = serializer.deserialize(SAVE_FILE);
            if (library != null) {
                System.out.println("✓ Состояние библиотеки восстановлено");
                return library;
            }
        } catch (IOException e) {
            System.err.println("✗ Ошибка при загрузке: " + e.getMessage());
            // Файла может не существовать при первом запуске
        }

        // Если не удалось загрузить, создаем новую библиотеку
        System.out.println("✓ Создана новая библиотека");
        return new Library("Моя библиотека");
    }

    // Резервное копирование
    public void createBackup(Library library, String backupName) throws IOException {
        String backupFile = "backups/" + backupName + "_backup.json";
        serializer.serialize(library, backupFile);
        System.out.println("✓ Создана резервная копия: " + backupFile);
    }
}