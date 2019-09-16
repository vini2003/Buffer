package buffer.utility;

import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface BufferProvider extends InventoryProvider {
    public static final BooleanProperty one = BooleanProperty.of("one");
    public static final BooleanProperty two = BooleanProperty.of("two");
    public static final BooleanProperty three = BooleanProperty.of("three");
    public static final BooleanProperty four = BooleanProperty.of("four");
    public static final BooleanProperty five = BooleanProperty.of("five");
    public static final BooleanProperty six = BooleanProperty.of("six");

    @Override
    public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos);
}