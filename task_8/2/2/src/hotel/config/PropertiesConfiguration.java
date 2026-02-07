package hotel.config;

import hotel.annotation.Component;
import hotel.annotation.ConfigProperty;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Component
public class PropertiesConfiguration implements HotelConfiguration {
    private static final String CONFIG_FILE = "hotel.properties";

    @ConfigProperty(configFileName = CONFIG_FILE, propertyName = "room.status.modifiable")
    private boolean roomStatusModifiable = false;

    @ConfigProperty(propertyName = "bookings.max.entries")
    private int maxBookingEntries = 2;

    @ConfigProperty(propertyName = "bookings.deletion.allowed")
    private boolean bookingDeletionAllowed = false;

    @ConfigProperty(propertyName = "bookings.history.enabled")
    private boolean bookingHistoryEnabled = false;

    public void initialize(String configFile) throws Exception {
        Path configPath = Paths.get(CONFIG_FILE);

        if (!Files.exists(configPath)) {
            System.out.println("Конфигурационный файл не найден, создается новый");
            saveConfig();
        } else {
            try {
                AnnotationConfiguration.config(this);
            } catch (Exception e) {
                System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
            }
        }
    }

    public PropertiesConfiguration() throws Exception {
        initialize(CONFIG_FILE);
    }

    private void saveConfig(){
        Properties properties = new Properties();
        properties.setProperty("room.status.modifiable", String.valueOf(roomStatusModifiable));
        properties.setProperty("bookings.max.entries", String.valueOf(maxBookingEntries));
        properties.setProperty("bookings.deletion.allowed", String.valueOf(bookingDeletionAllowed));
        properties.setProperty("bookings.history.enabled", String.valueOf(bookingHistoryEnabled));

        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Конфигурация отеля");
        } catch (IOException e) {
            System.err.println("Ошибка создания конфигурационного файла: " + e.getMessage());
        }
    }

    @Override
    public boolean isRoomStatusModifiable() {
        return roomStatusModifiable;
    }

    @Override
    public int getNumberOfGuestsInRoomHistory(Integer idRoom) {
        return maxBookingEntries;
    }

    @Override
    public boolean isBookingDeletionAllowed() {
        return bookingDeletionAllowed;
    }

    @Override
    public boolean isBookingHistoryEnabled() {
        return bookingHistoryEnabled;
    }
}
