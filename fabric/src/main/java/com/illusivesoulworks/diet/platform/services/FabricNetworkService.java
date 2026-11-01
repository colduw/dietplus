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

package com.illusivesoulworks.diet.platform.services;

import com.illusivesoulworks.diet.common.DietFabricNetwork;
import com.illusivesoulworks.diet.common.data.effect.DietEffectsInfo;
import com.illusivesoulworks.diet.common.network.server.SPacketActivate;
import com.illusivesoulworks.diet.common.network.server.SPacketDiet;
import com.illusivesoulworks.diet.common.network.server.SPacketEaten;
import com.illusivesoulworks.diet.common.network.server.SPacketEffectsInfo;
import com.illusivesoulworks.diet.common.network.server.SPacketGroups;
import com.illusivesoulworks.diet.common.network.server.SPacketSuites;
import java.util.Map;
import java.util.Set;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

public class FabricNetworkService implements INetworkService {
  @Override
  public void sendEffectsInfoS2C(ServerPlayer player, DietEffectsInfo info) {
    ServerPlayNetworking.send(player, new SPacketEffectsInfo(info));
  }

  @Override
  public void sendDietS2C(ServerPlayer player, String suite, Map<String, Float> groups) {
    ServerPlayNetworking.send(player, new SPacketDiet(suite, groups));
  }

  @Override
  public void sendActivationS2C(ServerPlayer player, boolean flag) {
    ServerPlayNetworking.send(player, new SPacketActivate(flag));
  }

  @Override
  public void sendEatenS2C(ServerPlayer player, Set<Item> items) {
    ServerPlayNetworking.send(player, new SPacketEaten(items));
  }

  @Override
  public void sendDietGroupsS2C(ServerPlayer player, CompoundTag groups, Map<Item, Set<String>> generated) {
    ServerPlayNetworking.send(player, new SPacketGroups(groups, generated));
  }

  @Override
  public void sendDietSuitesS2C(ServerPlayer player, CompoundTag suites) {
    ServerPlayNetworking.send(player, new SPacketSuites(suites));
  }
}
