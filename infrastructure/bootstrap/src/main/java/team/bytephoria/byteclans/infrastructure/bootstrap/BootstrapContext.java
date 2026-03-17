package team.bytephoria.byteclans.infrastructure.bootstrap;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class BootstrapContext {

    private final @NotNull Path dataDirectory;

    public BootstrapContext(final @NotNull Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public @NotNull Path dataDirectory() {
        return this.dataDirectory;
    }
}
