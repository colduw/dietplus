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
import com.illusivesoulworks.diet.common.DietFabricNetwork;
import com.illusivesoulworks.diet.update.UpdateCheck;
import com.illusivesoulworks.diet.update.UpdateResult;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;

public class DietFabricClientMod implements ClientModInitializer {
  private static UpdateResult updateResult;

  @Override
  public void onInitializeClient() {
    UpdateCheck.updateManager().asyncUpdate(DietConstants.MOD_ID, update -> updateResult = update);

    ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
      if (updateResult != null && client.player != null) {
        DietConstants.LOG.info("Diet+ version {}. (from version JSON): latest: {}, recommended: {}", updateResult.currentVersion, updateResult.latestVersion, updateResult.recommendedVersion);
        switch (updateResult.verStates) {
          case AHEAD -> client.player.sendSystemMessage(
                  Component.translatable("dietplus.init.update.ahead",
                          Component.literal(updateResult.currentVersion).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD),
                          Component.literal(updateResult.latestVersion).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD),
                          Component.literal(updateResult.recommendedVersion).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD)
                  )
          );
          case LATEST -> client.player.sendSystemMessage(
                  Component.translatable("dietplus.init.update.latest",
                          Component.literal(updateResult.latestVersion).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD)
                  )
          );
          case RECOMMENDED -> client.player.sendSystemMessage(
                  Component.translatable("dietplus.init.update.recommended",
                          Component.literal(updateResult.recommendedVersion).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD),
                          Component.literal(updateResult.latestVersion).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD)
                  )
          );
          case BEHIND_LATEST_AHEAD_RECOMMENDED -> client.player.sendSystemMessage(
                  Component.translatable("dietplus.init.update.behindlatestaheadrecommended",
                          Component.literal(updateResult.currentVersion).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD),
                          Component.literal(updateResult.latestVersion).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD),
                          Component.literal(updateResult.recommendedVersion).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD)
                  ).withStyle(ChatFormatting.GOLD)
          );
          case BEHIND -> client.player.sendSystemMessage(
                  Component.translatable("dietplus.init.update.behind",
                          Component.literal(updateResult.currentVersion).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD),
                          Component.literal(updateResult.latestVersion).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD),
                          Component.literal(updateResult.recommendedVersion).withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD)
                  ).withStyle(ChatFormatting.GOLD)
          );
          case ERROR -> client.player.sendSystemMessage(
                  Component.translatable("dietplus.init.update.error",
                          Component.literal(updateResult.currentVersion) // stores the ex message
                  ).withStyle(ChatFormatting.RED)
          );
        }
      }
    }));

    DietFabricNetwork.setupClient();
    KeyBindingHelper.registerKeyBinding(DietKeys.get());
    ClientTickEvents.END_CLIENT_TICK.register(DietClientEvents::tick);
    ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> DietClientEvents.renderItemTooltip(Minecraft.getInstance().player, stack, lines));
    ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> ScreenEvents.afterRender(screen).register((screen1, matrices, mouseX, mouseY, tickDelta) -> DietClientEvents.renderEffectsTooltip(screen1, matrices)));
    ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
      if (screen instanceof InventoryScreen inventoryScreen) {
        Screens.getButtons(inventoryScreen).add(DietClientEvents.getButton(inventoryScreen));
      }
    });
  }
}
