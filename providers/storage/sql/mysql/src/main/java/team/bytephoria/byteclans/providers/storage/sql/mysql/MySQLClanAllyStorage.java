package team.bytephoria.byteclans.providers.storage.sql.mysql;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.providers.storage.sql.AbstractSQLClanAllyStorage;
import team.bytephoria.byteclans.providers.storage.sql.AbstractSQLStorageConnection;
import team.bytephoria.byteclans.providers.storage.sql.mysql.util.UUIDUtil;
import team.bytephoria.byteclans.spi.storage.entry.ClanAllyEntry;
import team.bytephoria.byteclans.spi.storage.view.ClanAllyView;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MySQLClanAllyStorage extends AbstractSQLClanAllyStorage {

    private static final String FIND_BY_CLAN_UNIQUE_ID_QUERY = """
            SELECT ca.ally_clan_unique_id, c.name
            FROM allies ca
            JOIN clans c ON c.unique_id = ca.ally_clan_unique_id
            WHERE ca.clan_unique_id = ?;
            """;

    private static final String CREATE_BATCH_QUERY = """
            INSERT INTO allies (clan_unique_id, ally_clan_unique_id, created_at)
            VALUES (?, ?, ?);
            """;

    private static final String DELETE_BATCH_QUERY = """
            DELETE FROM allies
            WHERE clan_unique_id = ?
            AND ally_clan_unique_id = ?;
            """;

    private final ExecutorService executorService;

    public MySQLClanAllyStorage(
            final @NotNull AbstractSQLStorageConnection storageConnection,
            final @NotNull Logger logger,
            final @NotNull ExecutorService executorService
    ) {
        super(storageConnection, logger);
        this.executorService = executorService;
    }

    @Override
    public Collection<ClanAllyView> findByClanUniqueId(final @NotNull UUID clanUniqueId) {
        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement ps = connection.prepareStatement(FIND_BY_CLAN_UNIQUE_ID_QUERY)
        ) {
            ps.setBytes(1, UUIDUtil.uuidToBytes(clanUniqueId));

            try (final ResultSet resultSet = ps.executeQuery()) {
                final List<ClanAllyView> allies = new ArrayList<>();
                while (resultSet.next()) {
                    allies.add(new ClanAllyView(
                            resultSet.getObject(1, UUID.class),
                            resultSet.getString(2)
                    ));
                }

                return Collections.unmodifiableList(allies);
            }
        } catch (final SQLException exception) {
            this.logger().log(Level.WARNING, exception.getMessage(), exception);
            return Collections.emptyList();
        }
    }

    @Override
    public void createBatch(final @NotNull Collection<ClanAllyEntry> entries) {
        if (entries.isEmpty()) return;

        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement ps = connection.prepareStatement(CREATE_BATCH_QUERY)
        ) {
            connection.setAutoCommit(false);
            try {
                for (final ClanAllyEntry entry : entries) {
                    ps.setObject(1, UUIDUtil.uuidToBytes(entry.clanUniqueId()));
                    ps.setObject(2, UUIDUtil.uuidToBytes(entry.allyClanUniqueId()));
                    ps.setTimestamp(3, Timestamp.from(Instant.now()));
                    ps.addBatch();
                }

                ps.executeBatch();
                connection.commit();
            } catch (final SQLException exception) {
                connection.rollback();
                throw exception;
            }

        } catch (final SQLException exception) {
            this.logger().log(Level.WARNING, exception.getMessage(), exception);
        }
    }

    @Override
    public void deleteBatch(final @NotNull Collection<ClanAllyEntry> entries) {
        if (entries.isEmpty()) return;

        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BATCH_QUERY)
        ) {
            connection.setAutoCommit(false);
            try {
                for (final ClanAllyEntry entry : entries) {
                    preparedStatement.setBytes(1, UUIDUtil.uuidToBytes(entry.clanUniqueId()));
                    preparedStatement.setBytes(2, UUIDUtil.uuidToBytes(entry.allyClanUniqueId()));
                    preparedStatement.addBatch();
                }

                preparedStatement.executeBatch();
                connection.commit();
            } catch (final SQLException exception) {
                connection.rollback();
                throw exception;
            }

        } catch (final SQLException exception) {
            this.logger().log(Level.WARNING, exception.getMessage(), exception);
        }
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull Async async() {
        return new Async() {

            private @NotNull MySQLClanAllyStorage instance() {
                return MySQLClanAllyStorage.this;
            }

            @Override
            public CompletableFuture<Collection<ClanAllyView>> findByClanUniqueId(final @NotNull UUID clanUniqueId) {
                return CompletableFuture.supplyAsync(() -> this.instance().findByClanUniqueId(clanUniqueId), this.instance().executorService);
            }

            @Override
            public CompletableFuture<Void> createBatch(final @NotNull Collection<ClanAllyEntry> entries) {
                return CompletableFuture.runAsync(() -> this.instance().createBatch(entries), this.instance().executorService);
            }

            @Override
            public CompletableFuture<Void> deleteBatch(final @NotNull Collection<ClanAllyEntry> entries) {
                return CompletableFuture.runAsync(() -> this.instance().deleteBatch(entries), this.instance().executorService);
            }
        };
    }
}