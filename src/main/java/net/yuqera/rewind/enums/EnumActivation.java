package net.yuqera.rewind.enums;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.Optional;

public enum EnumActivation implements IStringSerializable {

    UNACTIVATED(0, "unactivated", false),
    ACTIVATED(1, "activated", true);

    private final byte nbtID;
    private final String name;
    private final boolean active;

    EnumActivation(int i_nbtID, String i_name, boolean i_active) {
        this.nbtID = (byte)i_nbtID;
        this.name = i_name;
        this.active = i_active;
    }

    @Nonnull
    @Override
    public String getString() { return this.name; }
    public String toString() { return this.name; }
    public boolean getState() { return this.active; }
    public byte getNbtID() { return this.nbtID; }

    public EnumActivation activate() {
        if (!active) {
            return ACTIVATED;
        }
        return this;
    }
    public EnumActivation deactivate() {
        if (active) {
            return UNACTIVATED;
        }
        return this;
    }

    public static EnumActivation fromNBT(CompoundNBT compoundNBT, String tagName) {
        byte activeID = 0;
        if (compoundNBT != null && compoundNBT.contains(tagName)) {
            activeID = compoundNBT.getByte(tagName);
        }
        Optional<EnumActivation> state = getStateFromID(activeID);
        return state.orElse(UNACTIVATED);
    }
    public void putIntoNBT(CompoundNBT compoundNBT, String tagName) {
        compoundNBT.putByte(tagName, nbtID);
    }

    private static Optional<EnumActivation> getStateFromID(byte ID) {
        for (EnumActivation state : EnumActivation.values()) {
            if (state.nbtID == ID) return Optional.of(state);
        }
        return Optional.empty();
    }
}