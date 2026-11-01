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

package com.illusivesoulworks.diet;

import com.illusivesoulworks.diet.api.DietApi;
import com.illusivesoulworks.diet.client.DietKeys;
import com.illusivesoulworks.diet.common.DietEvents;
import com.illusivesoulworks.diet.common.capability.DietCapability;
import com.illusivesoulworks.diet.common.command.DietCommand;
import com.illusivesoulworks.diet.common.command.DietGroupArgument;
import com.illusivesoulworks.diet.common.config.DietConfigLoader;
import com.illusivesoulworks.diet.common.data.group.DietGroups;
import com.illusivesoulworks.diet.common.data.suite.DietSuites;
import com.illusivesoulworks.diet.common.integration.IntegrationManager;
import com.illusivesoulworks.diet.common.network.DietNeoForgeNetwork;
import com.illusivesoulworks.diet.common.util.DietOverride;
import com.illusivesoulworks.diet.common.util.DietValueGenerator;
import com.illusivesoulworks.diet.data.DietBlockTagsProvider;
import com.illusivesoulworks.diet.data.DietTagsProvider;
import java.util.concurrent.CompletableFuture;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(DietConstants.MOD_ID)
public class DietNeoForgeMod {
  private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, DietConstants.MOD_ID);
  public static final DeferredHolder<Attribute, Attribute> NATURAL_REGEN = ATTRIBUTES.register("natural_regeneration", () -> DietApi.getInstance().getNaturalRegeneration());

  private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPE_INFO_DEFERRED_REGISTER = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, DietConstants.MOD_ID);
  private static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> GROUP_ARGUMENT = ARGUMENT_TYPE_INFO_DEFERRED_REGISTER.register("group", () -> ArgumentTypeInfos.registerByClass(DietGroupArgument.class, SingletonArgumentInfo.contextFree(DietGroupArgument::group)));

  public DietNeoForgeMod(IEventBus eventBus) {
    eventBus.addListener(this::setup);
    eventBus.addListener(this::process);
    eventBus.addListener(this::gatherData);
    DietCapability.ATTACHMENT_TYPE_DEFERRED_REGISTER.register(eventBus);
    eventBus.addListener(DietNeoForgeNetwork::setup);
    eventBus.addListener(this::modifyAttributes);
    DietConfigLoader.setup();
    ATTRIBUTES.register(eventBus);
    ARGUMENT_TYPE_INFO_DEFERRED_REGISTER.register(eventBus);
    NeoForge.EVENT_BUS.addListener(this::serverStarting);
    NeoForge.EVENT_BUS.addListener(this::onDatapackSync);
    NeoForge.EVENT_BUS.addListener(this::setupCommands);
    NeoForge.EVENT_BUS.addListener(this::addReloaders);
  }

  private void setup(final FMLCommonSetupEvent evt) {
    IntegrationManager.setup();
    // evt.enqueueWork(() -> ArgumentTypeInfos.registerByClass(DietGroupArgument.class, SingletonArgumentInfo.contextFree(DietGroupArgument::group)));
  }

  private void addReloaders(final AddServerReloadListenersEvent evt) {
    evt.addListener(DietCommonMod.resource("groups"), DietGroups.SERVER);
    evt.addListener(DietCommonMod.resource("suites"), DietSuites.SERVER);
  }

  private void process(final InterModProcessEvent evt) {
    DietOverride.process(evt.getIMCStream());
  }

  private void gatherData(final GatherDataEvent.Server evt) {
    DataGenerator generator = evt.getGenerator();

    CompletableFuture<HolderLookup.Provider> lookupProvider = evt.getWorldLookupProvider();
    PackOutput packOutput = generator.getPackOutput();
    DietBlockTagsProvider blockTagsProvider = new DietBlockTagsProvider(packOutput, lookupProvider);
    generator.addProvider(true, blockTagsProvider);
    generator.addProvider(true, new DietTagsProvider(packOutput, Registries.ITEM, lookupProvider, DietConstants.MOD_ID));
  }

  private void modifyAttributes(final EntityAttributeModificationEvent evt) {
    evt.add(EntityTypes.PLAYER, BuiltInRegistries.ATTRIBUTE.wrapAsHolder(DietApi.getInstance().getNaturalRegeneration()));
  }

  private void serverStarting(final ServerStartingEvent evt) {
    DietValueGenerator.reload(evt.getServer());
  }

  private void onDatapackSync(final OnDatapackSyncEvent evt) {

    if (evt.getPlayer() == null) {
      DietEvents.syncDatapack(evt.getPlayerList().getServer());
    } else {
      DietEvents.syncDatapack(evt.getPlayer());
    }
  }

  private void setupCommands(final RegisterCommandsEvent evt) {
    DietCommand.register(evt.getDispatcher());
  }

  @EventBusSubscriber(modid = DietConstants.MOD_ID, value = Dist.CLIENT)
  public static class ClientModEvents {
    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent evt) {
      evt.register(DietKeys.get());
    }
  }
}
