package eleks.mentorship.bigbang.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emiliia Nesterovych on 7/15/2018.
 */
@Component
public class RoomManager {
    private List<Room> rooms = new ArrayList<>();

    public void cleanEmptyRooms() {
        for (Room room : rooms) {
            if (room.isEmpty()) {
                rooms.remove(room);
            }
        }
    }

    public Room assignUserToRoom(WebSocketSession session) {
        Room freeRoom = findFreeRoom();
        freeRoom.addPlayer(session);
        return freeRoom;
    }

    private Room findFreeRoom() {
        return rooms.stream().filter(r -> !r.isFilled()).findFirst().orElseGet(() -> {
            Room newRoom = new Room();
            rooms.add(newRoom);
            return newRoom;
        });
    }
}
