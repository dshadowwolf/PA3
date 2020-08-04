package com.mcmoddev.poweradvantage.feature;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;

import com.mcmoddev.lib.feature.FluidTankFeature;

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
		this.source = source;
	}

	@Nullable
	private TileEntity getAdjacentTE(EnumFacing facing) {
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

		if (getExternalTank().getFluid() != null) {
			// see if we can push to a neighboring TE that has fluid handler caps
			for (EnumFacing f : EnumFacing.VALUES) {
				if (getExternalTank().getFluid() == null) break;
				TileEntity te = source.getWorld().getTileEntity(source.getPos().offset(f));
				if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite())) {
					IFluidHandler fh = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite());
					int canTake = fh.fill(getExternalTank().drain(getExternalTank().getCapacity(), false), false); // try to send as much as we contain
					fh.fill(getExternalTank().drain(canTake, true), true); // only send what the other side can take
				}
			}
		}
		
		// try to fill from the world
	}
}
