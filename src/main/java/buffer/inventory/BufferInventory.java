package buffer.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;

import blue.endless.jankson.annotation.Nullable;
import buffer.utility.BufferUtility;
import buffer.utility.Tuple;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class BufferInventory implements SidedInventory {
    protected Integer bufferTier = 1;
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
            // TODO: Implement
            super.onClick(x, y, button);
        }
    }

    public class VoidStack {
        public int stackQuantity = 0;
        public int stackMaximum = getInvMaxStackAmount();

        private ItemStack wrapperStack = ItemStack.EMPTY;
        private Item wrapperItem;
        private CompoundTag wrapperTag = null;

        private ItemStack initialStack = ItemStack.EMPTY;

        public void setStack(ItemStack itemStack) {
            this.wrapperStack = itemStack;
            this.wrapperItem = itemStack.getItem();
            this.wrapperTag = itemStack.getTag();
        }

        public ItemStack getStack() {
            return this.wrapperStack;
        }

        public Item getItem() {
            return this.wrapperItem;
        }

        public CompoundTag getTag() {
            return this.wrapperTag;
        }

        public boolean canInsert(ItemStack itemStack) {
            if (wrapperStack.getCount() + itemStack.getCount() < stackMaximum 
            &&  wrapperStack.getItem() == itemStack.getItem()
            &&  wrapperStack.getTag() == itemStack.getTag()) {
                return true;
            } else {
                return false;
            }
        }

        public boolean canExtract(ItemStack itemStack) {
            if (itemStack.getCount() <= wrapperStack.getCount()
            &&  wrapperStack.getItem() == itemStack.getItem()
            &&  wrapperStack.getTag() == itemStack.getTag()) {
                return true;
            } else {
                return false;
            }
        }
        
        public ItemStack insertStack(ItemStack insertStack) {
            if (wrapperStack.getItem() == Items.AIR) {
                this.setStack(insertStack.copy());
                if (insertStack.hasTag()) {
                    this.wrapperTag = insertStack.getTag();
                    this.wrapperStack.setTag(wrapperTag);
                }
                return ItemStack.EMPTY;
            } else  if (wrapperStack.getItem() != insertStack.getItem()) {
                return insertStack;
            }

            int wrapperQuantity = this.wrapperStack.getCount();
            int insertQuantity = insertStack.getCount();
            int totalQuantity = stackQuantity + wrapperQuantity;

            int insertMaximum = insertStack.getMaxCount();

            this.stackMaximum = getInvMaxStackAmount() + wrapperStack.getMaxCount();

            if (totalQuantity + insertQuantity <= stackMaximum) {
                this.stackQuantity += insertQuantity;
                insertStack.decrement(insertQuantity);
            }

            else if (totalQuantity + insertQuantity > stackMaximum) {
                int differenceQuantity = (totalQuantity + insertQuantity) - stackMaximum;
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

        public Boolean restockStack(Boolean isInitial) {
            int wrapperQuantity = this.wrapperStack.getCount();

            if (this.wrapperStack.getCount() == 0 && this.stackQuantity > 0) {
                if (!isInitial) {
                    wrapperItem = initialStack.getItem();
                }
            } else if (this.wrapperStack.getCount() > 0 && this.stackQuantity > 0) {    
                if (!isInitial) {
                    this.initialStack = wrapperStack.copy();
                    wrapperItem = wrapperStack.getItem();
                    if (wrapperStack.hasTag()) {
                        wrapperTag = wrapperStack.getTag();
                    }

                }
            }

            if (wrapperQuantity >= 0 && stackQuantity > 0) {
                wrapperQuantity = this.wrapperStack.getCount();
                int differenceQuantity = this.wrapperStack.getMaxCount() - wrapperQuantity;
                if (this.stackQuantity >= differenceQuantity) {
                    this.setStack(new ItemStack(wrapperItem, wrapperQuantity + differenceQuantity));
                    this.wrapperStack.setTag(wrapperTag);
                    this.stackQuantity -= differenceQuantity;
                } else {
                    this.setStack(new ItemStack(wrapperItem, wrapperQuantity + stackQuantity));
                    this.wrapperStack.setTag(wrapperTag);
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

        public void clear() {
            this.stackQuantity = 0;
            this.stackMaximum = 0;
            this.wrapperItem = null;
            this.wrapperStack = null;
            this.wrapperTag = null;
        }
    }

    public void restockAll() {
        for (VoidStack voidStack : this.voidStacks) {
            voidStack.restockStack(false);
        }
    }

    @Nullable
    public BufferInventory(Integer tier) {
        if (tier == null) {
            this.setTier(1);            
        } else {
            this.setTier(tier);
        }
    }

    public void setTier(Integer tier) {
        this.bufferTier = tier;
        for (int bufferSlot = 0; bufferSlot < getInvMaxSlotAmount(); ++bufferSlot) {
            if (voidStacks.size() - 1 < bufferSlot) {
                voidStacks.add(new VoidStack());
            }
        }
    }

    public Integer getTier() {
        return this.bufferTier;
    }

    public VoidStack getSlot(int bufferSlot) {
        if (voidStacks.size() - 1 >= bufferSlot) {
            return voidStacks.get(bufferSlot);
        } else {
            return null;
        }
    }
    
    public Integer getStored(int bufferSlot) {
        VoidStack bufferStack = getSlot(bufferSlot);
        if (bufferStack != null) {
            return bufferStack.getStored();
        } else {
            return null;
        }

    }

    @Override
    public int getInvSize() {
        return getInvMaxSlotAmount() - 1;
    }

    @Override
    public ItemStack getInvStack(int bufferSlot) {
        VoidStack bufferStack = getSlot(bufferSlot);
        if (bufferStack != null) {
            return bufferStack.getStack();
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInvStack(int bufferSlot, ItemStack itemStack) {
        VoidStack bufferStack = getSlot(bufferSlot);
        if (bufferStack != null) {
            bufferStack.clear();
            bufferStack.setStack(itemStack);
        }
    }

    @Override
    public ItemStack takeInvStack(int bufferSlot, int itemQuantity) {
        VoidStack bufferStack = getSlot(bufferSlot);
        if (bufferStack != null) { 
            if (bufferStack.getStack().getCount() >= itemQuantity) {
                ItemStack returnStack = new ItemStack(bufferStack.getStack().getItem(), itemQuantity);
                bufferStack.wrapperStack.decrement(itemQuantity);
                return returnStack;
            } else {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack removeInvStack(int bufferSlot) {
        if (voidStacks.size() <= bufferSlot && bufferSlot >= 0) {
            ItemStack returnStack = voidStacks.get(bufferSlot).getStack();
            voidStacks.get(bufferSlot).setStack(ItemStack.EMPTY);
            return returnStack;
        } else {
            return ItemStack.EMPTY;
        }
    }
    

    public int getInvMaxSlotAmount() {
        return bufferTier;
    }

    public int getInvMaxStackAmount() {
        return BufferUtility.getStackSize(bufferTier);
    }

    @Override
    public int[] getInvAvailableSlots(Direction direction) {
        return IntStream.rangeClosed(0, bufferTier - 1).toArray();
    }   

    // -1 = NO SLOT
    //  0 = EMPTY SLOT
    // +1 = MATCHING SLOT
    public Tuple<Integer, Integer> tryInsert(ItemStack insertionStack) {
        Tuple<Integer, Integer> insertionMode = new Tuple<Integer, Integer>(-1, null);
        for (int slot : this.getInvAvailableSlots(null)) {
            VoidStack bufferStack = this.voidStacks.get(slot);
            if (insertionStack.getItem() == bufferStack.getStack().getItem()) {
                if (insertionStack.hasTag() && bufferStack.getStack().hasTag()
                &&  insertionStack.getTag().equals(bufferStack.getStack().getTag())) {
                    insertionMode.setFirst(+1);
                    insertionMode.setSecond(slot);
                    break;
                }
                if (!insertionStack.hasTag() && !bufferStack.getStack().hasTag()) {
                    insertionMode.setFirst(+1);
                    insertionMode.setSecond(slot);
                    break;
                }
            }
            if (bufferStack.getStack().isEmpty()) {
                insertionMode.setFirst(0);
                insertionMode.setSecond(slot);
            }
        }
        return insertionMode;
    }

    public ItemStack insertStack(ItemStack insertionStack) {
        Tuple<Integer, Integer> insertionData = this.tryInsert(insertionStack);
        if (insertionData.getFirst() == -1) {
            return insertionStack;
        }
        if (insertionData.getFirst() == 0 || insertionData.getFirst() == +1) {
            VoidStack voidStack = this.getSlot(insertionData.getSecond());
            return voidStack.insertStack(insertionStack);
        }
        return insertionStack;
    }

    @Override
    public boolean canInsertInvStack(int bufferSlot, ItemStack itemStack, @Nullable Direction direction) {
        VoidStack bufferStack = getSlot(bufferSlot);
        return bufferStack.canInsert(itemStack);
    }

    @Override
    public boolean canExtractInvStack(int bufferSlot, ItemStack itemStack, Direction direction) {
        VoidStack bufferStack = getSlot(bufferSlot);
        return bufferStack.canInsert(itemStack);
    }

    @Override
    public boolean isInvEmpty() {
        Boolean isEmpty = true;
        for (VoidStack bufferStack : this.voidStacks) {
            if (bufferStack.getStored() > 0) {
                isEmpty = false;
            }
        }
        return isEmpty;
    }

    @Override
    public void clear() {
        // TODO: Implement
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

    public static CompoundTag toTag(BufferInventory bufferInventory, CompoundTag bufferTag) {
        bufferTag.putInt("tier", bufferInventory.getTier());
        for (int bufferSlot : bufferInventory.getInvAvailableSlots(null)) {
            VoidStack voidStack = bufferInventory.getSlot(bufferSlot);
            bufferTag.putInt(Integer.toString(bufferSlot), voidStack.stackQuantity);
            bufferTag.putInt(Integer.toString(bufferSlot) + "_size", voidStack.getStack().getCount());
            if (voidStack.wrapperTag != null) {
                bufferTag.put(Integer.toString(bufferSlot) + "_tag", voidStack.wrapperTag.copy());
            }
            bufferTag.putString(Integer.toString(bufferSlot) + "_item", voidStack.getStack().getItem().toString());
        }
        return bufferTag;
    }

    public static BufferInventory fromTag(CompoundTag bufferTag) {
        BufferInventory bufferInventory = new BufferInventory(null);
        bufferInventory.setTier(bufferTag.getInt("tier"));
        for (int bufferSlot : bufferInventory.getInvAvailableSlots(null)) {
            VoidStack voidStack = bufferInventory.getSlot(bufferSlot);
            voidStack.stackQuantity = bufferTag.getInt(Integer.toString(bufferSlot));
            Integer wrapperQuantity = bufferTag.getInt(Integer.toString(bufferSlot) + "_size");
            voidStack.wrapperItem = Registry.ITEM.get(new Identifier(bufferTag.getString(Integer.toString(bufferSlot) + "_item")));
            ItemStack itemStack = new ItemStack(voidStack.wrapperItem, wrapperQuantity);
            if (bufferTag.containsKey(Integer.toString(bufferSlot) + "_slot")) {
                voidStack.wrapperTag = (CompoundTag)bufferTag.getTag(Integer.toString(bufferSlot) + "_tag");
                itemStack.setTag(voidStack.wrapperTag);
            }
            voidStack.setStack(itemStack.copy());
            voidStack.restockStack(true);
        }
        return bufferInventory;
    }
}