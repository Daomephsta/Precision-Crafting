package leviathan143.precisioncrafting.common.precisiontable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class GhostSlot extends SlotItemHandler
{
	public GhostSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition)
	{
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean canTakeStack(EntityPlayer playerIn)
	{
		return false;
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return false;
	}

	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		return 1;
	}

	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
