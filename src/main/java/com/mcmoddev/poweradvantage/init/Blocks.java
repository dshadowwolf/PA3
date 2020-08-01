package com.mcmoddev.poweradvantage.init;

import java.util.List;
import java.util.stream.Collectors;

import com.mcmoddev.lib.client.renderer.FluidStateMapper;
import com.mcmoddev.lib.data.SharedStrings;
import com.mcmoddev.lib.events.MMDLibRegisterBlocks;
import com.mcmoddev.lib.item.ItemMMDBlock;
import com.mcmoddev.lib.material.MMDMaterial;

import com.mcmoddev.poweradvantage.PowerAdvantage;
import com.mcmoddev.poweradvantage.blocks.BlockInfinite;
import com.mcmoddev.poweradvantage.data.Names;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
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
    	addNewBlock(new BlockInfinite(Material.PISTON, "steam"), Names.INFINITE_STEAM.toString(), myMat, pa_tab);
    	addNewBlock(new BlockInfinite(Material.PISTON, "electricity"), Names.INFINITE_ELECTRICITY.toString(), myMat, pa_tab);
    	addNewBlock(new BlockInfinite(Material.PISTON, "quantum"), Names.INFINITE_QUANTUM.toString(), myMat, pa_tab);
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
    	event.getRegistry().register(machines.getFluidBlock());
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
    	List<ItemStack> machines = Materials.getMaterialByName("pa-machines").getItemRegistry().entrySet().stream().map(ent -> ent.getValue()).collect(Collectors.toList());
    	// Did we get ItemBlocks in the Block.addBlock() call ?
    	PowerAdvantage.LOGGER.info("Trying for registration of items or itemblocks, %d in number", machines.size());
    	machines.stream().map(ItemStack::getItem).filter(it -> !it.getRegistryName().getPath().equalsIgnoreCase("steam")).forEach((Item it) -> { PowerAdvantage.LOGGER.info("Registering ItemBlock %s", it.getRegistryName()); event.getRegistry().register(it); });
    }
    
    @SubscribeEvent
    public static void registerModels(final ModelRegistryEvent event) {
    	List<ItemStack> machines = Materials.getMaterialByName("pa-machines").getItemRegistry().entrySet().stream().map(ent -> ent.getValue()).collect(Collectors.toList());
    	PowerAdvantage.LOGGER.info("Trying for registration of #inventory variants, %d in number", machines.size());
    	machines.stream()
    	.map(ItemStack::getItem)
    	.forEach(it -> {
    		PowerAdvantage.LOGGER.info("Registering #inventory variant for %s", it.getRegistryName());
    		ModelLoader.setCustomModelResourceLocation(it, 0, new ModelResourceLocation(it.getRegistryName(), "inventory"));
    	});

    	final Block block = Materials.getMaterialByName("pa-machines").getBlock("steam");
    	final Item item = Item.getItemFromBlock(block);
    	final ResourceLocation resLoc = block.getRegistryName();
    	final FluidStateMapper mapper = new FluidStateMapper( resLoc.toString() );
    	ModelBakery.registerItemVariants(item);
    	ModelLoader.setCustomMeshDefinition(item, mapper);
    	ModelLoader.setCustomStateMapper(block, mapper);
    }
}
