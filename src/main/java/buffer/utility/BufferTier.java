package buffer.utility;

public enum BufferTier {

    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX;
    
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static BufferTier fromString(String string) {
        switch(string) {
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
			case "one":
			default:
                return ONE;
        }
    }
    
    public int toInt() {
        return this.ordinal() + 1;
    }

    public static BufferTier fromInt(int type) {
        switch(type) {
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
			case 1:
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