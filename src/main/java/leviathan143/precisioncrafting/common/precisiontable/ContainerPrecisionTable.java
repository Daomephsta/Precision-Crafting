package leviathan143.precisioncrafting.common.precisiontable;

import java.util.*;

import com.google.common.collect.ImmutableSet;

import leviathan143.precisioncrafting.common.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.*;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ContainerPrecisionTable extends Container
{
	public static final int SLOT_OUTPUT = 0;	

	private final TilePrecisionTable table;
	private final InventoryPlayer playerInv;
	private final InventoryCrafting crafting = new InventoryCrafting(this, 3, 3);

	private final List<QuantifiableIngredientI> consolidatedIngredients = new ArrayList<>();
	private final List<QuantifiableIngredientF> requiredIngredients = new ArrayList<>();
	private IRecipe lastMatchingRecipe;
	private IRecipe matchingRecipe;
	private boolean validRecipeType = false;
	private boolean hasRequiredIngredients;

	public ContainerPrecisionTable(TilePrecisionTable table, InventoryPlayer playerInv)
	{
		this.table = table;
		this.playerInv = playerInv;

		addPlayerInvSlots(this.playerInv);
		// Pattern
		for (int row = 0; row < 3; row++)
		{
			for (int column = 0; column < 3; column++)
			{
				final int x = 30 + 18 * column;
				final int y = 17 + 18 * row;
				final int index = 3 * row + column;
				final Slot slot = addSlotToContainer(new GhostSlot(table.getPattern(), index, x, y)
				{
					@Override
					public void onSlotChanged()
					{
						super.onSlotChanged();
						onPatternSlotChanged(this);
					}
				});
				crafting.setInventorySlotContents(index, slot.getStack());
			}
		}

		final IItemHandler tableInv = table.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		// Output
		addSlotToContainer(new SlotItemHandler(tableInv, SLOT_OUTPUT, 124, 35)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return false;
			}

			@Override
			public boolean canTakeStack(EntityPlayer playerIn)
			{
				return canCraft();
			}

			@Override
			public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
			{
				final IItemHandler tableInv = table.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				for (int i = 0; i < requiredIngredients.size(); i++)
				{
					final QuantifiableIngredientF qIngredient = requiredIngredients.get(i);
					tableInv.extractItem(i + 1, MathHelper.ceil(qIngredient.getQuantity()), false);
				}
				final ItemStack stackOutput = matchingRecipe.getCraftingResult(crafting).copy();
				stackOutput.setCount(table.getOutputQuantity());
				((IItemHandlerModifiable) tableInv).setStackInSlot(0, stackOutput);
				return super.onTake(thePlayer, stack);
			}
		});
		// Ingredients
		for (int column = 0; column < 9; column++)
		{
			final int x = 8 + 18 * column;
			addSlotToContainer(new SlotItemHandler(tableInv, column + 1, x, 75)
			{
				@Override
				public void onSlotChanged()
				{
					super.onSlotChanged();
					checkIngredients();
				}
			});
		}
		updateRecipe();
	}

	private void addPlayerInvSlots(InventoryPlayer playerInv)
	{
		// Hotbar
		for (int column = 0; column < 9; column++)
		{
			final int x = 8 + 18 * column;
			addSlotToContainer(new Slot(playerInv, column, x, 184));
		}
		// Inventory
		for (int row = 1; row < 4; row++)
		{
			for (int column = 0; column < 9; column++)
			{
				final int x = 8 + 18 * column;
				final int y = 108 + 18 * row;
				addSlotToContainer(new Slot(playerInv, 9 * row + column, x, y));
			}
		}
	}

	public void onOutputQuantityChanged()
	{
		if (matchingRecipe != null)
		{
			if (validRecipeType)
			{
				hasRequiredIngredients = false;
				final IItemHandlerModifiable tableInv = (IItemHandlerModifiable) table
						.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				final ItemStack stack = matchingRecipe.getCraftingResult(crafting).copy();
				stack.setCount(table.getOutputQuantity());
				tableInv.setStackInSlot(0, stack);
			}
			computeRequiredIngredients();
		}
		table.markDirty();
	}

	private void onPatternSlotChanged(Slot updatedSlot)
	{
		crafting.setInventorySlotContents(updatedSlot.getSlotIndex(), updatedSlot.getStack());
		updateRecipe();
		checkIngredients();
	}

	private void checkIngredients()
	{
		final IItemHandler tableInv = table.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		hasRequiredIngredients = true;
		for (int i = 0; i < requiredIngredients.size(); i++)
		{
			final QuantifiableIngredientF qIngredient = requiredIngredients.get(i);
			if (!qIngredient.apply(tableInv.getStackInSlot(i + 1))) hasRequiredIngredients = false;
		}
	}

	private void updateRecipe()
	{

		final IItemHandlerModifiable tableInv = (IItemHandlerModifiable) table
				.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		matchingRecipe = CraftingManager.findMatchingRecipe(crafting, table.getWorld());

		/* Don't update the recipe if it hasn't actually changed, because
		 * Slot#onSlotChanged() is called 5 times per change for some stupid
		 * reason */
		validRecipeType = matchingRecipe != null 
			? RecipeWhitelist.isWhitelisted(matchingRecipe) && !RecipeBlacklist.isBlacklisted(matchingRecipe) 
			: true;
		if (!validRecipeType) return;
		if (lastMatchingRecipe != matchingRecipe && matchingRecipe != null)
		{
			ItemStack result = matchingRecipe.getCraftingResult(crafting).copy();
			result.setCount(table.getOutputQuantity());
			tableInv.setStackInSlot(0, result);
			consolidateIngredients();
			computeRequiredIngredients();
		}
		else tableInv.setStackInSlot(0, ItemStack.EMPTY);
		if (matchingRecipe == null)
		{
			consolidatedIngredients.clear();
			requiredIngredients.clear();
		}
		table.markDirty();
		lastMatchingRecipe = matchingRecipe;
	}

	private void consolidateIngredients()
	{
		consolidatedIngredients.clear();
		for (final Ingredient ingredient : matchingRecipe.getIngredients())
		{
			boolean unique = true;
			for (final QuantifiableIngredientI entry : consolidatedIngredients)
			{
				if (areIngredientsEqual(ingredient, entry.getIngredient()))
				{
					unique = false;
					entry.grow(1);
					break;
				}
			}
			// Empty ingredients are not ingredients for my purposes
			if (unique && ingredient != Ingredient.EMPTY)
				consolidatedIngredients.add(new QuantifiableIngredientI(ingredient, 1));
		}
	}

	private boolean areIngredientsEqual(Ingredient a, Ingredient b)
	{
		if (a == b) return true;
		for (final ItemStack aStack : a.getMatchingStacks())
		{
			if (!b.apply(aStack)) return false;
		}
		for (final ItemStack bStack : b.getMatchingStacks())
		{
			if (!a.apply(bStack)) return false;
		}
		return true;
	}

	private void computeRequiredIngredients()
	{
		requiredIngredients.clear();
		final int resultCount = matchingRecipe.getCraftingResult(crafting).getCount();
		final float multiplier = (float) table.getOutputQuantity() / resultCount;
		for (final QuantifiableIngredientI entry : consolidatedIngredients)
		{
			final float result = entry.getQuantity() * multiplier;
			requiredIngredients.add(new QuantifiableIngredientF(entry.getIngredient(), result));
		}
	}

	public List<QuantifiableIngredientF> getRequiredIngredients()
	{
		return requiredIngredients;
	}

	public boolean canCraft()
	{
		if (validRecipeType && hasRequiredIngredients) return true;
		return false;
	}

	public boolean hasRecipe()
	{
		return matchingRecipe != null;
	}

	public boolean isRecipeTypeValid()
	{
		return validRecipeType;
	}

	public boolean hasRequiredIngredients()
	{
		return hasRequiredIngredients;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player)
	{
		if (0 <= slotId && slotId < inventorySlots.size())
		{
			final Slot slot = inventorySlots.get(slotId);
			if (slot instanceof GhostSlot)
			{
				if (clickType == ClickType.PICKUP_ALL)
				{
					// Clear pattern
					for (int i = 36; i <= 44; i++)
					{
						inventorySlots.get(i).putStack(ItemStack.EMPTY);
					}
				}
				else
				{
					final ItemStack cursorStack = player.inventory.getItemStack();
					final ItemStack newSlotStack = cursorStack.copy();
					newSlotStack.setCount(1);
					slot.putStack(newSlotStack);
				}
			}
		}
		return super.slotClick(slotId, dragType, clickType, player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack stack = ItemStack.EMPTY;
		final Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			final ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();

			// Hotbar
			if (index <= 8)
			{
				if (!mergeItemStack(slotStack, 46, 55, false)) return ItemStack.EMPTY;
			}
			// Player Inventory
			else if (index >= 9 && index <= 35)
			{
				if (!mergeItemStack(slotStack, 46, 55, false)) return ItemStack.EMPTY;
			}
			// Pattern
			else if (index >= 36 && index <= 44)
			{
				return stack;
			}
			// Output
			else if (index == 45)
			{
				if (!mergeItemStack(slotStack, 0, 36, true)) return ItemStack.EMPTY;
			}
			// Ingredients
			else if (index >= 46 && index <= 54)
			{
				if (!mergeItemStack(slotStack, 0, 36, true)) return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return stack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return playerIn.getDistanceSq(table.getPos()) <= 64.0D;
	}
}
