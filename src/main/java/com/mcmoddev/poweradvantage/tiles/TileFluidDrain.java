package com.mcmoddev.poweradvantage.tiles;

import com.mcmoddev.lib.container.gui.FeatureWrapperGui;
import com.mcmoddev.lib.container.gui.GuiContext;
import com.mcmoddev.lib.container.gui.IWidgetGui;
import com.mcmoddev.lib.container.gui.layout.GridLayout;
import com.mcmoddev.lib.tile.MMDStandardTileEntity;
import com.mcmoddev.poweradvantage.PowerAdvantage;
import com.mcmoddev.poweradvantage.feature.FluidDrainFeature;

public class TileFluidDrain extends MMDStandardTileEntity {
	private final FluidDrainFeature feat;
	public TileFluidDrain() {
		super();
		PowerAdvantage.LOGGER.info("TileFluidDrain.<init>");
		feat = new FluidDrainFeature("drain", 2000, this);
		this.addFeature(feat);
	}

	@Override
	protected IWidgetGui getMainContentWidgetGui(GuiContext context) {
		return  new GridLayout(1, 1)
                .addPiece(new FeatureWrapperGui(context, this, "drain"), 0, 0, 1, 1);
	}

}
