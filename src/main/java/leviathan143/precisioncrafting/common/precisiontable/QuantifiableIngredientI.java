package leviathan143.precisioncrafting.common.precisiontable;

import com.google.common.base.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class QuantifiableIngredientI implements Predicate<ItemStack>
{
	private final Ingredient ingredient;
	private int quantity;

	public QuantifiableIngredientI(Ingredient ingredient, int quantity)
	{
		this.ingredient = ingredient;
		this.quantity = quantity;
	}

	@Override
	public boolean apply(ItemStack input)
	{
		return input.getCount() == quantity && ingredient.apply(input);
	}

	public Ingredient getIngredient()
	{
		return ingredient;
	}

	public int getQuantity()
	{
		return quantity;
	}

	public void grow(int value)
	{
		quantity += value;
	}

	public void shrink(int value)
	{
		quantity -= value;
	}
}
