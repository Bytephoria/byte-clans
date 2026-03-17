package team.bytephoria.byteclans.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclans.api.Clan;
import team.bytephoria.byteclans.api.ClanGlobalSettings;
import team.bytephoria.byteclans.api.ClanMember;
import team.bytephoria.byteclans.api.manager.*;
import team.bytephoria.byteclans.api.validator.ClanDisplayNameValidator;
import team.bytephoria.byteclans.api.validator.ClanNameValidator;
import team.bytephoria.byteclans.core.cache.ClanInvitationCache;
import team.bytephoria.byteclans.core.factory.ClanFactory;
import team.bytephoria.byteclans.core.factory.ClanMemberFactory;
import team.bytephoria.byteclans.core.loader.DefaultClanLoader;
import team.bytephoria.byteclans.core.loader.DefaultUserLoader;
import team.bytephoria.byteclans.core.manager.*;
import team.bytephoria.byteclans.core.processor.DefaultClanCombatProcessor;
import team.bytephoria.byteclans.core.validator.DefaultClanDisplayNameValidator;
import team.bytephoria.byteclans.core.validator.DefaultClanNameValidator;
import team.bytephoria.byteclans.core.registry.DefaultClanRoleRegistry;
import team.bytephoria.byteclans.core.util.IdentityCachedMap;
import team.bytephoria.byteclans.spi.eventbus.ClanEventBus;
import team.bytephoria.byteclans.spi.loader.ClanLoader;
import team.bytephoria.byteclans.spi.loader.UserLoader;
import team.bytephoria.byteclans.spi.processors.ClanCombatProcessor;
import team.bytephoria.byteclans.spi.storage.ClanMemberStorage;
import team.bytephoria.byteclans.spi.storage.ClanStorage;
import team.bytephoria.byteclans.spi.storage.transaction.TransactionManager;

import java.util.concurrent.ConcurrentHashMap;

public final class ApplicationFacade {

    private final IdentityCachedMap<Clan> clanCache;
    private final IdentityCachedMap<ClanMember> clanMemberCache;

    private final DefaultClanRoleRegistry clanRoleRegistry;

    private final ClanInvitationCache clanInvitationCache;
    private final ClanGlobalSettings clanGlobalSettings;

    private final ClanManager clanManager;
    private final ClanMemberManager clanMemberManager;
    private final ClanInviteManager clanInviteManager;
    private final ClanSettingsManager clanSettingsManager;
    private final ClanStatisticManager clanStatisticManager;

    private final ClanCombatProcessor combatProcessor;

    private final ClanLoader clanLoader;
    private final UserLoader userLoader;

    private final ClanFactory clanFactory;
    private final ClanMemberFactory clanMemberFactory;

    private final ClanNameValidator clanNameValidator;
    private final ClanDisplayNameValidator clanDisplayNameValidator;

    public ApplicationFacade(
            final @NotNull ClanGlobalSettings clanGlobalSettings,
            final @NotNull ClanStorage clanStorage,
            final @NotNull ClanMemberStorage clanMemberStorage,
            final @NotNull ClanEventBus clanEventBus,
            final @Nullable ClanInvitationCache clanInvitationCache,
            final @NotNull TransactionManager transactionManager
    ) {

        this.clanCache = new IdentityCachedMap<>(new ConcurrentHashMap<>());
        this.clanMemberCache = new IdentityCachedMap<>(new ConcurrentHashMap<>());

        this.clanRoleRegistry = new DefaultClanRoleRegistry();
        this.clanFactory = new ClanFactory();
        this.clanMemberFactory = new ClanMemberFactory();

        this.clanInvitationCache = clanInvitationCache != null ? clanInvitationCache : new ClanInvitationCache();
        this.clanGlobalSettings = clanGlobalSettings;

        this.clanNameValidator = new DefaultClanNameValidator(clanGlobalSettings, clanStorage);
        this.clanDisplayNameValidator = new DefaultClanDisplayNameValidator();

        this.clanMemberManager = new DefaultClanMemberManager(
                this.clanMemberCache,
                clanGlobalSettings,
                clanMemberStorage,
                clanStorage,
                this.clanMemberFactory,
                clanEventBus,
                this.clanRoleRegistry,
                transactionManager
        );

        this.clanManager = new DefaultClanManager(
                this.clanCache,
                this.clanMemberCache,
                clanStorage,
                clanMemberStorage,
                this.clanMemberFactory,
                this.clanFactory,
                clanEventBus,
                clanGlobalSettings,
                this.clanRoleRegistry,
                this.clanNameValidator
        );

        this.clanStatisticManager = new DefaultClanStatisticsManager(clanStorage);

        this.clanInviteManager = new DefaultClanInviteManager(
                this.clanMemberCache,
                this.clanCache,
                this.clanMemberManager,
                this.clanInvitationCache,
                clanEventBus
        );

        this.clanSettingsManager = new DefaultClanSettingsManager(clanStorage, clanEventBus, this.clanDisplayNameValidator);
        this.combatProcessor = new DefaultClanCombatProcessor(clanEventBus);

        this.clanLoader = new DefaultClanLoader(
                this.clanCache,
                this.clanMemberCache,
                clanStorage,
                this.clanFactory
        );

        this.userLoader = new DefaultUserLoader(
                this.clanMemberCache,
                clanMemberStorage,
                this.clanMemberFactory,
                this.clanRoleRegistry,
                this.clanLoader
        );

    }

    public DefaultClanRoleRegistry clanRoleRegistry() {
        return this.clanRoleRegistry;
    }

    public IdentityCachedMap<Clan> clanCache() {
        return this.clanCache;
    }

    public IdentityCachedMap<ClanMember> clanMemberCache() {
        return this.clanMemberCache;
    }

    public ClanInvitationCache clanInvitationCache() {
        return this.clanInvitationCache;
    }

    public ClanGlobalSettings clanGlobalSettings() {
        return this.clanGlobalSettings;
    }

    public ClanManager clanManager() {
        return this.clanManager;
    }

    public ClanMemberManager clanMemberManager() {
        return this.clanMemberManager;
    }

    public ClanInviteManager clanInviteManager() {
        return this.clanInviteManager;
    }

    public ClanSettingsManager clanSettingsManager() {
        return this.clanSettingsManager;
    }

    public ClanStatisticManager clanStatisticManager() {
        return this.clanStatisticManager;
    }

    public ClanCombatProcessor combatProcessor() {
        return this.combatProcessor;
    }

    public ClanLoader clanLoader() {
        return this.clanLoader;
    }

    public UserLoader userLoader() {
        return this.userLoader;
    }

    public ClanFactory clanFactory() {
        return this.clanFactory;
    }

    public ClanMemberFactory clanMemberFactory() {
        return this.clanMemberFactory;
    }

    public ClanNameValidator clanNameProcessor() {
        return this.clanNameValidator;
    }

    public ClanDisplayNameValidator clanDisplayNameProcessor() {
        return this.clanDisplayNameValidator;
    }
}
