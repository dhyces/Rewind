package net.yuqera.rewind.setup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class RewindItemGroup extends ItemGroup {
    public RewindItemGroup(String label) { super(label); }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.TIME_WATCHER.get());
    }
}
