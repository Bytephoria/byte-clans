package team.bytephoria.byteclans.providers.storage.sql.h2;

import org.h2.api.H2Type;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanInviteState;
import team.bytephoria.byteclans.api.ClanPvPMode;
import team.bytephoria.byteclans.providers.storage.sql.AbstractSQLClanStorage;
import team.bytephoria.byteclans.providers.storage.sql.AbstractSQLStorageConnection;
import team.bytephoria.byteclans.spi.storage.entry.ClanEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanField;
import team.bytephoria.byteclans.spi.storage.view.ClanView;

import java.sql.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class H2ClanStorage extends AbstractSQLClanStorage {

    private final ExecutorService executorService;
    public H2ClanStorage(
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
                               points,
                               kills,
                               deaths,
                               kills_streak,
                               display_last_changed_at,
                               created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

    private static final String UPDATE_CLAN_QUERY = """
            UPDATE clans SET
                owner_name = ?,
                owner_unique_id = ?,
                display_name = ?,
                invite_state = ?,
                pvp_mode = ?,
                max_members = ?,
                points = ?,
                kills = ?,
                deaths = ?,
                kills_streak = ?,
                display_last_changed_at = ?
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
        try (
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
            preparedStatement.setObject(1, clanEntry.clanUniqueId(), H2Type.UUID);
            preparedStatement.setString(2, clanEntry.ownerName());
            preparedStatement.setObject(3, clanEntry.ownerUniqueId(), H2Type.UUID);
            preparedStatement.setString(4, clanEntry.clanName());
            preparedStatement.setString(5, clanEntry.displayName());
            preparedStatement.setString(6, clanEntry.clanInviteState().name());
            preparedStatement.setString(7, clanEntry.clanPvPMode().name());
            preparedStatement.setInt(8, clanEntry.maxMembers());
            preparedStatement.setInt(9, clanEntry.points());
            preparedStatement.setInt(10, clanEntry.kills());
            preparedStatement.setInt(11, clanEntry.deaths());
            preparedStatement.setInt(12, clanEntry.killsStreak());

            final Instant displayLastChangedAt = clanEntry.displayLastChangedAt();
            if (displayLastChangedAt != null) {
                preparedStatement.setTimestamp(13, Timestamp.from(displayLastChangedAt));
            } else {
                preparedStatement.setNull(13, Types.TIMESTAMP);
            }

            preparedStatement.setTimestamp(14, Timestamp.from(clanEntry.createdAt()));
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
            preparedStatement.setString(1, clanEntry.ownerName());
            preparedStatement.setObject(2, clanEntry.ownerUniqueId(), H2Type.UUID);
            preparedStatement.setString(3, clanEntry.displayName());
            preparedStatement.setString(4, clanEntry.clanInviteState().name());
            preparedStatement.setString(5, clanEntry.clanPvPMode().name());
            preparedStatement.setInt(6, clanEntry.maxMembers());
            preparedStatement.setInt(7, clanEntry.points());
            preparedStatement.setInt(8, clanEntry.kills());
            preparedStatement.setInt(9, clanEntry.deaths());
            preparedStatement.setInt(10, clanEntry.killsStreak());

            final Instant displayLastChangedAt = clanEntry.displayLastChangedAt();
            if (displayLastChangedAt != null) {
                preparedStatement.setTimestamp(11, Timestamp.from(displayLastChangedAt));
            } else {
                preparedStatement.setNull(11, Types.TIMESTAMP);
            }

            preparedStatement.setObject(12, clanEntry.clanUniqueId(), H2Type.UUID);
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
                    case OWNER_NAME -> "owner_name = ?";
                    case DISPLAY_NAME -> "display_name = ?";
                    case INVITE_STATE ->  "invite_state = ?";
                    case PVP_MODE -> "pvp_mode = ?";
                    case MAX_MEMBERS -> "max_members = ?";
                    case POINTS -> "points = ?";
                    case KILLS ->  "kills = ?";
                    case DEATHS ->  "deaths = ?";
                    case KILLS_STREAK -> "kills_streak = ?";
                    case DISPLAY_LAST_CHANGED_AT -> "display_last_changed_at = ?";
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
                    case OWNER_UNIQUE_ID -> preparedStatement.setObject(index++, clanEntry.ownerUniqueId(), H2Type.UUID);
                    case OWNER_NAME -> preparedStatement.setString(index++, clanEntry.ownerName());
                    case DISPLAY_NAME -> preparedStatement.setString(index++, clanEntry.displayName());
                    case INVITE_STATE -> preparedStatement.setString(index++, clanEntry.clanInviteState().name());
                    case PVP_MODE -> preparedStatement.setString(index++, clanEntry.clanPvPMode().name());
                    case MAX_MEMBERS -> preparedStatement.setInt(index++, clanEntry.maxMembers());
                    case POINTS -> preparedStatement.setInt(index++, clanEntry.points());
                    case KILLS -> preparedStatement.setInt(index++, clanEntry.kills());
                    case DEATHS -> preparedStatement.setInt(index++, clanEntry.deaths());
                    case KILLS_STREAK -> preparedStatement.setInt(index++, clanEntry.killsStreak());
                    case DISPLAY_LAST_CHANGED_AT -> preparedStatement.setTimestamp(index++, Timestamp.from(clanEntry.displayLastChangedAt()));
                }
            }

            preparedStatement.setObject(index, clanEntry.clanUniqueId(), H2Type.UUID);
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
            preparedStatement.setObject(1, uniqueId, H2Type.UUID);
            final int rows = preparedStatement.executeUpdate();
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
            preparedStatement.setObject(1, uniqueId, H2Type.UUID);
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
            preparedStatement.setObject(1, uniqueId, H2Type.UUID);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                final Timestamp displayLastChangedAt = resultSet.getTimestamp("display_last_changed_at");
                return Optional.of(new ClanView(
                        resultSet.getObject("unique_id", UUID.class),
                        resultSet.getString("owner_name"),
                        resultSet.getObject("owner_unique_id", UUID.class),
                        resultSet.getString("name"),
                        resultSet.getString("display_name"),
                        ClanInviteState.valueOf(resultSet.getString("invite_state")),
                        ClanPvPMode.valueOf(resultSet.getString("pvp_mode")),
                        resultSet.getInt("max_members"),
                        resultSet.getInt("points"),
                        resultSet.getInt("kills"),
                        resultSet.getInt("deaths"),
                        resultSet.getInt("kills_streak"),
                        displayLastChangedAt != null ? displayLastChangedAt.toInstant() : null,
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

            private H2ClanStorage instance() {
                return H2ClanStorage.this;
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