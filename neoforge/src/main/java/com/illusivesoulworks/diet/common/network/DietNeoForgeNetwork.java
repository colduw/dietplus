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
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class DietNeoForgeNetwork {
  private static final String PTC_VERSION = "1";

  public static void setup(RegisterPayloadHandlerEvent event) {
    IPayloadRegistrar registrar = event.registrar(DietConstants.MOD_ID).versioned(PTC_VERSION);

    /*
      public static final ResourceLocation EFFECTS_INFO = DietCommonMod.resource("effects_info");
      public static final ResourceLocation DIET = DietCommonMod.resource("diet");
      public static final ResourceLocation ACTIVATION = DietCommonMod.resource("activation");
      public static final ResourceLocation EATEN = DietCommonMod.resource("eaten");
      public static final ResourceLocation GROUPS = DietCommonMod.resource("groups");
      public static final ResourceLocation SUITES = DietCommonMod.resource("suites");
    */

    // Server Packets
    register(registrar, SPacketDiet.DIET, SPacketDiet::decode, SPacketDiet::handle);
    register(registrar, SPacketEffectsInfo.EFFECTS_INFO, SPacketEffectsInfo::decode, SPacketEffectsInfo::handle);
    register(registrar, SPacketActivate.ACTIVATION, SPacketActivate::decode, SPacketActivate::handle);
    register(registrar, SPacketEaten.EATEN, SPacketEaten::decode, SPacketEaten::handle);
    register(registrar, SPacketGroups.GROUPS, SPacketGroups::decode, SPacketGroups::handle);
    register(registrar, SPacketSuites.SUITES, SPacketSuites::decode, SPacketSuites::handle);
  }

  public static <M extends CustomPacketPayload> void register(IPayloadRegistrar registrar, ResourceLocation resourceLocation, FriendlyByteBuf.Reader<M> reader, Consumer<M> handler) {
    registrar.play(resourceLocation, reader, h -> h.client((m, playPayloadContext) -> playPayloadContext.workHandler().execute(() -> handler.accept(m))));
  }
}
