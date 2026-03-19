package team.bytephoria.byteclans.platform.spigot.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class ServerVersion {

    private ServerVersion() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static boolean isAtLeastVersion(final int major, final int minor, final int patch) {
        return isAtLeast(
                Bukkit.getBukkitVersion().split("-")[0],
                major, minor, patch
        );
    }

    public static boolean isAtLeast(final @NotNull String version, int major, int minor, int patch) {
        final String[] parts = version.split("\\.");

        final int vMajor = Integer.parseInt(parts[0]);
        final int vMinor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        final int vPatch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

        if (vMajor != major) {
            return vMajor > major;
        }

        if (vMinor != minor) {
            return vMinor > minor;
        }

        return vPatch >= patch;
    }

}
