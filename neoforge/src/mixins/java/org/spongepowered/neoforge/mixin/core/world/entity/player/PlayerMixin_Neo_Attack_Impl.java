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
package org.spongepowered.neoforge.mixin.core.world.entity.player;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.common.util.DamageEventUtil;
import org.spongepowered.neoforge.mixin.core.world.entity.LivingEntityMixin_Neo_Attack_Impl;

@Mixin(Player.class)
public class PlayerMixin_Neo_Attack_Impl extends LivingEntityMixin_Neo_Attack_Impl {
    private DamageEventUtil.Attack<Player> attackImpl$attack;

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/entity/player/CriticalHitEvent;isCriticalHit()Z"))
    private boolean attackImpl$critHook(final CriticalHitEvent event) {
        if (event.isCriticalHit()) {
            this.attackImpl$attack.functions().add(DamageEventUtil.provideCriticalAttackFunction(this.attackImpl$attack.sourceEntity(), event.getDamageMultiplier()));
            return true;
        }
        return false;
    }

    /**
     * Set absorbed damage after calling {@link Player#setAbsorptionAmount} in which we called the event
     */
    @ModifyVariable(method = "actuallyHurt", ordinal = 2,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setAbsorptionAmount(F)V", shift = At.Shift.AFTER))
    public float attackImpl$setAbsorbed(final float value) {
        if (this.attackImpl$actuallyHurtResult.event().isCancelled()) {
            return 0;
        }
        return this.attackImpl$actuallyHurtResult.damageAbsorbed().orElse(0f);
    }
}
