package com.mcmoddev.poweradvantage.init;

import java.util.List;
import java.util.stream.Collectors;

import com.mcmoddev.lib.data.Names;
import com.mcmoddev.lib.events.MMDLibRegisterItems;
import com.mcmoddev.lib.material.MMDMaterial;
import com.mcmoddev.poweradvantage.PowerAdvantage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class Items extends com.mcmoddev.lib.init.Items {
	@SubscribeEvent
	public static void createItems(final MMDLibRegisterItems ev) {
      	if (!Materials.getMaterialByName("steel").hasItem("ingot")) {
    		MMDMaterial material = Materials.getMaterialByName("steel");
            create(Names.BLEND, material);
            create(Names.INGOT, material);
            create(Names.NUGGET, material);
            create(Names.ROD, material);
    	}
	}
	
	@SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
    	List<ItemStack> machines = Materials.getMaterialByName("pa-machines").getItemRegistry().entrySet().stream().map(ent -> ent.getValue()).collect(Collectors.toList());
    	// Did we get ItemBlocks in the Block.addBlock() call ?
    	PowerAdvantage.LOGGER.info("Trying for registration of items or itemblocks, %d in number", machines.size());
    	machines.stream().map(ItemStack::getItem).filter(it -> !it.getRegistryName().getPath().equalsIgnoreCase("steam")).forEach((Item it) -> { PowerAdvantage.LOGGER.info("Registering ItemBlock %s", it.getRegistryName()); event.getRegistry().register(it); });
    	Materials.getMaterialByName("steel").getItems().stream().map(ItemStack::getItem).filter(it -> it.getRegistryName().getNamespace().equals(PowerAdvantage.MODID)).forEach( it -> event.getRegistry().register(it));
    }

}
