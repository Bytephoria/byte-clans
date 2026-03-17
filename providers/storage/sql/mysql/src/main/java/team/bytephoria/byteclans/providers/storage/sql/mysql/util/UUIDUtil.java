package team.bytephoria.byteclans.providers.storage.sql.mysql.util;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

public final class UUIDUtil {

    private UUIDUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static byte @NotNull [] uuidToBytes(final @NotNull UUID uuid) {
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    public static @NotNull UUID bytesToUUID(final byte[] bytes) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new UUID(buffer.getLong(), buffer.getLong());
    }

}
