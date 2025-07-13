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
package org.spongepowered.common.event.lifecycle;

import io.leangen.geantyref.TypeToken;
import org.spongepowered.api.Client;
import org.spongepowered.api.Engine;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.lifecycle.FreezeRegistryEvent;
import org.spongepowered.api.registry.RegistryHolder;

public abstract class FreezeRegistryEventImpl extends AbstractLifecycleEvent implements FreezeRegistryEvent {

    protected FreezeRegistryEventImpl(final Cause cause, final Game game) {
        super(cause, game);
    }

    public static abstract class PostImpl extends FreezeRegistryEventImpl implements Post {

        protected PostImpl(final Cause cause, final Game game) {
            super(cause, game);
        }

        public static final class GameImpl extends PostImpl implements GameScoped {

            public GameImpl(final Cause cause, final Game game) {
                super(cause, game);
            }

            @Override
            public RegistryHolder holder() {
                return this.game;
            }
        }

        public static final class EngineImpl<E extends Engine> extends PostImpl implements EngineScoped<E> {

            private final RegistryHolder registryHolder;
            private final TypeToken<E> paramType;

            private EngineImpl(final Cause cause, final Game game, final RegistryHolder registryHolder, final TypeToken<E> paramType) {
                super(cause, game);
                this.registryHolder = registryHolder;
                this.paramType = paramType;
            }

            @Override
            public TypeToken<E> paramType() {
                return this.paramType;
            }

            @Override
            public RegistryHolder holder() {
                return this.registryHolder;
            }

            public static EngineImpl<Server> server(final Cause cause, final Game game, final RegistryHolder registryHolder) {
                return new EngineImpl<>(cause, game, registryHolder, TypeToken.get(Server.class));
            }

            public static EngineImpl<Client> client(final Cause cause, final Game game, final RegistryHolder registryHolder) {
                return new EngineImpl<>(cause, game, registryHolder, TypeToken.get(Client.class));
            }
        }
    }
}
