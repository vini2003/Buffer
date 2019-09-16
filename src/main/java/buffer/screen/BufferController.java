package buffer.screen;

import java.util.ArrayList;
import java.util.List;

import buffer.inventory.InventoryBuffer;
import buffer.inventory.InventoryBuffer.WVoidSlot;
import buffer.utility.BufferType;
import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Tickable;

public class BufferController extends CottonCraftingController implements Tickable {
    private WPlainPanel rootPanel = null;

    private List<WItemSlot> slots = new ArrayList<>();

    private WVoidSlot slotOne = null;
    private WVoidSlot slotTwo = null;
    private WVoidSlot slotThree = null;
    private WVoidSlot slotFour = null;
    private WVoidSlot slotFive = null;
    private WVoidSlot slotSix = null;

    private WLabel labelOne = null;
    private WLabel labelTwo = null;
    private WLabel labelThree = null;
    private WLabel labelFour = null;
    private WLabel labelFive = null;
    private WLabel labelSix = null;

    private InventoryBuffer bufferInventory = null;

    private int sectionX = 48;
    private int sectionY = 20;

    @Override
    public ItemStack onSlotClick(int slotNumber, int button, SlotActionType action, PlayerEntity player) {
            Slot slot;
            if (slotNumber < 0 || slotNumber >= super.slotList.size()) {
                return ItemStack.EMPTY;
            } else {
                slot = super.slotList.get(slotNumber);
            }
            if (slot == null || !slot.canTakeItems(player)) {
                return ItemStack.EMPTY;
            } else {
                if (action == SlotActionType.QUICK_MOVE) {
                    ItemStack quickStack = bufferInventory.insertStack(slot.getStack().copy());
                    this.setStackInSlot(slotNumber, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return quickStack;
                } else if (action == SlotActionType.PICKUP) {
                    if (slot.inventory instanceof InventoryBuffer) {
                        InventoryBuffer bufferInvTemporary = ((InventoryBuffer)slot.inventory);
                        if (player.inventory.getCursorStack().isEmpty() && slot.hasStack()) {
                            player.inventory.setCursorStack(bufferInvTemporary.getInvStack(slotNumber).copy());
                            bufferInvTemporary.getSlot(slotNumber).setWrappedStack(ItemStack.EMPTY);
                            //bufferInvTemporary.setInvStack(slotNumber, ItemStack.EMPTY);
                        } else if (!player.inventory.getCursorStack().isEmpty() && !slot.hasStack()) {
                            bufferInvTemporary.getSlot(slotNumber).setWrappedStack(player.inventory.getCursorStack().copy());
                            //bufferInvTemporary.setInvStack(slotNumber, player.inventory.getCursorStack().copy()); 
                            player.inventory.setCursorStack(ItemStack.EMPTY);
                        }
                        player.inventory.updateItems();
                        this.sendContentUpdates();
                        return ItemStack.EMPTY;
                    } else {
                        return super.onSlotClick(slotNumber, button, action, player);
                    }
                } else {
                    return super.onSlotClick(slotNumber, button, action, player);
                }
            }
    }

    public void tick() {
        for (int slot : this.bufferInventory.getInvAvailableSlots(null)) {
            Boolean bool = this.bufferInventory.voidStacks.get(slot).restockStack(this);
        }

        if (bufferInventory.getType() == BufferType.ONE) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
        }
        if (bufferInventory.getType() == BufferType.TWO) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
        }
        if (bufferInventory.getType() == BufferType.THREE) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(2))));
        }
        if (bufferInventory.getType() == BufferType.FOUR) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(2))));
            labelFour.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(3))));
        }
        if (bufferInventory.getType() == BufferType.FIVE) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(2))));
            labelFour.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(3))));
            labelFive.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(4))));
        }
        if (bufferInventory.getType() == BufferType.SIX) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(2))));
            labelFour.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(3))));
            labelFive.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(4))));
            labelSix.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(5))));
        }
    }

    public BufferController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(RecipeType.CRAFTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));
        
        this.playerInventory = playerInventory;

        this.bufferInventory = new InventoryBuffer(playerInventory.getMainHandStack().getTag());

        this.rootPanel = new WPlainPanel();

        setRootPanel(rootPanel);

        this.initInterface();
        this.postInterface();

        this.rootPanel.validate(this);
    }

    public void initInterface() {
        slotOne = bufferInventory.new WVoidSlot(this.bufferInventory, 0, 1, 1, playerInventory);
        slotTwo = bufferInventory.new WVoidSlot(this.bufferInventory, 1, 1, 1, playerInventory);
        slotThree = bufferInventory.new WVoidSlot(this.bufferInventory, 2, 1, 1, playerInventory);
        slotFour = bufferInventory.new WVoidSlot(this.bufferInventory, 3, 1, 1, playerInventory);
        slotFive = bufferInventory.new WVoidSlot(this.bufferInventory, 4, 1, 1, playerInventory);
        slotSix = bufferInventory.new WVoidSlot(this.bufferInventory, 5, 1, 1, playerInventory);

        labelOne = new WLabel("");
        labelTwo = new WLabel("");
        labelThree = new WLabel("");
        labelFour = new WLabel("");
        labelFive = new WLabel("");
        labelSix = new WLabel("");

        slots.add(slotOne);
        slots.add(slotTwo);
        slots.add(slotThree);
        slots.add(slotFour);
        slots.add(slotFive);
        slots.add(slotSix);

        if (bufferInventory.getType() == BufferType.ONE) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));

            this.rootPanel.add(slotOne, sectionX * 2 - 27, sectionY - 12);

            this.rootPanel.add(labelOne, sectionX * 2 - 27, sectionY + 10);
        }
        if (bufferInventory.getType() == BufferType.TWO) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
            
            this.rootPanel.add(slotOne, sectionX * 1 - 7, sectionY - 12);
            this.rootPanel.add(slotTwo, sectionX * 2 + 1, sectionY - 12);

            this.rootPanel.add(labelOne, sectionX * 1 - 7, sectionY + 10);
            this.rootPanel.add(labelTwo, sectionX * 2 + 1, sectionY + 10);
        }
        if (bufferInventory.getType() == BufferType.THREE) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(2))));

            this.rootPanel.add(slotOne, sectionX * 1 - 36, sectionY - 12);
            this.rootPanel.add(slotTwo, sectionX * 2 - 27, sectionY - 12);
            this.rootPanel.add(slotThree, sectionX * 3 - 18, sectionY - 12);

            this.rootPanel.add(labelOne, sectionX * 1 - 36, sectionY + 10);
            this.rootPanel.add(labelTwo, sectionX * 2 - 27, sectionY + 10);
            this.rootPanel.add(labelThree, sectionX * 3 - 18, sectionY + 10);
        }
        if (bufferInventory.getType() == BufferType.FOUR) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(2))));
            labelFour.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(3))));

            this.rootPanel.add(slotOne, sectionX * 1 - 36, sectionY - 12);
            this.rootPanel.add(slotTwo, sectionX * 2 - 27, sectionY - 12);
            this.rootPanel.add(slotThree, sectionX * 3 - 18, sectionY - 12);
            this.rootPanel.add(slotFour, sectionX * 2 - 27, sectionY + 8);
        
            this.rootPanel.add(labelOne, sectionX * 1 - 36, sectionY + 10);
            this.rootPanel.add(labelTwo, sectionX * 2 - 27, sectionY + 10);
            this.rootPanel.add(labelThree, sectionX * 3 - 18, sectionY + 10);
            this.rootPanel.add(labelFour, sectionX * 2 - 27, sectionY + 30);
        }
        if (bufferInventory.getType() == BufferType.FIVE) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(2))));
            labelFour.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(3))));
            labelFive.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(4))));

            this.rootPanel.add(slotOne, sectionX * 1 - 36, sectionY - 12);
            this.rootPanel.add(slotTwo, sectionX * 2 - 27, sectionY - 12);
            this.rootPanel.add(slotThree, sectionX * 3 - 18, sectionY - 12);
            this.rootPanel.add(slotFour, sectionX * 1 - 7, sectionY + 8);
            this.rootPanel.add(slotFive, sectionX * 2 + 1, sectionY + 8);

            this.rootPanel.add(labelOne, sectionX * 1 - 36, sectionY + 10);
            this.rootPanel.add(labelTwo, sectionX * 2 - 27, sectionY  + 10);
            this.rootPanel.add(labelThree, sectionX * 3 - 18, sectionY + 10);
            this.rootPanel.add(labelFour, sectionX * 1 - 7, sectionY + 30);
            this.rootPanel.add(labelFive, sectionX * 2 + 1, sectionY + 30);
        }
        if (bufferInventory.getType() == BufferType.SIX) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(2))));
            labelFour.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(3))));
            labelFive.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(4))));
            labelSix.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(5))));

            this.rootPanel.add(slotOne, sectionX * 1 - 36, sectionY - 12);
            this.rootPanel.add(slotTwo, sectionX * 2 - 27, sectionY - 12);
            this.rootPanel.add(slotThree, sectionX * 3 - 18, sectionY - 12);
            this.rootPanel.add(slotFour, sectionX * 1 - 36, sectionY + 8);
            this.rootPanel.add(slotFive, sectionX * 2 - 27, sectionY + 8);        
            this.rootPanel.add(slotSix, sectionX * 3 - 18, sectionY + 8);  

            this.rootPanel.add(labelOne, sectionX * 1 - 36, sectionY + 10);
            this.rootPanel.add(labelTwo, sectionX * 2 - 27, sectionY  + 10);
            this.rootPanel.add(labelThree, sectionX * 3 - 18, sectionY + 10);
            this.rootPanel.add(labelFour, sectionX * 1 - 36, sectionY + 30);
            this.rootPanel.add(labelFive, sectionX * 2 - 27, sectionY + 30);        
            this.rootPanel.add(labelSix, sectionX * 3 - 18, sectionY + 30);  
        }

        this.rootPanel.add(this.createPlayerInventoryPanel(), 0, sectionY * 3);
    }

    public void postInterface() {
        
    }
    
    @Override   
	public int getCraftingResultSlotIndex() {
		return -1;
    }
    
    @Override
    public boolean canUse(PlayerEntity entity) {
        return true;
    }
}