package buffer.utility;

public enum BufferUtility {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX;
    
    public String toString() {
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

    public static BufferUtility fromString(String string) {
        switch(string) {
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
    
    public int toInt() {
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

    public static BufferUtility fromInt(int type) {
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

    public static Integer getStackSize(Integer tier) {
        switch(tier) {
            case 1:
                return 192;
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
            default:
                return 1;
        }
    }
}