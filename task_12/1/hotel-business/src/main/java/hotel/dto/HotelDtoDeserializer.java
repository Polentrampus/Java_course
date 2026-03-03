package hotel.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.model.users.employee.Employee;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HotelDtoDeserializer extends JsonDeserializer<HotelDto> {
    @Override
    public HotelDto deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        HotelDto dto = new HotelDto();

        dto.setRooms(parseMap(node.get("rooms"), Room.class, p));
        dto.setEmployees(parseMap(node.get("employees"), Employee.class, p));
        dto.setClients(parseMap(node.get("clients"), Client.class, p));
        dto.setBookings(parseMap(node.get("bookings"), Bookings.class, p));
        dto.setServices(parseMap(node.get("services"), Services.class, p));

        return dto;
    }

    private <T> Map<Integer, T> parseMap(JsonNode node, Class<T> valueType, JsonParser p)
            throws IOException {
        if (node == null || !node.isObject()) {
            return new HashMap<>();
        }

        Map<Integer, T> map = new HashMap<>();
        ObjectMapper mapper = (ObjectMapper) p.getCodec();

        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            Integer key = Integer.parseInt(entry.getKey());
            T value = mapper.treeToValue(entry.getValue(), valueType);
            map.put(key, value);
        }

        return map;
    }
}

