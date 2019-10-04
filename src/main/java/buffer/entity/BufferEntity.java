package buffer.entity;

import buffer.inventory.BufferInventory;
import buffer.registry.EntityRegistry;
import buffer.screen.BufferEntityController;
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

/**
 * BufferEntity extension of BlockEntity which implements client serialization, ticking methods,
 * inventory providing and sidedness.
 */
public class BufferEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable, InventoryProvider, SidedInventory {
    public BufferEntityController bufferController;

    public BufferInventory bufferInventory = new BufferInventory(1);

    protected boolean isUpToDate = false;

    /**
     * Default constructor.
     */
    public BufferEntity() {
        super(EntityRegistry.ENTITY_BUFFER);
    }

    /**
     * Redirection for vanilla's 'getInventory'.
     */
    @Override
    public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos) {
        return bufferInventory;
    }


    /**
     * Redirection for vanilla's 'getInvSize'.
     */
    @Override
    public int getInvSize() {
        return bufferInventory.getInvSize();
    }

    /**
     * Redirection for vanilla's 'getInvStack'.
     */
    @Override
    public ItemStack getInvStack(int slot) {
        return bufferInventory.getInvStack(slot);
    }

    /**
     * Redirection for vanilla's 'setInvStack'.
     */
    @Override
    public void setInvStack(int slot, ItemStack stack) {
        bufferInventory.setInvStack(slot, stack);
    }

    /**
     * Redirection for vanilla's 'takeInvStack'.
     */
    @Override
    public ItemStack takeInvStack(int slot, int quantity) {
        return bufferInventory.takeInvStack(slot, quantity);
    }

    /**
     * Redirection for vanilla's 'removeInvStack'.
     */
    @Override
    public ItemStack removeInvStack(int slot) {
        return bufferInventory.removeInvStack(slot);
    }

    /**
     * Redirection for vanilla's 'getInvAvailableSlots'.
     */
    @Override
    public int[] getInvAvailableSlots(Direction direction) {
        return bufferInventory.getInvAvailableSlots(direction);
    }


    /**
     * Redirection for vanilla's 'canInsertInvStack'.
     */
    @Override
    public boolean canInsertInvStack(int slot, ItemStack stack, Direction direction) {
        return bufferInventory.canInsertInvStack(slot, stack, direction);
    }

    /**
     * Redirection for vanilla's 'canExtractInvStack'.
     */
    @Override
    public boolean canExtractInvStack(int slot, ItemStack stack, Direction direction) {
        return bufferInventory.canExtractInvStack(slot, stack, direction);
    }

    /**
     * Redirection for vanilla's 'isInvEmpty'.
     */
    @Override
    public boolean isInvEmpty() {
        return bufferInventory.isInvEmpty();
    }

    /**
     * Redirection for vanilla's 'clear'.
     */
    @Override
    public void clear() {
        bufferInventory.clear();
    }

    /**
     * Redirection for vanilla's 'canPlayerUseInv'.
     */
    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        return bufferInventory.canPlayerUseInv(player);
    }

    /**
     * Redirection for vanilla's 'toTag'.
     */
    @Override
    public CompoundTag toTag(CompoundTag bufferTag) {
        bufferTag = BufferInventory.toTag(bufferInventory, bufferTag);
        return super.toTag(bufferTag);
    }

    /**
     * Redirection for vanilla's 'fromTag'.
     */
    @Override
    public void fromTag(CompoundTag bufferTag) {
        this.bufferInventory = BufferInventory.fromTag(bufferTag);
        super.fromTag(bufferTag);
    }

    /**
     * Redirection for vanilla's 'toClientTag'.
     */
    @Override
    public CompoundTag toClientTag(CompoundTag bufferTag) {
        bufferTag = BufferInventory.toTag(bufferInventory, bufferTag);
        return super.toTag(bufferTag);
    }

    /**
     * Redirection for vanilla's 'fromClientTag'.
     */
    @Override
    public void fromClientTag(CompoundTag bufferTag) {
        this.bufferInventory = BufferInventory.fromTag(bufferTag);
        super.fromTag(bufferTag);
    }

    /**
     * Override for vanilla's 'tick'.
     */
    @Override
    public void tick() {
        bufferInventory.restockAll();
        if (bufferController != null && world.isClient) {
            bufferController.tick();
        }
    }
}