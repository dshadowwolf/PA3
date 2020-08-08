package com.mcmoddev.poweradvantage.tiles;

import com.mcmoddev.lib.container.gui.FeatureWrapperGui;
import com.mcmoddev.lib.container.gui.GuiContext;
import com.mcmoddev.lib.container.gui.IWidgetGui;
import com.mcmoddev.lib.container.gui.layout.GridLayout;
import com.mcmoddev.lib.feature.FluidTankFeature;
import com.mcmoddev.lib.tile.MMDStandardTileEntity;

import net.minecraft.util.ITickable;

public class StorageTankTileEntity extends MMDStandardTileEntity implements ITickable {
	private final FluidTankFeature tank;
	private final int capacity;
	
	public StorageTankTileEntity(int capacity) {
		this.tank = new FluidTankFeature("tank", capacity, fs -> true, fs -> true);
		this.capacity = capacity;
		this.addFeature(this.tank);
	}
	
	public final int getCapacity() {
		return capacity;
	}
	
	public final int getCurrentFillLevel() {
		return tank.getExternalTank().getFluidAmount();
	}
	
	@Override
	protected IWidgetGui getMainContentWidgetGui(GuiContext context) {
		return new GridLayout(1, 1)
				.addPiece(new FeatureWrapperGui(context, this, "tank"), 0, 0, 1, 1);
	}
}
