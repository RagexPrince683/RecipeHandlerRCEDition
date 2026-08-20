package assets.recipehandler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public final class ClientEventHandler implements RecipeMod.IRegister{
    private KeyBinding key;
    private ItemStack oldItem = null;
    private boolean pressed = false;
    private boolean bulkClickHeld = false;
    private Container bulkClickContainer = null;

    @Override
    public void register(){
        if(RecipeMod.switchKey) {
            key = new KeyBinding("RecipeSwitch", Keyboard.KEY_ADD, "key.categories.gui");
            ClientRegistry.registerKeyBinding(key);
        }
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(GuiEventHandler.INSTANCE);
        if(RecipeMod.cornerText)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public EntityPlayer getPlayer(){
        return FMLClientHandler.instance().getClientPlayerEntity();
    }

    public static World getWorld(){
        return FMLClientHandler.instance().getWorldClient();
    }

	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Text event) {
		if (getPlayer() != null) {
            int result = CraftingHandler.getNumberOfCraft(getPlayer().openContainer, getWorld());
            if (result > 1) {
                event.right.add(StatCollector.translateToLocalFormatted("handler.found.text", result));
            }
		}
	}

    @SubscribeEvent
    public void keyDown(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            handleRecipeSwitchKey();
        } else {
            // FML posts the END phase from onPostClientTick, after Minecraft.runTick
            // has dispatched mouse input to the open GuiScreen.
            handleBulkCraftClick();
        }
    }

    private void handleRecipeSwitchKey() {
        if (getPlayer() == null || FMLClientHandler.instance().getClient().currentScreen == null || key == null) {
            pressed = false;
            return;
        }
        if (Keyboard.isKeyDown(key.getKeyCode())) {
            if (!pressed) {
                pressed = true;
                pressed();
            }
        } else {
            pressed = false;
        }
    }

    private void handleBulkCraftClick() {
        Minecraft minecraft = FMLClientHandler.instance().getClient();
        GuiScreen screen = minecraft.currentScreen;
        if (!(screen instanceof GuiContainer) || getPlayer() == null || !Mouse.isButtonDown(0)
                || !GuiScreen.isShiftKeyDown()) {
            resetBulkClick();
            return;
        }

        GuiContainer gui = (GuiContainer) screen;
        Container container = gui.inventorySlots;
        Slot result = CraftingHandler.getCraftingResultSlot(container);
        if (result == null || !isMouseOver(gui, result)) {
            resetBulkClick();
            return;
        }

        if (bulkClickContainer != container) {
            bulkClickHeld = false;
            bulkClickContainer = container;
        }
        if (bulkClickHeld) {
            return;
        }
        bulkClickHeld = true;

        InventoryCrafting craft = CraftingHandler.getCraftingMatrix(container);
        int selected = CraftingHandler.getNormalizedRecipeIndex(craft, minecraft.theWorld);
        if (selected > 0 && CraftingHandler.getMatchingRecipeCount(craft, minecraft.theWorld) >= 2) {
            RecipeMod.networkWrapper.sendToServer(ChangePacket.bulk(container.windowId,
                    result.slotNumber, selected).toProxy(Side.SERVER));
        }
    }

    private void resetBulkClick() {
        bulkClickHeld = false;
        bulkClickContainer = null;
    }

    private boolean isMouseOver(GuiContainer gui, Slot slot) {
        Minecraft minecraft = FMLClientHandler.instance().getClient();
        int mouseX = Mouse.getX() * gui.width / minecraft.displayWidth;
        int mouseY = gui.height - Mouse.getY() * gui.height / minecraft.displayHeight - 1;
        int left = ReflectionHelper.getPrivateValue(GuiContainer.class, gui, "guiLeft", "field_147003_i");
        int top = ReflectionHelper.getPrivateValue(GuiContainer.class, gui, "guiTop", "field_147009_r");
        return mouseX >= left + slot.xDisplayPosition - 1 && mouseX < left + slot.xDisplayPosition + 17
                && mouseY >= top + slot.yDisplayPosition - 1 && mouseY < top + slot.yDisplayPosition + 17;
    }

    public void pressed() {
        InventoryCrafting craft = CraftingHandler.getCraftingMatrix(getPlayer().openContainer);
        if (craft != null) {
            ItemStack res = CraftingHandler.findNextMatchingRecipe(craft, getWorld());
            if (res != null && !ItemStack.areItemStacksEqual(res, oldItem)) {
                int selectedIndex = CraftingHandler.getNormalizedRecipeIndex(craft, getWorld());
                RecipeMod.networkWrapper.sendToServer(new ChangePacket(0, res, selectedIndex).toProxy(Side.SERVER));
                oldItem = res;
            }
        }
    }
}
