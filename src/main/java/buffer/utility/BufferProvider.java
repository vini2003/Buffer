package buffer.utility;

import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface BufferProvider extends InventoryProvider {
    public IntProperty tier = IntProperty.of("tier", 1, 6);

    @Override
    public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos);
}