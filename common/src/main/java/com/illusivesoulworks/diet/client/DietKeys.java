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

package com.illusivesoulworks.diet.client;

import com.illusivesoulworks.diet.DietConstants;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;

public class DietKeys {
  public static final KeyMapping OPEN_GUI = new KeyMapping("key.dietplus.open.desc", InputConstants.UNKNOWN.getValue(), KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath(DietConstants.MOD_ID, "category")));

  public static KeyMapping get() {
    return OPEN_GUI;
  }
}
