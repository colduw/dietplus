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

package com.illusivesoulworks.diet.client.screen;

import com.google.common.collect.Lists;
import com.illusivesoulworks.diet.DietConstants;
import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.api.type.IDietSuite;
import com.illusivesoulworks.diet.api.util.DietColor;
import com.illusivesoulworks.diet.client.DietKeys;
import com.illusivesoulworks.diet.common.config.DietConfig;
import com.illusivesoulworks.diet.common.data.effect.DietEffectsInfo;
import com.illusivesoulworks.diet.common.data.suite.DietSuites;
import com.illusivesoulworks.diet.platform.Services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;

public class DietScreen extends Screen {

  private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/demo_background.png");
  private static final Identifier ICONS = Identifier.fromNamespaceAndPath(DietConstants.MOD_ID, "textures/gui/icons.png");

  public static DietEffectsInfo tooltip = new DietEffectsInfo();
  private final Set<IDietGroup> groups = new HashSet<>();

  private final int xSize;
  private int ySize;
  private final boolean fromInventory;

  public DietScreen(boolean fromInventory) {
    super(Component.translatable("gui." + DietConstants.MOD_ID + ".title"));
    this.xSize = 248;
    this.fromInventory = fromInventory;
  }

  @Override
  protected void init() {
    super.init();

    if (this.minecraft != null && this.minecraft.player != null && this.minecraft.level != null) {
      groups.addAll(Services.CAPABILITY.get(this.minecraft.player).map(
          tracker -> DietSuites.getSuite(this.minecraft.level, tracker.getSuite())
              .map(IDietSuite::getGroups).orElse(Set.of())).orElse(Set.of()));
    }
    this.ySize = this.groups.size() * 20 + 60;
    this.addRenderableWidget(
        Button.builder(Component.translatable("gui.diet.close"), (button) -> {
          if (this.minecraft != null && this.minecraft.player != null) {

            if (fromInventory) {
              this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
            } else {
              this.onClose();
            }
          }
        }).size(100, 20).pos(this.width / 2 - 50, (this.height + this.ySize) / 2 - 30).build());
  }

  @Override
  public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    // this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
    super.render(guiGraphics, mouseX, mouseY, partialTicks);
    this.renderForeground(guiGraphics, mouseX, mouseY);
    this.renderTitle(guiGraphics, mouseX, mouseY);
  }

  public void renderTitle(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    int titleWidth = this.font.width(this.title.getString());
    guiGraphics.drawString(this.font, this.title, this.width / 2 - titleWidth / 2,
        this.height / 2 - this.ySize / 2 + 10, getTextColor(), false);
    List<DietEffectsInfo.AttributeModifier> modifiers = DietScreen.tooltip.getModifiers();
    List<DietEffectsInfo.StatusEffect> effects = DietScreen.tooltip.getEffects();

    if (this.minecraft != null && (!modifiers.isEmpty() || !effects.isEmpty())) {
      int lowerX = this.width / 2 + titleWidth / 2 + 5;
      int lowerY = this.height / 2 - this.ySize / 2 + 7;
      int upperX = lowerX + 16;
      int upperY = lowerY + 16;
      guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ICONS, lowerX, lowerY, 0, 37, 16, 16, 16, 16, 256, 256);

      if (mouseX >= lowerX && mouseX <= upperX && mouseY >= lowerY && mouseY <= upperY) {
        List<Component> tooltips = DietTooltip.getEffects();
        guiGraphics.setComponentTooltipForNextFrame(this.font, tooltips, mouseX, mouseY);
      }
    }
  }

  private int getTextColor() {
    String config = DietConfig.CLIENT.textColor.get();
    if (config.startsWith("#")) {
      return ARGB.opaque(Integer.parseInt(config.substring(1), 16));
    }
    return ARGB.opaque(Integer.parseInt(config));
  }

  public void renderForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {

    if (this.minecraft != null && this.minecraft.level != null) {
      LocalPlayer player = this.minecraft.player;

      if (player != null) {
        Services.CAPABILITY.get(player).ifPresent(
            diet -> DietSuites.getSuite(this.minecraft.level, diet.getSuite()).ifPresent(suite -> {
              int y = this.height / 2 - this.ySize / 2 + 25;
              int x = this.width / 2 - this.xSize / 2 + 10;
              Component tooltip = null;

              for (IDietGroup group : suite.getGroups()) {
                guiGraphics.renderItem(new ItemStack(group.getIcon()), x, y - 5);
                MutableComponent text = Component.translatable(
                    "groups." + DietConstants.MOD_ID + "." + group.getName() + ".name");
                guiGraphics.drawString(this.font, text, x + 20, y, getTextColor(), false);
                DietColor color = diet.isActive() ? group.getColor() : DietColor.GRAY;
                int red = color.red();
                int green = color.green();
                int blue = color.blue();
                int percent = (int) Math.floor(diet.getValue(group.getName()) * 100.0f);
                String percentText = percent + "%";
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ICONS, x + 90, y + 2, 20, 0, 102, 5, 102, 5, 256, 256, ARGB.color(255, red, green, blue));

                if (percent > 0) {
                  int texWidth = percent + 1;
                  guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ICONS, x + 90, y + 2, 20, 5, texWidth, 5, texWidth, 5, 256, 256, ARGB.color(255, red, green, blue));
                }
                int xPos = x + 200;
                int yPos = y + 1;
                guiGraphics.drawString(this.font, percentText, (xPos + 1), yPos, ARGB.opaque(0), false);
                guiGraphics.drawString(this.font, percentText, (xPos - 1), yPos, ARGB.opaque(0), false);
                guiGraphics.drawString(this.font, percentText, xPos, (yPos + 1), ARGB.opaque(0), false);
                guiGraphics.drawString(this.font, percentText, xPos, (yPos - 1), ARGB.opaque(0), false);
                guiGraphics.drawString(this.font, percentText, xPos, yPos, ARGB.opaque(color.getRGB()), false);
                int lowerY = y - 5;
                int upperX = x + 16;
                int upperY = lowerY + 16;

                if (mouseX >= x && mouseX <= upperX && mouseY >= lowerY && mouseY <= upperY) {
                  String key =
                      "groups." + DietConstants.MOD_ID + "." + group.getName() + ".tooltip";

                  if (Language.getInstance().has(key)) {
                    tooltip = Component.translatable(key);
                  }
                }
                y += 20;
              }

              if (tooltip != null) {
                List<Component> tooltips = Lists.newArrayList(tooltip);
                guiGraphics.setComponentTooltipForNextFrame(this.font, tooltips, mouseX, mouseY);
              }
            }));
      }
    }
  }

  @Override
  public void renderBackground(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    super.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);

    if (this.minecraft != null) {
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i, j, 0, 0, this.xSize, 4, 248, 4, 256, 256);
      guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i, j + 4, 0, 4, this.xSize, this.ySize - 8, 248, 24, 256, 256);
      guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, i, j + this.ySize - 4, 0, 162, this.xSize, 4, 248, 4, 256, 256);
    }
  }

  @Override
  public boolean keyPressed(KeyEvent event) {
    if (this.minecraft != null && this.minecraft.player != null) {
      if (this.minecraft.options.keyInventory.matches(event)) {
        this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
        return true;
      } else if (DietKeys.OPEN_GUI.matches(event)) {

        if (fromInventory) {
          this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
        } else {
          this.onClose();
        }
        return true;
      }
    }
    return super.keyPressed(event);
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }
}
