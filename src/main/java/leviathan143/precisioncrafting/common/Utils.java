package leviathan143.precisioncrafting.common;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Utils
{
	public static TileEntity getTileSafely(World world, BlockPos pos)
	{
		return world.isBlockLoaded(pos) ? world.getTileEntity(pos) : null;
	}
}
