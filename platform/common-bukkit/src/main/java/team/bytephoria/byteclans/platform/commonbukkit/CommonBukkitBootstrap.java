package team.bytephoria.byteclans.platform.commonbukkit;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanGlobalSettings;
import team.bytephoria.byteclans.api.validator.ClanDisplayNameValidator;
import team.bytephoria.byteclans.bukkitapi.validator.LegacyAmpersandClanDisplayNameValidator;
import team.bytephoria.byteclans.bukkitapi.validator.MiniMessageClanDisplayNameValidator;
import team.bytephoria.byteclans.core.ApplicationFacade;
import team.bytephoria.byteclans.core.DefaultClanGlobalSettings;
import team.bytephoria.byteclans.core.cache.ClanInvitationCache;
import team.bytephoria.byteclans.core.validator.DefaultClanDisplayNameValidator;
import team.bytephoria.byteclans.infrastructure.bootstrap.BootstrapContext;
import team.bytephoria.byteclans.infrastructure.bootstrap.PluginLifecycle;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.Configuration;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.storage.Credentials;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.storage.Pool;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.storage.Storage;
import team.bytephoria.byteclans.platform.commonbukkit.concurrent.AsyncExecutor;
import team.bytephoria.byteclans.providers.storage.sql.SQLTransactionManager;
import team.bytephoria.byteclans.providers.storage.sql.config.JdbcCredentials;
import team.bytephoria.byteclans.providers.storage.sql.config.JdbcPoolConfig;
import team.bytephoria.byteclans.providers.storage.sql.h2.*;
import team.bytephoria.byteclans.providers.storage.sql.mariadb.*;
import team.bytephoria.byteclans.providers.storage.sql.mysql.*;
import team.bytephoria.byteclans.spi.storage.*;
import team.bytephoria.byteclans.spi.storage.transaction.TransactionManager;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CommonBukkitBootstrap implements PluginLifecycle {

    private final Configuration configuration;
    private final Logger logger;
    private final BukkitClanEventBus eventBus;
    private BootstrapContext bootstrapContext;

    private ClanGlobalSettings clanGlobalSettings;
    private StorageConnection storageConnection;
    private ClanStorage clanStorage;
    private ClanMemberStorage clanMemberStorage;
    private ClanAllyStorage clanAllyStorage;
    private ClanEnemyStorage clanEnemyStorage;
    private ClanDisplayNameValidator clanDisplayNameValidator;
    private TransactionManager transactionManager;
    private ApplicationFacade applicationFacade;

    public CommonBukkitBootstrap(
            final @NotNull Configuration configuration,
            final @NotNull Logger logger,
            final @NotNull BukkitClanEventBus eventBus,
            final @NotNull BootstrapContext bootstrapContext
    ) {
        this.configuration = configuration;
        this.logger = logger;
        this.eventBus = eventBus;
        this.bootstrapContext = bootstrapContext;
    }

    @Override
    public void load() {
    }

    @Override
    public void enable() {
        this.clanGlobalSettings = new DefaultClanGlobalSettings(
                this.configuration.clan().defaults().maxMembers(),
                this.configuration.clan().defaults().pvpMode(),
                this.configuration.clan().defaults().inviteState(),
                this.configuration.clan().display().cooldown().toDuration(),
                this.configuration.clan().creation().timeout().toDuration(),
                this.configuration.clan().name().minimumChars(),
                this.configuration.clan().name().maximumChars(),
                this.configuration.clan().defaults().maxAllies(),
                this.configuration.clan().defaults().maxEnemies(),
                this.configuration.clan().points().limits().minimum(),
                this.configuration.clan().points().limits().maximum(),
                this.configuration.clan().points().actions().kills(),
                this.configuration.clan().points().actions().deaths()
        );

        try {
            this.initializeStorage();
        } catch (IllegalArgumentException exception) {
            this.logger.info("An error has occurred while initializing storage: " + exception.getMessage());
        }

        this.storageConnection.connect();

        this.clanDisplayNameValidator = switch (this.configuration.settings().serializer().toLowerCase(Locale.ROOT)) {
            case "mini_message" -> new MiniMessageClanDisplayNameValidator();
            case "legacy_ampersand" -> new LegacyAmpersandClanDisplayNameValidator();
            default -> new DefaultClanDisplayNameValidator();
        };

        this.applicationFacade = new ApplicationFacade(
                this.clanGlobalSettings,
                this.clanStorage,
                this.clanMemberStorage,
                this.eventBus,
                new ClanInvitationCache(
                        this.configuration.invitations().ttl().amount(),
                        this.configuration.invitations().ttl().unit()
                ),
                this.transactionManager,
                this.clanAllyStorage,
                this.clanEnemyStorage,
                this.clanDisplayNameValidator
        );
    }

    @Override
    public void disable() {
        this.applicationFacade = null;
        this.transactionManager = null;
        this.clanStorage = null;
        this.clanMemberStorage = null;
        this.clanEnemyStorage = null;
        this.clanAllyStorage = null;

        if (this.storageConnection != null) {
            try {
                this.storageConnection.disconnect();
            } catch (final Exception exception) {
                this.logger.log(Level.SEVERE, "Error while disconnecting storage", exception);
            }
        }

        this.clanDisplayNameValidator = null;
        this.storageConnection = null;
        this.clanGlobalSettings = null;
        this.bootstrapContext = null;
    }

    private void initializeStorage() throws IllegalArgumentException {
        final Storage storage = this.configuration.storage();
        final Credentials credentials = storage.credentials();
        final Pool pool = storage.pool();

        final JdbcPoolConfig jdbcPoolConfig = new JdbcPoolConfig(
                pool.connectionTimeout(),
                pool.maxLifetime(),
                pool.maxPoolSize(),
                pool.minIdle()
        );

        final JdbcCredentials jdbcCredentials = new JdbcCredentials(
                credentials.host(),
                credentials.port(),
                credentials.database(),
                credentials.username(),
                credentials.password(),
                credentials.useSsl()
        );

        final ExecutorService executorService = AsyncExecutor.getExecutor();

        switch (storage.type().toLowerCase(Locale.ROOT)) {
            case "h2" -> {
                final String h2ResolvePath = this.configuration.storage().h2().file();
                final File h2File = new File(this.bootstrapContext.dataDirectory().toFile(), h2ResolvePath);

                final HikariDataSource dataSource = H2StorageConnectionData.builder()
                        .file(h2File)
                        .jdbcPoolConfig(jdbcPoolConfig)
                        .jdbcCredentials(jdbcCredentials)
                        .build();

                final H2StorageConnection h2StorageConnection = new H2StorageConnection(dataSource);

                this.storageConnection = h2StorageConnection;
                this.clanStorage = new H2ClanStorage(h2StorageConnection, this.logger, executorService);
                this.clanMemberStorage = new H2ClanMemberStorage(h2StorageConnection, this.logger, executorService);
                this.transactionManager = new SQLTransactionManager(h2StorageConnection, executorService);
                this.clanAllyStorage = new H2ClanAllyStorage(h2StorageConnection, this.logger, executorService);
                this.clanEnemyStorage = new H2ClanEnemyStorage(h2StorageConnection, this.logger, executorService);
            }

            case "mysql" -> {
                final HikariDataSource dataSource = MySQLStorageConnectionData.builder()
                        .jdbcCredentials(jdbcCredentials)
                        .jdbcPoolConfig(jdbcPoolConfig)
                        .build();

                final MySQLStorageConnection mySQLStorageConnection = new MySQLStorageConnection(dataSource);

                this.storageConnection = mySQLStorageConnection;
                this.clanStorage = new MySQLClanStorage(mySQLStorageConnection, this.logger, executorService);
                this.clanMemberStorage = new MySQLClanMemberStorage(mySQLStorageConnection, this.logger, executorService);
                this.transactionManager = new SQLTransactionManager(mySQLStorageConnection, executorService);
                this.clanAllyStorage = new MySQLClanAllyStorage(mySQLStorageConnection, this.logger, executorService);
                this.clanEnemyStorage = new MySQLClanEnemyStorage(mySQLStorageConnection, this.logger, executorService);
            }

            case "mariadb" -> {
                final HikariDataSource hikariDataSource = MariaDBStorageConnectionData.builder()
                        .jdbcCredentials(jdbcCredentials)
                        .jdbcPoolConfig(jdbcPoolConfig)
                        .build();

                final MariaDBStorageConnection mariaDBStorageConnection = new MariaDBStorageConnection(hikariDataSource);

                this.storageConnection = mariaDBStorageConnection;
                this.clanStorage = new MariaDBClanStorage(mariaDBStorageConnection, this.logger, executorService);
                this.clanMemberStorage = new MariaDBClanMemberStorage(mariaDBStorageConnection, this.logger, executorService);
                this.transactionManager = new SQLTransactionManager(mariaDBStorageConnection, executorService);
                this.clanAllyStorage = new MariaDBClanAllyStorage(mariaDBStorageConnection, this.logger, executorService);
                this.clanEnemyStorage = new MariaDBClanEnemyStorage(mariaDBStorageConnection, this.logger, executorService);
            }

            default -> throw new IllegalArgumentException("Storage type not supported.");
        }
    }

    public ClanGlobalSettings clanGlobalSettings() {
        return this.clanGlobalSettings;
    }

    public StorageConnection storageConnection() {
        return this.storageConnection;
    }

    public ClanStorage clanStorage() {
        return this.clanStorage;
    }

    public ClanMemberStorage clanMemberStorage() {
        return this.clanMemberStorage;
    }

    public TransactionManager transactionManager() {
        return this.transactionManager;
    }

    public ApplicationFacade applicationFacade() {
        return this.applicationFacade;
    }

    public BootstrapContext bootstrapContext() {
        return this.bootstrapContext;
    }

    public ClanDisplayNameValidator clanDisplayNameValidator() {
        return this.clanDisplayNameValidator;
    }
}
