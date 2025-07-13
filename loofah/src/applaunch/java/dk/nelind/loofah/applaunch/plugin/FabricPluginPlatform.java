/*
 * This file is part of Loofah, licensed under the MIT License (MIT).
 *
 * Copyright (c) Nelind <https://www.nelind.dk>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dk.nelind.loofah.applaunch.plugin;

import dk.nelind.loofah.applaunch.plugin.resource.FabricPluginResource;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.common.applaunch.AppLaunch;
import org.spongepowered.common.applaunch.config.LaunchConfig;
import org.spongepowered.common.applaunch.config.TokenReplacement;
import org.spongepowered.common.applaunch.plugin.PluginPlatform;
import org.spongepowered.common.applaunch.plugin.PluginPlatformConstants;
import org.spongepowered.plugin.Environment;
import org.spongepowered.plugin.PluginCandidate;
import org.spongepowered.plugin.PluginLanguageService;
import org.spongepowered.plugin.PluginResource;
import org.spongepowered.plugin.PluginResourceLocatorService;
import org.spongepowered.plugin.blackboard.Keys;
import org.spongepowered.plugin.builtin.StandardEnvironment;
import org.spongepowered.plugin.builtin.jvm.JVMKeys;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Adapted from {@link org.spongepowered.vanilla.applaunch.plugin.VanillaPluginPlatform} and
 * {@link org.spongepowered.forge.applaunch.plugin.ForgePluginPlatform}
 */
public class FabricPluginPlatform implements PluginPlatform {
    private static volatile boolean bootstrapped;

    private final Environment environment;
    private final LaunchConfig config;
    private final TokenReplacement tokens;

    private final Map<String, PluginResourceLocatorService<?>> locatorServices;
    private final Map<String, PluginLanguageService> languageServices;

    private final Map<String, Set<? extends PluginResource>> locatorResources;
    private final Map<PluginLanguageService, List<PluginCandidate>> pluginCandidates;

    public static synchronized void bootstrap() {
        if (FabricPluginPlatform.bootstrapped) {
            return;
        }
        final FabricPluginPlatform platform;
        try {
            platform = new FabricPluginPlatform();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        AppLaunch.setPluginPlatform(platform);
        FabricPluginPlatform.bootstrapped = true;
    }

    public FabricPluginPlatform() throws IOException {
        final FabricLoader loader = FabricLoader.getInstance();
        this.environment = new StandardEnvironment(LogManager.getLogger("Loofah/AppLaunch"));

        ModContainer loofahModContainer = loader
            .getModContainer("loofah")
            .orElseThrow(() -> new IllegalStateException("Tried to get own ModContainer, but it wasn't available. This should be impossible!!"));
        String loofahVersion = loofahModContainer.getMetadata().getVersion().getFriendlyString();
        this.setVersion(loofahVersion == null ? "dev" : loofahVersion);

        final Path baseDirectory = loader.getGameDir();
        this.setBaseDirectory(baseDirectory);
        this.setMetadataFilePath(PluginPlatformConstants.METADATA_FILE_LOCATION);

        this.config = LaunchConfig.load(baseDirectory, true);

        final Path modsDirectory = baseDirectory.resolve("mods");
        this.tokens = new TokenReplacement();
        this.tokens.register("BASE_DIR", baseDirectory);
        this.tokens.register("CONFIG_DIR", this.configDirectory());
        this.tokens.register("MODS_DIR", modsDirectory);

        this.locatorServices = new HashMap<>();
        this.languageServices = new HashMap<>();
        this.locatorResources = new HashMap<>();
        this.pluginCandidates = new IdentityHashMap<>();

        final Path additionalPluginsDirectory = Path.of(this.tokens.replace(this.config.additionalPluginsDirectory()));
        Files.createDirectories(additionalPluginsDirectory);
        this.setPluginDirectories(List.of(modsDirectory, additionalPluginsDirectory));
    }

    @Override
    public String version() {
        return this.environment.blackboard().get(Keys.VERSION);
    }

    public void setVersion(String version) {
        this.environment.blackboard().set(Keys.VERSION, version);
    }

    @Override
    public Logger logger() {
        return this.environment.logger();
    }

    @Override
    public boolean vanilla() {
        return false;
    }

    @Override
    public Path baseDirectory() {
        return this.environment.blackboard().get(Keys.BASE_DIRECTORY);
    }

    @Override
    public Path configDirectory() {
        return this.baseDirectory().resolve("config");
    }

    @Override
    public LaunchConfig config() {
        return this.config;
    }

    @Override
    public TokenReplacement tokens() {
        return this.tokens;
    }

    public void setBaseDirectory(Path path) {
        this.environment.blackboard().set(Keys.BASE_DIRECTORY, path);
    }

    @Override
    public List<Path> pluginDirectories() {
        return this.environment.blackboard().get(Keys.PLUGIN_DIRECTORIES);
    }

    public void setPluginDirectories(List<Path> list) {
        this.environment.blackboard().set(Keys.PLUGIN_DIRECTORIES, list);
    }

    public String metadataFilePath() {
        return this.environment.blackboard().get(Keys.METADATA_FILE_PATH);
    }

    public void setMetadataFilePath(String path) {
        this.environment.blackboard().set(Keys.METADATA_FILE_PATH, path);
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public Map<String, PluginResourceLocatorService<? extends PluginResource>> getLocatorServices() {
        return Collections.unmodifiableMap(this.locatorServices);
    }

    public Map<String, PluginLanguageService> getLanguageServices() {
        return Collections.unmodifiableMap(this.languageServices);
    }

    public Map<String, Set<? extends PluginResource>> getResources() {
        return Collections.unmodifiableMap(this.locatorResources);
    }

    public Map<PluginLanguageService, List<PluginCandidate>> getCandidates() {
        return Collections.unmodifiableMap(this.pluginCandidates);
    }

    public void discoverLocatorServices() {
        final var serviceLoader = ServiceLoader.load(PluginResourceLocatorService.class, FabricPluginPlatform.class.getClassLoader());

        for (final var iter = serviceLoader.iterator(); iter.hasNext(); ) {
            final PluginResourceLocatorService<?> next;

            try {
                next = iter.next();
            } catch (final ServiceConfigurationError e) {
                this.environment.logger().error("Error encountered initializing plugin resource locator!", e);
                continue;
            }

            this.logger().info("Plugin resource locator '{}' found.", next.name());
            this.locatorServices.put(next.name(), next);
        }
    }

    public void discoverLanguageServices() {
        this.environment.blackboard().set(JVMKeys.JVM_PLUGIN_RESOURCE_FACTORY, FabricPluginResource::new);
        this.environment.blackboard().set(JVMKeys.ENVIRONMENT_LOCATOR_VARIABLE_NAME, "SPONGE_PLUGINS");
        final ServiceLoader<PluginLanguageService> serviceLoader = ServiceLoader.load(
            PluginLanguageService.class, FabricPluginPlatform.class.getClassLoader()
        );

        for (final Iterator<PluginLanguageService> iter = serviceLoader.iterator(); iter.hasNext(); ) {
            final PluginLanguageService next;

            try {
                next = iter.next();
            } catch (final ServiceConfigurationError e) {
                this.environment.logger().error("Error encountered initializing plugin language service!", e);
                continue;
            }

            this.logger().info("Plugin language loader '{}' found.", next.name());
            this.languageServices.put(next.name(), next);
        }
    }

    public void locatePluginResources() {
        for (final Map.Entry<String, PluginResourceLocatorService<?>> locatorEntry : this.locatorServices.entrySet()) {
            final PluginResourceLocatorService<?> locatorService = locatorEntry.getValue();
            final Set<? extends PluginResource> resources = locatorService.locatePluginResources(this.environment);
            if (!resources.isEmpty()) {
                this.locatorResources.put(locatorEntry.getKey(), resources);
            }
        }
    }

    public void createPluginCandidates() {
        for (final PluginLanguageService languageService : this.languageServices.values()) {
            if (languageService.name().equals("fabric_mod")) {
                for (final PluginResource pluginResource : this.locatorResources.values().stream().flatMap(Collection::stream).filter(pluginResource -> pluginResource.locator().equals("fabric_mods")).toList()) {
                    try {
                        final List<PluginCandidate> candidates = languageService.createPluginCandidates(this.environment,
                            pluginResource);
                        if (candidates.isEmpty()) {
                            continue;
                        }
                        this.pluginCandidates.computeIfAbsent(languageService, k -> new LinkedList<>()).addAll(candidates);
                    } catch (final Exception ex) {
                        this.environment.logger().error("Failed to create plugin candidates", ex);
                    }
                }
            } else {
                for (final PluginResource pluginResource : this.locatorResources.values().stream().flatMap(Collection::stream).filter(pluginResource -> !pluginResource.locator().equals("fabric_mods")).toList()) {
                    try {
                        final List<PluginCandidate> candidates = languageService.createPluginCandidates(this.environment,
                            pluginResource);
                        if (candidates.isEmpty()) {
                            continue;
                        }
                        this.pluginCandidates.computeIfAbsent(languageService, k -> new LinkedList<>()).addAll(candidates);
                    } catch (final Exception ex) {
                        this.environment.logger().error("Failed to create plugin candidates", ex);
                    }
                }
            }
        }
    }
}
