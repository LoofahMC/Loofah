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
package dk.nelind.loofah.compat;

import me.lucko.fabric.api.permissions.v0.OfflineOptionRequestEvent;
import me.lucko.fabric.api.permissions.v0.OfflinePermissionCheckEvent;
import me.lucko.fabric.api.permissions.v0.OptionRequestEvent;
import me.lucko.fabric.api.permissions.v0.PermissionCheckEvent;
import net.fabricmc.fabric.api.util.TriState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCause;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class FabricPermissionApiCompat {
    public static void registerEventHandlers() {
        PermissionCheckEvent.EVENT.register((source, permission) -> {
            var permValue = ((CommandCause) source).permissionValue(permission);
            return switch (permValue) {
                case TRUE -> TriState.TRUE;
                case FALSE -> TriState.FALSE;
                case UNDEFINED -> TriState.DEFAULT;
            };
        });
        OfflinePermissionCheckEvent.EVENT.register(((uuid, permission) -> {
            if (Sponge.isServerAvailable()) {
                return Sponge.server().userManager()
                    .loadOrCreate(uuid)
                    .thenApply((user -> switch (user.permissionValue(permission)) {
                        case TRUE -> TriState.TRUE;
                        case FALSE -> TriState.FALSE;
                        case UNDEFINED -> TriState.DEFAULT;
                    }));
            }

            // If the server isn't available we return DEFAULT. This is considered correct because the permissions API
            // does the same in case offline permission checks aren't supported
            return CompletableFuture.completedFuture(TriState.DEFAULT);
        }));

        OptionRequestEvent.EVENT.register((source, key) -> ((CommandCause) source).option(key));
        OfflineOptionRequestEvent.EVENT.register((uuid, key) -> {
            if (Sponge.isServerAvailable()) {
                return Sponge.server().userManager()
                    .loadOrCreate(uuid)
                    .thenApply((user -> user.option(key)));
            }

            // If the server isn't available we return Optional.empty(). This is considered correct because the
            // permissions API does the same (in a newer version after a bug fix) in case offline options checks aren't
            // supported
            return CompletableFuture.completedFuture(Optional.empty());
        });
    }
}
