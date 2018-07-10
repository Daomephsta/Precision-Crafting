package leviathan143.precisioncrafting;

import leviathan143.precisioncrafting.client.GuiHandler;
import leviathan143.precisioncrafting.common.packets.PacketHandler;
import leviathan143.precisioncrafting.common.precisiontable.BlockPrecisionTable;
import leviathan143.precisioncrafting.common.precisiontable.TilePrecisionTable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = PrecisionCrafting.MODID, name = PrecisionCrafting.MODNAME, version = PrecisionCrafting.VERSION)
public class PrecisionCrafting
{
	public static final String 
		VERSION = "0.0.1", 
		MODID = "precisioncrafting", 
		MODNAME = "Precision Crafting";

	@Instance(PrecisionCrafting.MODID)
	public static PrecisionCrafting INSTANCE;

	@ObjectHolder(PrecisionCrafting.MODID + ":precision_table")
	public static final Block PRECISION_TABLE = null;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());
		PacketHandler.registerPackets();
		GameRegistry.registerTileEntity(TilePrecisionTable.class, PrecisionCrafting.MODID + ":precision_table");
	}

	@Mod.EventBusSubscriber(modid = PrecisionCrafting.MODID)
	private static class RegistryHandler
	{
		@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event)
		{
			event.getRegistry().register(new BlockPrecisionTable().setCreativeTab(CreativeTabs.DECORATIONS));
		}

		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event)
		{
			event.getRegistry()
					.register(new ItemBlock(PRECISION_TABLE).setRegistryName(PRECISION_TABLE.getRegistryName()));
			
			registerOreDictEntries();
		}
		
		private static void registerOreDictEntries()
		{
			OreDictionary.registerOre("craftingTable", Blocks.CRAFTING_TABLE);
		}
	}

	@Mod.EventBusSubscriber(modid = PrecisionCrafting.MODID, value = Side.CLIENT)
	private static class ModelHandler
	{
		@SubscribeEvent
		public static void registerModels(ModelRegistryEvent event)
		{
			final Item tableIB = Item.getItemFromBlock(PRECISION_TABLE);
			ModelLoader.setCustomModelResourceLocation(tableIB, 0,
					new ModelResourceLocation(tableIB.getRegistryName(), "inventory"));
		}
	}
}
