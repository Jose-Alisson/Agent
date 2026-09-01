package sunshineax.agent.provider;

import sunshineax.agent.data.Room;

public interface RoomProvider {

    Room loadRoom(String domain);
}
