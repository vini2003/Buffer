package buffer.entity;

import buffer.inventory.InventoryBuffer;
import buffer.registry.EntityRegistry;
import buffer.utility.BufferProvider;
import buffer.utility.BufferType;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class EntityBuffer extends BlockEntity implements Tickable, BufferProvider, BlockEntityClientSerializable {
    public InventoryBuffer bufferInventory = new InventoryBuffer();
    protected Boolean isUpToDate = false;

    public EntityBuffer() {
        super(EntityRegistry.ENTITY_TESSERACT);
    }

    @Override
    public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos) {
        return bufferInventory;
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag_1) {
        return super.toTag(compoundTag_1);
    }

    @Override
    public void fromTag(CompoundTag compoundTag_1) {
        super.fromTag(compoundTag_1);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag blockTag) {
        return super.toTag(blockTag);
    }

    @Override
    public void fromClientTag(CompoundTag blockTag) {
        super.fromTag(blockTag);
    }

    @Override
    public void tick() {
        if (!isUpToDate) {
            BlockState blockState = this.world.getBlockState(this.getPos());
            if (blockState.get(BufferProvider.two)) {
                this.bufferInventory = new InventoryBuffer(BufferType.TWO);
            }
            if (blockState.get(BufferProvider.three)) {
                this.bufferInventory = new InventoryBuffer(BufferType.THREE);
            }
            if (blockState.get(BufferProvider.four)) {
                this.bufferInventory = new InventoryBuffer(BufferType.FOUR);
            }
            if (blockState.get(BufferProvider.five)) {
                this.bufferInventory = new InventoryBuffer(BufferType.FIVE);
            }
            if (blockState.get(BufferProvider.six)) {
                this.bufferInventory = new InventoryBuffer(BufferType.SIX);
            }
        }
        isUpToDate = true;
    }
}