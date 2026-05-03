package team.bytephoria.byteclans.platform.paper;

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
import team.bytephoria.byteclans.providers.storage.sql.mysql.*;
import team.bytephoria.byteclans.spi.storage.*;
import team.bytephoria.byteclans.spi.storage.transaction.TransactionManager;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public final class PaperBootstrap implements PluginLifecycle {

    private PaperPlugin paperPlugin;
    private ClanGlobalSettings clanGlobalSettings;

    private StorageConnection storageConnection;
    private ClanStorage clanStorage;
    private ClanMemberStorage clanMemberStorage;
    private ClanAllyStorage clanAllyStorage;
    private ClanEnemyStorage clanEnemyStorage;
    private ClanDisplayNameValidator clanDisplayNameValidator;

    private TransactionManager transactionManager;
    private ApplicationFacade applicationFacade;
    private BootstrapContext bootstrapContext;

    public PaperBootstrap(
            final @NotNull PaperPlugin paperPlugin,
            final @NotNull BootstrapContext bootstrapContext
    ) {
        this.paperPlugin = paperPlugin;
        this.bootstrapContext = bootstrapContext;
    }

    @Override
    public void load() {
    }

    @Override
    public void enable() {
        final Configuration configuration = this.paperPlugin.configuration();
        this.clanGlobalSettings = new DefaultClanGlobalSettings(
                configuration.clan().defaults().maxMembers(),
                configuration.clan().defaults().pvpMode(),
                configuration.clan().defaults().inviteState(),
                configuration.clan().name().minimumChars(),
                configuration.clan().name().maximumChars(),
                configuration.clan().points().limits().minimum(),
                configuration.clan().points().limits().maximum(),
                configuration.clan().points().actions().kills(),
                configuration.clan().points().actions().deaths()
        );

        try {
            this.initializeStorage();
        } catch (IllegalArgumentException exception) {
            this.paperPlugin.getSLF4JLogger().info("An error has occurred while initializing storage: {}", exception.getMessage(), exception);
        }

        this.storageConnection.connect();

        this.clanDisplayNameValidator = switch (configuration.settings().serializer().toLowerCase(Locale.ROOT)) {
            case "mini_message" -> new MiniMessageClanDisplayNameValidator();
            case "legacy_ampersand"  -> new LegacyAmpersandClanDisplayNameValidator();
            default -> new DefaultClanDisplayNameValidator();
        };

        this.applicationFacade = new ApplicationFacade(
                this.clanGlobalSettings,
                this.clanStorage,
                this.clanMemberStorage,
                this.paperPlugin.bukkitClanEventBuss(),
                new ClanInvitationCache(
                        configuration.invitations().ttl().amount(),
                        configuration.invitations().ttl().unit()
                ),
                this.transactionManager,
                this.clanAllyStorage,
                this.clanEnemyStorage,
                this.clanDisplayNameValidator
        );

    }

    @Override
    public void disable() {
        if (this.applicationFacade != null) {
            this.applicationFacade = null;
        }

        if (this.transactionManager != null) {
            this.transactionManager = null;
        }

        if (this.clanStorage != null) {
            this.clanStorage = null;
        }

        if (this.clanMemberStorage != null) {
            this.clanMemberStorage = null;
        }

        if (this.clanEnemyStorage != null) {
            this.clanEnemyStorage = null;
        }

        if (this.clanAllyStorage != null) {
            this.clanAllyStorage = null;
        }

        if (this.storageConnection != null) {
            try {
                this.storageConnection.disconnect();
            } catch (final Exception exception) {
                this.paperPlugin.getSLF4JLogger().error("Error while disconnecting storage", exception);
            }
        }

        this.clanDisplayNameValidator = null;
        this.storageConnection = null;
        this.clanGlobalSettings = null;
        this.bootstrapContext = null;
        this.paperPlugin = null;
    }

    void initializeStorage() throws IllegalArgumentException {
        final Configuration configuration = this.paperPlugin.configuration();

        final Storage storage = configuration.storage();
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

        final Logger logger = this.paperPlugin.getLogger();
        final ExecutorService executorService = AsyncExecutor.getExecutor();

        switch (storage.type().toLowerCase(Locale.ROOT)) {
            case "h2" -> {
                final String h2ResolvePath = configuration.storage().h2().file();
                final File h2File = new File(this.bootstrapContext.dataDirectory().toFile(), h2ResolvePath);

                final HikariDataSource dataSource = H2StorageConnectionData.builder()
                        .file(h2File)
                        .jdbcPoolConfig(jdbcPoolConfig)
                        .jdbcCredentials(jdbcCredentials)
                        .build();

                final H2StorageConnection h2StorageConnection = new H2StorageConnection(dataSource);

                this.storageConnection = h2StorageConnection;
                this.clanStorage = new H2ClanStorage(h2StorageConnection, logger, executorService);
                this.clanMemberStorage = new H2ClanMemberStorage(h2StorageConnection, logger, executorService);
                this.transactionManager = new SQLTransactionManager(h2StorageConnection, executorService);
                this.clanAllyStorage = new H2ClanAllyStorage(h2StorageConnection, logger, executorService);
                this.clanEnemyStorage = new H2ClanEnemyStorage(h2StorageConnection, logger, executorService);
            }

            case "mysql" -> {
                final HikariDataSource dataSource = MySQLStorageConnectionData.builder()
                        .jdbcCredentials(jdbcCredentials)
                        .jdbcPoolConfig(jdbcPoolConfig)
                        .build();

                final MySQLStorageConnection mySQLStorageConnection = new MySQLStorageConnection(dataSource);

                this.storageConnection = mySQLStorageConnection;
                this.clanStorage = new MySQLClanStorage(mySQLStorageConnection, logger, executorService);
                this.clanMemberStorage = new MySQLClanMemberStorage(mySQLStorageConnection, logger, executorService);
                this.transactionManager = new SQLTransactionManager(mySQLStorageConnection, executorService);
                this.clanAllyStorage = new MySQLClanAllyStorage(mySQLStorageConnection, logger, executorService);
                this.clanEnemyStorage = new MySQLClanEnemyStorage(mySQLStorageConnection, logger, executorService);
            }

            default -> throw new IllegalArgumentException("Storage type not supported.");
        }

    }

    public PaperPlugin paperPlugin() {
        return this.paperPlugin;
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
