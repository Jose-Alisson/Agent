package sunshineax.agent.data.session;

public interface Session {

    String getId();
    void sendMessage(String text);
    void sendMessage(byte[] message);
    void close();
    boolean isOpen();
}
