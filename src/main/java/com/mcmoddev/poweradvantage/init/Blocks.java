package com.mcmoddev.poweradvantage.init;

import java.util.List;
import java.util.stream.Collectors;

import com.mcmoddev.lib.client.renderer.FluidStateMapper;
import com.mcmoddev.lib.data.SharedStrings;
import com.mcmoddev.lib.events.MMDLibRegisterBlocks;
import com.mcmoddev.lib.item.ItemMMDBlock;
import com.mcmoddev.lib.material.MMDMaterial;
import com.mcmoddev.lib.block.MMDBlockWithTile;

import com.mcmoddev.poweradvantage.PowerAdvantage;
import com.mcmoddev.poweradvantage.blocks.BlockFrame;
import com.mcmoddev.poweradvantage.blocks.BlockInfinitePower;
import com.mcmoddev.poweradvantage.blocks.BlockInfiniteSteam;
import com.mcmoddev.poweradvantage.data.Names;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid=PowerAdvantage.MODID)
public class Blocks extends com.mcmoddev.lib.init.Blocks {

    private Blocks() {
            throw new IllegalAccessError(SharedStrings.NOT_INSTANTIABLE);
    }

    private static void addNewBlock(Block block, String name, MMDMaterial material, CreativeTabs tab) {
    	addBlock(block, name, material, tab);
    	material.addNewBlock(name, block);
    	addNewItemBlock(block, name, material);
    }
    
    private static void addNewItemBlock(Block block, String name, MMDMaterial material) {
    	String fullName = String.format("machine_%s", name);
    	ItemMMDBlock itemBlock = new ItemMMDBlock(material, block);
    	itemBlock.setRegistryName(block.getRegistryName());
		itemBlock.setTranslationKey(PowerAdvantage.MODID + "." + fullName);
		material.addNewItem("ItemBlock_"+fullName, itemBlock);
    }
    /**
     *
     */
    @SubscribeEvent
    public static void init(final MMDLibRegisterBlocks event) {
    	PowerAdvantage.LOGGER.info("Registering Blocks with MMDLib!");
    	MMDMaterial myMat = Materials.getMaterialByName("pa-machines");
    	CreativeTabs pa_tab = ItemGroups.getTab(PowerAdvantage.MODID, "tab");
    	addNewBlock(new BlockInfiniteSteam(), Names.INFINITE_STEAM.toString(), myMat, pa_tab);
    	addNewBlock(new BlockInfinitePower(), Names.INFINITE_POWER.toString(), myMat, pa_tab);
    	Block steelFrame = new BlockFrame(Materials.getMaterialByName("steel"));
    	steelFrame.setHardness(0.75f);
    	addNewBlock(steelFrame, Names.FRAME.toString(), Materials.getMaterialByName("steel"), pa_tab);
    	if (!Materials.getMaterialByName("steel").hasBlock("block")) {
    		MMDMaterial steel = Materials.getMaterialByName("steel");
    		create(com.mcmoddev.lib.data.Names.BLOCK, steel);
    		create(com.mcmoddev.lib.data.Names.BARS, steel);
    	}
    	PowerAdvantage.LOGGER.info("End of block registration, %d blocks registered with MMDLib", myMat.getBlocks().size());
    }
    
    /**
     * Registers Blocks for this mod.
     *
     * @param event The Event.
     */
    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
    	MMDMaterial machines = Materials.getMaterialByName("pa-machines");
    	PowerAdvantage.LOGGER.info("Trying to register my blocks - %d in number", machines.getBlocks().size());
    	machines.getBlocks().stream().forEach( bl -> { PowerAdvantage.LOGGER.info("Registering block %s", bl.getRegistryName()) ; event.getRegistry().register(bl); } );
    	machines.getBlocks().stream().filter( bl -> bl instanceof MMDBlockWithTile).forEach( bl -> ((MMDBlockWithTile)bl).registerTile());
    	event.getRegistry().register(Materials.getMaterialByName("steel").getBlock(Names.FRAME.toString()));
    	Materials.getMaterialByName("steel").getBlocks().stream().filter(bl -> bl.getRegistryName().getNamespace().equals(PowerAdvantage.MODID)).forEach( bl -> event.getRegistry().register(bl));
    	event.getRegistry().register(machines.getFluidBlock());
    }

}
