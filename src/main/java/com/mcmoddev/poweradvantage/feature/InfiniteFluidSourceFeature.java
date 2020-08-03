package com.mcmoddev.poweradvantage.feature;

import javax.annotation.Nullable;

import com.mcmoddev.lib.container.gui.GuiContext;
import com.mcmoddev.lib.container.gui.IWidgetGui;
import com.mcmoddev.lib.feature.FeatureDirtyLevel;
import com.mcmoddev.lib.feature.FluidTankFeature;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class InfiniteFluidSourceFeature extends FluidTankFeature implements ITickable {
	private final TileEntity source;
	private final String fluidName;
	private final int storageAmount;

	public InfiniteFluidSourceFeature(TileEntity source, int amount, String name, String fluid) {
		super(name, amount*2, (k) -> true, (k) -> true);
		this.source = source;
		this.fluidName = fluid;
		this.storageAmount = amount;
		getInternalTank().fill(FluidRegistry.getFluidStack(fluid, amount), true);
		getExternalTank().fill(FluidRegistry.getFluidStack(fluid, amount), true);
	}

	private void doInteraction(TileEntity targetEntity, IFluidHandler target) {
		int sendMax = target.fill(getInternalTank().drain(storageAmount, false), false);
		if (sendMax > 0) {
			target.fill(getInternalTank().drain(sendMax, false), true);
			this.setDirty(FeatureDirtyLevel.GUI);
		}
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
		if (getInternalTank().getFluidAmount() < storageAmount*2 || getExternalTank().getFluidAmount() < storageAmount *2) {
			getInternalTank().fill(FluidRegistry.getFluidStack(fluidName, storageAmount*2), true);
			getExternalTank().fill(FluidRegistry.getFluidStack(fluidName, storageAmount*2), true);			
		}
		for( EnumFacing facing : EnumFacing.values() ) {
			TileEntity target = getAdjacentTE(facing);
			if(target != null && target.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {
				IFluidHandler tCap = (IFluidHandler) target.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
				doInteraction(target, tCap);
			}
		}
	}

	@Override
	public boolean supportsClickToFill() {
		return false;
	}

	@Override
	public boolean supportsClickToDrain() {
		return true;
	}

	// GUI Bits Follow
	@Override
	public IWidgetGui getRootWidgetGui(final GuiContext context) {
		return super.getRootWidgetGui(context);
	}
}
