package buffer.screen;

import java.util.ArrayList;
import java.util.List;

import buffer.entity.BufferEntity;
import buffer.inventory.BufferInventory;
import buffer.inventory.BufferInventory.BufferStack;
import buffer.inventory.BufferInventory.WVoidSlot;
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
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.LiteralText;

public class BufferEntityController extends CottonCraftingController {
    private WPlainPanel rootPanel = null;

    private List<WItemSlot> slots = new ArrayList<>();

    private List<WLabel> labels = new ArrayList<>();

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

    private int sectionX = 48;
    private int sectionY = 20;

    public BufferInventory bufferInventory = null;

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
                    ItemStack quickStack;
                    BufferStack bufferStack = bufferInventory.getSlot(slotNumber);
                    if (slot.inventory instanceof BufferInventory) {
                        bufferStack.restockStack(false);
                        final ItemStack wrappedStack = bufferStack.getStack().copy();
                        Boolean success = player.inventory.insertStack(wrappedStack.copy());
                        if (success) {
                            bufferStack.setStack(ItemStack.EMPTY);
                            return ItemStack.EMPTY;
                        } else {
                            return wrappedStack.copy();
                        }
                    } else {
                        if (slot.getStack() == player.getMainHandStack()) {
                            return ItemStack.EMPTY;
                        } else {
                            quickStack = bufferInventory.insertStack(slot.getStack().copy());
                            this.setStackInSlot(slotNumber, quickStack.copy());
                        }
                    }
                    return quickStack;
                } else if (action == SlotActionType.PICKUP) {
                    if (slot.inventory instanceof BufferInventory) {
                        BufferStack bufferStack = bufferInventory.getSlot(slotNumber);
                        if (player.inventory.getCursorStack().isEmpty() && !bufferStack.getStack().isEmpty()) {
                                bufferStack.restockStack(false);
                                final ItemStack wrappedStack = bufferStack.getStack().copy();
                                player.inventory.setCursorStack(wrappedStack.copy());
                                bufferStack.setStack(ItemStack.EMPTY);
                        } else if (!player.inventory.getCursorStack().isEmpty() && !slot.hasStack()) {
                            bufferInventory.getSlot(slotNumber).setStack(player.inventory.getCursorStack().copy());
                            player.inventory.setCursorStack(ItemStack.EMPTY);
                        }
                        player.inventory.updateItems();
                        this.sendContentUpdates();
                        slot.markDirty();
                        player.inventory.markDirty();
                        return ItemStack.EMPTY;
                    } else {
                        return super.onSlotClick(slotNumber, button, action, player);
                    }
                } else {
                    return super.onSlotClick(slotNumber, button, action, player);
                }
            }
    }

    public void screenTick() {
        for (Integer bufferSlot : this.bufferInventory.getInvAvailableSlots(null)) {
            labels.get(bufferSlot).setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(bufferSlot))));
        }
    }

    public BufferEntity getBlockEntity(BlockContext context) {
        BufferEntity lambdaBypass[] = { null };

        context.run((world, blockPosition) -> {
            BufferEntity temporaryEntity = (BufferEntity)world.getBlockEntity(blockPosition);
            lambdaBypass[0] = temporaryEntity;
        });

        return lambdaBypass[0];
    }

    public BufferEntityController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(RecipeType.CRAFTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));
    
        this.playerInventory = playerInventory;
        this.bufferInventory = ((BufferEntity)this.getBlockEntity(context)).bufferInventory;
        this.rootPanel = new WPlainPanel();

        this.setRootPanel(rootPanel);

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

        labels.add(labelOne);
        labels.add(labelTwo);
        labels.add(labelThree);
        labels.add(labelFour);
        labels.add(labelFive);
        labels.add(labelSix);

        if (bufferInventory.getTier() == 1) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));

            this.rootPanel.add(slotOne, sectionX * 2 - 27, sectionY - 12);

            this.rootPanel.add(labelOne, sectionX * 2 - 27, sectionY + 10);
        }
        if (bufferInventory.getTier() == 2) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
            
            this.rootPanel.add(slotOne, sectionX * 2 + 1, sectionY - 12);
            this.rootPanel.add(slotTwo, sectionX * 1 - 7, sectionY - 12);

            this.rootPanel.add(labelOne, sectionX * 2 + 1, sectionY + 10);
            this.rootPanel.add(labelTwo, sectionX * 1 - 7, sectionY + 10);
        }
        if (bufferInventory.getTier() == 3) {
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
        if (bufferInventory.getTier() == 4) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(2))));
            labelFour.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(3))));

            this.rootPanel.add(slotOne, sectionX * 1 - 36, sectionY - 12);
            this.rootPanel.add(slotTwo, sectionX * 2 - 27, sectionY - 12);
            this.rootPanel.add(slotThree, sectionX * 3 - 18, sectionY - 12);
            this.rootPanel.add(slotFour, sectionX * 2 - 27, sectionY * 2 + 4);
        
            this.rootPanel.add(labelOne, sectionX * 1 - 36, sectionY + 10);
            this.rootPanel.add(labelTwo, sectionX * 2 - 27, sectionY + 10);
            this.rootPanel.add(labelThree, sectionX * 3 - 18, sectionY + 10);
            this.rootPanel.add(labelFour, sectionX * 2 - 27, sectionY * 2 + 26);
        }
        if (bufferInventory.getTier() == 5) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(2))));
            labelFour.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(3))));
            labelFive.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(4))));

            this.rootPanel.add(slotOne, sectionX * 1 - 36, sectionY - 12);
            this.rootPanel.add(slotTwo, sectionX * 2 - 27, sectionY - 12);
            this.rootPanel.add(slotThree, sectionX * 3 - 18, sectionY - 12);
            this.rootPanel.add(slotFour, sectionX * 1 - 7, sectionY * 2 + 4);
            this.rootPanel.add(slotFive, sectionX * 2 + 1, sectionY * 2 + 4);

            this.rootPanel.add(labelOne, sectionX * 1 - 36, sectionY + 10);
            this.rootPanel.add(labelTwo, sectionX * 2 - 27, sectionY  + 10);
            this.rootPanel.add(labelThree, sectionX * 3 - 18, sectionY + 10);
            this.rootPanel.add(labelFour, sectionX * 1 - 7, sectionY * 2 + 26);
            this.rootPanel.add(labelFive, sectionX * 2 + 1, sectionY * 2 + 26);
        }
        if (bufferInventory.getTier() == 6) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(2))));
            labelFour.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(3))));
            labelFive.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(4))));
            labelSix.setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(5))));

            this.rootPanel.add(slotOne, sectionX * 1 - 36, sectionY - 12);
            this.rootPanel.add(slotTwo, sectionX * 2 - 27, sectionY - 12);
            this.rootPanel.add(slotThree, sectionX * 3 - 18, sectionY - 12);
            this.rootPanel.add(slotFour, sectionX * 1 - 36, sectionY * 2 + 4);
            this.rootPanel.add(slotFive, sectionX * 2 - 27, sectionY * 2 + 4);        
            this.rootPanel.add(slotSix, sectionX * 3 - 18, sectionY * 2 + 4);  

            this.rootPanel.add(labelOne, sectionX * 1 - 36, sectionY + 10);
            this.rootPanel.add(labelTwo, sectionX * 2 - 27, sectionY  + 10);
            this.rootPanel.add(labelThree, sectionX * 3 - 18, sectionY + 10);
            this.rootPanel.add(labelFour, sectionX * 1 - 36, sectionY * 2 + 26);
            this.rootPanel.add(labelFive, sectionX * 2 - 27, sectionY * 2 + 26);        
            this.rootPanel.add(labelSix, sectionX * 3 - 18, sectionY * 2 + 26);  
        }

        this.rootPanel.add(this.createPlayerInventoryPanel(), 0, sectionY * 4);
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