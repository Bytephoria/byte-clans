package team.bytephoria.byteclans.platform.spigot;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class SpigotLibraryLoader {

    private final JavaPlugin plugin;
    private final BukkitLibraryManager libraryManager;

    public SpigotLibraryLoader(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.libraryManager = new BukkitLibraryManager(plugin);
    }

    public void load() {
        final JsonObject json = this.parseJson("/bukkit-libraries.json");
        this.loadRepositories(json);
        this.loadDependencies(json);
    }

    private JsonObject parseJson(final String resourcePath) {
        try (final InputStream inputStream = this.plugin.getResource(resourcePath.replaceFirst("/", ""))) {
            if (inputStream == null) {
                throw new IllegalStateException("Resource not found: " + resourcePath);
            }

            final String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return JsonParser.parseString(json).getAsJsonObject();
        } catch (final IOException exception) {
            throw new RuntimeException("Failed to read " + resourcePath, exception);
        }
    }

    private void loadRepositories(final @NotNull JsonObject json) {
        if (!json.has("repositories")) {
            this.libraryManager.addMavenCentral();
            return;
        }

        final JsonObject repositories = json.getAsJsonObject("repositories");
        for (final Map.Entry<String, JsonElement> entry : repositories.entrySet()) {
            final String url = entry.getValue().getAsString();
            this.libraryManager.addRepository(url);
        }

        this.libraryManager.addMavenCentral();
    }

    private void loadDependencies(final @NotNull JsonObject json) {
        if (!json.has("dependencies")) {
            return;
        }

        final JsonArray dependencies = json.getAsJsonArray("dependencies");
        for (final JsonElement element : dependencies) {
            final String dependency = element.getAsString();
            final String[] parts = dependency.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid dependency format: " + dependency);
            }

            final String groupId = parts[0].replace(".", "{}");
            final String artifactId = parts[1];
            final String version = parts[2];

            final Library library = Library.builder()
                    .groupId(groupId)
                    .resolveTransitiveDependencies(true)
                    .excludeTransitiveDependency("org{}jetbrains", "annotations")
                    .artifactId(artifactId)
                    .version(version)
                    .isolatedLoad(false)
                    .build();

            this.libraryManager.loadLibrary(library);
        }
    }
}