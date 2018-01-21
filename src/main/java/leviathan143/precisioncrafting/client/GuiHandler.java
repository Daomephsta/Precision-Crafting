package leviathan143.precisioncrafting.client;

import leviathan143.precisioncrafting.common.Utils;
import leviathan143.precisioncrafting.common.precisiontable.ContainerPrecisionTable;
import leviathan143.precisioncrafting.common.precisiontable.TilePrecisionTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	public static final int PRECISION_TABLE = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{
		case PRECISION_TABLE:
			TileEntity te = Utils.getTileSafely(world, new BlockPos(x, y, z));
			if (te instanceof TilePrecisionTable)
				return new ContainerPrecisionTable((TilePrecisionTable) te, player.inventory);
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{
		case PRECISION_TABLE:
			TileEntity te = Utils.getTileSafely(world, new BlockPos(x, y, z));
			if (te instanceof TilePrecisionTable)
				return new GuiPrecisionTable((TilePrecisionTable) te, player.inventory);
		default:
			return null;
		}
	}

}
