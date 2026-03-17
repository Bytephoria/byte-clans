package team.bytephoria.byteclans.spi.storage;

public interface StorageConnection {

    void connect();
    void disconnect();

    boolean isConnected();

}
