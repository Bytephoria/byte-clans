package team.bytephoria.byteclans.platform.spigot;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.exception.NoPermissionException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import team.bytephoria.byteclans.api.access.ByteClansProvider;
import team.bytephoria.byteclans.core.ApplicationFacade;
import team.bytephoria.byteclans.infrastructure.adventure.ComponentSerializerAdapter;
import team.bytephoria.byteclans.infrastructure.adventure.ComponentSerializerFactory;
import team.bytephoria.byteclans.infrastructure.bootstrap.BootstrapContext;
import team.bytephoria.byteclans.infrastructure.configuration.ConfigurationLoader;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.Configuration;
import team.bytephoria.byteclans.infrastructure.configuration.roles.Roles;
import team.bytephoria.byteclans.platform.commonbukkit.BukkitClanEventBus;
import team.bytephoria.byteclans.platform.commonbukkit.RoleLoader;
import team.bytephoria.byteclans.platform.commonbukkit.concurrent.AsyncExecutor;
import team.bytephoria.byteclans.platform.commonbukkit.listener.PlayerJoinListener;
import team.bytephoria.byteclans.platform.commonbukkit.listener.PlayerQuitListener;
import team.bytephoria.byteclans.platform.spigot.command.ClanCommand;
import team.bytephoria.byteclans.platform.spigot.command.ClanInviteCommand;
import team.bytephoria.byteclans.platform.spigot.hook.PlaceholderAPIHook;
import team.bytephoria.byteclans.platform.spigot.listener.AsyncPlayerChatListener;
import team.bytephoria.byteclans.platform.spigot.listener.v1_20_3.V1_20_3EntityDamageByEntityListener;
import team.bytephoria.byteclans.platform.spigot.listener.v1_20_3.V1_20_3PlayerDeathListener;
import team.bytephoria.byteclans.platform.spigot.listener.v1_20_4.V1_20_4EntityDamageByEntityListener;
import team.bytephoria.byteclans.platform.spigot.listener.v1_20_4.V1_20_4PlayerDeathListener;
import team.bytephoria.byteclans.platform.spigot.message.Messenger;
import team.bytephoria.byteclans.platform.spigot.util.ServerVersion;

public final class SpigotPlugin extends JavaPlugin {

    private BukkitAudiences bukkitAudiences;

    private Configuration configuration;
    private Roles roles;
    private ConfigurationNode messages;
    private ComponentSerializerAdapter serializerAdapter;

    private Messenger messenger;

    private BukkitClanEventBus bukkitClanEventBus;
    private SpigotBootstrap spigotBootstrap;

    private LegacyPaperCommandManager<Player> commandManager;
    private Metrics metrics;

    @Override
    public void onLoad() {
        this.getLogger().info("SpigotPlugin is loading...");

        this.getLogger().info("Loading libraries...");
        new SpigotLibraryLoader(this).load();
        this.getLogger().info("The libraries was loaded!");

        final ConfigurationLoader configurationLoader = new ConfigurationLoader(
                this.getDataFolder().toPath(),
                fileName -> this.saveResource(fileName, false)
        );

        this.configuration = configurationLoader.loadConfiguration();
        this.roles = configurationLoader.loadRoles();
        this.messages = configurationLoader.loadMessages();
        this.getLogger().info("SpigotPlugin has been loaded!");
    }

    @Override
    public void onEnable() {
        this.getLogger().info("SpigotPlugin is starting...");

        this.bukkitAudiences = BukkitAudiences.create(this);
        this.serializerAdapter = ComponentSerializerFactory.create(this.configuration.settings().serializer());
        this.bukkitClanEventBus = new BukkitClanEventBus();
        this.spigotBootstrap = new SpigotBootstrap(this, new BootstrapContext(this.getDataFolder().toPath()));
        this.spigotBootstrap.enable();

        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(this).register();
            this.getLogger().info("PlaceholderAPI has been hooked!");
        }

        this.messenger = new Messenger(this, this.messages, this.serializerAdapter);

        final ApplicationFacade applicationFacade = this.spigotBootstrap.applicationFacade();
        new RoleLoader(this.roles, applicationFacade.clanRoleRegistry()).loadAll();

        this.registerListeners(
                new PlayerJoinListener(applicationFacade.userLoader()),
                new PlayerQuitListener(applicationFacade.userLoader()),
                new AsyncPlayerChatListener(this, this.configuration, this.serializerAdapter, applicationFacade.clanMemberCache())
        );

        final boolean isAtLeast1204 = ServerVersion.isAtLeast(
                Bukkit.getBukkitVersion().split("-")[0],
                1, 20, 4
        );

        if (isAtLeast1204) {
            this.registerListeners(
                    new V1_20_4EntityDamageByEntityListener(
                            applicationFacade.clanMemberCache(),
                            applicationFacade.combatProcessor()
                    ),
                    new V1_20_4PlayerDeathListener(
                            applicationFacade.clanMemberCache(),
                            applicationFacade.clanStatisticManager()
                    )
            );

            this.getLogger().info("Registering v1.20.4 listeners...");
        } else {
            this.registerListeners(
                    new V1_20_3EntityDamageByEntityListener(
                            applicationFacade.clanMemberCache(),
                            applicationFacade.combatProcessor()
                    ),
                    new V1_20_3PlayerDeathListener(
                            applicationFacade.clanMemberCache(),
                            applicationFacade.clanStatisticManager()
                    )
            );

            this.getLogger().info("Registering v1.20.3 listeners...");
        }

        final SenderMapper<CommandSender, Player> mapper = SenderMapper.create(
                commandSender -> (Player) commandSender,
                player -> player
        );

        this.commandManager = new LegacyPaperCommandManager<>(
                this,
                ExecutionCoordinator.simpleCoordinator(),
                mapper
        );

        final AnnotationParser<Player> annotationParser = new AnnotationParser<>(this.commandManager, Player.class);

        annotationParser.parse(new ClanCommand(
                this,
                this.messenger,
                applicationFacade.clanManager(),
                applicationFacade.clanMemberManager(),
                applicationFacade.clanGlobalSettings(),
                applicationFacade.clanCache(),
                applicationFacade.clanMemberCache()
        ));

        annotationParser.parse(new ClanInviteCommand(
                applicationFacade.clanInviteManager(),
                this.messenger,
                applicationFacade.clanCache(),
                applicationFacade.clanMemberCache()
        ));

        this.commandManager.exceptionController().registerHandler(
                InvalidSyntaxException.class,
                context -> context.context().sender().sendMessage("Unknown Command.")
        );

        this.commandManager.exceptionController().registerHandler(
                NoPermissionException.class,
                context -> {
                    //context.context().sender().sendMessage(Component.text("You don't have permission.", NamedTextColor.RED));
                    context.context().sender().sendMessage("&cYou don't have permission.");
                }
        );

        ByteClansProvider.setInstance(
                new SpigotByteClans(
                        applicationFacade.clanCache(),
                        applicationFacade.clanMemberCache(),
                        applicationFacade.clanRoleRegistry(),
                        applicationFacade.clanInviteManager(),
                        applicationFacade.clanManager(),
                        applicationFacade.clanMemberManager(),
                        applicationFacade.clanSettingsManager(),
                        applicationFacade.clanStatisticManager(),
                        applicationFacade.clanNameProcessor(),
                        applicationFacade.clanDisplayNameProcessor()
                )
        );

        this.metrics = new Metrics(this, 30263);
        this.getLogger().info("SpigotPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("SpigotPlugin is stopping...");

        HandlerList.unregisterAll(this);
        if (this.commandManager != null) {
            try {
                this.commandManager.deleteRootCommand("clan");
            } catch (final Exception exception) {
                this.getLogger().severe("Error while unregistering commands");
            }
        }

        ByteClansProvider.resetInstance();
        if (this.spigotBootstrap != null) {
            this.spigotBootstrap.disable();
        }

        AsyncExecutor.shutdown();
        if (this.metrics != null) {
            this.metrics.shutdown();
        }

        if (this.bukkitAudiences != null) {
            this.bukkitAudiences.close();
        }

        this.metrics = null;
        this.messenger = null;
        this.configuration = null;
        this.roles = null;
        this.messages = null;
        this.commandManager = null;
        this.spigotBootstrap = null;
        this.bukkitClanEventBus = null;
        this.serializerAdapter = null;
        this.bukkitAudiences = null;

        this.getLogger().info("SpigotPlugin has been disabled!");
    }

    public @NotNull BukkitAudiences adventure() {
        if (this.bukkitAudiences == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }

        return this.bukkitAudiences;
    }

    public void runMainThread(final @NotNull Runnable runnable) {
        this.getServer().getScheduler().runTask(this, runnable);
    }

    void registerListeners(final @NotNull Listener @NotNull ... listeners) {
        for (final Listener listener : listeners) {
            this.getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public SpigotBootstrap paperBootstrap() {
        return this.spigotBootstrap;
    }

    public Configuration configuration() {
        return this.configuration;
    }

    public ComponentSerializerAdapter serializerAdapter() {
        return this.serializerAdapter;
    }

    public Roles roles() {
        return this.roles;
    }

    public ConfigurationNode messages() {
        return this.messages;
    }

    public BukkitClanEventBus bukkitClanEventBuss() {
        return this.bukkitClanEventBus;
    }

    public Messenger messenger() {
        return this.messenger;
    }

    public LegacyPaperCommandManager<Player> commandManager() {
        return this.commandManager;
    }

    public Metrics metrics() {
        return this.metrics;
    }

}
