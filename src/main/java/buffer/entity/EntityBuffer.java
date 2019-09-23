package buffer.entity;

import buffer.inventory.InventoryBuffer;
import buffer.inventory.InventoryBuffer.VoidStack;
import buffer.registry.EntityRegistry;
import buffer.utility.BufferHandler;
import buffer.utility.BufferProvider;
import buffer.utility.BufferType;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
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
        Integer tier = world.getBlockState(this.getPos()).get(BufferProvider.tier);
        if (tier != null) {
            this.bufferInventory.setType(BufferType.fromInt(tier));
        }
        else {
            BlockState blockState = this.world.getBlockState(this.getPos());
            if (blockState.get(BufferProvider.tier) == 2) {
                this.bufferInventory = new InventoryBuffer(BufferType.TWO);
            }
            if (blockState.get(BufferProvider.tier) == 3) {
                this.bufferInventory = new InventoryBuffer(BufferType.THREE);
            }
            if (blockState.get(BufferProvider.tier) == 4) {
                this.bufferInventory = new InventoryBuffer(BufferType.FOUR);
            }
            if (blockState.get(BufferProvider.tier) == 5) {
                this.bufferInventory = new InventoryBuffer(BufferType.FIVE);
            }
            if (blockState.get(BufferProvider.tier) == 6) {
                this.bufferInventory = new InventoryBuffer(BufferType.SIX);
            }
        }
        isUpToDate = true;
    }
}