package leviathan143.precisioncrafting.common.packets;

import io.netty.buffer.ByteBuf;
import leviathan143.precisioncrafting.common.Utils;
import leviathan143.precisioncrafting.common.precisiontable.ContainerPrecisionTable;
import leviathan143.precisioncrafting.common.precisiontable.TilePrecisionTable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class PacketSetOutputQuantity implements IMessage
{
	private BlockPos tilePos;
	private int quantity;

	public PacketSetOutputQuantity()
	{}

	public PacketSetOutputQuantity(BlockPos tilePos, int quantity)
	{
		this.tilePos = tilePos;
		this.quantity = quantity;
	}

	public static class PacketSetQuantityHandler implements IMessageHandler<PacketSetOutputQuantity, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetOutputQuantity message, final MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() -> processMessage(message, ctx));
			return null;
		}

		private void processMessage(PacketSetOutputQuantity message, MessageContext ctx)
		{
			World world = ctx.getServerHandler().player.world;
			TileEntity te = Utils.getTileSafely(world, message.tilePos);
			if (te instanceof TilePrecisionTable)
			{
				((TilePrecisionTable) te).setOutputQuantity(message.quantity);
				// Mark the TE dirty so that it will be serialised to NBT
				te.markDirty();
				// The te should be synced to clients, but a block update is unnecessary
				IBlockState state = world.getBlockState(message.tilePos);
				world.notifyBlockUpdate(message.tilePos, state, state, 2);
			}
			// Inform the container that the quantity has changed
			if (ctx.getServerHandler().player.openContainer instanceof ContainerPrecisionTable)
				((ContainerPrecisionTable) ctx.getServerHandler().player.openContainer).onOutputQuantityChanged();
		}
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.tilePos = BlockPos.fromLong(buf.readLong());
		this.quantity = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(tilePos.toLong());
		buf.writeInt(this.quantity);
	}
}
