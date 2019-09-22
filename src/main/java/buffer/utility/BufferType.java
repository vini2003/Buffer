package buffer.utility;

public enum BufferType {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX;
    
    public int asInt() {
        switch(this) {
            case ONE:
                return 1;
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            case SIX:
                return 6;
            default:
                return 1;
        }
    }

    public static BufferType fromInt(int type) {
        switch(type) {
            case 1:
                return ONE;
            case 2:
                return TWO;
            case 3:
                return THREE;
            case 4:
                return FOUR;
            case 5:
                return FIVE;
            case 6:
                return SIX;
            default:
                return ONE;
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
                return 192;
            case TWO:
                return 448;
            case THREE:
                return 960;
            case FOUR:
                return 4032;
            case FIVE:
                return 8128;
            case SIX:
                return 16320;
            default:
                return 1;
        }
    }
}