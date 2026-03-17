package team.bytephoria.byteclans.providers.storage.sql;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.providers.storage.sql.wrapper.NonClosingConnection;
import team.bytephoria.byteclans.spi.storage.StorageConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public abstract class AbstractSQLStorageConnection implements StorageConnection {

    private final ThreadLocal<Connection> transactionConnection = new ThreadLocal<>();
    private final HikariDataSource dataSource;

    public AbstractSQLStorageConnection(final @NotNull HikariDataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    public abstract void createTables();

    @Override
    public void connect() {
        this.createTables();
    }

    @Override
    public void disconnect() {
        if (!this.dataSource.isClosed()) {
            this.dataSource.close();
        }
    }

    @Override
    public boolean isConnected() {
        return !this.dataSource.isClosed();
    }

    public Connection getConnection() throws SQLException {
        final Connection noClosingConnection = this.transactionConnection.get();
        if (noClosingConnection != null) {
            return new NonClosingConnection(noClosingConnection);
        }

        return this.dataSource.getConnection();
    }

    public <T> T withTransaction(final @NotNull SQLFunction<Connection, T> function) throws SQLException {
        final Connection connection = this.dataSource.getConnection();

        try {
            connection.setAutoCommit(false);
            this.transactionConnection.set(connection);

            final T result = function.apply(connection);

            connection.commit();
            return result;
        } catch (Exception exception) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            throw new SQLException(exception);
        } finally {
            this.transactionConnection.remove();
            connection.close();
        }
    }
}