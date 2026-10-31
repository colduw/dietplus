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
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class DietCapability {
  public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPE_DEFERRED_REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DietConstants.MOD_ID);
  public static final Supplier<AttachmentType<IDietTracker>> DIET_TRACKER = ATTACHMENT_TYPE_DEFERRED_REGISTER.register("diet_tracker", () -> AttachmentType.<IDietTracker>builder(
          iAttachmentHolder -> new PlayerDietTracker((Player) iAttachmentHolder)
  ).serialize(new IAttachmentSerializer<IDietTracker>() {
            @Override
            public boolean write(IDietTracker iDietTracker, ValueOutput valueOutput) {
              CompoundTag tag = new CompoundTag();
              iDietTracker.save(tag);
              valueOutput.store(DietConstants.MOD_ID, CompoundTag.CODEC, tag);
              return true;
            }

            @Override
            public IDietTracker read(IAttachmentHolder holder, ValueInput valueInput) {
              IDietTracker dietTracker = new PlayerDietTracker((Player) holder);
              valueInput.read(DietConstants.MOD_ID, CompoundTag.CODEC).ifPresent(dietTracker::load);
              return dietTracker;
            }
          })
          .build());
  // private static final Map<UUID, LazyOptional<IDietTracker>> SERVER_CACHE = new HashMap<>();
  // private static final Map<UUID, LazyOptional<IDietTracker>> CLIENT_CACHE = new HashMap<>();

  public static Optional<IDietTracker> get(final Player player) {
//    UUID key = player.getUUID();
//    Map<UUID, LazyOptional<IDietTracker>> cache =
//        player.getLevel().isClientSide() ? CLIENT_CACHE : SERVER_CACHE;
//    return cache.computeIfAbsent(key, (k) -> {
//      LazyOptional<IDietTracker> opt = player.getCapability(DIET_TRACKER);
//      opt.addListener((v) -> cache.remove(key));
//      return opt;
//    });
    return Optional.of(player.getData(DIET_TRACKER));
  }
}
