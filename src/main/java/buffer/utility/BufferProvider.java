package buffer.utility;

import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface BufferProvider extends InventoryProvider {
    IntProperty tier = IntProperty.of("tier", 1, 6);

    @Override
    SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos);
}