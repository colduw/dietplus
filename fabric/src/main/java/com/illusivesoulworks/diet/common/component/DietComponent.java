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

package com.illusivesoulworks.diet.common.component;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.common.capability.PlayerDietTracker;
import javax.annotation.Nonnull;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.ladysnake.cca.api.v3.component.Component;

public class DietComponent extends PlayerDietTracker implements Component {
  public DietComponent(Player player) {
    super(player);
  }

  @Override
  public void readData(ValueInput valueInput) {
    valueInput.read(DietConstants.MOD_ID, CompoundTag.CODEC).ifPresent(this::load);
  }

  @Override
  public void writeData(ValueOutput valueOutput) {
    CompoundTag compoundTag = new CompoundTag();
    this.save(compoundTag);
    valueOutput.store(DietConstants.MOD_ID, CompoundTag.CODEC, compoundTag);
  }
}
