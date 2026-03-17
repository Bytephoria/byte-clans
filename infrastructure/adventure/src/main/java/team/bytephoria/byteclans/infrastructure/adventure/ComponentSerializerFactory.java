package team.bytephoria.byteclans.infrastructure.adventure;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class ComponentSerializerFactory {

    private ComponentSerializerFactory() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static @NotNull ComponentSerializerAdapter create(final @NotNull String format) {
        return switch (format.trim().toUpperCase(Locale.ROOT)) {
            case "LEGACY_AMPERSAND" -> new LegacyAmpersandComponentSerializerAdapter();
            case "MINI_MESSAGE" -> new MiniMessageComponentSerializerAdapter();
            default -> new PlainComponentSerializerAdapter();
        };
    }
}
