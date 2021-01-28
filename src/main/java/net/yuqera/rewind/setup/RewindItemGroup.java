package net.yuqera.rewind.setup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class RewindItemGroup extends ItemGroup {
    public RewindItemGroup(String label) { super(label); }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.TIME_WATCHER.get());
    }
}
