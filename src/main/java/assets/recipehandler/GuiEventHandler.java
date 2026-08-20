package assets.recipehandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * Created by Olivier on 15/09/2014.
 */
public final class GuiEventHandler {
    public static final GuiEventHandler INSTANCE = new GuiEventHandler();

    private GuiEventHandler(){}

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onPostInitGui(GuiScreenEvent.InitGuiEvent.Post event){
        if(RecipeMod.cycleButton && event.gui instanceof GuiContainer){
            InventoryCrafting craft = CraftingHandler.getCraftingMatrix(((GuiContainer) event.gui).inventorySlots);
            if (craft != null){
                int guiLeft = (event.gui.width + 176) / 2;
                int guiTop = (event.gui.height) / 2;
                event.buttonList.add(new CreativeButton(event.buttonList.size() + 2, guiLeft, guiTop));
            }
        }
    }

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!(event.gui instanceof GuiContainer) || Mouse.getEventButton() != 0
                || !Mouse.getEventButtonState() || !GuiContainer.isShiftKeyDown()) {
            return;
        }
        GuiContainer gui = (GuiContainer) event.gui;
        InventoryCrafting craft = CraftingHandler.getCraftingMatrix(gui.inventorySlots);
        Slot result = CraftingHandler.getCraftingResultSlot(gui.inventorySlots);
        int selected = CraftingHandler.getNormalizedRecipeIndex(craft, Minecraft.getMinecraft().theWorld);
        if (result == null || selected <= 0 || CraftingHandler.getMatchingRecipeCount(craft,
                Minecraft.getMinecraft().theWorld) < 2 || !isMouseOver(gui, result)) {
            return;
        }
        event.setCanceled(true);
        RecipeMod.networkWrapper.sendToServer(ChangePacket.bulk(gui.inventorySlots.windowId,
                result.slotNumber, selected).toProxy(Side.SERVER));
    }

    private boolean isMouseOver(GuiContainer gui, Slot slot) {
        int mouseX = Mouse.getEventX() * gui.width / Minecraft.getMinecraft().displayWidth;
        int mouseY = gui.height - Mouse.getEventY() * gui.height / Minecraft.getMinecraft().displayHeight - 1;
        int left = ReflectionHelper.getPrivateValue(GuiContainer.class, gui, "guiLeft", "field_147003_i");
        int top = ReflectionHelper.getPrivateValue(GuiContainer.class, gui, "guiTop", "field_147009_r");
        return mouseX >= left + slot.xDisplayPosition - 1 && mouseX < left + slot.xDisplayPosition + 17
                && mouseY >= top + slot.yDisplayPosition - 1 && mouseY < top + slot.yDisplayPosition + 17;
    }

    public final class CreativeButton extends GuiButton {
        private final ResourceLocation texture = new ResourceLocation("textures/gui/container/villager.png");
        private static final int WIDTH = 12, HEIGHT = WIDTH + 7;
        private int deltaX = 0;
        public CreativeButton(int id, int posX, int posY){
            super(id, posX-WIDTH-3, posY-2*HEIGHT, WIDTH, HEIGHT, "0");
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY){
            if (this.visible) {
                displayString = String.valueOf(CraftingHandler.getNumberOfCraft(mc.thePlayer.openContainer, mc.theWorld));
                enabled = !("0".equals(displayString));
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                mc.renderEngine.bindTexture(this.texture);
                deltaX = !(mc.currentScreen instanceof InventoryEffectRenderer) || mc.thePlayer.getActivePotionEffects().isEmpty() ? 0 : 60;
                int k = 176;
                if (!this.enabled)
                    k += this.width * 2;
                else if(super.mousePressed(mc, mouseX - deltaX, mouseY))
                    k += this.width;
                this.drawTexturedModalRect(this.xPosition + deltaX, this.yPosition, k, 0, this.width, this.height);
                if(!RecipeMod.cornerText) {
                    int l = this.enabled ? 0xFFFFFF : 10526880;
                    this.drawCenteredString(mc.fontRenderer, this.displayString, this.xPosition + deltaX, this.yPosition + this.height / 2, l);
                }
            }
        }

        @Override
        public boolean mousePressed (Minecraft mc, int mouseX, int mouseY){
            boolean onButton = super.mousePressed(mc, mouseX - deltaX, mouseY);
            if (onButton){
                ((ClientEventHandler)RecipeMod.registry).pressed();
            }
            return onButton;
        }
    }
}
