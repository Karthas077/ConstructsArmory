package c4.conarm.armor.common.inventory;

import c4.conarm.armor.ArmorModifiers;
import c4.conarm.armor.ConstructsArmor;
import c4.conarm.armor.modifiers.ModTravelBelt;
import c4.conarm.armor.modifiers.ModTravelGoggles;
import c4.conarm.armor.modifiers.ModTravelSack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.utils.ModifierTagHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerKnapsack extends Container {

    private final IItemHandler knapsack;
    private final int numRows;
    private final int yOffset;

    public ContainerKnapsack(IInventory playerInventory, ItemStack knapsack) {

        ModifierTagHolder modtag = ModifierTagHolder.getModifier(knapsack, ArmorModifiers.modTravelSack.getIdentifier());
        ModTravelSack.KnapsackData data = modtag.getTagData(ModTravelSack.KnapsackData.class);

        this.knapsack = data.knapsack;
        this.numRows = ModTravelSack.SACK_SIZE / 9;
        this.yOffset = (this.numRows - 4) * 18;
        initSlots(playerInventory, this.knapsack);
    }

    private void initSlots(IInventory playerInventory, IItemHandler knapsack) {
        addKnapsackSlots(knapsack);
        addPlayerSlots(playerInventory);
    }

    private void addPlayerSlots(IInventory playerInventory) {

        //Main inventory
        for (int row = 0; row < 3; row++)
        {
            for (int col = 0; col < 9; col++)
            {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 103 + row * 18 + yOffset));
            }
        }

        //Hotbar
        for (int hotbar = 0; hotbar < 9; hotbar++)
        {
            this.addSlotToContainer(new Slot(playerInventory, hotbar, 8 + hotbar * 18, 161 + yOffset));
        }
    }

    private void addKnapsackSlots(IItemHandler knapsack) {

        for (int j = 0; j < this.numRows; ++j)
        {
            for (int k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new SlotItemHandler(knapsack, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
    }

    public String getUnformattedText() {
        return Util.translate(String.format("%s.name", ConstructsArmor.travelSack.getUnlocalizedName()));
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.getSlot(index);

        if (slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < ModTravelSack.SACK_SIZE) {
                if (!this.mergeItemStack(itemstack1, ModTravelSack.SACK_SIZE, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, ModTravelSack.SACK_SIZE, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        ModifierTagHolder modtag = ModifierTagHolder.getModifier(playerIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST), ArmorModifiers.modTravelSack.getIdentifier());
        ModTravelSack.KnapsackData data = modtag.getTagData(ModTravelSack.KnapsackData.class);
        for (int i = 0; i < this.knapsack.getSlots(); i++) {
            data.knapsack.setStackInSlot(i, this.knapsack.getStackInSlot(i).copy());
        }
        modtag.save();
    }
}
