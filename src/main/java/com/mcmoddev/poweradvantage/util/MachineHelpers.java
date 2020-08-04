package com.mcmoddev.poweradvantage.util;

import com.mcmoddev.lib.data.SharedStrings;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class MachineHelpers {

	private MachineHelpers() {
        throw new IllegalAccessError(SharedStrings.NOT_INSTANTIABLE);		
	}

	public static TileEntity getNeighboringTileEntity(World worldIn, BlockPos pos, EnumFacing facing) {
		return worldIn.getTileEntity(pos.offset(facing));
	}
	
	public static int doFluidSendInteractionByOffset(TileEntity source, EnumFacing offsetFacing, int maxAmount, EnumFacing sourceFacing) {
		return doFluidSendInteraction(source, getNeighboringTileEntity(source.getWorld(), source.getPos(), offsetFacing), maxAmount, sourceFacing);
	}
	
	public static int doFluidSendInteractionWithBlock(TileEntity source, BlockPos target, int maxAmount, EnumFacing sourceFacing) {
		return doFluidSendInteraction(source, source.getWorld().getTileEntity(target), maxAmount, sourceFacing);
	}
	
	public static int doFluidSendInteraction(TileEntity source, TileEntity target, int maxAmount, EnumFacing sourceFacing) {
		if (source == null || target == null) return 0;
		if (source.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sourceFacing) && target.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sourceFacing.getOpposite()))
			return doFluidSendInteractionInternal(source, target, maxAmount, sourceFacing);
		
		return 0;
	}
	
	private static int doFluidSendInteractionInternal(TileEntity source, TileEntity target, int maxAmount, EnumFacing sourceFacing) {
		IFluidHandler sourceCap = source.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sourceFacing);
		IFluidHandler targetCap = target.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sourceFacing.getOpposite());
		
		return targetCap.fill(sourceCap.drain(maxAmount, true), true);
	}
	
	public static int doFluidGetInteractionByOffset(TileEntity source, EnumFacing offsetFacing, int maxAmount, EnumFacing sourceFacing) {
		return doFluidGetInteraction(source, getNeighboringTileEntity(source.getWorld(), source.getPos(), offsetFacing), maxAmount, sourceFacing);
	}
	
	public static int doFluidGetInteractionWithBlock(TileEntity source, BlockPos target, int maxAmount, EnumFacing sourceFacing) {
		return doFluidGetInteraction(source, source.getWorld().getTileEntity(target), maxAmount, sourceFacing);
	}
	
	public static int doFluidGetInteraction(TileEntity source, TileEntity target, int maxAmount, EnumFacing sourceFacing) {
		if (source == null || target == null) return 0;
		if (source.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sourceFacing) && target.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sourceFacing.getOpposite()))
			return doFluidGetInteractionInternal(source, target, maxAmount, sourceFacing);
		
		return 0;
	}
	
	private static int doFluidGetInteractionInternal(TileEntity source, TileEntity target, int maxAmount, EnumFacing sourceFacing) {
		IFluidHandler sourceCap = source.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sourceFacing);
		IFluidHandler targetCap = target.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sourceFacing.getOpposite());

		return sourceCap.fill(targetCap.drain(maxAmount, true), true);
	}
	
	public static int doPowerSendInteractionByOffset(TileEntity source, EnumFacing offsetFacing, int maxAmount, EnumFacing sourceFacing) {
		return doPowerSendInteraction(source, getNeighboringTileEntity(source.getWorld(), source.getPos(), offsetFacing), maxAmount, sourceFacing);
	}
	
	public static int doPowerSendInteractionWithBlock(TileEntity source, BlockPos target, int maxAmount, EnumFacing sourceFacing) {
		return doPowerSendInteraction(source, source.getWorld().getTileEntity(target), maxAmount, sourceFacing);
	}
	
	public static int doPowerSendInteraction(TileEntity source, TileEntity target, int maxAmount, EnumFacing sourceFacing) {
		if (source == null || target == null) return 0;
		if (source.hasCapability(CapabilityEnergy.ENERGY, sourceFacing) && target.hasCapability(CapabilityEnergy.ENERGY, sourceFacing.getOpposite()))
			return doPowerSendInteractionInternal(source, target, maxAmount, sourceFacing);
		
		return 0;
	}

	private static int doPowerSendInteractionInternal(TileEntity source, TileEntity target, int maxAmount, EnumFacing sourceFacing) {
		IEnergyStorage sourceCap = source.getCapability(CapabilityEnergy.ENERGY, sourceFacing);
		IEnergyStorage targetCap = target.getCapability(CapabilityEnergy.ENERGY, sourceFacing.getOpposite());
		
		if (!sourceCap.canExtract() || !targetCap.canReceive()) return 0;
		
		int canSend = sourceCap.extractEnergy(maxAmount, true);
		int canTake = targetCap.receiveEnergy(canSend, true);
		
		return sourceCap.extractEnergy(targetCap.receiveEnergy(canTake, false), false);
	}
	
	public static int doPowerGetInteractionByOffset(TileEntity source, EnumFacing offsetFacing, int maxAmount, EnumFacing sourceFacing) {
		return doPowerGetInteraction(source, getNeighboringTileEntity(source.getWorld(), source.getPos(), offsetFacing), maxAmount, sourceFacing);
	}
	
	public static int doPowerGetInteractionWithBlock(TileEntity source, BlockPos target, int maxAmount, EnumFacing sourceFacing) {
		return doPowerGetInteraction(source, source.getWorld().getTileEntity(target), maxAmount, sourceFacing);
	}

	public static int doPowerGetInteraction(TileEntity source, TileEntity target, int maxAmount, EnumFacing sourceFacing) {
		if (source == null || target == null) return 0;
		if (source.hasCapability(CapabilityEnergy.ENERGY, sourceFacing) && target.hasCapability(CapabilityEnergy.ENERGY, sourceFacing.getOpposite()))
			return doPowerGetInteractionInternal(source, target, maxAmount, sourceFacing);
		
		return 0;
	}

	private static int doPowerGetInteractionInternal(TileEntity source, TileEntity target, int maxAmount, EnumFacing sourceFacing) {
		IEnergyStorage sourceCap = source.getCapability(CapabilityEnergy.ENERGY, sourceFacing);
		IEnergyStorage targetCap = target.getCapability(CapabilityEnergy.ENERGY, sourceFacing.getOpposite());
		
		if (!sourceCap.canReceive() || !targetCap.canExtract()) return 0;
		
		int canSend = targetCap.receiveEnergy(maxAmount, true);
		int canTake = sourceCap.extractEnergy(canSend, true);
		
		return targetCap.extractEnergy(sourceCap.receiveEnergy(canTake, false), false);
	}

}
