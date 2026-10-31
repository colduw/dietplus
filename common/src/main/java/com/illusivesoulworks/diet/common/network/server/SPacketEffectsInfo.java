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
import com.illusivesoulworks.diet.common.data.effect.DietEffectsInfo;
import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class SPacketEffectsInfo implements CustomPacketPayload {
  public static final ResourceLocation EFFECTS_INFO = DietCommonMod.resource("effects_info");
  private final DietEffectsInfo info;

  @Override
  public void write(FriendlyByteBuf buf) {
    encode(this, buf);
  }

  @Override
  public ResourceLocation id() {
    return EFFECTS_INFO;
  }

  public SPacketEffectsInfo(DietEffectsInfo infoIn) {
    info = infoIn;
  }

  public static void encode(SPacketEffectsInfo msg, FriendlyByteBuf buf) {
    buf.writeNbt(msg.info.write());
  }

  public static SPacketEffectsInfo decode(FriendlyByteBuf buf) {
    return new SPacketEffectsInfo(DietEffectsInfo.read(
        Objects.requireNonNull(buf.readNbt())));
  }

  public static void handle(SPacketEffectsInfo msg) {
    DietClientPacketReceiver.handleEffectsInfo(msg.info);
  }
}
