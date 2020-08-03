package com.mcmoddev.poweradvantage.init;

import com.mcmoddev.lib.data.SharedStrings;
import com.mcmoddev.lib.events.MMDLibRegisterMaterials;
import com.mcmoddev.lib.material.MMDMaterialType.MaterialType;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Materials extends com.mcmoddev.lib.init.Materials {

    private Materials() {
            throw new IllegalAccessError(SharedStrings.NOT_INSTANTIABLE);
    }

    /**
     *
     */
    @SubscribeEvent
    public static void init(final MMDLibRegisterMaterials event) {
    	createOrelessRareMaterial("pa-machine", MaterialType.METAL, 0.75f, 1.00f, 0.00f, 0xFFFFFFFF);
    }
}
