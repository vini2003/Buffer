package buffer.utility;

import buffer.inventory.BufferInventory;
import buffer.inventory.BufferInventory.VoidStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BufferHandler {
    public static CompoundTag toTag(BufferInventory bufferInventory, CompoundTag bufferTag) {
        BlockPos tagPosition = new BlockPos(bufferTag.getInt("x"), bufferTag.getInt("y"), bufferTag.getInt("z"));
        Identifier tagIdentifier = new Identifier(bufferTag.getString("id"));
        bufferTag.putString("type", bufferInventory.getType().toString());                                          // Type - ONE
        for (int slot : bufferInventory.getInvMaxSlotAmount()) {
            VoidStack voidStack = bufferInventory.getSlot(slot);
            bufferTag.putInt(Integer.toString(slot), voidStack.getStored());                                        // Total - 1024
            bufferTag.put(Integer.toString(slot) + "_tag", voidStack.wrapperTag.copy());                            // Tag - ?
            bufferTag.putString(Integer.toString(slot) + "_item", voidStack.getWrappedStack().getItem().toString());// Item - Cobblestone
        }
        bufferTag.putInt("x", tagPosition.getX());
        bufferTag.putInt("y", tagPosition.getY());
        bufferTag.putInt("z", tagPosition.getZ());
        bufferTag.putString("id", tagIdentifier.toString());
        return bufferTag;
    }

    public static BufferInventory fromTag(CompoundTag bufferTag) {
        BufferInventory bufferInventory = new BufferInventory();
        
        bufferInventory.setType(BufferType.fromString(bufferTag.getString("type")));
        
        for (int slot : bufferInventory.getInvMaxSlotAmount()) {
            VoidStack voidStack = bufferInventory.getSlot(slot);
            voidStack.stackQuantity = bufferTag.getInt(Integer.toString(slot));
            voidStack.wrapperItem = Registry.ITEM.get(new Identifier(bufferTag.getString(Integer.toString(slot) + "_item")));
            voidStack.wrapperTag = (CompoundTag)bufferTag.getTag(Integer.toString(slot) + "_tag");
            voidStack.restockStack(true);
        }

        return bufferInventory;
    }
}