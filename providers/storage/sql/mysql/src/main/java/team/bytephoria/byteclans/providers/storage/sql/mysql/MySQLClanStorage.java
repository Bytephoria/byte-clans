package team.bytephoria.byteclans.providers.storage.sql.mysql;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.ClanPvPMode;
import team.bytephoria.byteclans.providers.storage.sql.AbstractSQLClanStorage;
import team.bytephoria.byteclans.providers.storage.sql.AbstractSQLStorageConnection;
import team.bytephoria.byteclans.providers.storage.sql.mysql.util.UUIDUtil;
import team.bytephoria.byteclans.spi.storage.entry.ClanEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanField;
import team.bytephoria.byteclans.spi.storage.view.ClanView;

import java.sql.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class MySQLClanStorage extends AbstractSQLClanStorage {

    private final ExecutorService executorService;
    public MySQLClanStorage(
            final @NotNull AbstractSQLStorageConnection storageConnection,
            final @NotNull Logger logger,
            final @NotNull ExecutorService executorService
    ) {
        super(storageConnection, logger);
        this.executorService = executorService;
    }

    private static final String CREATE_CLAN_QUERY = """
            INSERT INTO clans (unique_id,
                               owner_name,
                               owner_unique_id,
                               name,
                               display_name,
                               invite_state,
                               pvp_mode,
                               max_members,
                               kills,
                               deaths,
                               kills_streak,
                               created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

    private static final String UPDATE_CLAN_QUERY = """
            UPDATE clans SET
                owner_name = ?,
                owner_unique_id = ?,
                display_name = ?,
                invite_state = ?,
                pvp_mode = ?,
                max_members = ?,
                kills = ?,
                deaths = ?,
                kills_streak = ?
            WHERE unique_id = ?;
            """;

    private static final String DELETE_CLAN_QUERY = """
            DELETE FROM clans WHERE unique_id = ?;
            """;

    private static final String EXISTS_BY_UNIQUE_ID_QUERY = """
            SELECT COUNT(*) FROM clans WHERE unique_id = ?;
            """;

    private static final String FIND_BY_UNIQUE_ID_QUERY = """
            SELECT * FROM clans WHERE unique_id = ?;
            """;

    @Override
    public int countClans() {
        try(
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM clans");
        ) {

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }

                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(final @NotNull ClanEntry clanEntry) {
        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CLAN_QUERY)
        ) {
            preparedStatement.setBytes(1, UUIDUtil.uuidToBytes(clanEntry.clanUniqueId()));
            preparedStatement.setString(2, clanEntry.ownerName());
            preparedStatement.setBytes(3, UUIDUtil.uuidToBytes(clanEntry.ownerUniqueId()));
            preparedStatement.setString(4, clanEntry.clanName());
            preparedStatement.setString(5, clanEntry.displayName());
            preparedStatement.setString(6, clanEntry.clanInviteState().name());
            preparedStatement.setString(7, clanEntry.clanPvPMode().name());
            preparedStatement.setInt(8, clanEntry.maxMembers());
            preparedStatement.setInt(9, clanEntry.kills());
            preparedStatement.setInt(10, clanEntry.deaths());
            preparedStatement.setInt(11, clanEntry.killsStreak());
            preparedStatement.setTimestamp(12, Timestamp.from(clanEntry.createdAt()));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            this.logger().log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public void update(final @NotNull ClanEntry clanEntry) {
        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CLAN_QUERY)
        ) {
            preparedStatement.setString(1, clanEntry.clanName());
            preparedStatement.setBytes(2, UUIDUtil.uuidToBytes(clanEntry.ownerUniqueId()));
            preparedStatement.setString(3, clanEntry.displayName());
            preparedStatement.setString(4, clanEntry.clanInviteState().name());
            preparedStatement.setString(5, clanEntry.clanPvPMode().name());
            preparedStatement.setInt(6, clanEntry.maxMembers());
            preparedStatement.setInt(7, clanEntry.kills());
            preparedStatement.setInt(8, clanEntry.deaths());
            preparedStatement.setInt(9, clanEntry.killsStreak());
            preparedStatement.setBytes(10, UUIDUtil.uuidToBytes(clanEntry.clanUniqueId()));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            this.logger().log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public void update(final @NotNull ClanEntry clanEntry, final @NotNull ClanField @NotNull ... fields) {
        final String setClause = Arrays.stream(fields)
                .map(field -> switch (field) {
                    case OWNER_UNIQUE_ID -> "owner_unique_id = ?";
                    case OWNER_NAME ->  "owner_name = ?";
                    case DISPLAY_NAME -> "display_name = ?";
                    case INVITE_STATE ->  "invite_state = ?";
                    case PVP_MODE -> "pvp_mode = ?";
                    case MAX_MEMBERS -> "max_members = ?";
                    case KILLS ->  "kills = ?";
                    case DEATHS ->  "deaths = ?";
                    case KILLS_STREAK -> "kills_streak = ?";
                })
                .collect(Collectors.joining(", "));

        final String query = "UPDATE clans SET " + setClause + " WHERE unique_id = ?;";

        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {

            int index = 1;
            for (final ClanField field : fields) {
                switch (field) {
                    case OWNER_UNIQUE_ID -> preparedStatement.setBytes(index++, UUIDUtil.uuidToBytes(clanEntry.ownerUniqueId()));
                    case DISPLAY_NAME -> preparedStatement.setString(index++, clanEntry.displayName());
                    case INVITE_STATE -> preparedStatement.setString(index++, clanEntry.clanInviteState().name());
                    case PVP_MODE -> preparedStatement.setString(index++, clanEntry.clanPvPMode().name());
                    case MAX_MEMBERS -> preparedStatement.setInt(index++, clanEntry.maxMembers());
                    case KILLS -> preparedStatement.setInt(index++, clanEntry.kills());
                    case DEATHS -> preparedStatement.setInt(index++, clanEntry.deaths());
                    case KILLS_STREAK -> preparedStatement.setInt(index++, clanEntry.killsStreak());
                }
            }

            preparedStatement.setBytes(index, UUIDUtil.uuidToBytes(clanEntry.clanUniqueId()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            this.logger().log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public void deleteByUniqueId(final @NotNull UUID uniqueId) {
        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CLAN_QUERY)
        ) {
            preparedStatement.setBytes(1, UUIDUtil.uuidToBytes(uniqueId));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            this.logger().log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByUniqueId(final @NotNull UUID uniqueId) {
        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(EXISTS_BY_UNIQUE_ID_QUERY)
        ) {
            preparedStatement.setBytes(1, UUIDUtil.uuidToBytes(uniqueId));
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            this.logger().log(Level.WARNING, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Optional<ClanView> findByUniqueId(final @NotNull UUID uniqueId) {
        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_UNIQUE_ID_QUERY)
        ) {
            preparedStatement.setObject(1, UUIDUtil.uuidToBytes(uniqueId));
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(new ClanView(
                        UUIDUtil.bytesToUUID(resultSet.getBytes("unique_id")),
                        resultSet.getString("owner_name"),
                        UUIDUtil.bytesToUUID(resultSet.getBytes("owner_unique_id")),
                        resultSet.getString("name"),
                        resultSet.getString("display_name"),
                        ClanInviteState.valueOf(resultSet.getString("invite_state")),
                        ClanPvPMode.valueOf(resultSet.getString("pvp_mode")),
                        resultSet.getInt("max_members"),
                        resultSet.getInt("kills"),
                        resultSet.getInt("deaths"),
                        resultSet.getInt("kills_streak"),
                        resultSet.getTimestamp("created_at").toInstant()
                ));
            }
        } catch (SQLException e) {
            this.logger().log(Level.WARNING, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public @NotNull Async async() {
        return new Async() {

            private MySQLClanStorage instance() {
                return MySQLClanStorage.this;
            }

            @Override
            public @NotNull CompletableFuture<Void> create(final @NotNull ClanEntry clanEntry) {
                return CompletableFuture.runAsync(() -> this.instance().create(clanEntry), this.instance().executorService);
            }

            @Override
            public @NotNull CompletableFuture<Void> update(final @NotNull ClanEntry clanEntry) {
                return CompletableFuture.runAsync(() -> this.instance().update(clanEntry), this.instance().executorService);
            }

            @Override
            public CompletableFuture<Void> update(final @NotNull ClanEntry clanEntry, @NotNull ClanField @NotNull ... fields) {
                return CompletableFuture.runAsync(() -> this.instance().update(clanEntry, fields), this.instance().executorService);
            }

            @Override
            public @NotNull CompletableFuture<Void> deleteByUniqueId(final @NotNull UUID uniqueId) {
                return CompletableFuture.runAsync(() -> this.instance().deleteByUniqueId(uniqueId), this.instance().executorService);
            }

            @Override
            public @NotNull CompletableFuture<Boolean> existsByUniqueId(final @NotNull UUID uniqueId) {
                return CompletableFuture.supplyAsync(() -> this.instance().existsByUniqueId(uniqueId), this.instance().executorService);
            }

            @Override
            public @NotNull CompletableFuture<Optional<ClanView>> findByUniqueId(final @NotNull UUID uniqueId) {
                return CompletableFuture.supplyAsync(() -> this.instance().findByUniqueId(uniqueId), this.instance().executorService);
            }
        };
    }
}
