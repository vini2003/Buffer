package buffer.entity;

import buffer.inventory.BufferInventory;
import buffer.inventory.BufferInventory.VoidStack;
import buffer.registry.EntityRegistry;
import buffer.utility.BufferHandler;
import buffer.utility.BufferProvider;
import buffer.utility.BufferType;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class BufferEntity extends BlockEntity implements Tickable, BufferProvider, BlockEntityClientSerializable, InventoryProvider, SidedInventory {
    public BufferInventory bufferInventory = new BufferInventory();
    
    protected Boolean isUpToDate = false;

    public BufferEntity() {
        super(EntityRegistry.ENTITY_TESSERACT);
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
        bufferTag.putString("type", bufferInventory.getType().toString());                                          // Type - ONE
        for (int slot : bufferInventory.getInvMaxSlotAmount()) {
            VoidStack voidStack = bufferInventory.getSlot(slot);
            bufferTag.putInt(Integer.toString(slot), voidStack.stackQuantity);                                        // Total - 1024
            bufferTag.putInt(Integer.toString(slot) + "_size", voidStack.getWrappedStack().getCount());
            if (voidStack.wrapperTag != null) {
                bufferTag.put(Integer.toString(slot) + "_tag", voidStack.wrapperTag.copy());                            // Tag - ?
            }
            bufferTag.putString(Integer.toString(slot) + "_item", voidStack.getWrappedStack().getItem().toString());// Item - Cobblestone
        }
        return super.toTag(bufferTag);
    }

    @Override
    public void fromTag(CompoundTag bufferTag) {
        bufferInventory.setType(BufferType.fromString(bufferTag.getString("type")));
        for (int slot : bufferInventory.getInvMaxSlotAmount()) {
            VoidStack voidStack = bufferInventory.getSlot(slot);
            voidStack.stackQuantity = bufferTag.getInt(Integer.toString(slot));
            Integer wrapperQuantity = bufferTag.getInt(Integer.toString(slot) + "_size");
            voidStack.wrapperItem = Registry.ITEM.get(new Identifier(bufferTag.getString(Integer.toString(slot) + "_item")));
            ItemStack itemStack = new ItemStack(voidStack.wrapperItem, wrapperQuantity);
            if (bufferTag.containsKey(Integer.toString(slot) + "_slot")) {
                voidStack.wrapperTag = (CompoundTag)bufferTag.getTag(Integer.toString(slot) + "_tag");
                itemStack.setTag(voidStack.wrapperTag);
            }
            voidStack.setWrappedStack(itemStack.copy());
            voidStack.restockStack(true);
        }
        super.fromTag(bufferTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag bufferTag) {
        bufferTag.putString("type", bufferInventory.getType().toString());                                          // Type - ONE
        for (int slot : bufferInventory.getInvMaxSlotAmount()) {
            VoidStack voidStack = bufferInventory.getSlot(slot);
            bufferTag.putInt(Integer.toString(slot), voidStack.stackQuantity);                                      // Total - 1024
            bufferTag.putInt(Integer.toString(slot) + "_size", voidStack.getWrappedStack().getCount());
            if (voidStack.wrapperTag != null) {
                bufferTag.put(Integer.toString(slot) + "_tag", voidStack.wrapperTag.copy());                            // Tag - ?
            }
            bufferTag.putString(Integer.toString(slot) + "_item", voidStack.getWrappedStack().getItem().toString());// Item - Cobblestone
        }
        return super.toTag(bufferTag);
    }

    @Override
    public void fromClientTag(CompoundTag bufferTag) {
        bufferInventory.setType(BufferType.fromString(bufferTag.getString("type")));
        for (int slot : bufferInventory.getInvMaxSlotAmount()) {
            VoidStack voidStack = bufferInventory.getSlot(slot);
            voidStack.stackQuantity = bufferTag.getInt(Integer.toString(slot));
            Integer wrapperQuantity = bufferTag.getInt(Integer.toString(slot) + "_size");
            voidStack.wrapperItem = Registry.ITEM.get(new Identifier(bufferTag.getString(Integer.toString(slot) + "_item")));
            ItemStack itemStack = new ItemStack(voidStack.wrapperItem, wrapperQuantity);
            if (bufferTag.containsKey(Integer.toString(slot) + "_slot")) {
                voidStack.wrapperTag = (CompoundTag)bufferTag.getTag(Integer.toString(slot) + "_tag");
                itemStack.setTag(voidStack.wrapperTag);
            }
            voidStack.setWrappedStack(itemStack.copy());
            voidStack.restockStack(true);
        }
        super.fromTag(bufferTag);
    }

    @Override
    public void tick() {
        this.markDirty();
        bufferInventory.restockAll();
        Integer tier = world.getBlockState(this.getPos()).get(BufferProvider.tier);
        if (tier != null) {
            this.bufferInventory.setType(BufferType.fromInt(tier));
        }
        else {
            BlockState blockState = this.world.getBlockState(this.getPos());
            if (blockState.get(BufferProvider.tier) == 2) {
                this.bufferInventory = new BufferInventory(BufferType.TWO);
            }
            if (blockState.get(BufferProvider.tier) == 3) {
                this.bufferInventory = new BufferInventory(BufferType.THREE);
            }
            if (blockState.get(BufferProvider.tier) == 4) {
                this.bufferInventory = new BufferInventory(BufferType.FOUR);
            }
            if (blockState.get(BufferProvider.tier) == 5) {
                this.bufferInventory = new BufferInventory(BufferType.FIVE);
            }
            if (blockState.get(BufferProvider.tier) == 6) {
                this.bufferInventory = new BufferInventory(BufferType.SIX);
            }
        }
        isUpToDate = true;
    }
}