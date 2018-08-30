package eleks.mentorship.bigbang.websocket;

import eleks.mentorship.bigbang.mapper.JsonMessageMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emiliia Nesterovych on 7/15/2018.
 */
@Component
public class RoomManager {
    private List<Room> rooms;
    private JsonMessageMapper mapper;
    private MessageAggregator aggregator;

    public RoomManager(JsonMessageMapper mapper, MessageAggregator aggregator) {
        this.mapper = mapper;
        this.aggregator = aggregator;
        this.rooms = new ArrayList<>();
    }

    public void cleanEmptyRooms() {
        for (Room room : rooms) {
            if (room.isEmpty()) {
                rooms.remove(room);
            }
        }
    }

    public Room assignUserToRoom(WebSocketSession session) {
        Room freeRoom = findFreeRoom();
        freeRoom.registerPlayer(session);

        return freeRoom;
    }

    private Room findFreeRoom() {
        return rooms.stream()
                .filter(r -> !r.isFilled() && !r.isGameStarted())
                .findFirst().orElseGet(() -> {
                    Room newRoom = new Room(mapper, aggregator);
                    rooms.add(newRoom);
                    return newRoom;
                });
    }
}
