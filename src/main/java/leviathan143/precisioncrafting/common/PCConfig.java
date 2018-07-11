package leviathan143.precisioncrafting.common;

import leviathan143.precisioncrafting.PrecisionCrafting;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Config(modid = PrecisionCrafting.MODID, category = "")
@Mod.EventBusSubscriber(modid = PrecisionCrafting.MODID)
public class PCConfig
{
	@Name("whitelist")
	public static final Whitelist whitelistCategory = new Whitelist();
	@Name("blacklist")
	public static final Blacklist blacklistCategory = new Blacklist();
	@Name("debug")
	public static final Debug debugCategory = new Debug();

	public static class Whitelist
	{
		@Comment("A list of the registry names of recipes that should be whitelisted")
		public String[] recipeNameWhitelist = {};
		@Comment("A list of the fully qualified names of recipe classes that should be whitelisted")
		public String[] recipeClassWhitelist = 
			{
				ShapelessRecipes.class.getName(), 
				ShapelessOreRecipe.class.getName(), 
				ShapedRecipes.class.getName(), 
				ShapedOreRecipe.class.getName()
			};
	}

	public static class Blacklist
	{
		@Comment("A list of the registry names of recipes that should be blacklisted")
		public String[] recipeNameBlacklist = {};
		@Comment("A list of the fully qualified names of recipe classes that should be blacklisted")
		public String[] recipeClassBlacklist = {};
	}

	public static class Debug
	{
		@LangKey(PrecisionCrafting.MODID + ".config.debugLogging")
		@Comment("Enables debug log messages if true")
		public boolean debugLogging = false;
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent e)
	{
		if (e.getModID().equals(PrecisionCrafting.MODID))
		{
			ConfigManager.sync(PrecisionCrafting.MODID, Type.INSTANCE);
			RecipeBlacklist.loadFromConfig(blacklistCategory.recipeClassBlacklist, blacklistCategory.recipeNameBlacklist);
			RecipeWhitelist.loadFromConfig(whitelistCategory.recipeClassWhitelist, whitelistCategory.recipeNameWhitelist);
		}
	}
}
