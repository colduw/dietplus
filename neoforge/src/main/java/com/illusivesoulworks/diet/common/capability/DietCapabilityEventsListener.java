/*
 * Copyright (C) 2021-2023 Illusive Soulworks
 *
 * Diet is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Diet is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Diet.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.diet.common.capability;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.api.type.IDietTracker;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = DietConstants.MOD_ID)
public class DietCapabilityEventsListener {
  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void playerClone(final PlayerEvent.Clone evt) {
    if (evt.getEntity() instanceof ServerPlayer player) {
      Player original = evt.getOriginal();
      DietCapability.get(player).ifPresent(tracker -> tracker.copy(original, evt.isWasDeath()));
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void playerRespawned(final PlayerEvent.PlayerRespawnEvent evt) {
    if (evt.getEntity() instanceof ServerPlayer player) {
      DietCapability.get(player).ifPresent(IDietTracker::sync);
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void playerLoggedIn(final PlayerEvent.PlayerLoggedInEvent evt) {
    if (evt.getEntity() instanceof ServerPlayer player) {
      DietCapability.get(player).ifPresent(IDietTracker::sync);
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void playerDimensionTravel(final PlayerEvent.PlayerChangedDimensionEvent evt) {
    if (evt.getEntity() instanceof ServerPlayer player) {
      DietCapability.get(player).ifPresent(IDietTracker::sync);
    }
  }

  @SubscribeEvent
  @SuppressWarnings("unused")
  public static void playerTick(final PlayerTickEvent.Post evt) {
    DietCapability.get(evt.getEntity()).ifPresent(IDietTracker::tick);
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  @SuppressWarnings("unused")
  public static void finishItemUse(final LivingEntityUseItemEvent.Finish evt) {
    ItemStack stack = evt.getItem();
    LivingEntity livingEntity = evt.getEntity();

    if (!livingEntity.level().isClientSide() && livingEntity instanceof Player) {
      FoodProperties food = stack.get(DataComponents.FOOD);

      if (food != null) {
        DietCapability.get((Player) livingEntity).ifPresent(diet -> diet.consume(stack));
      }
    }
  }
}
