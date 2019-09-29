package buffer.inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;

import blue.endless.jankson.annotation.Nullable;
import buffer.utility.BufferTier;
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
    public List<BufferStack> bufferStacks = new ArrayList<>();
    protected List<InventoryListener> listeners;

    public ItemStack itemStack = null;

    public Integer selectedSlot = 0;

    public Boolean isVoid = false;

    public Boolean isPickup = false;

    public static String TIER_RETRIEVER() {
        return "tier";
    }

    public static String SELECTED_SLOT_RETRIEVER() {
        return "selected_slot";
    }

    public static String VOID_RETRIEVER() {
        return "void";
    }

    public static String PICKUP_RETRIEVER() {
        return "pickup";
    }


    public static String STACK_RETRIEVER(Integer integer) {
        return Integer.toString(integer);
    }

    public static String SIZE_RETRIEVER(Integer integer) {
        return Integer.toString(integer) + "_size";
    }

    public static String TAG_RETRIEVER(Integer integer) {
        return Integer.toString(integer) + "_tag";
    }

    public static String ITEM_RETRIEVER(Integer integer) {
        return Integer.toString(integer) + "_item";
    }



    public class WBufferSlot extends WItemSlot {
        protected int bufferSlot = 0;
        protected PlayerInventory playerInventory = null;

        public WBufferSlot(Inventory inventory, int temporaryIndex, int slotsWide, int slotsHigh, PlayerInventory temporaryInventory) {
            super(inventory, temporaryIndex, slotsWide, slotsHigh, false, false);
            bufferSlot = temporaryIndex;
            playerInventory = temporaryInventory;
        }

        @Override
        public void onClick(int x, int y, int button) {
            super.onClick(x, y, button);
        }
    }

    public class BufferStack {
        public int stackQuantity = 0;
        public int stackMaximum = getInvMaxStackAmount();

        private ItemStack wrapperStack = ItemStack.EMPTY;
        private Item wrapperItem;
        private CompoundTag wrapperTag = null;

        private ItemStack initialStack = ItemStack.EMPTY;

        public void setStack(ItemStack itemStack) {
            this.wrapperStack = itemStack.copy();
            this.wrapperTag = itemStack.getTag();
        }

        public ItemStack getStack() {
            return this.wrapperStack;
        }

        public Item getItem() {
            return this.wrapperItem;
        }

        public void setTag(CompoundTag itemTag) {
            this.wrapperTag = itemTag;
            this.wrapperStack.setTag(itemTag);
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
            } else if (wrapperStack.getTag() != insertStack.getTag()) {
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
                if (isVoid) {
                    insertStack = ItemStack.EMPTY;
                } else {
                    insertStack.decrement(offsetQuantity);
                }
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

    public void swapPickup() {
        this.isPickup = !this.isPickup;
    }

    public void swapVoid() {
        this.isVoid = !this.isVoid;
    }

    public void swapSlot() {
        if (selectedSlot < this.getTier() - 1) {
            ++selectedSlot;
        }
        else {
            selectedSlot = -1;
        }
    }

    public void restockAll() {
        for (BufferStack bufferStack : this.bufferStacks) {
            bufferStack.restockStack(false);
        }
    }

    public void restockInitial() {
        for (BufferStack bufferStack : this.bufferStacks) {
            bufferStack.restockStack(true);
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
            if (bufferStacks.size() - 1 < bufferSlot) {
                bufferStacks.add(new BufferStack());
            }
        }
    }

    public Integer getTier() {
        return this.bufferTier;
    }

    public BufferStack getSlot(int bufferSlot) {
        if (bufferStacks.size() - 1 >= bufferSlot) {
            return bufferStacks.get(bufferSlot);
        } else {
            return null;
        }
    }
    
    public Integer getStored(int bufferSlot) {
        BufferStack bufferStack = getSlot(bufferSlot);
        if (bufferStack != null) {
            return bufferStack.getStored();
        } else {
            return null;
        }
    }
    
    public Integer getStoredInternally(int bufferSlot) {
        BufferStack bufferStack = getSlot(bufferSlot);
        if (bufferStack != null) {
            return bufferStack.stackQuantity;
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
        BufferStack bufferStack = getSlot(bufferSlot);
        if (bufferStack != null) {
            return bufferStack.getStack();
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInvStack(int bufferSlot, ItemStack itemStack) {
        BufferStack bufferStack = getSlot(bufferSlot);
        if (bufferStack != null) {
            bufferStack.setStack(itemStack);
        }
    }

    @Override
    public ItemStack takeInvStack(int bufferSlot, int itemQuantity) {
        BufferStack bufferStack = getSlot(bufferSlot);
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
        if (bufferStacks.size() <= bufferSlot && bufferSlot >= 0) {
            ItemStack returnStack = bufferStacks.get(bufferSlot).getStack();
            bufferStacks.get(bufferSlot).setStack(ItemStack.EMPTY);
            return returnStack;
        } else {
            return ItemStack.EMPTY;
        }
    }
    

    public int getInvMaxSlotAmount() {
        return bufferTier;
    }

    public int getInvMaxStackAmount() {
        return BufferTier.getStackSize(bufferTier);
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
            BufferStack bufferStack = this.bufferStacks.get(slot);
            if (insertionStack.getItem() == bufferStack.getStack().getItem()) {
                if (insertionStack.hasTag() && bufferStack.getStack().hasTag()
                &&  insertionStack.getTag().equals(bufferStack.getStack().getTag())
                ||  !insertionStack.hasTag() && !bufferStack.getStack().hasTag()) {
                    insertionMode.first = +1;
                    insertionMode.second = slot;
                    break;
                }
            }
            if (bufferStack.getStack().isEmpty()) {
                insertionMode.first = 0;
                insertionMode.second = slot;
            }
        }
        return insertionMode;
    }

    public ItemStack insertStack(ItemStack insertionStack) {
        Tuple<Integer, Integer> insertionData = this.tryInsert(insertionStack);
        if (insertionData.first == -1) {
            return insertionStack;
        }
        if (insertionData.first == +1) {
            BufferStack bufferStack = this.getSlot(insertionData.second);
            insertionStack = bufferStack.insertStack(insertionStack);
        }
        if (insertionData.first == 0) {
            BufferStack bufferStack = this.getSlot(insertionData.second);
            bufferStack.insertStack(insertionStack);
            bufferStack.restockStack(true);
            insertionStack = ItemStack.EMPTY;
        }
        return insertionStack;
    }

    @Override
    public boolean canInsertInvStack(int bufferSlot, ItemStack itemStack, @Nullable Direction direction) {
        BufferStack bufferStack = getSlot(bufferSlot);
        return bufferStack.canInsert(itemStack);
    }

    @Override
    public boolean canExtractInvStack(int bufferSlot, ItemStack itemStack, Direction direction) {
        BufferStack bufferStack = getSlot(bufferSlot);
        return bufferStack.canInsert(itemStack);
    }

    @Override
    public boolean isInvEmpty() {
        boolean isEmpty = true;
        for (BufferStack bufferStack : this.bufferStacks) {
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
            Iterator<InventoryListener> iterator = this.listeners.iterator();
            while(iterator.hasNext()) {
                InventoryListener inventoryListener = iterator.next();
                inventoryListener.onInvChange(this);
            }
        }
    }

    public static CompoundTag toTag(BufferInventory bufferInventory, CompoundTag bufferTag) {
        bufferInventory.restockAll();
        bufferTag.putInt(TIER_RETRIEVER(), bufferInventory.getTier());
        bufferTag.putInt(SELECTED_SLOT_RETRIEVER(), bufferInventory.selectedSlot);
        bufferTag.putBoolean(PICKUP_RETRIEVER(), bufferInventory.isPickup);
        bufferTag.putBoolean(VOID_RETRIEVER(), bufferInventory.isVoid);
        for (int bufferSlot : bufferInventory.getInvAvailableSlots(null)) {
            BufferStack bufferStack = bufferInventory.getSlot(bufferSlot);
            bufferTag.putInt(STACK_RETRIEVER(bufferSlot), bufferStack.stackQuantity);
            bufferTag.putInt(SIZE_RETRIEVER(bufferSlot), bufferStack.getStack().getCount());
            bufferTag.putString(ITEM_RETRIEVER(bufferSlot), bufferStack.getStack().getItem().toString());
            if (bufferStack.getStack().hasTag()) {
                bufferTag.put(TAG_RETRIEVER(bufferSlot), bufferStack.wrapperTag.copy());
            }
        }
        return bufferTag;
    }

    public static BufferInventory fromTag(CompoundTag bufferTag) {
        BufferInventory bufferInventory = new BufferInventory(null);
        if (bufferTag != null) {
            bufferInventory.setTier(bufferTag.getInt(TIER_RETRIEVER()));
            bufferInventory.isPickup = bufferTag.getBoolean(PICKUP_RETRIEVER());
            bufferInventory.isVoid = bufferTag.getBoolean(VOID_RETRIEVER());
            bufferInventory.selectedSlot = bufferTag.getInt(SELECTED_SLOT_RETRIEVER());
            for (int bufferSlot : bufferInventory.getInvAvailableSlots(null)) {
                BufferStack bufferStack = bufferInventory.getSlot(bufferSlot);
                bufferStack.stackQuantity = bufferTag.getInt(STACK_RETRIEVER(bufferSlot));
                int wrapperQuantity = bufferTag.getInt(SIZE_RETRIEVER(bufferSlot));
                bufferStack.wrapperItem = Registry.ITEM.get(new Identifier(bufferTag.getString(ITEM_RETRIEVER(bufferSlot))));
                ItemStack itemStack = new ItemStack(bufferStack.wrapperItem, wrapperQuantity);
                if (bufferTag.containsKey(TAG_RETRIEVER(bufferSlot))) {
                    bufferStack.wrapperTag = (CompoundTag)bufferTag.getTag(TAG_RETRIEVER(bufferSlot));
                    itemStack.setTag(bufferStack.wrapperTag);
                }
                bufferStack.setStack(itemStack.copy());
                bufferStack.restockStack(true);
            }
        }
        return bufferInventory;
    }
}