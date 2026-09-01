package sunshineax.agent.registry.impl;

import lombok.*;
import sunshineax.agent.acessor.DependencyProvider;
import sunshineax.agent.annotations.Inject;
import sunshineax.agent.data.Room;
import sunshineax.agent.exception.NotMemberJoinRoom;
import sunshineax.agent.factory.manager.LoaderService;
import sunshineax.agent.provider.RoomProvider;

import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Data
public class RoomManager implements LoaderService {

    private Map<Class<?>, RoomProvider> providers = new ConcurrentHashMap<>();
    private Map<String, Room> rooms = new ConcurrentHashMap<>();
    private Map<String, Set<String>> roomBySession = new ConcurrentHashMap<>();

    @Inject
    private DependencyProvider provider;

    public void joinRoom(String name, String identifier) {
        Room room = getRoom(name);
        room.add(identifier);

        Set<String> member = roomBySession.putIfAbsent(identifier, new HashSet<>(Set.of(room.name())));

        if (member != null) {
            member.add(room.name());
        }
    }

    private Room getRoom(String name) {
        RoomProvider roomProvider = (RoomProvider) provider.get(RoomProvider.class).getFirst();
        Room loadRoom = roomProvider.loadRoom(name);
        Room room = rooms.putIfAbsent(loadRoom.name(), loadRoom);
        return room != null ? room : loadRoom;
    }

    public Set<String> getRoomsNameByChildren(String session) {
        Set<String> rooms = roomBySession.get(session);
        if (rooms == null) {
            throw new RuntimeException("The room by session %s, not ready or not found".formatted(session));
        }
        return roomBySession.get(session);
    }

    public Room getByName(String name){
        Room room = rooms.get(name);

        if (room == null) {
            throw new RuntimeException("The room %s not found".formatted(name));
        }

        return rooms.get(name);
    }

    public Set<String> getAllChildrenByAllRooms(){
      return rooms.values().parallelStream().map(Room::children).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public void leave(String identifier) {
        for (String roomId : roomBySession.get(identifier)) {
            Room room = rooms.get(roomId);
            if (room != null) {
                room.remove(identifier);
            }
        }
    }

    public List<String> getChildrenByRoom(String name) {
        return rooms.get(name).children().stream().toList();
    }

    public Set<String> broadcast(String session) {
        Set<String> sessions = new HashSet<>();
        Set<String> rooms = roomBySession.get(session);

        if (rooms == null) {
            throw new NotMemberJoinRoom("The session is not in any room.");
        }

        for (String room : rooms) {
            Room room1 = this.rooms.get(room);
            sessions.addAll(room1.children().stream().filter(s -> !s.equals(session)).toList());
        }

        return sessions;
    }

    @Override
    public void load(URLClassLoader classLoader) {
        ServiceLoader<RoomProvider> serviceLoader = ServiceLoader.load(RoomProvider.class, classLoader);

        for (RoomProvider roomProvider : serviceLoader) {
            providers.putIfAbsent(roomProvider.getClass(), roomProvider);
            provider.inject(roomProvider);
            provider.add(roomProvider);
        }
    }
}
