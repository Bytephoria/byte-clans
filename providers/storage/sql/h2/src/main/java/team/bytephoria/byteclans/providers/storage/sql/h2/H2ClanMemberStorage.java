package team.bytephoria.byteclans.providers.storage.sql.h2;

import org.h2.api.H2Type;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.util.Identity;
import team.bytephoria.byteclans.providers.storage.sql.AbstractSQLClanMemberStorage;
import team.bytephoria.byteclans.providers.storage.sql.AbstractSQLStorageConnection;
import team.bytephoria.byteclans.spi.storage.entry.ClanMemberEntry;
import team.bytephoria.byteclans.spi.storage.field.ClanMemberField;
import team.bytephoria.byteclans.spi.storage.view.ClanMemberView;

import java.sql.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class H2ClanMemberStorage extends AbstractSQLClanMemberStorage {

    private final ExecutorService executorService;

    public H2ClanMemberStorage(
            final @NotNull AbstractSQLStorageConnection storageConnection,
            final @NotNull Logger logger,
            final @NotNull ExecutorService executorService
    ) {
        super(storageConnection, logger);
        this.executorService = executorService;
    }

    private static final String CREATE_MEMBER_QUERY = """
            INSERT INTO members (unique_id,
                                 clan_id,
                                 name,
                                 role_id,
                                 joined_at,
                                 last_seen_at)
            VALUES (?, (SELECT id FROM clans WHERE unique_id = ?), ?, ?, ?, ?);
            """;

    private static final String UPDATE_MEMBER_QUERY = """
            UPDATE members SET
                role_id = ?,
                last_seen_at = ?
            WHERE unique_id = ?;
            """;

    private static final String FIND_BY_UNIQUE_ID_QUERY = """
            SELECT c.unique_id as clan_unique_id,
                   m.unique_id,
                   m.name,
                   m.role_id,
                   m.joined_at,
                   m.last_seen_at
            FROM members m
            JOIN clans c ON m.clan_id = c.id
            WHERE m.unique_id = ?;
            """;

    private static final String DELETE_BY_UNIQUE_ID_QUERY = """
            DELETE FROM members WHERE unique_id = ?;
            """;

    @Override
    public int countMembers() {
        try (final Connection connection = this.storageConnection().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM members")
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
    public void create(final @NotNull ClanMemberEntry clanMemberEntry) {
        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(CREATE_MEMBER_QUERY)
        ) {
            preparedStatement.setObject(1, clanMemberEntry.memberUniqueId(), H2Type.UUID);
            preparedStatement.setObject(2, clanMemberEntry.clanUniqueId(), H2Type.UUID);
            preparedStatement.setString(3, clanMemberEntry.memberName());
            preparedStatement.setString(4, clanMemberEntry.roleId());
            preparedStatement.setTimestamp(5, Timestamp.from(clanMemberEntry.joinedAt()));
            preparedStatement.setTimestamp(6, Timestamp.from(clanMemberEntry.lastSeenAt()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            this.logger().log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public void update(final @NotNull ClanMemberEntry clanMemberEntry) {
        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MEMBER_QUERY)
        ) {
            preparedStatement.setString(1, clanMemberEntry.roleId());
            preparedStatement.setTimestamp(2, Timestamp.from(clanMemberEntry.lastSeenAt()));
            preparedStatement.setObject(3, clanMemberEntry.memberUniqueId(), H2Type.UUID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            this.logger().log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public void update(final @NotNull ClanMemberEntry clanMemberEntry, final @NotNull ClanMemberField @NotNull ... fields) {
        final String setClauses = Arrays.stream(fields)
                .map(field -> switch (field) {
                    case ROLE_ID -> "role_id = ?";
                    case LAST_SEEN_AT -> "last_seen_at = ?";
                })
                .collect(Collectors.joining(", "));

        final String query = "UPDATE members SET " + setClauses + " WHERE unique_id = ?;";
        try (final Connection connection = this.storageConnection().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {

            int index = 1;
            for (final ClanMemberField field : fields) {
                switch (field) {
                    case ROLE_ID -> preparedStatement.setString(index++, clanMemberEntry.roleId());
                    case LAST_SEEN_AT -> preparedStatement.setTimestamp(index++, Timestamp.from(clanMemberEntry.lastSeenAt()));
                }
            }

            preparedStatement.setObject(index, clanMemberEntry.memberUniqueId(), H2Type.UUID);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void deleteByUniqueId(final @NotNull UUID uniqueId) {
        try (final Connection connection = this.storageConnection().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_UNIQUE_ID_QUERY)
        ) {
            preparedStatement.setObject(1, uniqueId, H2Type.UUID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ClanMemberView> findByUniqueId(final @NotNull UUID uniqueId) {
        try (
                final Connection connection = this.storageConnection().getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_UNIQUE_ID_QUERY)
        ) {
            preparedStatement.setObject(1, uniqueId, H2Type.UUID);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(new ClanMemberView(
                        resultSet.getObject(1, UUID.class),
                        resultSet.getObject(2, UUID.class),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getTimestamp(5).toInstant(),
                        resultSet.getTimestamp(6).toInstant()
                ));
            }
        } catch (SQLException e) {
            this.logger().log(Level.WARNING, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ClanMemberView> findByIdentity(final @NotNull Identity identity) {
        return this.findByUniqueId(identity.uniqueId());
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull Async async() {
        return new Async() {

            private @NotNull H2ClanMemberStorage instance() {
                return H2ClanMemberStorage.this;
            }
            
            @Override
            public CompletableFuture<Void> create(final @NotNull ClanMemberEntry clanMemberEntry) {
                return CompletableFuture.runAsync(() -> this.instance().create(clanMemberEntry), this.instance().executorService);
            }

            @Override
            public CompletableFuture<Void> update(final @NotNull ClanMemberEntry clanMemberEntry) {
                return CompletableFuture.runAsync(() -> this.instance().update(clanMemberEntry), this.instance().executorService);
            }

            @Override
            public CompletableFuture<Void> update(final @NotNull ClanMemberEntry clanMemberEntry, final @NotNull ClanMemberField @NotNull ... fields) {
                return CompletableFuture.runAsync(() -> this.instance().update(clanMemberEntry, fields), this.instance().executorService);
            }

            @Override
            public CompletableFuture<Void> deleteByUniqueId(final @NotNull UUID uniqueId) {
                return CompletableFuture.runAsync(() -> this.instance().deleteByUniqueId(uniqueId), this.instance().executorService);
            }

            @Override
            public CompletableFuture<Optional<ClanMemberView>> findByUniqueId(final @NotNull UUID uniqueId) {
                return CompletableFuture.supplyAsync(() -> this.instance().findByUniqueId(uniqueId), this.instance().executorService);
            }

            @Override
            public CompletableFuture<Optional<ClanMemberView>> findByIdentity(final @NotNull Identity identity) {
                return CompletableFuture.supplyAsync(() -> this.instance().findByIdentity(identity), this.instance().executorService);
            }
        };
    }
}