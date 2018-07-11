package leviathan143.precisioncrafting.common;

import java.util.HashSet;
import java.util.Set;

import leviathan143.precisioncrafting.PrecisionCrafting;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class RecipeBlacklist
{
	private static Set<Class<?>> blacklistedClasses = new HashSet<>();
	private static Set<ResourceLocation> blacklistedRegistryNames = new HashSet<>();

	static void loadFromConfig(String[] recipeClassBlacklist, String[] recipeNameBlacklist)
	{
		for(String recipeClass : recipeClassBlacklist)
		{
			blacklistRecipeClass(recipeClass);
		}
		for(String recipeName : recipeNameBlacklist)
		{
			blacklist(new ResourceLocation(recipeName));
		}
	}
	
	public static void blacklistRecipeClass(String qualifiedClassName)
	{
		try
		{
			Class<?> clazz = Class.forName(qualifiedClassName);
			if (IRecipe.class.isAssignableFrom(clazz))
				blacklistedClasses.add(clazz);
			else
				PrecisionCrafting.logger.warn("Unable to blacklist {} because it does not implement IRecipe", qualifiedClassName);
		}
		catch (ClassNotFoundException e)
		{
			PrecisionCrafting.logger.warn(String.format("Unable to blacklist %1$s because no class exists with the name %1$s", qualifiedClassName));
		}
	}
	
	public static void blacklist(ResourceLocation registryName)
	{
		if(ForgeRegistries.RECIPES.containsKey(registryName)) blacklistedRegistryNames.add(registryName);
		else PrecisionCrafting.logger.warn(String.format("Unable to blacklist %1$s because no recipe exists with the registry name %1$s", registryName));
	}

	public static boolean isBlacklisted(IRecipe recipe)
	{
		if (blacklistedClasses.contains(recipe.getClass()))
		{
			Utils.logDebug("{} is blacklisted because its class is {}", recipe.getRegistryName(), recipe.getClass().getName());
			return true;
		}
		if (blacklistedRegistryNames.contains(recipe.getRegistryName()))
		{
			Utils.logDebug(String.format("%1$s is blacklisted because its registry name is %1$s", recipe.getRegistryName()));
			return true;
		}
		return false;
	}
}
