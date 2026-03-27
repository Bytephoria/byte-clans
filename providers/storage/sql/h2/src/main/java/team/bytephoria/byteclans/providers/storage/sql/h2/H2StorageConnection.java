package team.bytephoria.byteclans.providers.storage.sql.h2;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.providers.storage.sql.AbstractSQLStorageConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class H2StorageConnection extends AbstractSQLStorageConnection {

    public H2StorageConnection(final @NotNull HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void createTables() {
        try (final Connection connection = this.getConnection()) {

            this.execute(connection, """
                    CREATE TABLE IF NOT EXISTS clans
                    (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        unique_id UUID NOT NULL UNIQUE,
                        owner_name VARCHAR(18) NOT NULL,
                        owner_unique_id UUID NOT NULL,
                        name VARCHAR(30) NOT NULL,
                        display_name VARCHAR(256) NOT NULL,
                        invite_state VARCHAR(20) NOT NULL,
                        pvp_mode VARCHAR(20) NOT NULL,
                        max_members TINYINT NOT NULL,
                        kills INT NOT NULL,
                        deaths INT NOT NULL,
                        kills_streak INT NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                    );
                    """);

            this.execute(connection, """
                    CREATE TABLE IF NOT EXISTS members
                    (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        unique_id UUID NOT NULL UNIQUE,
                        clan_id INT NOT NULL,
                        name VARCHAR(40) NOT NULL,
                        role_id VARCHAR(30) NOT NULL,
                        joined_at TIMESTAMP NOT NULL,
                        last_seen_at TIMESTAMP NOT NULL,

                        INDEX idx_members_clan_id (clan_id),

                        FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
                    );
                    """);

            this.execute(connection, """
                    CREATE TABLE IF NOT EXISTS allies
                    (
                        clan_unique_id UUID NOT NULL,
                        ally_clan_unique_id UUID NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        PRIMARY KEY (clan_unique_id, ally_clan_unique_id),

                        INDEX idx_allies_ally (ally_clan_unique_id),

                        FOREIGN KEY (clan_unique_id) REFERENCES clans(unique_id) ON DELETE CASCADE,
                        FOREIGN KEY (ally_clan_unique_id) REFERENCES clans(unique_id) ON DELETE CASCADE
                    );
                    """);

            this.execute(connection, """
                    CREATE TABLE IF NOT EXISTS enemies
                    (
                        clan_unique_id UUID NOT NULL,
                        enemy_clan_unique_id UUID NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        PRIMARY KEY (clan_unique_id, enemy_clan_unique_id),

                        INDEX idx_enemies_enemy (enemy_clan_unique_id),

                        FOREIGN KEY (clan_unique_id) REFERENCES clans(unique_id) ON DELETE CASCADE,
                        FOREIGN KEY (enemy_clan_unique_id) REFERENCES clans(unique_id) ON DELETE CASCADE
                    );
                    """);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void execute(
            final @NotNull Connection connection,
            final @NotNull String query
    ) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}