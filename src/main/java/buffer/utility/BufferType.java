package buffer.utility;

import net.minecraft.state.property.BooleanProperty;

public enum BufferType {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX;
    
    public BooleanProperty asProperty() {
        switch(this) {
            case ONE:
                return BufferProvider.one;
            case TWO:
                return BufferProvider.two;
            case THREE:
                return BufferProvider.three;
            case FOUR:
                return BufferProvider.four;
            case FIVE:
                return BufferProvider.five;
            case SIX:
                return BufferProvider.six;
            default:
                return BufferProvider.one;
        }
    }

    public String asString() {
        switch(this) {
            case ONE:
                return "one";
            case TWO:
                return "two";
            case THREE:
                return "three";
            case FOUR:
                return "four";
            case FIVE:
                return "five";
            case SIX:
                return "six";
            default:
                return "one";
        }
    }

    public static BufferType fromString(String type) {
        switch(type) {
            case "one":
                return ONE;
            case "two":
                return TWO;
            case "three":
                return THREE;
            case "four":
                return FOUR;
            case "five":
                return FIVE;
            case "six":
                return SIX;
            default:
                return ONE;
        }
    }

    public int[] getSlotAmount() {
        switch(this) {
            case ONE:
                return new int[] { 0 };
            case TWO:
                return new int[] { 0, 1 };
            case THREE:
                return new int[] { 0, 1, 2 };
            case FOUR:
                return new int[] { 0, 1, 2, 3 };
            case FIVE:
                return new int[] { 0, 1, 2, 3, 4 };
            case SIX:
                return new int[] { 0, 1, 2, 3, 4, 5 };
            default:
                return new int[] { 0 };
        }
    }

    public int getStackSize() {
        switch(this) {
            case ONE:
                return 64;
            case TWO:
                return 256;
            case THREE:
                return 1024;
            case FOUR:
                return 4096;
            case FIVE:
                return 8192;
            case SIX:
                return 16384;
            default:
                return 1;
        }
    }
}