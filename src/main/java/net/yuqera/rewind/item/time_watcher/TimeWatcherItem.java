package net.yuqera.rewind.item.time_watcher;

import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.yuqera.rewind.block.UnwoundJukeboxBlock;
import net.yuqera.rewind.config.RewindConfig;
import net.yuqera.rewind.enums.EnumActivation;
import net.yuqera.rewind.setup.ModBlocks;
import net.yuqera.rewind.setup.Registration;
import net.yuqera.rewind.utils.BlockHistoryHandler;
import net.yuqera.rewind.utils.SoundManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class TimeWatcherItem extends Item {
    static private final int MAXIMUM_NUMBER_OF_TIME_WATCHERS = 1;
    static private final int MAXIMUM_NUMBER_OF_SAVED_REWINDS = RewindConfig.rewind_max_size.get();

    static private final SoundEvent ACTIVATION_EVENT_SOUND = SoundEvents.ENTITY_ENDER_EYE_DEATH;
    static private final SoundEvent DEACTIVATION_EVENT_SOUND = SoundEvents.BLOCK_BEACON_POWER_SELECT;

    public static final String NBT_TAG_NAME_ACTIVATION = "activated";
    public static final String NBT_TAG_NAME_LOOKING_DIRECTION = "look_direction";
    public static final String NBT_TAG_NAME_FULLNESS = "fullness";

    public TimeWatcherItem() {
        super(new Item.Properties().maxStackSize(MAXIMUM_NUMBER_OF_TIME_WATCHERS).group(Registration.REWIND_ITEM_GROUP));
    }

    //region Overrides

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull World worldIn, @Nonnull Entity entityIn, int itemSlot, boolean isSelected) {
        if (!isSelected) {
            CompoundNBT compoundNBT = stack.getOrCreateTag();
            EnumActivation activationState = EnumActivation.fromNBT(compoundNBT, NBT_TAG_NAME_ACTIVATION);
            changeActivatedWatchLookingDirectionAtRandom(activationState, compoundNBT);
        }
    }

    @Override
    public boolean hasEffect(@Nonnull ItemStack stack) {
        EnumActivation enumActivation = getActivationState(stack);
        return enumActivation.getState();
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        CompoundNBT compoundNBT = stack.getTag();

        if (player.isSneaking()) { // Player activating item
            return toggleItemActivation(world, player, stack, compoundNBT);
        }

        if (compoundNBT != null && compoundNBT.contains(NBT_TAG_NAME_ACTIVATION)) {
            EnumActivation enumActivation = EnumActivation.fromNBT(compoundNBT, NBT_TAG_NAME_ACTIVATION);
            if (enumActivation.getState()) {
                player.setActiveHand(hand);
            }
        }

        return ActionResult.resultPass(stack);
    }
    
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
    	ItemStack stackContext = context.getItem();
    	if (getActivationState(stackContext).getState()) return ActionResultType.PASS;
    	World worldContext = context.getWorld();
    	BlockPos posContext = context.getPos();
    	BlockState state = worldContext.getBlockState(posContext);
    	if (!worldContext.isRemote && state.getBlock() instanceof JukeboxBlock && !state.get(JukeboxBlock.HAS_RECORD)) {
    		if (worldContext.setBlockState(posContext, ModBlocks.UNWOUND_JUKEBOX.get().getDefaultState())) {
    			BlockState newState = worldContext.getBlockState(posContext);
    			if (newState.getBlock() instanceof UnwoundJukeboxBlock) {
    				((UnwoundJukeboxBlock)newState.getBlock()).setWatch(worldContext, posContext, newState, stackContext);
    				stackContext.shrink(1);
    				return ActionResultType.SUCCESS;
    			}
    		}
    	}
    	return ActionResultType.PASS;
    }
    
    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
    	return 1.0 - getFullnessPropertyOverride(stack, (World)null, (LivingEntity)null);
    }
    
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
    	return getActivationState(stack).getState();
    }

    //endregion

    //region Enum accessors

    public static EnumActivation getActivationState(ItemStack stack) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        return EnumActivation.fromNBT(compoundNBT, NBT_TAG_NAME_ACTIVATION);
    }

    public static EnumActivatedWatchLookingDirection getLookingDirection(ItemStack stack) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        return EnumActivatedWatchLookingDirection.fromNBT(compoundNBT, NBT_TAG_NAME_LOOKING_DIRECTION);
    }

    //endregion

    //region Model overrides accessors

    public static float getActivationPropertyOverride(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        EnumActivation enumActivation = getActivationState(itemStack);
        return enumActivation.getNbtID();
    }

    public static float getLookingDirectionPropertyOverride(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        EnumActivatedWatchLookingDirection enumLookingDirection = getLookingDirection(itemStack);
        return enumLookingDirection.getNbtID();
    }

    public static float getFullnessPropertyOverride(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        return (float)BlockHistoryHandler.getTimeWatchHistoryCount(itemStack) / (float)MAXIMUM_NUMBER_OF_SAVED_REWINDS;
    }

    //endregion

    public enum EnumActivatedWatchLookingDirection implements IStringSerializable {
        BOTTOM_LEFT(0, "bottom-left", "Eye looks bottom left"),
        BOTTOM_RIGHT(1, "bottom-right", "Eye looks bottom right"),
        TOP_RIGHT(2, "top-right", "Eye looks top right"),
        TOP_LEFT(3, "top-left", "Eye looks top left");

        private final byte nbtID;
        private final String name;
        private final String description;

        EnumActivatedWatchLookingDirection(int i_nbtID, String i_name, String i_description) {
            this.nbtID = (byte)i_nbtID;
            this.name = i_name;
            this.description = i_description;
        }

        @Nonnull
        @Override
        public String getString() { return this.name; }
        @Override
		public String toString() { return this.name; }
        public String getDescription() { return  this.description; }
        public byte getNbtID() { return this.nbtID; }

        public EnumActivatedWatchLookingDirection getRandom() {
            byte randomDirection = (byte)(Math.random()*4);
            Optional<EnumActivatedWatchLookingDirection> lookingDirection = getStateFromID(randomDirection);
            return lookingDirection.orElse(BOTTOM_LEFT);
        }

        public static EnumActivatedWatchLookingDirection fromNBT(CompoundNBT compoundNBT, String tagName) {
            byte activeID = 0;
            if (compoundNBT != null && compoundNBT.contains(tagName)) {
                activeID = compoundNBT.getByte(tagName);
            }
            Optional<EnumActivatedWatchLookingDirection> state = getStateFromID(activeID);
            return state.orElse(BOTTOM_LEFT);
        }
        public void putIntoNBT(CompoundNBT compoundNBT, String tagName) {
            compoundNBT.putByte(tagName, nbtID);
        }

        private static Optional<EnumActivatedWatchLookingDirection> getStateFromID(byte ID) {
            for (EnumActivatedWatchLookingDirection state : EnumActivatedWatchLookingDirection.values()) {
                if (state.nbtID == ID) return Optional.of(state);
            }
            return Optional.empty();
        }
    }

    //region Utility methods

    private void changeActivatedWatchLookingDirectionAtRandom(EnumActivation enumActivation, CompoundNBT compoundNBT) {
        if (enumActivation.getState()) {
            double rng = Math.random()*100;

            if (rng <= 1) {
                EnumActivatedWatchLookingDirection enumLookingDirection = EnumActivatedWatchLookingDirection.fromNBT(compoundNBT, NBT_TAG_NAME_LOOKING_DIRECTION);
                enumLookingDirection = enumLookingDirection.getRandom();
                enumLookingDirection.putIntoNBT(compoundNBT, NBT_TAG_NAME_LOOKING_DIRECTION);
            }
        }
    }

    private ActionResult<ItemStack> toggleItemActivation(World world, PlayerEntity player, ItemStack stack, @Nullable CompoundNBT compoundNBT) {
        if (compoundNBT == null) {
            compoundNBT = new CompoundNBT();
            stack.setTag(compoundNBT);
        }

        EnumActivation enumActivation = getActivationState(stack);
        if (enumActivation.getState()) { // If item is active
            SoundManager.playSoundAtPlayer(world, player, DEACTIVATION_EVENT_SOUND, SoundCategory.PLAYERS);
            enumActivation = enumActivation.deactivate();
        }
        else { // If item is not active
            SoundManager.playSoundAtPlayer(world, player, ACTIVATION_EVENT_SOUND, SoundCategory.PLAYERS);
            enumActivation = enumActivation.activate();
        }
        enumActivation.putIntoNBT(compoundNBT, NBT_TAG_NAME_ACTIVATION);

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    //endregion

}