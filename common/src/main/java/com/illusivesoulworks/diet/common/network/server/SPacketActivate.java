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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SPacketActivate(boolean flag) implements CustomPacketPayload {
  public static final Type<SPacketActivate> ACTIVATION = new Type<>(DietCommonMod.resource("activation"));
  public static final StreamCodec<FriendlyByteBuf, SPacketActivate> CODEC = StreamCodec.ofMember(SPacketActivate::encode, SPacketActivate::decode);

  @Override
  public Type<SPacketActivate> type() {
    return ACTIVATION;
  }

  public static void encode(SPacketActivate msg, FriendlyByteBuf buf) {
    buf.writeBoolean(msg.flag);
  }
  public static SPacketActivate decode(FriendlyByteBuf buf) {
    return new SPacketActivate(buf.readBoolean());
  }
  public static void handle(SPacketActivate msg) {
    DietClientPacketReceiver.handleActivate(msg);
  }
}
