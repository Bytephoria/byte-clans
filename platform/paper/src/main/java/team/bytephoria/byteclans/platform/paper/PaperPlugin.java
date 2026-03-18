package team.bytephoria.byteclans.platform.paper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bstats.bukkit.Metrics;
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
import team.bytephoria.byteclans.platform.commonbukkit.concurrent.AsyncExecutor;
import team.bytephoria.byteclans.platform.commonbukkit.listener.PlayerJoinListener;
import team.bytephoria.byteclans.platform.commonbukkit.listener.PlayerQuitListener;
import team.bytephoria.byteclans.platform.paper.command.ClanCommand;
import team.bytephoria.byteclans.platform.paper.command.ClanInviteCommand;
import team.bytephoria.byteclans.platform.paper.hook.PlaceholderAPIHook;
import team.bytephoria.byteclans.platform.paper.listener.AsyncChatEventListener;
import team.bytephoria.byteclans.platform.paper.listener.EntityDamageByEntityListener;
import team.bytephoria.byteclans.platform.paper.listener.PlayerDeathListener;
import team.bytephoria.byteclans.platform.commonbukkit.RoleLoader;
import team.bytephoria.byteclans.platform.paper.message.Messenger;

public final class PaperPlugin extends JavaPlugin {

    private Configuration configuration;
    private Roles roles;
    private ConfigurationNode messages;
    private ComponentSerializerAdapter serializerAdapter;

    private Messenger messenger;

    private BukkitClanEventBus bukkitClanEventBus;
    private PaperBootstrap paperBootstrap;

    private LegacyPaperCommandManager<Player> commandManager;
    private Metrics metrics;

    @Override
    public void onLoad() {
        this.getLogger().info("PaperPlugin is loading...");
        final ConfigurationLoader configurationLoader = new ConfigurationLoader(
                this.getDataFolder().toPath(),
                fileName -> this.saveResource(fileName, false)
        );

        this.configuration = configurationLoader.loadConfiguration();
        this.roles = configurationLoader.loadRoles();
        this.messages = configurationLoader.loadMessages();
        this.getLogger().info("PaperPlugin has been loaded!");
    }

    @Override
    public void onEnable() {
        this.getLogger().info("PaperPlugin is starting...");

        this.serializerAdapter = ComponentSerializerFactory.create(this.configuration.settings().serializer());
        this.bukkitClanEventBus = new BukkitClanEventBus();
        this.paperBootstrap = new PaperBootstrap(this, new BootstrapContext(this.getDataPath()));
        this.paperBootstrap.enable();

        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(this).register();
        }

        this.messenger = new Messenger(this.messages, this.serializerAdapter);

        final ApplicationFacade applicationFacade = this.paperBootstrap.applicationFacade();
        new RoleLoader(this.roles, applicationFacade.clanRoleRegistry()).loadAll();

        this.registerListeners(
                new PlayerJoinListener(applicationFacade.userLoader()),
                new PlayerQuitListener(applicationFacade.userLoader()),
                new EntityDamageByEntityListener(
                        applicationFacade.clanMemberCache(),
                        applicationFacade.combatProcessor()
                ),
                new AsyncChatEventListener(this.configuration, this.serializerAdapter, applicationFacade.clanMemberCache()),
                new PlayerDeathListener(
                        applicationFacade.clanMemberCache(),
                        applicationFacade.clanStatisticManager()
                )
        );

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
                context -> context.context().sender().sendMessage(Component.text("You don't have permission.", NamedTextColor.RED))
        );

        ByteClansProvider.setInstance(
                new PaperByteClans(
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
        this.getLogger().info("PaperPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("PaperPlugin is stopping...");

        HandlerList.unregisterAll(this);
        if (this.commandManager != null) {
            try {
                this.commandManager.deleteRootCommand("clan");
            } catch (final Exception exception) {
                this.getSLF4JLogger().error("Error while unregistering commands", exception);
            }
        }

        ByteClansProvider.resetInstance();
        if (this.paperBootstrap != null) {
            this.paperBootstrap.disable();
        }

        AsyncExecutor.shutdown();
        if (this.metrics != null) {
            this.metrics.shutdown();
        }

        this.metrics = null;
        this.messenger = null;
        this.configuration = null;
        this.roles = null;
        this.messages = null;
        this.commandManager = null;
        this.paperBootstrap = null;
        this.bukkitClanEventBus = null;
        this.serializerAdapter = null;

        this.getLogger().info("PaperPlugin has been disabled!");
    }

    public void runMainThread(final @NotNull Runnable runnable) {
        this.getServer().getScheduler().runTask(this, runnable);
    }

    void registerListeners(final @NotNull Listener @NotNull ... listeners) {
        for (final Listener listener : listeners) {
            this.getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public PaperBootstrap paperBootstrap() {
        return this.paperBootstrap;
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
