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

    public ItemStack itemStack = null;


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
        int stackQuantity = 0;
        int totalMaximum = getInvMaxStackAmount();

        ItemStack wrapperStack = ItemStack.EMPTY;
        ItemStack previousStack = ItemStack.EMPTY;

        Item slotItem;

        public VoidStack() {
            // ...
        }

        public void setWrappedStack(ItemStack stack) {
            this.wrapperStack = stack.copy();
        }

        public ItemStack getWrappedStack() {
            return this.wrapperStack.copy();
        }

        public void setPreviousStack(ItemStack stack) {
            this.previousStack = stack.copy();
        }

        public ItemStack getPreviousStack() {
            return this.previousStack;
        }

        public BufferResult canInsertStack(ItemStack stack) {
            if (this.wrapperStack.getCount() + stack.getCount() < this.totalMaximum && this.wrapperStack.getItem() == stack.getItem()) {
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

        public ItemStack insertStack(ItemStack insertStack) {
            if (wrapperStack.getItem() == Items.AIR) {
                this.setWrappedStack(insertStack.copy());
                return ItemStack.EMPTY;
            } else  if (wrapperStack.getItem() != insertStack.getItem()) {
                return ItemStack.EMPTY;
            }

            int wrapperQuantity = this.wrapperStack.getCount();
            int insertQuantity = insertStack.getCount();
            int totalQuantity = stackQuantity + wrapperQuantity;

            int insertMaximum = insertStack.getMaxCount();

            this.totalMaximum = getInvMaxStackAmount() + wrapperStack.getMaxCount();

            if (totalQuantity + insertQuantity <= totalMaximum) {
                this.stackQuantity += insertQuantity;
                insertStack.decrement(insertQuantity);
            }

            else if (totalQuantity + insertQuantity > totalMaximum) {
                int differenceQuantity = (totalQuantity + insertQuantity) - totalMaximum;
                int offsetQuantity = insertMaximum - differenceQuantity;
                this.stackQuantity += offsetQuantity;
                insertStack.decrement(offsetQuantity);
            }

            if (insertStack.getCount() == 0) {
                return ItemStack.EMPTY;
            } else {
                return insertStack;
            }
        }

        public Boolean restockStack(BufferController controller) {
            int wrapperQuantity = this.wrapperStack.getCount();

            Item wrapperItem = null;

            if (this.wrapperStack.getCount() == 0 && this.stackQuantity > 0) {
                wrapperItem = previousStack.getItem();
            } else if (this.wrapperStack.getCount() > 0 && this.stackQuantity > 0) {    
                this.previousStack = wrapperStack.copy();
                wrapperItem = wrapperStack.getItem();
            }
            
            //if (this.wrapperStack.getCount() == 0 && this.stackQuantity > 0 || this.wrapperStack.isEmpty() && this.stackQuantity > 0) {
            //    this.wrapperStack.increment(1);
            //    wrapperItem = this.wrapperStack.getItem();
            //    this.wrapperStack.decrement(1);
            //} else {
            //    wrapperItem = this.wrapperStack.getItem();
            //}

            if (wrapperQuantity >= 0 && stackQuantity > 0) {
                int differenceQuantity = this.wrapperStack.getMaxCount() - wrapperQuantity;
                if (this.stackQuantity >= differenceQuantity) {
                    this.setWrappedStack(new ItemStack(wrapperItem, wrapperQuantity + differenceQuantity));
                    this.stackQuantity -= differenceQuantity;
                } else {
                    this.setWrappedStack(new ItemStack(wrapperItem, wrapperQuantity + stackQuantity));
                    this.stackQuantity -= stackQuantity;
                }
                return true;
            } else if (stackQuantity == 0 && wrapperQuantity == 0) {
                this.wrapperStack = ItemStack.EMPTY;
                return false;
            } else {
                return false;
            }
        }

        public int getStored() {
            return this.stackQuantity + this.wrapperStack.getCount();
        }
    }

    public void restockAll(BufferController controller) {
        for (VoidStack voidStack : this.voidStacks) {
            voidStack.restockStack(controller);
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

    @Override
    public ItemStack getInvStack(int slot) {
        ItemStack returnStack = ItemStack.EMPTY;

        if (this.voidStacks.get(slot) != null) {
            returnStack = this.voidStacks.get(slot).getWrappedStack().copy();
        }

        return returnStack;
    }

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
            bufferStack.stackQuantity = 0;
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
        for (int slot : this.getInvAvailableSlots(null)) {
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