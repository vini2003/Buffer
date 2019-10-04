package buffer.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;

import blue.endless.jankson.annotation.Nullable;
import buffer.item.BufferItem;
import buffer.registry.ItemRegistry;
import buffer.utility.BufferTier;
import buffer.utility.Tuple;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

/**
 * BufferInventory implementation of SidedInventory which implements base methods and general
 * wrapping capabilities to allow large compatibility with most mods.
 */
public class BufferInventory implements SidedInventory {
    public List<BufferStack> bufferStacks = new ArrayList<>();
    protected List<InventoryListener> listeners;
    protected int bufferTier = 1;

    public int selectedSlot = -1;

    public boolean isVoid = false;
    public boolean isPickup = false;

    public static final String TIER_RETRIEVER = "tier";
    public static final String SELECTED_SLOT_RETRIEVER = "selected_slot";
    public static final String VOID_RETRIEVER = "void";
    public static final String PICKUP_RETRIEVER = "pickup";

    /**
     * Constructor which defines a BufferInventory by the given tier.
     * @param bufferTier Tier to set BufferInventory to.
     */
    public BufferInventory(int bufferTier) {
        this.setTier(bufferTier);
    }

    /**
     * Get a retriever for CompoundTag's 'stackQuantity' field.
     * @param integer Tier to get retriver for.
     * @return String to retrieve requested NBT data.
     */
    public static String STACK_RETRIEVER(int integer) {
        return Integer.toString(integer);
    }

    /**
     * Get a retriever for CompoundTag's 'wrapperQuantity' field.
     * @param integer Tier to get retriver for.
     * @return String to retrieve requested NBT data.
     */
    public static String SIZE_RETRIEVER(int integer) {
        return integer + "_size";
    }

    /**
     * Get a retriever for CompoundTag's 'wrapperTag' field.
     * @param integer Tier to get retriver for.
     * @return String to retrieve requested NBT data.
     */
    public static String TAG_RETRIEVER(int integer) {
        return integer + "_tag";
    }

    /**
     * Get a retriever for CompoundTag's 'wrapperItem' field.
     * @param integer Tier to get retriver for.
     * @return String to retrieve requested NBT data.
     */
    public static String ITEM_RETRIEVER(int integer) {
        return integer + "_item";
    }

    /**
     * Custom wrapper for BufferInventory's stacks, to be as compatible as possible with everything else.
     */
    public class BufferStack {
        public int stackQuantity = 0;
        public int stackMaximum = getInvMaxStackAmount();

        private ItemStack wrapperStack = ItemStack.EMPTY;
        private Item wrapperItem;
        private CompoundTag wrapperTag = null;

        private ItemStack initialStack = ItemStack.EMPTY;

        /**
         * Set new 'wrapperStack', which also updates 'wrapperTag'.
         * @param itemStack ItemStack to set 'wrapperStack' to.
         */
        public void setStack(ItemStack itemStack) {
            this.wrapperStack = itemStack;
            setTag(wrapperStack.getTag());
        }

        /**
         * Returns 'wrappedStack'.
         * @return ItemStack of 'wrapperStack'.
         */
        public ItemStack getStack() {
            if (this.wrapperStack == null) {
                return ItemStack.EMPTY;
            } else {
                return this.wrapperStack;
            }
        }

        /**
         * Returns 'wrapperItem'.
         * @return Item of 'wrapperItem'.
         */
        public Item getItem() {
            return this.wrapperStack.getItem();
        }

        /**
         * Set new 'wrapperTag'.
         * @param itemTag CompoundTag to set 'wrapperTag' to.
         */
        public void setTag(CompoundTag itemTag) {
            this.wrapperTag = itemTag;
        }

        /**
         * Checks if 'wrapperTag' exists.
         * @return true if it exists, false if it doesn't.
         */
        public boolean hasTag() {
            return this.wrapperTag == null ? false : true;
        }

        /**
         * Returns 'wrapperTag'.
         * @return CompoundTag of 'wrapperTag'.
         */
        public CompoundTag getTag() {
            return this.wrapperTag;
        }

        /**
         * Checks if provided ItemStack can be inserted.
         * @param itemStack ItemStack to check insertion status for.
         * @return true if can insert, false if can't.
         */
        public boolean canInsert(ItemStack itemStack) {
            return wrapperStack.getCount() + itemStack.getCount() < stackMaximum
                    && wrapperStack.getItem() == itemStack.getItem()
                    && wrapperStack.getTag() == itemStack.getTag();
        }

        /**
         * Checks if provided ItemStack can be extracted.
         * @param itemStack ItemStack to check extraction status for.
         * @return true if can extract, false if can't.
         */
        public boolean canExtract(ItemStack itemStack) {
            return itemStack.getCount() <= wrapperStack.getCount()
                    && wrapperStack.getItem() == itemStack.getItem()
                    && wrapperStack.getTag() == itemStack.getTag();
        }

        /**
         * Insert ItemStack into BufferStack.
         * @param insertStack ItemStack to insert.
         * @return ItemStack remaining after insertion.
         */
        public ItemStack insertStack(ItemStack insertStack) {
            if (insertStack.getItem() == ItemRegistry.BUFFER_ITEM) {
                return insertStack;
            }
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

            this.stackMaximum = getInvMaxStackAmount() + wrapperStack.getMaxCount() + (64 - wrapperStack.getMaxCount());

            if (totalQuantity + insertQuantity <= stackMaximum) {
                this.stackQuantity += insertQuantity;
                insertStack.decrement(insertQuantity);
            } else if (totalQuantity + insertQuantity > stackMaximum) {
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

        /**
         * Restock BufferStack's 'wrapperStack'.
         * @param isInitial Boolean of 'is initial insertion'. If true, update 'initialStack', else, base operations on it.
         */
        public void restock(Boolean isInitial) {
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
                int differenceQuantity;
                if (!isInitial) {
                    differenceQuantity = this.initialStack.getMaxCount() - wrapperQuantity;
                } else {
                    differenceQuantity = this.wrapperStack.getMaxCount() - wrapperQuantity;
                }

                if (this.stackQuantity >= differenceQuantity) {
                    this.setStack(new ItemStack(wrapperItem, wrapperQuantity + differenceQuantity));
                    this.wrapperStack.setTag(wrapperTag);
                    this.stackQuantity -= differenceQuantity;
                } else {
                    this.setStack(new ItemStack(wrapperItem, wrapperQuantity + stackQuantity));
                    this.wrapperStack.setTag(wrapperTag);
                    this.stackQuantity -= stackQuantity;
                }
            } else if (stackQuantity == 0 && wrapperQuantity == 0) {
                this.wrapperStack = ItemStack.EMPTY;
            }
        }

        /**
         * Returns BufferStack's total stored amount.
         * @return Integer with total stored amount.
         */
        public int getStored() {
            return this.stackQuantity + this.wrapperStack.getCount();
        }
    }

    /**
     * Invert pickup mode.
     */
    public void swapPickup() {
        this.isPickup = !this.isPickup;
    }

    /**
     * Invert void mode.
     */
    public void swapVoid() {
        this.isVoid = !this.isVoid;
    }

    /**
     * Switch selected slot.
     */
    public void swapSlot() {
        if (selectedSlot < this.getTier() - 1) {
            ++selectedSlot;
        }
        else {
            selectedSlot = -1;
        }
    }

    /**
     * Restock all BufferStacks of BufferInventory.
     */
    public void restockAll() {
        for (BufferStack bufferStack : this.bufferStacks) {
            bufferStack.restock(false);
        }
    }

    /**
     * Sets 'bufferTier' to given tier, adding BufferStacks when needed.
     * @param bufferTier Tier to set 'bufferTier' to.
     */
    public void setTier(int bufferTier) {
        this.bufferTier = bufferTier;
        for (int bufferSlot = 0; bufferSlot < getInvMaxSlotAmount(); ++bufferSlot) {
            if (bufferStacks.size() - 1 < bufferSlot) {
                bufferStacks.add(new BufferStack());
            }
        }
    }

    /**
     * Returns BufferInventory's 'bufferTier'.
     * @return Tier of BufferInventory.
     */
    public int getTier() {
        return this.bufferTier;
    }

    /**
     * Returns BufferStack of given slot.
     * @param bufferSlot Slot to get BufferStack of.
     * @return Retrieved BufferStack.
     */
    public BufferStack getSlot(int bufferSlot) {
        if (bufferStacks.size() - 1 >= bufferSlot) {
            return bufferStacks.get(bufferSlot);
        } else {
            return null;
        }
    }

    /**
     * Returns BufferStack of given slot's total stored amount.
     * @param bufferSlot Slot to get BufferStack of.
     * @return Retrieved total stored amount.
     */
    public int getStored(int bufferSlot) {
        BufferStack bufferStack = getSlot(bufferSlot);
        if (bufferStack != null) {
            return bufferStack.getStored();
        } else {
            return 0;
        }
    }

    /**
     * Returns the maximum amount of slots the BufferInventory can hold.
     * @return Integer of maximum amount of slots.
     */
    public int getInvMaxSlotAmount() {
        return bufferTier;
    }

    /**
     * Returns the maximum amount of items the BufferInventory's BufferStacks can hold.
     * @return Integer of maximum amount of items.
     */
    public int getInvMaxStackAmount() {
        return BufferTier.getStackSize(bufferTier);
    }


    /**
     * Check if stack can be inserted into the BufferInventory.
     * @param insertionStack ItemStack to check with.
     * @return Where x is slot number:
     * <ul>
     *  <li> Tuple<0, x>  if an empty slot can hold it. </li>
     *  <li> Tuple<-1, x> if no slots can hold it. </li>
     *  <li> Tuple<+1, x>  if a slot of the same item can hold it. </li>
     * </ul>
     */
    public Tuple<Integer, Integer> tryInsert(ItemStack insertionStack) {
        Tuple<Integer, Integer> insertionMode = new Tuple<>(-1, null);
        for (int slot : this.getInvAvailableSlots(null)) {
            BufferStack bufferStack = this.bufferStacks.get(slot);
            if (insertionStack.getItem() == bufferStack.getItem()) {
                if (insertionStack.hasTag() && bufferStack.hasTag()
                &&  insertionStack.getTag().equals(bufferStack.getTag())
                ||  !insertionStack.hasTag() && !bufferStack.hasTag()) {
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

    /**
     * Insert ItemStack into matching BufferStack in BufferInventory.
     * @param insertStack ItemStack to insert.
     * @return ItemStack remaining after insertion.
     */
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
            insertionStack = bufferStack.insertStack(insertionStack);
            bufferStack.restock(true);
        }
        return insertionStack;
    }

        /**
     * Implementation for vanilla 'addListener'.
     * @param inventoryListener InventoryListener to add to 'listeners'.
     */
    public void addListener(InventoryListener inventoryListener) {
        if (this.listeners == null) {
           this.listeners = Lists.newArrayList();
        }
        this.listeners.add(inventoryListener);
     }

    /**
     * Implementation for 'removeListener'.
     * @param inventoryListener InventoryListener to remove from 'listeners'.
     */
    public void removeListener(InventoryListener inventoryListener) {
       this.listeners.remove(inventoryListener);
    }


    /**
     * Serializes BufferInventory data to a CompoundTag.
     * @param bufferInventory BufferInventory to serialize.
     * @param bufferTag CompoundTag to append data to.
     * @return Resulting CompoundTag.
     */
    public static CompoundTag toTag(BufferInventory bufferInventory, CompoundTag bufferTag) {
        bufferTag.putInt(TIER_RETRIEVER, bufferInventory.getTier());
        bufferTag.putInt(SELECTED_SLOT_RETRIEVER, bufferInventory.selectedSlot);
        bufferTag.putBoolean(PICKUP_RETRIEVER, bufferInventory.isPickup);
        bufferTag.putBoolean(VOID_RETRIEVER, bufferInventory.isVoid);
        for (int bufferSlot : bufferInventory.getInvAvailableSlots(null)) {
            BufferStack bufferStack = bufferInventory.getSlot(bufferSlot);
            bufferTag.putInt(STACK_RETRIEVER(bufferSlot), bufferStack.stackQuantity);
            bufferTag.putInt(SIZE_RETRIEVER(bufferSlot), bufferStack.getStack().getCount());
            bufferTag.putString(ITEM_RETRIEVER(bufferSlot), bufferStack.getItem().toString());
            if (bufferStack.getStack().hasTag()) {
                bufferTag.put(TAG_RETRIEVER(bufferSlot), bufferStack.wrapperTag.copy());
            }
        }
        return bufferTag;
    }

    /**
     * Deserializes CompoundTag.
     * @param bufferTag CompoundTag to deserialize.
     * @return Resulting BufferInventory.
     */
    public static BufferInventory fromTag(CompoundTag bufferTag) {
        BufferInventory bufferInventory = new BufferInventory(1);
        if (bufferTag != null) {
            bufferInventory.setTier(bufferTag.getInt(TIER_RETRIEVER));
            bufferInventory.isPickup = bufferTag.getBoolean(PICKUP_RETRIEVER);
            bufferInventory.isVoid = bufferTag.getBoolean(VOID_RETRIEVER);
            bufferInventory.selectedSlot = bufferTag.getInt(SELECTED_SLOT_RETRIEVER);
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
                bufferStack.restock(true);
            }
        }
        return bufferInventory;
    }

    /**
     * Wrapper for vanilla's 'getInvSize'.
     */
    @Override
    public int getInvSize() {
        return getInvMaxSlotAmount() - 1;
    }

    /**
     * Wrapper for vanilla's 'getInvStack'.
     */
    @Override
    public ItemStack getInvStack(int bufferSlot) {
        BufferStack bufferStack = getSlot(bufferSlot);
        if (bufferStack != null) {
            return bufferStack.getStack();
        } else {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Wrapper for vanilla's 'setInvStack'.
     */
    @Override
    public void setInvStack(int bufferSlot, ItemStack itemStack) {
        BufferStack bufferStack = getSlot(bufferSlot);
        if (bufferStack != null) {
            bufferStack.setStack(itemStack);
        }
    }

    /**
     * Wrapper for vanilla's 'takeInvStack'.
     */
    @Override
    public ItemStack takeInvStack(int bufferSlot, int itemQuantity) {
        BufferStack bufferStack = getSlot(bufferSlot);
        if (bufferStack != null) {
            if (bufferStack.getStack().getCount() >= itemQuantity) {
                ItemStack returnStack = new ItemStack(bufferStack.getItem(), itemQuantity);
                bufferStack.wrapperStack.decrement(itemQuantity);
                return returnStack;
            } else {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Wrapper for vanilla's 'removeInvStack'.
     */
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

    /**
     * Wrapper for vanilla's 'getInvAvailableSlots'.
     */
    @Override
    public int[] getInvAvailableSlots(Direction direction) {
        return IntStream.rangeClosed(0, bufferTier - 1).toArray();
    }

    /**
     * Wrapper for vanilla 'canInsertInvStack'.
     */
    @Override
    public boolean canInsertInvStack(int bufferSlot, ItemStack itemStack, @Nullable Direction direction) {
        BufferStack bufferStack = getSlot(bufferSlot);
        return bufferStack.canInsert(itemStack);
    }

    /**
     * Wrapper for vanilla 'canExtractInvStack'.
     */
    @Override
    public boolean canExtractInvStack(int bufferSlot, ItemStack itemStack, Direction direction) {
        BufferStack bufferStack = getSlot(bufferSlot);
        return bufferStack.canInsert(itemStack);
    }

    /**
     * Wrapper for vanilla 'isInvEmpty'.
     */
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

    /**
     * Wrapper for vanilla 'clear', except I don't know what it's meant to do.
     * TODO: Figure out what this is meant to do.
     */
    @Override
    public void clear() {
    }

    /**
     * Override for vanilla 'canPlayerUseInv'.
     */
    @Override
    public boolean canPlayerUseInv(PlayerEntity playerEntity) {
        return true;
    }

    /**
     * Override for vanilla 'markDirt'.
     */
    @Override
    public void markDirty() {
        if (this.listeners != null) {
            for (InventoryListener inventoryListener : this.listeners) {
                inventoryListener.onInvChange(this);
            }
        }
    }
}