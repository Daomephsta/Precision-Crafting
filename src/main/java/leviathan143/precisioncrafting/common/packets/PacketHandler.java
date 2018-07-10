package leviathan143.precisioncrafting.common.packets;

import leviathan143.precisioncrafting.PrecisionCrafting;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
	public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(PrecisionCrafting.MODID);
	private static int packetID = 0;

	public static void registerPackets()
	{
		registerPacket(PacketSetOutputQuantity.PacketSetQuantityHandler.class, PacketSetOutputQuantity.class,
				Side.SERVER);
	}

	private static <REQ extends IMessage, REPLY extends IMessage> void registerPacket(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side)
	{
		CHANNEL.registerMessage(messageHandler, requestMessageType, packetID, side);
		packetID++;
	}
}
