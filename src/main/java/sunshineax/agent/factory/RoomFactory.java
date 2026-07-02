package sunshineax.agent.factory;

import sunshineax.agent.factory.room.Room;

import java.util.Map;

public class RoomFactory {

    private Map<String, Room> rooms;

    public Room to(String name) {
        Room room = new Room(name);
        rooms.putIfAbsent(name, room);
        return rooms.get(name);
    }


}
