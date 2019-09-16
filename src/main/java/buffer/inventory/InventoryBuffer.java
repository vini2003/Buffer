package buffer.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import blue.endless.jankson.annotation.Nullable;
import buffer.screen.BufferController;
import buffer.utility.BufferResult;
import buffer.utility.BufferType;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

public class InventoryBuffer implements SidedInventory {
    protected BufferType bufferType = BufferType.ONE;
    public List<VoidStack> voidStacks = new ArrayList<>();
    protected List<InventoryListener> listeners;

    public class WVoidSlot extends WItemSlot {
        protected int slotIndex = 0;
        protected PlayerInventory playerInventory = null;

        public WVoidSlot(Inventory inventory, int temporaryIndex, int slotsWide, int slotsHigh, PlayerInventory temporaryInventory) {
            super(inventory, temporaryIndex, slotsWide, slotsHigh, false, false);
            slotIndex = temporaryIndex;
            playerInventory = temporaryInventory;
        }

        @Override
        public void onClick(int x, int y, int button) {
            //if (!playerInventory.getCursorStack().isEmpty() && button == 0) {
            //    VoidStack bufferStack = voidStacks.get(slotIndex);
            //    ItemStack cursorStack = playerInventory.getCursorStack().copy();
            //    if (bufferStack.getWrappedStack() == ItemStack.EMPTY) {
            //        bufferStack.setWrappedStack(cursorStack.copy());
            //        playerInventory.setCursorStack(ItemStack.EMPTY);
            //        playerInventory.updateItems();
            //    } else {
            //        playerInventory.setCursorStack(bufferStack.insertStack(cursorStack.copy()));
            //        playerInventory.updateItems();
            //        //cursorStack = bufferStack.insertStack(cursorStack.copy());
            //    }
            //}
            super.onClick(x, y, button);
        }

        //@Override
        //public void tick() {
        //    for (VoidStack voidStack : voidStacks) {
        //        voidStack.restockStack();
        //    }
        //}
    }

    public class VoidStack {
        int quantityStack = 0;
        int quantityMaximum = getInvMaxStackAmount();

        ItemStack wrapperStack = ItemStack.EMPTY;
        ItemStack previousStack = ItemStack.EMPTY;

        Item slotItem;

        public VoidStack() {
            // ...
        }

        public void setWrappedStack(ItemStack stack) {
            this.wrapperStack = stack.copy();
            this.previousStack = stack.copy();
        }

        public ItemStack getWrappedStack() {
            return this.wrapperStack.copy();
        }

        public BufferResult canInsertStack(ItemStack stack) {
            if (this.wrapperStack.getCount() + stack.getCount() < this.quantityMaximum && this.wrapperStack.getItem() == stack.getItem()) {
                return BufferResult.SUCCESS;
            } else {
                return BufferResult.FAIL;
            }
        }

        public BufferResult canExtractStack(ItemStack stack) {
            if (stack.getCount() <= this.wrapperStack.getCount() && this.wrapperStack.getItem() == stack.getItem()) {
                return BufferResult.SUCCESS;
            } else {
                return BufferResult.FAIL;
            }
        }

        public ItemStack insertStack(ItemStack stackInsert) {
            if (stackInsert.isEmpty()) {
                return stackInsert;
            }

            this.quantityMaximum = bufferType.getStackSize();

            int wrapperQuantity = this.wrapperStack.getCount();
            int insertQuantity = stackInsert.getCount();

            // Added: Intercept click, update stock.
            if (stackInsert.getItem() == wrapperStack.getItem()) {
                if (wrapperQuantity == wrapperStack.getMaxCount()) {
                    if (this.quantityStack + insertQuantity <= this.quantityMaximum) {
                        this.quantityStack += insertQuantity;
                        stackInsert.decrement(insertQuantity);
                        if (stackInsert.getCount() == 0) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else {
                    int availableQuantity = wrapperStack.getMaxCount() - wrapperStack.getCount();
                    if (insertQuantity <= availableQuantity) {
                        this.wrapperStack.increment(insertQuantity);
                    } else if (insertQuantity > availableQuantity) {
                        int int_1 = Math.abs(availableQuantity - insertQuantity);
                        this.wrapperStack.increment(availableQuantity);
                        stackInsert.decrement(availableQuantity);

                        this.quantityStack += int_1;
                        if (stackInsert.getCount() > 0) {
                            return stackInsert;
                        } else {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            } else if (wrapperStack.isEmpty()) {
                this.wrapperStack = stackInsert.copy();
                stackInsert = ItemStack.EMPTY;
            }
            return stackInsert;
        }

        public Boolean restockStack(BufferController controller) {
            if (this.wrapperStack.getCount() >= 0 && this.quantityStack > 0) {
                if (quantityStack - (wrapperStack.getMaxCount() - wrapperStack.getCount()) > 0) {
                    quantityStack -= (wrapperStack.getMaxCount() - wrapperStack.getCount());
                    this.setWrappedStack(new ItemStack(slotItem, this.wrapperStack.getCount() + (wrapperStack.getMaxCount() - wrapperStack.getCount())));
                    controller.sendContentUpdates();
                }
                else {
                    int difference = this.wrapperStack.getMaxCount() - Math.abs(this.quantityStack - wrapperStack.getMaxCount());
                    wrapperStack.setCount(this.wrapperStack.getCount() + difference);
                    this.quantityStack -= difference;
                    this.setWrappedStack(new ItemStack(slotItem, this.wrapperStack.getCount() + difference));
                    controller.sendContentUpdates();
                }
            }
            if (this.wrapperStack.getCount() == 0 && this.quantityStack == 0) {
                this.setWrappedStack(ItemStack.EMPTY);
                this.slotItem = Items.AIR;
                controller.sendContentUpdates();
            } else {
                this.slotItem = this.wrapperStack.copy().getItem();
            }

            if (this.quantityStack == 0 && this.wrapperStack.getCount() == 0) {
                this.setWrappedStack(ItemStack.EMPTY);
                controller.sendContentUpdates();
            }

            this.previousStack = this.wrapperStack.copy();

            return false;
        }

        public int getStored() {
            if (this.quantityStack > 0) {
                return this.quantityStack + this.wrapperStack.getCount();
            } else {
                return this.wrapperStack.getCount();
            }
        }
    }

    public InventoryBuffer(BufferType newBufferType) {
        this.setType(newBufferType);
    }

    public InventoryBuffer(CompoundTag compoundTag) {
        this.setType(compoundTag);   
    }

    public InventoryBuffer() {
        this.setType(BufferType.ONE);
    }

    public void setType(CompoundTag itemTag) {
        try {
            BufferType newBufferType = BufferType.fromString(itemTag.getString("type"));
        } catch (NullPointerException exception) {
            this.bufferType = BufferType.ONE;
        }
        for (int slot = 0; slot < this.getInvMaxSlotAmount().length- this.voidStacks.size(); ++slot) {
            this.voidStacks.add(new VoidStack());
        }
    }

    public void setType(BufferType newBufferType) {
        this.bufferType = newBufferType;
        for (int slot = 0; slot < this.getInvMaxSlotAmount().length- this.voidStacks.size(); ++slot) {
            this.voidStacks.add(new VoidStack());
        }
    }

    public BufferType getType() {
        return this.bufferType;
    }

    public VoidStack getSlot(int slot) {
        if (this.voidStacks.size() >= slot) {
            return this.voidStacks.get(slot);
        } else {
            return null;
        }
    }

    public int getStored(int slot) {
        return this.voidStacks.get(slot).getStored();
    }

    @Override
    public int getInvSize() {
        return this.getInvMaxSlotAmount().length;
    }

    // Gets slot stack.
    @Override
    public ItemStack getInvStack(int slot) {
        ItemStack returnStack = ItemStack.EMPTY;

        if (this.voidStacks.get(slot) != null) {
            returnStack = this.voidStacks.get(slot).getWrappedStack().copy();
        }

        return returnStack;
    }

    // Sets slot stack.
    @Override
    public void setInvStack(int slot, ItemStack stack) {
        if (this.voidStacks.get(slot) != null) {
            VoidStack voidStack = this.voidStacks.get(slot);
            voidStack.setWrappedStack(stack);
        }
    }

    public int[] getInvMaxSlotAmount() {
        return bufferType.getSlotAmount();
    }

    public int getInvMaxStackAmount() {
        return bufferType.getStackSize();
    }

    @Override
    public int[] getInvAvailableSlots(Direction direction) {
        return this.getInvMaxSlotAmount();
    }   

    @Override
    public ItemStack removeInvStack(int var1) {
        ItemStack returnStack = null;
        for (int slot : this.getInvAvailableSlots(null)) {
            VoidStack bufferStack = this.voidStacks.get(slot);
            returnStack = bufferStack.getWrappedStack().copy();
            bufferStack.setWrappedStack(ItemStack.EMPTY);
            bufferStack.quantityStack = 0;
        }
        return returnStack;
    }

    @Override
    public ItemStack takeInvStack(int slotIndex, int itemQuantity) {
        ItemStack returnStack = null;
        for (int slot : this.getInvAvailableSlots(null)) {
            VoidStack bufferStack = this.voidStacks.get(slot);
            if (bufferStack.getWrappedStack().getCount() >= itemQuantity) {
                returnStack = bufferStack.wrapperStack.copy();
                bufferStack.wrapperStack.decrement(itemQuantity);
            }
        }
        return returnStack;
    }

    public ItemStack insertStack(ItemStack insertionStack) {
        for (int slot: this.getInvAvailableSlots(null)) {
            VoidStack bufferStack = this.voidStacks.get(slot);
            return bufferStack.insertStack(insertionStack);
        }
        return null;
    }

    @Override
    public boolean canInsertInvStack(int slot, ItemStack stack, @Nullable Direction direction) {
        VoidStack voidStack = voidStacks.get(slot);
        
        if (voidStack != null) {
            BufferResult result = voidStack.canInsertStack(stack);
            if (result == BufferResult.SUCCESS) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean canExtractInvStack(int slot, ItemStack stack, Direction direction) {
        VoidStack voidStack = voidStacks.get(slot);
        
        if (voidStack != null) {
            BufferResult result = voidStack.canExtractStack(stack);
            if (result == BufferResult.SUCCESS) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isInvEmpty() {
        Boolean full = false;
        for (int slot = 0; slot < this.getInvMaxSlotAmount().length; ++slot) {
            if (this.voidStacks.get(slot) != null) {
                VoidStack bufferStack = this.voidStacks.get(slot);
                if (bufferStack.getWrappedStack() != ItemStack.EMPTY) {
                    full = true;
                }
            } 
        }
        return full;
    }

    @Override
    public void clear() {
        // ...
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity playerEntity) {
        return true;
    }

    public void addListener(InventoryListener iventoryListener) {
        if (this.listeners == null) {
           this.listeners = Lists.newArrayList();
        }
  
        this.listeners.add(iventoryListener);
     }
  
     public void removeListener(InventoryListener inventoryListener) {
        this.listeners.remove(inventoryListener);
     }

    @Override
    public void markDirty() {
        if (this.listeners != null) {
            Iterator iterator = this.listeners.iterator();
   
            while(iterator.hasNext()) {
                InventoryListener inventoryListener = (InventoryListener)iterator.next();
                inventoryListener.onInvChange(this);
            }
        }
    }
}