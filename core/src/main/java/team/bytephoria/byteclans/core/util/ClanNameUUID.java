package team.bytephoria.byteclans.core.util;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

public final class ClanNameUUID {

    private ClanNameUUID() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static @NotNull UUID from(final @NotNull String clanName) {
        final String normalizedName = clanName.toLowerCase(Locale.ROOT);
        final byte[] nameBytes = normalizedName.getBytes(StandardCharsets.UTF_8);
        return UUID.nameUUIDFromBytes(nameBytes);
    }



}
