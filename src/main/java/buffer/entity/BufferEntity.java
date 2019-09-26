package buffer.entity;

import buffer.inventory.BufferInventory;
import buffer.registry.EntityRegistry;
import buffer.utility.BufferProvider;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class BufferEntity extends BlockEntity implements Tickable, BufferProvider, BlockEntityClientSerializable, InventoryProvider, SidedInventory {
    public BufferInventory bufferInventory = new BufferInventory(null);

    public CompoundTag bufferTag = new CompoundTag();
    
    protected Boolean isUpToDate = false;

    public BufferEntity() {
        super(EntityRegistry.ENTITY_TESSERACT);
        this.bufferTag = bufferInventory.toTag(this.bufferInventory, this.bufferTag);
    }

    @Override
    public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos) {
        return bufferInventory;
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        bufferInventory.setInvStack(slot, stack);
    }

    @Override
    public ItemStack getInvStack(int slot) {
        return bufferInventory.getInvStack(slot);
    }

    @Override
    public ItemStack takeInvStack(int slot, int quantity) {
        return bufferInventory.takeInvStack(slot, quantity);
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        return bufferInventory.removeInvStack(slot);
    }

    @Override
    public boolean canInsertInvStack(int slot, ItemStack stack, Direction direction) {
        return bufferInventory.canInsertInvStack(slot, stack, direction);
    }

    @Override
    public boolean canExtractInvStack(int slot, ItemStack stack, Direction direction) {
        return bufferInventory.canExtractInvStack(slot, stack, direction);
    }

    @Override
    public int[] getInvAvailableSlots(Direction direction) {
        return bufferInventory.getInvAvailableSlots(direction);
    }

    @Override
    public int getInvSize() {
        return bufferInventory.getInvSize();
    }

    @Override
    public boolean isInvEmpty() {
        return bufferInventory.isInvEmpty();
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        return bufferInventory.canPlayerUseInv(player);
    }

    @Override
    public void clear() {
        bufferInventory.clear();
    }

    @Override
    public CompoundTag toTag(CompoundTag bufferTag) {
        bufferTag = BufferInventory.toTag(bufferInventory, bufferTag);
        return super.toTag(bufferTag);
    }

    @Override
    public void fromTag(CompoundTag bufferTag) {
        this.bufferInventory = BufferInventory.fromTag(bufferTag);
        super.fromTag(bufferTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag bufferTag) {
        bufferTag = BufferInventory.toTag(bufferInventory, bufferTag);
        return super.toTag(bufferTag);
    }

    @Override
    public void fromClientTag(CompoundTag bufferTag) {
        this.bufferInventory = BufferInventory.fromTag(bufferTag);
        super.fromTag(bufferTag);
    }

    public CompoundTag getTag() {
        return BufferInventory.toTag(this.bufferInventory, this.bufferTag);
    }

    @Override
    public void tick() {
        this.markDirty();
        bufferInventory.restockAll();
    }
}