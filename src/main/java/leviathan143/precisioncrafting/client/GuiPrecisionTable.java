package leviathan143.precisioncrafting.client;

import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;

import leviathan143.precisioncrafting.PrecisionCrafting;
import leviathan143.precisioncrafting.common.packets.PacketHandler;
import leviathan143.precisioncrafting.common.packets.PacketSetOutputQuantity;
import leviathan143.precisioncrafting.common.precisiontable.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiPrecisionTable extends GuiContainer
{
	private static final ResourceLocation PRECISION_TABLE_GUI = new ResourceLocation(PrecisionCrafting.MODID,
			"textures/gui/precision_table.png");
	private static final int BTN_INCREASE_QUANTITY = 0;
	private static final int BTN_DECREASE_QUANTITY = 1;
	private static final int STACK_CYCLE_TIME_MILLI = 1000;

	private final TilePrecisionTable table;
	private GuiTextField outputQuantityField;
	private int outputQuantity;

	public GuiPrecisionTable(TilePrecisionTable table, InventoryPlayer playerInv)
	{
		super(new ContainerPrecisionTable(table, playerInv));
		this.table = table;
		this.xSize = 176;
		this.ySize = 208;
		this.outputQuantity = table.getOutputQuantity();
		onOutputQuantityChanged();
	}

	@Override
	public void initGui()
	{
		super.initGui();
		int outputQuantitySelectorLeftX = this.guiLeft + this.xSize - 22 - 5;
		int outputQuantitySelectorTopY = this.guiTop + 18 + 9;
		outputQuantityField = new GuiTextField(0, this.fontRenderer, outputQuantitySelectorLeftX,
				outputQuantitySelectorTopY + 10, 20, 16);
		outputQuantityField.setValidator(this::validateOutputQuantity);
		outputQuantityField.setText(Integer.toString(outputQuantity));
		this.addButton(new GuiButtonExt(BTN_INCREASE_QUANTITY, outputQuantitySelectorLeftX - 1,
				outputQuantitySelectorTopY, 22, 8, "\u25B2"));
		this.addButton(new GuiButtonExt(BTN_DECREASE_QUANTITY, outputQuantitySelectorLeftX - 1,
				outputQuantitySelectorTopY + 28, 22, 8, "\u25BC"));
	}

	private boolean validateOutputQuantity(String str)
	{
		if (str.isEmpty())
		{
			outputQuantity = 0;
			onOutputQuantityChanged();
			return true;
		}
		try
		{
			outputQuantity = Integer.parseInt(str);
			if (outputQuantity > 64)
			{
				outputQuantity = 64;
				onOutputQuantityChanged();
				return false;
			}
			else if (outputQuantity < 1)
			{
				outputQuantity = 1;
				onOutputQuantityChanged();
				return false;
			}
			else onOutputQuantityChanged();
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	private void onOutputQuantityChanged()
	{
		table.setOutputQuantity(outputQuantity);
		((ContainerPrecisionTable) this.inventorySlots).onOutputQuantityChanged();
		PacketHandler.CHANNEL.sendToServer(new PacketSetOutputQuantity(table.getPos(), outputQuantity));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
		ContainerPrecisionTable tableContainer = (ContainerPrecisionTable) this.inventorySlots;
		if (tableContainer.hasRecipe() && !tableContainer.canCraft()
				&& (mouseX >= getIssueIndicatorX() && mouseX <= getIssueIndicatorX() + 11)
				&& (mouseY >= getIssueIndicatorY() && mouseY <= getIssueIndicatorY() + 11))
		{
			if (!tableContainer.isRecipeTypeValid())
			{
				this.drawHoveringText(I18n.format(PrecisionCrafting.MODID + ".error.invalidRecipeType"), mouseX - 100, mouseY);
				return;
			}

			if (!tableContainer.hasRequiredIngredients())
			{
				this.drawHoveringText(I18n.format(PrecisionCrafting.MODID + ".error.missingIngredients"), mouseX - 100, mouseY);
				return;
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		mc.getTextureManager().bindTexture(PRECISION_TABLE_GUI);
		drawTexturedModalRect((width - xSize) / 2.0F, (height - ySize) / 2.0F, 0, 0, xSize, ySize);
		ContainerPrecisionTable tableContainer = (ContainerPrecisionTable) this.inventorySlots;
		if (tableContainer.hasRecipe() && !tableContainer.canCraft())
		{
			mc.getTextureManager().bindTexture(PRECISION_TABLE_GUI);
			this.drawTexturedModalRect(getIssueIndicatorX(), getIssueIndicatorY(), 0, 208, 11, 11);

		}
		renderRequiredIngredients();
		outputQuantityField.drawTextBox();
	}
	
	private void renderRequiredIngredients()
	{
		List<QuantifiableIngredientF> reqIngredients = ((ContainerPrecisionTable) this.inventorySlots)
				.getRequiredIngredients();
		int ingredientRowLeftX = this.guiLeft + 8;
		int ingredientRowTopY = this.guiTop + 16 + 78;

		for (int i = 0; i < reqIngredients.size(); i++)
		{
			Slot slot = this.inventorySlots.inventorySlots.get(i + 46);
			QuantifiableIngredientF qIngredient = reqIngredients.get(i);
			if (qIngredient.apply(slot.getStack())) continue;

			int matchingStackCount = qIngredient.getIngredient().getMatchingStacks().length;

			RenderHelper.enableGUIStandardItemLighting();
			ItemStack stack = qIngredient.getIngredient()
					.getMatchingStacks()[(int) ((System.currentTimeMillis() / STACK_CYCLE_TIME_MILLI)
							% matchingStackCount)];
			int ingredientLeftX = ingredientRowLeftX + 18 * i;
			mc.getRenderItem().renderItemAndEffectIntoGUI(stack, ingredientLeftX, ingredientRowTopY);
			GlStateManager.depthFunc(GL11.GL_GREATER);
			Gui.drawRect(ingredientLeftX, ingredientRowTopY, ingredientLeftX + 16, ingredientRowTopY + 16, 0x80FFFFFF);
			GlStateManager.depthFunc(GL11.GL_LEQUAL);

			String quantityString = Integer.toString(MathHelper.ceil(qIngredient.getQuantity()));
			GlStateManager.disableDepth();
			mc.fontRenderer.drawString(quantityString,
					ingredientLeftX + 17 - mc.fontRenderer.getStringWidth(quantityString), ingredientRowTopY + 9,
					0xFFFFFF, true);
			GlStateManager.enableDepth();
			RenderHelper.disableStandardItemLighting();
		}
	}

	private int getIssueIndicatorX()
	{
		return (int) (this.guiLeft + (this.xSize / 2.0F) + 14);
	}

	private int getIssueIndicatorY()
	{
		return this.guiTop + 37;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRenderer.drawString(I18n.format(PrecisionCrafting.MODID + ".container.precisionCrafting"), 28, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		outputQuantityField.updateCursorCounter();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		switch (button.id)
		{
		case BTN_INCREASE_QUANTITY:
			outputQuantityField.setText(Integer.toString(++outputQuantity));
			onOutputQuantityChanged();
			break;
		case BTN_DECREASE_QUANTITY:
			outputQuantityField.setText(Integer.toString(--outputQuantity));
			onOutputQuantityChanged();
			break;
		default:
			break;
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);
		outputQuantityField.textboxKeyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		outputQuantityField.mouseClicked(mouseX, mouseY, mouseButton);
	}
}
