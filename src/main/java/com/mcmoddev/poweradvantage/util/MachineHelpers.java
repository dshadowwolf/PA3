package com.mcmoddev.poweradvantage.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mcmoddev.lib.data.SharedStrings;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class MachineHelpers {

	private MachineHelpers() {
        throw new IllegalAccessError(SharedStrings.NOT_INSTANTIABLE);		
	}

	@Nullable
	public static IEnergyStorage getEnergyNoChecks(TileEntity tile, EnumFacing facing) {
		if (tile == null) return null;
		else if (tile.hasCapability(CapabilityEnergy.ENERGY, facing)) return (IEnergyStorage) tile.getCapability(CapabilityEnergy.ENERGY, facing);
		else return null;
	}

	@Nullable
	public static IEnergyStorage getEnergyIfSendPossible(TileEntity tile, EnumFacing facing) {
		IEnergyStorage r = getEnergyNoChecks(tile, facing);
		if (r == null || !r.canExtract()) return null;
		else return r;
	}
	
	@Nullable
	public static IEnergyStorage getEnergyIfReceivePossible(TileEntity tile, EnumFacing facing) {
		IEnergyStorage r = getEnergyNoChecks(tile, facing);
		if (r == null || !r.canReceive()) return null;
		else return r;		
	}
	
	@Nullable
	public static IFluidHandler getFluidNoChecks(TileEntity tile, EnumFacing facing) {
		if (tile == null) return null;
		else if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing)) return (IFluidHandler) tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
		else return null;
	}

	@Nullable
	public static IFluidHandler getFluidIfSendPossible(TileEntity tile, EnumFacing facing) {
		IFluidHandler fh = getFluidNoChecks(tile, facing);
		if (fh == null || fh.drain(1000, false) == null ) return null;
		else if (fh.drain(1000, false).amount == 0) return null;
		else return fh;
	}
	
	@Nullable
	public static IFluidHandler getFluidIfReceivePossible(TileEntity tile, EnumFacing facing) {
		IFluidHandler fh = getFluidNoChecks(tile, facing);
		if (fh == null) return null;
		else {
			FluidStack fs = fh.drain(1, false);
			if (fs == null || fh.fill(fs, false) > 0) return fh;
		}
		return null;
	}
	
	@Nullable
	public static TileEntity getNeighboringTileEntity(World worldIn, BlockPos pos, EnumFacing facing) {
		return worldIn.getTileEntity(pos.offset(facing));
	}
	
	@Nullable
	public static int doFluidSendInteractionByOffset(TileEntity source, EnumFacing offsetFacing, int maxAmount, EnumFacing sourceFacing) {
		return doFluidSendInteraction(source, getNeighboringTileEntity(source.getWorld(), source.getPos(), offsetFacing), maxAmount, sourceFacing);
	}
	
	@Nullable
	public static int doFluidSendInteractionWithBlock(TileEntity source, BlockPos target, int maxAmount, EnumFacing sourceFacing) {
		return doFluidSendInteraction(source, source.getWorld().getTileEntity(target), maxAmount, sourceFacing);
	}

	@Nonnull
	public static int doFluidSendInteraction(@Nullable final TileEntity source, @Nullable final TileEntity target, final int maxAmount, @Nonnull final EnumFacing sourceFacing) {
		if (source == null || target == null) return 0;
		if (source.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sourceFacing) && target.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sourceFacing.getOpposite()))
			return doFluidSendInteractionInternal(source, target, maxAmount, sourceFacing);
		
		return 0;
	}
	
	@Nonnull
	public static int doFluidSendInteractionInternal(@Nonnull final TileEntity source, @Nonnull final TileEntity target, final int maxAmount, @Nonnull final EnumFacing sourceFacing) {
		IFluidHandler sourceCap = getFluidIfReceivePossible(target, sourceFacing.getOpposite());
		IFluidHandler targetCap = getFluidIfSendPossible(source, sourceFacing);

		if ( sourceCap == null || targetCap == null ) return 0;
		
		return sourceCap.fill(targetCap.drain(maxAmount, true), true);
	}
	
	public static int doFluidGetInteractionByOffset(TileEntity source, EnumFacing offsetFacing, int maxAmount, EnumFacing sourceFacing) {
		return doFluidGetInteraction(source, getNeighboringTileEntity(source.getWorld(), source.getPos(), offsetFacing), maxAmount, sourceFacing);
	}
	
	public static int doFluidGetInteractionWithBlock(TileEntity source, BlockPos target, int maxAmount, EnumFacing sourceFacing) {
		return doFluidGetInteraction(source, source.getWorld().getTileEntity(target), maxAmount, sourceFacing);
	}
	
	public static int doFluidGetInteraction(TileEntity source, TileEntity target, int maxAmount, EnumFacing sourceFacing) {
		if (source == null || target == null) return 0;
		return doFluidGetInteractionInternal(source, target, maxAmount, sourceFacing);
	}
	
	private static int doFluidGetInteractionInternal(TileEntity source, TileEntity target, int maxAmount, EnumFacing sourceFacing) {
		IFluidHandler sourceCap = getFluidIfReceivePossible(source, sourceFacing);
		IFluidHandler targetCap = getFluidIfSendPossible(target, sourceFacing.getOpposite());

		if ( sourceCap == null || targetCap == null ) return 0;
		
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
		return doPowerSendInteractionInternal(source, target, maxAmount, sourceFacing);
	}

	private static int powerTransfer(IEnergyStorage source, IEnergyStorage target, int maxAmount) {
		int canSend = source.extractEnergy(maxAmount, true);
		int canTake = target.receiveEnergy(canSend, true);
		
		return source.extractEnergy(target.receiveEnergy(canTake, false), false);
	}
	
	private static int doPowerSendInteractionInternal(TileEntity source, TileEntity target, int maxAmount, EnumFacing sourceFacing) {
		IEnergyStorage sourceCap = getEnergyIfSendPossible(source, sourceFacing);
		IEnergyStorage targetCap = getEnergyIfReceivePossible(target, sourceFacing.getOpposite());
		
		if (sourceCap == null || targetCap == null) return 0;

		return powerTransfer(sourceCap, targetCap, maxAmount);
	}
	
	public static int doPowerGetInteractionByOffset(TileEntity source, EnumFacing offsetFacing, int maxAmount, EnumFacing sourceFacing) {
		return doPowerGetInteraction(source, getNeighboringTileEntity(source.getWorld(), source.getPos(), offsetFacing), maxAmount, sourceFacing);
	}
	
	public static int doPowerGetInteractionWithBlock(TileEntity source, BlockPos target, int maxAmount, EnumFacing sourceFacing) {
		return doPowerGetInteraction(source, source.getWorld().getTileEntity(target), maxAmount, sourceFacing);
	}

	public static int doPowerGetInteraction(TileEntity source, TileEntity target, int maxAmount, EnumFacing sourceFacing) {
		if (source == null || target == null) return 0;
		return doPowerGetInteractionInternal(source, target, maxAmount, sourceFacing);
	}

	private static int doPowerGetInteractionInternal(TileEntity source, TileEntity target, int maxAmount, EnumFacing sourceFacing) {
		IEnergyStorage sourceCap = getEnergyIfSendPossible(target, sourceFacing.getOpposite());
		IEnergyStorage targetCap = getEnergyIfReceivePossible(source, sourceFacing);
		
		if (sourceCap == null || targetCap == null) return 0;

		return powerTransfer(sourceCap, targetCap, maxAmount);
	}

}
