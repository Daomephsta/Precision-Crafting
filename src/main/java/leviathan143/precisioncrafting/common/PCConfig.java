package leviathan143.precisioncrafting.common;

import leviathan143.precisioncrafting.PrecisionCrafting;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.*;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = PrecisionCrafting.MODID)
@Mod.EventBusSubscriber(modid = PrecisionCrafting.MODID)
public class PCConfig
{
	@LangKey(PrecisionCrafting.MODID + ".config.debugLogging")
	@Comment("Enables debug log messages if true")
	public static boolean debugLogging = false;
	
	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent e)
	{
		if (e.getModID().equals(PrecisionCrafting.MODID)) 
			ConfigManager.sync(PrecisionCrafting.MODID, Type.INSTANCE);
	}
}
