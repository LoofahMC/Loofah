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
package org.spongepowered.common.item.inventory.util;

import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

import javax.annotation.Nullable;

public abstract class ItemStackUtil {

    private ItemStackUtil() {
    }

    public static NBTTagCompound getTagCompound(net.minecraft.item.ItemStack itemStack) {
        NBTTagCompound compound = itemStack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
            itemStack.setTagCompound(compound);
        }
        return compound;
    }

    @Nullable
    public static net.minecraft.item.ItemStack toNative(@Nullable ItemStack stack) {
        if (stack instanceof net.minecraft.item.ItemStack || stack == null) {
            return (net.minecraft.item.ItemStack) stack;
        }
        throw new NativeStackException("The supplied item stack was not native to the current platform");
    }

    @Nullable
    public static ItemStack fromNative(@Nullable net.minecraft.item.ItemStack stack) {
        if (stack instanceof ItemStack || stack == null) {
            return (ItemStack) stack;
        }
        throw new NativeStackException("The supplied native item stack was not compatible with the target environment");
    }

    @Nullable
    public static net.minecraft.item.ItemStack cloneDefensiveNative(@Nullable net.minecraft.item.ItemStack stack) {
        return stack.copy();
    }

    @Nullable
    public static net.minecraft.item.ItemStack cloneDefensiveNative(@Nullable net.minecraft.item.ItemStack stack, int newSize) {
        net.minecraft.item.ItemStack clone = stack.copy();
        if (clone != null) {
            clone.func_190920_e(newSize);
        }
        return clone;
    }

    @Nullable
    public static ItemStack cloneDefensive(@Nullable net.minecraft.item.ItemStack stack) {
        return (ItemStack) ItemStackUtil.cloneDefensiveNative(stack);
    }

    @Nullable
    public static ItemStack cloneDefensive(@Nullable ItemStack stack) {
        return ItemStackUtil.cloneDefensive(ItemStackUtil.toNative(stack));
    }

    @Nullable
    public static ItemStack cloneDefensive(net.minecraft.item.ItemStack stack, int newSize) {
        return (ItemStack) ItemStackUtil.cloneDefensiveNative(stack, newSize);
    }

    @Nullable
    public static ItemStack cloneDefensive(@Nullable ItemStack stack, int newSize) {
        return ItemStackUtil.cloneDefensive(ItemStackUtil.toNative(stack), newSize);
    }

    public static Optional<ItemStack> cloneDefensiveOptional(@Nullable net.minecraft.item.ItemStack stack) {
        return Optional.<ItemStack>ofNullable(ItemStackUtil.cloneDefensive(stack));
    }

    public static Optional<ItemStack> cloneDefensiveOptional(@Nullable net.minecraft.item.ItemStack stack, int withdraw) {
        return Optional.<ItemStack>ofNullable(ItemStackUtil.cloneDefensive(stack));
    }

    public static boolean compare(net.minecraft.item.ItemStack stack1, net.minecraft.item.ItemStack stack2) {
        return net.minecraft.item.ItemStack.areItemStacksEqual(stack1, stack2);
    }

    public static boolean compare(net.minecraft.item.ItemStack stack1, ItemStack stack2) {
        return ItemStackUtil.compare(stack1, ItemStackUtil.toNative(stack2));
    }

    public static boolean compare(ItemStack stack1, ItemStack stack2) {
        return ItemStackUtil.compare(ItemStackUtil.toNative(stack1), ItemStackUtil.toNative(stack2));
    }

    public static boolean compare(ItemStack stack1, net.minecraft.item.ItemStack stack2) {
        return ItemStackUtil.compare(ItemStackUtil.toNative(stack1), stack2);
    }

    public static ItemStackSnapshot createSnapshot(net.minecraft.item.ItemStack item) {
        return ItemStackUtil.fromNative(item).createSnapshot();
    }

    public static ItemStackSnapshot snapshotOf(@Nullable net.minecraft.item.ItemStack itemStack) {
        return itemStack == null ? ItemStackSnapshot.NONE : fromNative(itemStack).createSnapshot();
    }

    public static ItemStackSnapshot snapshotOf(@Nullable ItemStack itemStack) {
        return itemStack == null ? ItemStackSnapshot.NONE : itemStack.createSnapshot();
    }

    @Nullable
    public static net.minecraft.item.ItemStack fromSnapshotToNative(@Nullable ItemStackSnapshot snapshot) {
        return snapshot == null ? net.minecraft.item.ItemStack.field_190927_a : snapshot == ItemStackSnapshot.NONE ? net.minecraft.item.ItemStack.field_190927_a: toNative(snapshot.createStack());
    }

    @Nullable
    public static ItemStack fromSnapshot(@Nullable ItemStackSnapshot snapshot) {
        return snapshot == null ? null : snapshot == ItemStackSnapshot.NONE ? null : snapshot.createStack();
    }
}
