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

package com.illusivesoulworks.diet.common;

import com.illusivesoulworks.diet.DietCommonMod;
import com.illusivesoulworks.diet.common.network.server.SPacketActivate;
import com.illusivesoulworks.diet.common.network.server.SPacketDiet;
import com.illusivesoulworks.diet.common.network.server.SPacketEaten;
import com.illusivesoulworks.diet.common.network.server.SPacketEffectsInfo;
import com.illusivesoulworks.diet.common.network.server.SPacketGroups;
import com.illusivesoulworks.diet.common.network.server.SPacketSuites;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class DietFabricNetwork {
  public static void setupServer() {
    PayloadTypeRegistry.playS2C().register(SPacketEffectsInfo.EFFECTS_INFO, SPacketEffectsInfo.CODEC);
    PayloadTypeRegistry.playS2C().register(SPacketDiet.DIET, SPacketDiet.CODEC);
    PayloadTypeRegistry.playS2C().register(SPacketActivate.ACTIVATION, SPacketActivate.CODEC);
    PayloadTypeRegistry.playS2C().register(SPacketEaten.EATEN, SPacketEaten.CODEC);
    PayloadTypeRegistry.playS2C().register(SPacketGroups.GROUPS, SPacketGroups.CODEC);
    PayloadTypeRegistry.playS2C().register(SPacketSuites.SUITES, SPacketSuites.CODEC);
  }

  public static void setupClient() {
    ClientPlayNetworking.registerGlobalReceiver(SPacketEffectsInfo.EFFECTS_INFO, (p, ctx) -> SPacketEffectsInfo.handle(p));
    ClientPlayNetworking.registerGlobalReceiver(SPacketDiet.DIET, (p, ctx) -> SPacketDiet.handle(p));
    ClientPlayNetworking.registerGlobalReceiver(SPacketActivate.ACTIVATION, (p, ctx) -> SPacketActivate.handle(p));
    ClientPlayNetworking.registerGlobalReceiver(SPacketEaten.EATEN, (p, ctx) -> SPacketEaten.handle(p));
    ClientPlayNetworking.registerGlobalReceiver(SPacketGroups.GROUPS, (p, ctx) -> SPacketGroups.handle(p));
    ClientPlayNetworking.registerGlobalReceiver(SPacketSuites.SUITES, (p, ctx) -> SPacketSuites.handle(p));
  }
}
