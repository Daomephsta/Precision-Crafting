package leviathan143.precisioncrafting.common.precisiontable;

import leviathan143.precisioncrafting.PrecisionCrafting;
import leviathan143.precisioncrafting.PrecisionCrafting.Constants;
import leviathan143.precisioncrafting.client.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockPrecisionTable extends Block
{
	public BlockPrecisionTable()
	{
		super(Material.WOOD);
		setRegistryName(Constants.MODID, "precision_table");
		setUnlocalizedName(Constants.MODID + ".precision_table");
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		player.openGui(PrecisionCrafting.INSTANCE, GuiHandler.PRECISION_TABLE, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntity te = world.getTileEntity(pos);
		IItemHandler tableInv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		for (int i = 0; i < tableInv.getSlots(); i++)
		{
			ItemStack stack = tableInv.getStackInSlot(i);
			if (i == ContainerPrecisionTable.SLOT_OUTPUT || stack.isEmpty() || world.isRemote) continue;
			EntityItem itemEntity = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
			world.spawnEntity(itemEntity);
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TilePrecisionTable();
	}

	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}
}
