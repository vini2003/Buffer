package buffer.utility;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Iterator {
    private static List<Direction> directionList = new ArrayList<>();
    
    public static List<Direction> getDirList() {
        return directionList;
    }

    public static List<BlockPos> getOffList(BlockPos blockPosition) {
        List<BlockPos> offsetList = new ArrayList<>();
        for (Direction direction : getDirList()) {
            offsetList.add(blockPosition.offset(direction));
        }
        return offsetList;
    }

    static {
        directionList.add(Direction.NORTH);
        directionList.add(Direction.SOUTH);
        directionList.add(Direction.WEST);
        directionList.add(Direction.EAST);
        directionList.add(Direction.UP);
        directionList.add(Direction.DOWN);
    }
}