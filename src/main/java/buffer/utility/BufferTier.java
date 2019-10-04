package buffer.utility;

import buffer.inventory.BufferInventory;
import net.minecraft.state.property.IntProperty;

/**
 * BufferTier enum for usage with BufferInventory.
 */
public enum BufferTier {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX;

    public static IntProperty bufferTier = IntProperty.of(BufferInventory.TIER_RETRIEVER, 1, 6);

    /**
     * Get maximum tier available for BufferTier.
     * @return Integer of maximum tier available.
     */
    public static int getMaximumTier() {
        return 6;
    }

    /**
     * Get maximum stack size available for BufferTier.
     * @return Integer of maximum stack size.
     */
    public static int getStackSize(int bufferTier) {
        switch(bufferTier) {
            case 2:
                return 448;
            case 3:
                return 960;
            case 4:
                return 4032;
            case 5:
                return 8128;
            case 6:
                return 16320;
            case 1:
            default:
                return 192;
        }
    }
}