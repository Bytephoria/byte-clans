package team.bytephoria.byteclans.core.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclans.api.ClanInvitation;
import team.bytephoria.byteclans.api.util.Identity;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class ClanInvitationCache {

    private final Cache<UUID, ClanInvitation> invitationCache;

    public ClanInvitationCache(final int ttlAmount, final @NotNull TimeUnit ttlTimeUnit) {
        this.invitationCache = Caffeine.newBuilder()
                .expireAfterWrite(ttlAmount, ttlTimeUnit)
                .build();
    }

    public ClanInvitationCache() {
        this(1, TimeUnit.MINUTES);
    }

    public void add(final @NotNull ClanInvitation invitation) {
        this.invitationCache.put(invitation.targetUniqueId(), invitation);
    }

    public @Nullable ClanInvitation get(final @NotNull Identity identity) {
        return this.invitationCache.getIfPresent(identity.uniqueId());
    }

    public @Nullable ClanInvitation getInvitation(final @NotNull UUID uuid) {
        return this.invitationCache.getIfPresent(uuid);
    }

    public boolean exists(final @NotNull UUID uuid) {
        return this.getInvitation(uuid) != null;
    }

    public ClanInvitation remove(final @NotNull UUID uuid) {
        return this.invitationCache.asMap().remove(uuid);
    }

}
