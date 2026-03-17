package team.bytephoria.byteclans.providers.storage.sql.mysql;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.providers.storage.sql.AbstractSQLStorageConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class MySQLStorageConnection extends AbstractSQLStorageConnection {

    public MySQLStorageConnection(final @NotNull HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void createTables() {
        try (final Connection connection = this.getConnection()) {
            this.execute(connection,"""
                    CREATE TABLE IF NOT EXISTS clans(
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        unique_id BINARY(16) NOT NULL UNIQUE,
                        owner_name VARCHAR(18) NOT NULL,
                        owner_unique_id BINARY(16) NOT NULL,
                        name VARCHAR(30) NOT NULL,
                        display_name VARCHAR(256) NOT NULL,
                        invite_state VARCHAR(20) NOT NULL,
                        pvp_mode VARCHAR(20) NOT NULL,
                        max_members TINYINT NOT NULL,
                        kills INT NOT NULL,
                        deaths INT NOT NULL,
                        kills_streak INT NOT NULL,
                        created_at TIMESTAMP NOT NULL
                    );
                """);

            this.execute(connection, """
                CREATE TABLE IF NOT EXISTS members
                (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    unique_id BINARY(16) NOT NULL UNIQUE,
                    clan_id INT NOT NULL,
                    name VARCHAR(40) NOT NULL,
                    role_id VARCHAR(30) NOT NULL,
                    joined_at TIMESTAMP NOT NULL,
                    last_seen_at TIMESTAMP NOT NULL,
                    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
                );
                """);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void execute(final @NotNull Connection connection, final @NotNull String query) {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
