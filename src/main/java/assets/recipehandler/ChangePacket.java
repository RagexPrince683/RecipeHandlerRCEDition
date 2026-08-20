package assets.recipehandler;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public final class ChangePacket {
    public final static String CHANNEL = "recipemod:key";
    public ItemStack itemstack;
    public int slot;
    private int index;
    private int action;
    private int windowId;
    public ChangePacket(){}
    public ChangePacket(int slot, ItemStack stack, int recipeIndex) {
        this.slot = slot;
        this.itemstack = stack;
        this.index = recipeIndex;
    }

    public static ChangePacket select(int windowId, int slot, ItemStack stack, int recipeIndex) {
        ChangePacket packet = new ChangePacket(slot, stack, recipeIndex);
        packet.windowId = windowId;
        return packet;
    }

    public static ChangePacket bulk(int windowId, int slot, int recipeIndex) {
        ChangePacket packet = new ChangePacket(slot, null, recipeIndex);
        packet.action = 1;
        packet.windowId = windowId;
        return packet;
    }

    public ChangePacket fromBytes(ByteBuf buf) {
        slot = buf.readInt();
        itemstack = ByteBufUtils.readItemStack(buf);
        index = buf.readInt();
        action = buf.readInt();
        windowId = buf.readInt();
        return this;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
        ByteBufUtils.writeItemStack(buf, itemstack);
        buf.writeInt(index);
        buf.writeInt(action);
        buf.writeInt(windowId);
    }

    ChangePacket handle(EntityPlayer player) {
        if (action == 1) {
            handleBulkCraft(player);
            return null;
        }
        Container container = player.openContainer;
        if(itemstack != null && slot >= 0 && index >= 0 && container != null
                && container.windowId == windowId) {
            InventoryCrafting crafting = CraftingHandler.getCraftingMatrix(container);
            if(crafting!=null) {
                IRecipe recipe = CraftingHandler.getMatchingRecipe(crafting, player.worldObj, index);
                ItemStack itr = recipe == null ? null : recipe.getCraftingResult(crafting);
                if(ItemStack.areItemStacksEqual(itr, itemstack)) {
                    IInventory result = CraftingHandler.getResultSlot(container, slot+1);
                    if (result != null) {
                        result.setInventorySlotContents(slot, itr.copy());
                        return select(windowId, slot, itr, index);
                    }
                }
            }
        }
        return null;
    }

    private void handleBulkCraft(EntityPlayer player) {
        Container container = player.openContainer;
        if (container == null || container.windowId != windowId || index <= 0) {
            return;
        }
        InventoryCrafting crafting = CraftingHandler.getCraftingMatrix(container);
        Slot resultSlot = CraftingHandler.getCraftingResultSlot(container);
        if (crafting == null || resultSlot == null || resultSlot.slotNumber != slot) {
            return;
        }

        // transferStackInSlot performs one craft and calls SlotCrafting.onPickupFromSlot;
        // restoring the selected result between calls keeps vanilla's consumption semantics.
        for (int guard = 0; guard < 100000 && player.openContainer == container; guard++) {
            IRecipe recipe = CraftingHandler.getMatchingRecipe(crafting, player.worldObj, index);
            if (recipe == null) {
                break;
            }
            ItemStack selected = recipe.getCraftingResult(crafting);
            if (selected == null) {
                break;
            }
            ItemStack[] before = copyMatrix(crafting);
            resultSlot.inventory.setInventorySlotContents(resultSlot.getSlotIndex(), selected.copy());
            ItemStack transferred = container.transferStackInSlot(player, resultSlot.slotNumber);
            if (transferred == null || matrixEquals(crafting, before)) {
                break;
            }
        }

        IRecipe recipe = CraftingHandler.getMatchingRecipe(crafting, player.worldObj, index);
        if (recipe != null) {
            ItemStack selected = recipe.getCraftingResult(crafting);
            if (selected != null) {
                resultSlot.inventory.setInventorySlotContents(resultSlot.getSlotIndex(), selected.copy());
            }
        }
        container.detectAndSendChanges();
    }

    private ItemStack[] copyMatrix(InventoryCrafting crafting) {
        ItemStack[] contents = new ItemStack[crafting.getSizeInventory()];
        for (int i = 0; i < crafting.getSizeInventory(); i++) {
            ItemStack stack = crafting.getStackInSlot(i);
            contents[i] = stack == null ? null : stack.copy();
        }
        return contents;
    }

    private boolean matrixEquals(InventoryCrafting crafting, ItemStack[] contents) {
        for (int i = 0; i < contents.length; i++) {
            if (!ItemStack.areItemStacksEqual(contents[i], crafting.getStackInSlot(i))) {
                return false;
            }
        }
        return true;
    }

    public FMLProxyPacket toProxy(Side side){
        ByteBuf buf = Unpooled.buffer();
        toBytes(buf);
        FMLProxyPacket proxy = new FMLProxyPacket(buf, CHANNEL);
        proxy.setTarget(side);
        return proxy;
    }

    int getWindowId() {
        return windowId;
    }
}
