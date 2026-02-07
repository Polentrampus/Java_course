package hotel.config;

import hotel.annotation.ConfigProperty;
import lombok.Data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Data
public class PropertiesConfiguration implements HotelConfiguration {
    private final Properties properties;

    @ConfigProperty(propertyName = "room.status.modifiable")
    private boolean roomStatusModifiable;

    @ConfigProperty(propertyName = "bookings.max.entries")
    private int maxBookingEntries;

    @ConfigProperty(propertyName = "bookings.deletion.allowed")
    private boolean bookingDeletionAllowed;

    @ConfigProperty(propertyName = "bookings.history.enabled")
    private boolean bookingHistoryEnabled;

    public PropertiesConfiguration(String configFile) throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().
                getClassLoader().
                getResourceAsStream(configFile)) {
            if(inputStream != null) {
                AnnotationConfiguration.config(this);
                this.properties = properties;
            }
            else {
                this.properties = properties;
                setDefaults();
                throw new FileNotFoundException("property file '" + configFile + "' not found in the classpath");
            }
        }
    }

    private void setDefaults(){
        properties.setProperty("room.status.modifiable", "true");
        properties.setProperty("bookings.max.entries","3");
        properties.setProperty("bookings.deletion.allowed", "true");
        properties.setProperty("bookings.history.enabled", "true");
    }

    @Override
    public boolean isRoomStatusModifiable() {
        return Boolean.parseBoolean(properties.getProperty("room.status.modifiable"));
    }

    @Override
    public int getNumberOfGuestsInRoomHistory(Integer idRoom) {
        return Integer.parseInt(properties.getProperty("bookings.max.entries"));
    }

    @Override
    public boolean isBookingDeletionAllowed() {
        return Boolean.parseBoolean(properties.getProperty("bookings.deletion.allowed"));
    }

    @Override
    public boolean isBookingHistoryEnabled() {
        return Boolean.parseBoolean(properties.getProperty("bookings.history.enabled"));
    }
}
