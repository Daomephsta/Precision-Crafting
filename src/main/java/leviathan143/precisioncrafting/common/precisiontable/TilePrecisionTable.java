package leviathan143.precisioncrafting.common.precisiontable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.*;

public class TilePrecisionTable extends TileEntity
{
	private final IItemHandler inventory = new ItemStackHandler(10);
	private final IItemHandlerModifiable pattern = new ItemStackHandler(9);

	private int outputQuantity;

	public IItemHandler getPattern()
	{
		return pattern;
	}

	public int getOutputQuantity()
	{
		return outputQuantity;
	}

	public void setOutputQuantity(int outputQuantity)
	{
		this.outputQuantity = MathHelper.clamp(outputQuantity, 1, 64);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
		return super.getCapability(capability, facing);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		// Inventory
		CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventory, null,
				compound.getTagList("inventory", NBT.TAG_COMPOUND));
		// Pattern
		CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(pattern, null,
				compound.getTagList("pattern", NBT.TAG_COMPOUND));
		// Output Quantity
		outputQuantity = compound.getInteger("quantity");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		// Inventory
		compound.setTag("inventory", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventory, null));
		// Pattern
		compound.setTag("pattern", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(pattern, null));
		// Output Quantity
		compound.setInteger("quantity", outputQuantity);
		return compound;
	}
}
