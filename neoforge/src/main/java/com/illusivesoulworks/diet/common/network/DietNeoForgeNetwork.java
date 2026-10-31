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

package com.illusivesoulworks.diet.common.network;

import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.common.network.server.SPacketActivate;
import com.illusivesoulworks.diet.common.network.server.SPacketDiet;
import com.illusivesoulworks.diet.common.network.server.SPacketEaten;
import com.illusivesoulworks.diet.common.network.server.SPacketEffectsInfo;
import com.illusivesoulworks.diet.common.network.server.SPacketGroups;
import com.illusivesoulworks.diet.common.network.server.SPacketSuites;

import java.util.function.Consumer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class DietNeoForgeNetwork {
  private static final String PTC_VERSION = "1";

  public static void setup(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar(DietConstants.MOD_ID).versioned(PTC_VERSION);

    /*
      public static final ResourceLocation EFFECTS_INFO = DietCommonMod.resource("effects_info");
      public static final ResourceLocation DIET = DietCommonMod.resource("diet");
      public static final ResourceLocation ACTIVATION = DietCommonMod.resource("activation");
      public static final ResourceLocation EATEN = DietCommonMod.resource("eaten");
      public static final ResourceLocation GROUPS = DietCommonMod.resource("groups");
      public static final ResourceLocation SUITES = DietCommonMod.resource("suites");
    */

    // Server Packets
    registrar.playToClient(SPacketDiet.DIET, SPacketDiet.CODEC, (p, ctx) -> SPacketDiet.handle(p));
    registrar.playToClient(SPacketEffectsInfo.EFFECTS_INFO, SPacketEffectsInfo.CODEC, (p, ctx) -> SPacketEffectsInfo.handle(p));
    registrar.playToClient(SPacketActivate.ACTIVATION, SPacketActivate.CODEC, (p, ctx) -> SPacketActivate.handle(p));
    registrar.playToClient(SPacketEaten.EATEN, SPacketEaten.CODEC, (p, ctx) -> SPacketEaten.handle(p));
    registrar.playToClient(SPacketGroups.GROUPS, SPacketGroups.CODEC, (p, ctx) -> SPacketGroups.handle(p));
    registrar.playToClient(SPacketSuites.SUITES, SPacketSuites.CODEC, (p, ctx) -> SPacketSuites.handle(p));
  }
}
