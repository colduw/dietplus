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

package com.illusivesoulworks.diet.common.network.server;

import com.illusivesoulworks.diet.DietCommonMod;
import com.illusivesoulworks.diet.client.DietClientPacketReceiver;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SPacketSuites(CompoundTag suites) implements CustomPacketPayload {
  public static final ResourceLocation SUITES = DietCommonMod.resource("suites");

  @Override
  public void write(FriendlyByteBuf buf) {
    encode(this, buf);
  }

  @Override
  public ResourceLocation id() {
    return SUITES;
  }

  public static void encode(SPacketSuites msg, FriendlyByteBuf buf) {
    buf.writeNbt(msg.suites());
  }
  public static SPacketSuites decode(FriendlyByteBuf buf) {
    return new SPacketSuites(buf.readNbt());
  }
  public static void handle(SPacketSuites msg) {
    DietClientPacketReceiver.handleSuites(msg.suites());
  }
}
