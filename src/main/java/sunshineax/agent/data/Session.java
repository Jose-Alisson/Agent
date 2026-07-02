package sunshineax.agent.data;

public interface Session {

    String getId();
    void sendMessage(String text);
    void sendMessage(byte[] message);
    void close();
    boolean isOpen();
}
