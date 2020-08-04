package com.mcmoddev.poweradvantage.util;

import com.mcmoddev.lib.data.SharedStrings;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

public class FluidUtils {

	private FluidUtils() {
        throw new IllegalAccessError(SharedStrings.NOT_INSTANTIABLE);
	}
	
	public static int getFluidAmountFromBlock(World worldIn, BlockPos pos) {
		FluidStack fluidStack = getFluidStackFromBlock(worldIn, pos);
		return fluidStack != null ? fluidStack.amount : 0;
	}

	public static FluidStack getFluidStackFromBlock(World worldIn, BlockPos pos) {
		IBlockState blockState = worldIn.getBlockState(pos);
		Block block = blockState.getBlock();
		int meta = block.getMetaFromState(blockState);
		
		if (block instanceof IFluidBlock) {
			IFluidBlock fluidBlock = ((IFluidBlock) block);
			return new FluidStack(fluidBlock.getFluid(), (int) (Fluid.BUCKET_VOLUME * fluidBlock.getFilledPercentage(worldIn, pos)));
		} else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER && meta == 0) {
				return new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
		} else if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA && meta == 0) {
				return new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
		}

		return null;
	}

	public static Fluid getFluidFromBlock(World worldIn, BlockPos pos) {
		FluidStack fs = getFluidStackFromBlock(worldIn, pos);
		return fs != null ? fs.getFluid() : null;
	}
	
	public static FluidStack drainBlock(World worldIn, BlockPos pos) {
		return drainBlock(worldIn, pos, 3); // call with proper update flags
	}
	
	public static FluidStack drainBlock(World worldIn, BlockPos pos, boolean doDrain) {
		if (doDrain) return drainBlock(worldIn, pos);
		
		if (worldIn == null || pos == null) return null;
		
		FluidStack fs = getFluidStackFromBlock(worldIn, pos);
		
		if (fs == null) return null;
		
		Block f = fs.getFluid().getBlock();
		
		if (f instanceof IFluidBlock && ((IFluidBlock)f).canDrain(worldIn, pos) ||
				f == Blocks.WATER || f == Blocks.FLOWING_WATER || f == Blocks.LAVA || f == Blocks.FLOWING_LAVA )
			return fs;
		return null;
	}
	
	public static FluidStack drainBlock(World worldIn, BlockPos pos, int updateFlags) {
		if (worldIn == null || pos == null) return null;
		
		FluidStack fs = getFluidStackFromBlock(worldIn, pos);
		
		if (fs == null) return null;
		
		Block f = fs.getFluid().getBlock();
		
		if (f instanceof IFluidBlock && ((IFluidBlock) f).canDrain(worldIn, pos)) {
				return ((IFluidBlock) f).drain(worldIn, pos, true);
		} else {
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), updateFlags);
			return fs;
		}
	}
	
	public static boolean isFillableBlock(World worldIn, BlockPos pos)
	{
		if (worldIn == null || pos == null) return false;

		IBlockState blockState = worldIn.getBlockState(pos); 
		Block block = blockState.getBlock();

		if (drainBlock(worldIn, pos, false) != null) {
			return false;
		} else if (block.isAir(blockState, worldIn, pos)) {
			return true;
		} else if (!(block instanceof IFluidBlock || block instanceof BlockLiquid) && block.isReplaceable(worldIn, pos)) {
			return true;
		}
		return false;
	}
	
	public static boolean isFillableFluid(World worldIn, BlockPos pos)
	{
		if (worldIn == null || pos == null) return false;

		IBlockState blockState = worldIn.getBlockState(pos);
		Block block = blockState.getBlock();
		int meta = block.getMetaFromState(blockState);

		if (drainBlock(worldIn, pos, false) != null) {
			return false;
		} else if (block instanceof IFluidBlock || block instanceof BlockLiquid) {
			return !(meta == 0);
		}
		return false;
	}
	
	private static IFluidBlock getFluidBlock(Block b) {
		if (b instanceof IFluidBlock) return (IFluidBlock)b;
		else return null;
	}
	
	public static int fillBlock(World worldIn, BlockPos pos, FluidStack stack, boolean doFill)
	{
		EntityPlayer faked = FakePlayerFactory.getMinecraft((WorldServer) worldIn);
		if ((isFillableBlock(worldIn, pos) || isFillableFluid(worldIn, pos)) && stack != null && stack.amount >= Fluid.BUCKET_VOLUME) {
			if (doFill)	{
				IBlockState blockState = worldIn.getBlockState(pos);
				Block block = blockState.getBlock();

				BlockPos nPos = pos.offset(EnumFacing.UP);
				IFluidBlock fb = getFluidBlock(stack.getFluid().getBlock());
				
				if (block != null) {
					if (block == Blocks.WATER && worldIn.isAirBlock(nPos)) {
						worldIn.setBlockState(nPos, blockState, 3);
					} else if (fb != null && net.minecraftforge.fluids.FluidUtil.tryPlaceFluid(faked, worldIn, nPos, ItemStack.EMPTY, stack).isSuccess()) {
						block.dropBlockAsItem(worldIn, pos, blockState, 1);
						block.breakBlock(worldIn, pos, blockState);
					}
				}

				worldIn.setBlockState(nPos, stack.getFluid().getBlock().getDefaultState(), 3);
			}
			return Fluid.BUCKET_VOLUME;
		}
		return 0;
	}

}
