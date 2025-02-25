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
package org.spongepowered.common.mixin.core.tags;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.common.bridge.tags.TagLoaderBridge;
import org.spongepowered.common.launch.Launch;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Mixin(TagManager.class)
public abstract class TagManagerMixin {

    @Inject(method = "reload", at = @At("HEAD"))
    private void impl$onReload(final PreparableReloadListener.PreparationBarrier $$0, final ResourceManager $$1, final ProfilerFiller $$2, final ProfilerFiller $$3, final Executor $$4, final Executor $$5, final CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        Launch.instance().lifecycle().establishTags($$1);
    }

    @SuppressWarnings("unchecked")
    @WrapOperation(method = "createLoader", at = @At(value = "NEW", target = "Lnet/minecraft/tags/TagLoader;"))
    private <T> TagLoader<T> impl$onCreateLoader(final Function<ResourceLocation, Optional<? extends T>> $$0, final String $$1, final Operation<TagLoader<T>> original,
            final ResourceManager resourceManager, Executor executor, RegistryAccess.RegistryEntry<T> registryEntry) {
        final TagLoader<T> loader = original.call($$0, $$1);
        ((TagLoaderBridge<T>) loader).bridge$registryEntry(registryEntry);
        return loader;
    }
}
