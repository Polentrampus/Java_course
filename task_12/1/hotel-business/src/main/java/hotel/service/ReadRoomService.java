package hotel.service;

import hotel.annotation.Component;
import hotel.exception.ConfigException;
import hotel.model.room.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

@Component("readRoomService")
public class ReadRoomService extends RoomService implements ReadIRoomService {
    private static final Logger log = LoggerFactory.getLogger(ReadRoomService.class);

    public ReadRoomService() {
    }

    @Override
    public boolean save(Room room) throws SQLException {
        throw  new ConfigException();
    }
}
