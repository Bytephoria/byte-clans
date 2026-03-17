package team.bytephoria.byteclans.infrastructure.configuration;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import team.bytephoria.byteclans.infrastructure.configuration.configuration.Configuration;
import team.bytephoria.byteclans.infrastructure.configuration.roles.Roles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigurationLoader {

    private final Path dataFolder;
    private final ResourceExporter exporter;
    private final ObjectMapper.Factory mapperFactory;

    public ConfigurationLoader(final @NotNull Path dataFolder, final @NotNull ResourceExporter exporter) {
        this.dataFolder = dataFolder;
        this.exporter = exporter;
        this.mapperFactory = ObjectMapper.factoryBuilder().build();
    }

    public @NotNull Configuration loadConfiguration() {
        return this.load("config.yml", Configuration.class);
    }

    public @NotNull Roles loadRoles() {
        return this.load("roles.yml", Roles.class);
    }

    public @NotNull ConfigurationNode loadMessages() {
        return this.loadNode("messages.yml");
    }

    private @NotNull <T> T load(final @NotNull String fileName, final @NotNull Class<T> clazz) {
        try {
            this.saveDefault(fileName);

            final YamlConfigurationLoader configurationLoader = this.createLoader(fileName);
            final T configurationNode = configurationLoader.load().get(clazz);
            if (configurationNode == null) {
                throw new IllegalStateException(String.format("No configuration found for file %s", fileName));
            }

            return configurationNode;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fileName, e);
        }
    }

    private @NotNull ConfigurationNode loadNode(final @NotNull String fileName) {
        try {
            this.saveDefault(fileName);
            return this.createLoader(fileName).load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + fileName, e);
        }
    }

    private void saveDefault(final @NotNull String fileName) {
        final Path file = this.dataFolder.resolve(fileName);
        if (!Files.exists(file)) {
            this.exporter.export(fileName);
        }
    }

    private @NotNull YamlConfigurationLoader createLoader(final @NotNull String fileName) {
        return YamlConfigurationLoader.builder()
                .path(this.dataFolder.resolve(fileName))
                .defaultOptions(options -> options
                        .serializers(build -> build
                                .registerAnnotatedObjects(this.mapperFactory)
                        )
                )
                .build();
    }

}