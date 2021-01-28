package net.yuqera.rewind.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.yuqera.rewind.config.RewindConfig;
import net.yuqera.rewind.setup.Registration;
import net.yuqera.rewind.utils.SoundManager;

import javax.annotation.Nullable;
import java.util.Optional;

public class TimeWatcherItem extends Item {
    static private final int MAXIMUM_NUMBER_OF_TIME_WATCHERS = 1;
    static private final int MAXIMUM_NUMBER_OF_SAVED_REWINDS = RewindConfig.rewind_max_size.get();
    static private final SoundEvent ACTIVATION_EVENT_SOUND = SoundEvents.ENTITY_ENDER_EYE_DEATH;
    static private final SoundEvent DEACTIVATION_EVENT_SOUND = SoundEvents.BLOCK_BEACON_POWER_SELECT;

    public static final String NBT_TAG_NAME_STATE = "activated";

    public TimeWatcherItem() {
        super(new Item.Properties().maxStackSize(MAXIMUM_NUMBER_OF_TIME_WATCHERS).group(Registration.REWIND_ITEM_GROUP));
    }

    public static float getActivatedPropertyOverride(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        EnumWatchState enumWatchState = getState(itemStack);
        return enumWatchState.getStatePropertyOverride();
    }

    public static EnumWatchState getState(ItemStack stack) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        return EnumWatchState.fromNBT(compoundNBT, NBT_TAG_NAME_STATE);
    }

    public static void setState(ItemStack stack, EnumWatchState enumWatchState) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        enumWatchState.putIntoNBT(compoundNBT, NBT_TAG_NAME_STATE);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        EnumWatchState enumWatchState = getState(stack);
        return enumWatchState.getState();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackHeld = playerIn.getHeldItem(hand);
        CompoundNBT nbtTagCompound = itemStackHeld.getTag();

        if (playerIn.isSneaking()) {
            if (nbtTagCompound == null) {
                nbtTagCompound = new CompoundNBT();
                itemStackHeld.setTag(nbtTagCompound);
            }

            nbtTagCompound.putBoolean(NBT_TAG_NAME_STATE, !nbtTagCompound.getBoolean(NBT_TAG_NAME_STATE));
            if (nbtTagCompound.getBoolean(NBT_TAG_NAME_STATE)) {
                SoundManager.playSoundAtPlayer(worldIn, playerIn, ACTIVATION_EVENT_SOUND, SoundCategory.PLAYERS);
            }
            else {
                SoundManager.playSoundAtPlayer(worldIn, playerIn, DEACTIVATION_EVENT_SOUND, SoundCategory.PLAYERS);
            }
            return new ActionResult<>(ActionResultType.SUCCESS, itemStackHeld);
        }

        boolean activated = false;
        if (nbtTagCompound != null && nbtTagCompound.contains(NBT_TAG_NAME_STATE)) {
            activated = nbtTagCompound.getBoolean(NBT_TAG_NAME_STATE);
        }
        if (activated) {
            playerIn.setActiveHand(hand);
            return new ActionResult<>(ActionResultType.PASS, itemStackHeld);
        }
        else {
            if (worldIn.isRemote) {
                playerIn.sendStatusMessage(new StringTextComponent("Please fill the watch with some juicy time blocks."), true);
            }
            return new ActionResult<>(ActionResultType.CONSUME, itemStackHeld);
        }
    }

    public enum EnumWatchState implements IStringSerializable {

        INACTIVE(0, "inactive", false),
        ACTIVE(1, "active", true);

        private final byte nbtID;
        private final String name;
        private final boolean active;

        EnumWatchState(int i_nbtID, String i_name, boolean i_active) {
            this.nbtID = (byte)i_nbtID;
            this.name = i_name;
            this.active = i_active;
        }

        @Override
        public String getString() {
            return this.name;
        }

        public boolean getState() {
            return active;
        }

        public float getStatePropertyOverride() {
            return nbtID;
        }

        public static EnumWatchState fromNBT(CompoundNBT compoundNBT, String tagName) {
            byte activeID = 0;
            if (compoundNBT != null && compoundNBT.contains(tagName)) {
                activeID = compoundNBT.getByte(tagName);
            }
            Optional<EnumWatchState> state = getStateFromID(activeID);
            return state.orElse(INACTIVE);
        }

        public void putIntoNBT(CompoundNBT compoundNBT, String tagName) {
            compoundNBT.putByte(tagName, nbtID);
        }

        private static Optional<EnumWatchState> getStateFromID(byte ID) {
            for (EnumWatchState state : EnumWatchState.values()) {
                if (state.nbtID == ID) return Optional.of(state);
            }
            return Optional.empty();
        }
    }
}
