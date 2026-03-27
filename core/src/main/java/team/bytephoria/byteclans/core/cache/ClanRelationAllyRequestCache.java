package team.bytephoria.byteclans.core.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanRequestAlly;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class ClanRelationAllyRequestCache {

    private final Cache<UUID, ClanRequestAlly> cache;

    public ClanRelationAllyRequestCache(final int amount, final @NotNull TimeUnit timeUnit) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(amount, timeUnit)
                .build();
    }

    public ClanRelationAllyRequestCache() {
        this(1, TimeUnit.MINUTES);
    }

    public void add(final @NotNull ClanRequestAlly requestAlly) {
        this.cache.put(requestAlly.clanReceiverUniqueId(), requestAlly);
    }

    public boolean exists(final @NotNull UUID senderUniqueId) {
        return this.find(senderUniqueId) != null;
    }

    public ClanRequestAlly find(final @NotNull UUID senderUniqueId) {
        return this.cache.getIfPresent(senderUniqueId);
    }

    public ClanRequestAlly remove(final @NotNull UUID senderUniqueId) {
        return this.cache.asMap().remove(senderUniqueId);
    }

}
