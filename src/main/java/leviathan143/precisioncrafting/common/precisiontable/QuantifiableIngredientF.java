package leviathan143.precisioncrafting.common.precisiontable;

import com.google.common.base.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class QuantifiableIngredientF implements Predicate<ItemStack>
{
	private final Ingredient ingredient;
	private final float quantity;

	public QuantifiableIngredientF(Ingredient ingredient, float quantity)
	{
		this.ingredient = ingredient;
		this.quantity = quantity;
	}

	@Override
	public boolean apply(ItemStack input)
	{
		return input.getCount() >= quantity && ingredient.apply(input);
	}

	public Ingredient getIngredient()
	{
		return ingredient;
	}

	public float getQuantity()
	{
		return quantity;
	}
}
