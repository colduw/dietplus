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

package com.illusivesoulworks.diet.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@SuppressWarnings("unused")
public class DietEvent extends PlayerEvent {
  public DietEvent(Player player) {
    super(player);
  }

  public static class ConsumeItemStack extends DietEvent implements ICancellableEvent {
    private final ItemStack stack;

    public ConsumeItemStack(ItemStack stackIn, Player player) {
      super(player);
      stack = stackIn;
    }

    public ItemStack getStack() {
      return stack;
    }
  }

  public static class ApplyDecay extends DietEvent implements ICancellableEvent {
    public ApplyDecay(Player player) {
      super(player);
    }
  }

  public static class ApplyEffect extends DietEvent implements ICancellableEvent {
    public ApplyEffect(Player player) {
      super(player);
    }
  }
}
