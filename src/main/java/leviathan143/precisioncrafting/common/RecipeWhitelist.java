package leviathan143.precisioncrafting.common;

import java.util.*;

import leviathan143.precisioncrafting.PrecisionCrafting;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class RecipeWhitelist
{
	private static Set<Class<?>> whitelistedClasses = new HashSet<>();
	private static Set<ResourceLocation> whitelistedRegistryNames = new HashSet<>();
	
	static void loadFromConfig(String[] recipeClassWhitelist, String[] recipeNameWhitelist)
	{
		for(String recipeClass : recipeClassWhitelist)
		{
			whitelistRecipeClass(recipeClass);
		}
		for(String recipeName : recipeNameWhitelist)
		{
			whitelist(new ResourceLocation(recipeName));
		}
	}
	
	public static void whitelistRecipeClass(String qualifiedClassName)
	{
		try
		{
			Class<?> clazz = Class.forName(qualifiedClassName);
			if (IRecipe.class.isAssignableFrom(clazz))
				whitelistedClasses.add(clazz);
			else
				PrecisionCrafting.logger.warn("Unable to whitelist {} because it does not implement IRecipe", qualifiedClassName);
		}
		catch (ClassNotFoundException e)
		{
			PrecisionCrafting.logger.warn(String.format("Unable to whitelist %1$s because no class exists with the name %1$s", qualifiedClassName));
		}
	}
	
	public static void whitelist(ResourceLocation registryName)
	{
		if(ForgeRegistries.RECIPES.containsKey(registryName)) whitelistedRegistryNames.add(registryName);
		else PrecisionCrafting.logger.warn(String.format("Unable to whitelist %1$s because no recipe exists with the registry name %1$s", registryName));
	}

	public static boolean isWhitelisted(IRecipe recipe)
	{
		if (whitelistedClasses.contains(recipe.getClass()))
		{
			Utils.logDebug("{} is whitelisted because its class is {}", recipe.getRegistryName(), recipe.getClass().getName());
			return true;
		}
		if (whitelistedRegistryNames.contains(recipe.getRegistryName()))
		{
			Utils.logDebug(String.format("%1$s is whitelisted because its registry name is %1$s", recipe.getRegistryName()));
			return true;
		}
		Utils.logDebug("{} (Class {}) is blacklisted because it is not on the whitelist", recipe.getRegistryName(), recipe.getClass().getName());
		return false;
	}
}
