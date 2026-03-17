package team.bytephoria.byteclans.infrastructure.configuration;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ResourceExporter {

    void export(final @NotNull String fileName);

}
