package team.bytephoria.byteclans.platform.commonbukkit.extension;

import team.bytephoria.byteclans.api.extension.ExtensionMeta;

import java.util.Objects;

public record DefaultExtensionMeta(
        String name,
        String version,
        String description,
        String author,
        String main
) implements ExtensionMeta {

    public DefaultExtensionMeta {
        Objects.requireNonNull(name, "name cannot be null.");
        Objects.requireNonNull(version, "version cannot be null.");
        Objects.requireNonNull(main, "main cannot be null.");

        if (!name.matches("[a-zA-Z0-9_-]+")) {
            throw new IllegalArgumentException(
                    "Invalid extension name: '" + name + "'. Only letters, numbers and '_' are allowed."
            );
        }

        if (description == null) {
            description = "";
        }

        if (author == null) {
            author = "";
        }
    }

}
