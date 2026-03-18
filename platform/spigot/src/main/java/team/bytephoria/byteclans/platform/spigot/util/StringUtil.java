package team.bytephoria.byteclans.platform.spigot.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    private StringUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static String[] split(final @NotNull String str, final char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    private static String[] splitWorker(final String str, final char separatorChar, final boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }

        final int len = str.length();
        if (len == 0) {
            return new String[0];
        }

        final List<String> list = new ArrayList<>();
        int i = 0;
        int start = 0;
        boolean match = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                }

                start = ++i;
                continue;
            }

            match = true;
            i++;
        }

        if (match || preserveAllTokens) {
            list.add(str.substring(start, i));
        }

        return list.toArray(new String[0]);
    }

}
