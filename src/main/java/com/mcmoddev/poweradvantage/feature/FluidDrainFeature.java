package com.mcmoddev.poweradvantage.feature;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;

import com.mcmoddev.lib.feature.FluidTankFeature;
import com.mcmoddev.poweradvantage.PowerAdvantage;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidDrainFeature extends FluidTankFeature implements ITickable {
	private final static int TICKS_PER_WORK = 30;
	private final TileEntity source;
	private int nextWork = TICKS_PER_WORK;
	
	public FluidDrainFeature(String key, int capacity, TileEntity source) {
		super(key, capacity, fs -> true, fs -> true);
		PowerAdvantage.LOGGER.info("FluidDrainFeature.<init>");
		this.source = source;
	}

	@Nullable
	private TileEntity getAdjacentTE(EnumFacing facing) {
		PowerAdvantage.LOGGER.info("FluidDrainFeature.getAdjacentTE(%s)", facing);		
		BlockPos pos = source.getPos().offset(facing);
		World world = source.getWorld();

		if (world == null || !world.isBlockLoaded(pos)) return null;
		return world.getTileEntity(pos);
	}

	@Override
	public void update() {
		if (nextWork == 0) {
			nextWork = TICKS_PER_WORK;
		} else {
			nextWork--;
			return;
		}
		PowerAdvantage.LOGGER.info("FluidDrainFeature.update()");
		// look all around for a tank, try to push if we can
		int idx = 0;
		FluidStack toDrain = getExternalTank().drain(1000, false);
		if (toDrain != null && toDrain.amount > 0) {
			while ((toDrain != null) && (toDrain.amount > 0) && (idx < EnumFacing.VALUES.length)) {
				EnumFacing facing = EnumFacing.VALUES[idx];
				PowerAdvantage.LOGGER.info("Checking for TE with CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY to drain %dmB of %s into at facing %s from %s", toDrain.getFluid().getName(), toDrain.amount, facing, source.getPos());
				idx += 1;

				TileEntity t = getAdjacentTE(facing);
				if (t != null) {
					if (t.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
						IFluidHandler c = t.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
						int max_take = c.fill(toDrain, false);
						toDrain = getExternalTank().drain(max_take, true);
						c.fill(toDrain, true);
						toDrain = getExternalTank().drain(1000, false);
					}
				}
			}
		}
		
		if (toDrain != null && toDrain.amount == getExternalTank().getCapacity()) return;
		// look 1 block "UP" for fluid, try to pick it up
		// also look for anything in a 32 block range (really?)
		BlockPos start = source.getPos().up();
		Block base = source.getWorld().getBlockState(start).getBlock();
		if ( base instanceof BlockLiquid || base instanceof IFluidBlock ) {
			Fluid fluid = getFluidBlock(base);
			
			if ( fluid == null) return;
			
			// if "fluid" is not null, look for the most distant block we have found that is the fluid and consume it
			Iterable<BlockPos> pBlocks = BlockPos.getAllInBox(start.subtract(new Vec3i(16, 0, 16)), start.add(new Vec3i(16,32,16)));
			Map<Integer, BlockPos> blocks = new TreeMap<>();
			List<Integer> distances = new LinkedList<>();
			
			for ( BlockPos p : pBlocks ) {
				if (isMatchingFluidSourceBlock(source.getWorld().getBlockState(p).getBlock(), fluid, source.getWorld(), p)) {
					Integer distance = Integer.valueOf((int)p.getDistance(start.getX(), start.getY(), start.getZ()));
					blocks.put(distance, p);
					distances.add(distance);
				}
			}
			
			if (distances.size() == 0) return;
			int max_d = distances.stream().max(Integer::compare).get();
			getExternalTank().fill(new FluidStack( fluid, 1000), true);
			//source.getWorld().setBlockToAir(blocks.get(max_d));
			source.getWorld().setBlockState(blocks.get(max_d), Blocks.AIR.getDefaultState(), 1);
		}
	}
	
	@Nullable
	protected static Fluid getFluidBlock(Block block) {
		Fluid fluid = null;
		if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
			// Minecraft fluid
			fluid = FluidRegistry.WATER;
		} else if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
			// Minecraft fluid
			fluid = FluidRegistry.LAVA;
		} else if (block instanceof IFluidBlock) {
			fluid = ((IFluidBlock) block).getFluid();
		} else {
			// Minecraft fluid?
			fluid = FluidRegistry.lookupFluidForBlock(block);
		}
		return fluid;
	}
	
	protected static boolean isMatchingFluidBlock(Block block, Fluid f) {
		if (f == FluidRegistry.WATER) {
			return block.equals(Blocks.FLOWING_WATER) || block.equals(Blocks.WATER);
		} else if (f == FluidRegistry.LAVA) {
			return block.equals(Blocks.FLOWING_LAVA) || block.equals(Blocks.LAVA);
		} else if (block instanceof IFluidBlock) {
			IFluidBlock fb = (IFluidBlock) block;
			return areEqual(fb.getFluid(), f);
		} else {
			return false;
		}
	}

	protected static boolean isMatchingFluidSourceBlock(Block block, Fluid f, World w, BlockPos p) {
		if (f == FluidRegistry.WATER) {
			return block.equals(Blocks.WATER) && w.getBlockState(p).getValue(BlockLiquid.LEVEL) == 0;
		} else if (f == FluidRegistry.LAVA) {
			return block.equals(Blocks.LAVA) && w.getBlockState(p).getValue(BlockLiquid.LEVEL) == 0;
		} else if (block instanceof IFluidBlock) {
			IFluidBlock fb = (IFluidBlock) block;
			return fb.canDrain(w, p);
		} else {
			return false;
		}
	}
	
	private static boolean areEqual(Object o1, Object o2) {
		if (o1 != null) {
			return o1.equals(o2);
		} else {
			return o2 == null;
		}
	}
}
