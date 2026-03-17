package team.bytephoria.byteclans.spi.storage.transaction;

@FunctionalInterface
public interface TransactionBlock {

    void run() throws Exception;

}