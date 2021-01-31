package net.yuqera.rewind.utils;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;

public class PlayerInventoryUtilities {
    public static ItemStack getFirstItemStack(PlayerInventory inventory) {
        ItemStack foundFirstItemStack = new ItemStack(Items.AIR, 1);

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (!inventory.getStackInSlot(i).getItem().equals(Items.AIR)) {
                foundFirstItemStack = inventory.getStackInSlot(i);
                break;
            }
        }

        return foundFirstItemStack;
    }

    public static ArrayList<ItemStack> getAllItemStacksWhere(PlayerInventory inventory, Item toCompare) {
        ArrayList<ItemStack> foundItemStacks = new ArrayList<>();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i).getItem().equals(toCompare)) {
                foundItemStacks.add(inventory.getStackInSlot(i));
            }
        }

        return foundItemStacks;
    }
}
