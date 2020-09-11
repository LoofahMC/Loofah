/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.spongepowered.vanilla.installer;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class LauncherCommandLine {

    private static final OptionParser PARSER = new OptionParser();
    private static final ArgumentAcceptingOptionSpec<Path> INSTALLER_DIRECTORY_ARG = PARSER.accepts("installerDir", "Alternative installer directory")
            .withRequiredArg().withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING)).defaultsTo(Paths.get("."));
    private static final ArgumentAcceptingOptionSpec<Path> LIBRARIES_DIRECTORY_ARG = PARSER.accepts("librariesDir", "Alternative libraries directory")
            .withRequiredArg().withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING)).defaultsTo(Paths.get("libraries"));
    private static final ArgumentAcceptingOptionSpec<Boolean> DOWNLOAD_MINECRAFT_JAR = PARSER.accepts("downloadMinecraftJar", "If true, we'll "
            + "download the Minecraft jar.").withOptionalArg().ofType(Boolean.class).defaultsTo(Boolean.TRUE);
    private static final ArgumentAcceptingOptionSpec<Boolean> CHECK_MINECRAFT_JAR_HASH = PARSER.accepts("checkMinecraftJarHash", "If true, we'll "
            + "verify the Minecraft jar hash (to protect against corruption/etc).").withOptionalArg().ofType(Boolean.class).defaultsTo(Boolean.TRUE);
    private static final ArgumentAcceptingOptionSpec<Boolean> DOWNLOAD_SRG_MAPPINGS = PARSER.accepts("downloadSrgMappings", "If true, we'll "
            + "download the SRG mappings").withOptionalArg().ofType(Boolean.class).defaultsTo(Boolean.TRUE);

    public static String[] RAW_ARGS;
    public static Path installerDirectory, librariesDirectory;
    public static boolean downloadMinecraftJar, checkMinecraftJarHash, downloadSrgMappings;

    public static void configure(final String[] args) {
        LauncherCommandLine.PARSER.allowsUnrecognizedOptions();

        final OptionSet options = LauncherCommandLine.PARSER.parse(args);
        LauncherCommandLine.installerDirectory = options.valueOf(LauncherCommandLine.INSTALLER_DIRECTORY_ARG);
        LauncherCommandLine.librariesDirectory = options.valueOf(LauncherCommandLine.LIBRARIES_DIRECTORY_ARG);
        LauncherCommandLine.downloadMinecraftJar = options.valueOf(LauncherCommandLine.DOWNLOAD_MINECRAFT_JAR);
        LauncherCommandLine.checkMinecraftJarHash = options.valueOf(LauncherCommandLine.CHECK_MINECRAFT_JAR_HASH);
        LauncherCommandLine.downloadSrgMappings = options.valueOf(LauncherCommandLine.DOWNLOAD_SRG_MAPPINGS);

        LauncherCommandLine.RAW_ARGS = args;
    }

    private LauncherCommandLine() {
    }
}
