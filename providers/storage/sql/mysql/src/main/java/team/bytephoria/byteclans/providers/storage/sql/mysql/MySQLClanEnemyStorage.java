package team.bytephoria.byteclans.providers.storage.sql.mysql;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.providers.storage.sql.AbstractSQLClanEnemyStorage;
import team.bytephoria.byteclans.providers.storage.sql.AbstractSQLStorageConnection;
import team.bytephoria.byteclans.providers.storage.sql.mysql.util.UUIDUtil;
import team.bytephoria.byteclans.spi.storage.entry.ClanEnemyEntry;
import team.bytephoria.byteclans.spi.storage.view.ClanEnemyView;
import team.bytephoria.byteclans.spi.storage.view.ClanTensionView;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MySQLClanEnemyStorage extends AbstractSQLClanEnemyStorage {

    private static final String FIND_BY_CLAN_UNIQUE_ID_QUERY = """
            SELECT ce.enemy_clan_unique_id, c.name
            FROM enemies ce
            JOIN clans c ON c.unique_id = ce.enemy_clan_unique_id
            WHERE ce.clan_unique_id = ?;
            """;

    private static final String FIND_TENSIONS_BY_CLAN_UNIQUE_ID_QUERY = """
            SELECT
                ce.enemy_clan_unique_id,
                enemy.name AS enemy_clan_name,
                ca.ally_clan_unique_id AS source_clan_unique_id,
                source.name AS source_clan_name
            FROM allies ca
            JOIN enemies ce ON ce.clan_unique_id = ca.ally_clan_unique_id
            JOIN clans enemy ON enemy.unique_id = ce.enemy_clan_unique_id
            JOIN clans source ON source.unique_id = ca.ally_clan_unique_id
            WHERE ca.clan_unique_id = ?;
            """;

    private static final String CREATE_QUERY = """
            INSERT INTO enemies (clan_unique_id, enemy_clan_unique_id, created_at)
            VALUES (?, ?, ?);
            """;

    private static final String DELETE_QUERY = """
            DELETE FROM enemies
            WHERE clan_unique_id = ?
            AND enemy_clan_unique_id = ?;
            """;

    private final ExecutorService executorService;

    public MySQLClanEnemyStorage(
            final @NotNull AbstractSQLStorageConnection storageConnection,
            final @NotNull Logger logger,
            final @NotNull ExecutorService executorService
    ) {
        super(storageConnection, logger);
        this.executorService = executorService;
    }

    @Override
    public Collection<ClanEnemyView> findByClanUniqueId(final @NotNull UUID clanUniqueId) {
        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CLAN_UNIQUE_ID_QUERY)
        ) {
            preparedStatement.setBytes(1, UUIDUtil.uuidToBytes(clanUniqueId));
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                final List<ClanEnemyView> enemies = new ArrayList<>();
                while (resultSet.next()) {
                    enemies.add(new ClanEnemyView(
                            resultSet.getObject(1, UUID.class),
                            resultSet.getString(2)
                    ));
                }

                return Collections.unmodifiableList(enemies);
            }
        } catch (final SQLException exception) {
            this.logger().log(Level.WARNING, exception.getMessage(), exception);
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<ClanTensionView> findTensionsByClanUniqueId(final @NotNull UUID clanUniqueId) {
        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(FIND_TENSIONS_BY_CLAN_UNIQUE_ID_QUERY)
        ) {
            preparedStatement.setBytes(1, UUIDUtil.uuidToBytes(clanUniqueId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                final List<ClanTensionView> tensions = new ArrayList<>();
                while (resultSet.next()) {
                    tensions.add(
                            new ClanTensionView(
                                    resultSet.getObject(1, UUID.class),
                                    resultSet.getString(2),
                                    resultSet.getObject(3, UUID.class),
                                    resultSet.getString(4)
                            )
                    );

                }
                return Collections.unmodifiableList(tensions);
            }
        } catch (final SQLException exception) {
            this.logger().log(Level.WARNING, exception.getMessage(), exception);
            return Collections.emptyList();
        }
    }

    @Override
    public void create(final @NotNull ClanEnemyEntry entry) {
        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(CREATE_QUERY)
        ) {
            preparedStatement.setBytes(1, UUIDUtil.uuidToBytes(entry.clanUniqueId()));
            preparedStatement.setBytes(2, UUIDUtil.uuidToBytes(entry.enemyClanUniqueId()));
            preparedStatement.setTimestamp(3, Timestamp.from(Instant.now()));
            preparedStatement.executeUpdate();
        } catch (final SQLException exception) {
            this.logger().log(Level.WARNING, exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(final @NotNull UUID clanUniqueId, final @NotNull UUID enemyClanUniqueId) {
        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY)
        ) {
            preparedStatement.setBytes(1, UUIDUtil.uuidToBytes(clanUniqueId));
            preparedStatement.setBytes(2, UUIDUtil.uuidToBytes(enemyClanUniqueId));
            preparedStatement.executeUpdate();
        } catch (final SQLException exception) {
            this.logger().log(Level.WARNING, exception.getMessage(), exception);
        }
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull Async async() {
        return new Async() {

            private @NotNull MySQLClanEnemyStorage instance() {
                return MySQLClanEnemyStorage.this;
            }

            @Override
            public CompletableFuture<Collection<ClanEnemyView>> findByClanUniqueId(final @NotNull UUID clanUniqueId) {
                return CompletableFuture.supplyAsync(() -> this.instance().findByClanUniqueId(clanUniqueId), this.instance().executorService);
            }

            @Override
            public CompletableFuture<Void> create(final @NotNull ClanEnemyEntry entry) {
                return CompletableFuture.runAsync(() -> this.instance().create(entry), this.instance().executorService);
            }

            @Override
            public CompletableFuture<Void> delete(final @NotNull UUID clanUniqueId, final @NotNull UUID enemyClanUniqueId) {
                return CompletableFuture.runAsync(() -> this.instance().delete(clanUniqueId, enemyClanUniqueId), this.instance().executorService);
            }

            @Override
            public CompletableFuture<Collection<ClanTensionView>> findTensionsByClanUniqueId(final @NotNull UUID clanUniqueId) {
                return CompletableFuture.supplyAsync(() -> this.instance().findTensionsByClanUniqueId(clanUniqueId), this.instance().executorService);
            }
        };
    }
}