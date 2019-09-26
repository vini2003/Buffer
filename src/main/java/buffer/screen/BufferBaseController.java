package buffer.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import buffer.entity.BufferEntity;
import buffer.inventory.BufferInventory;
import buffer.inventory.BufferInventory.BufferStack;
import buffer.inventory.BufferInventory.WVoidSlot;
import buffer.registry.ItemRegistry;
import buffer.utility.BufferPacket;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class BufferBaseController extends CottonCraftingController {
    public BufferInventory bufferInventory = new BufferInventory(null);

    protected WPlainPanel rootPanel = new WPlainPanel();

    protected WLabel labelOne = new WLabel("");
    protected WLabel labelTwo = new WLabel("");
    protected WLabel labelThree = new WLabel("");
    protected WLabel labelFour = new WLabel("");
    protected WLabel labelFive = new WLabel("");
    protected WLabel labelSix = new WLabel("");

    protected List<WItemSlot> controllerSlots = Arrays.asList(null, null, null, null, null, null);
    protected List<WLabel> controllerLabels = Arrays.asList(labelOne, labelTwo, labelThree, labelFour, labelFive, labelSix);

    protected static final int sectionX = 48;
    protected static final int sectionY = 20;

    public BufferBaseController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(RecipeType.CRAFTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));

        setRootPanel(rootPanel);
    }

    @Override
    public ItemStack onSlotClick(int slotNumber, int button, SlotActionType action, PlayerEntity playerEntity) {
        Slot slot;
        if (slotNumber < 0 || slotNumber >= super.slotList.size()) {
            return ItemStack.EMPTY;
        } else {
            slot = super.slotList.get(slotNumber);
        }
        if (slot == null || !slot.canTakeItems(playerEntity)) {
            return ItemStack.EMPTY;
        } else {
            if (slot.getStack().getItem() == ItemRegistry.BUFFER_ITEM) {
                return ItemStack.EMPTY;
            }
            if (action == SlotActionType.QUICK_MOVE) {
                ItemStack quickStack;
                if (slot.inventory instanceof BufferInventory) {
                    BufferStack bufferStack = bufferInventory.getSlot(slotNumber);
                    bufferStack.restockStack(false);
                    final ItemStack wrappedStack = bufferStack.getStack().copy();
                    Boolean success = playerEntity.inventory.insertStack(wrappedStack.copy());
                    if (success) {
                        bufferStack.setStack(ItemStack.EMPTY);
                        if (!world.isClient){BufferPacket.sendPacket((ServerPlayerEntity)playerEntity, slotNumber, bufferStack.getStored());}
                        return ItemStack.EMPTY;
                    } else {
                        if (!world.isClient){BufferPacket.sendPacket((ServerPlayerEntity)playerEntity, slotNumber, bufferStack.getStored());}
                        return wrappedStack.copy();
                    }
                } else {
                    quickStack = bufferInventory.insertStack(slot.getStack().copy());
                    this.setStackInSlot(slotNumber, quickStack.copy());
                }
                return quickStack;
            } else if (action == SlotActionType.PICKUP) {
                if (slot.inventory instanceof BufferInventory) {
                    BufferStack bufferStack = bufferInventory.getSlot(slotNumber);
                    if (playerEntity.inventory.getCursorStack().isEmpty() && !bufferStack.getStack().isEmpty()) {
                            bufferStack.restockStack(false);
                            final ItemStack wrappedStack = bufferStack.getStack().copy();
                            playerEntity.inventory.setCursorStack(wrappedStack.copy());
                            bufferStack.setStack(ItemStack.EMPTY);
                            if (!world.isClient){BufferPacket.sendPacket((ServerPlayerEntity)playerEntity, slotNumber, bufferStack.getStored());}
                    } else if (!playerEntity.inventory.getCursorStack().isEmpty() && !slot.hasStack()) {
                        bufferInventory.getSlot(slotNumber).setStack(playerEntity.inventory.getCursorStack().copy());
                        playerEntity.inventory.setCursorStack(ItemStack.EMPTY);
                        if (!world.isClient){BufferPacket.sendPacket((ServerPlayerEntity)playerEntity, slotNumber, bufferStack.getStored());}
                    }
                    return ItemStack.EMPTY;
                } else {
                    return super.onSlotClick(slotNumber, button, action, playerEntity);
                }
            } else {
                return super.onSlotClick(slotNumber, button, action, playerEntity);
            }
        }
    }

    public void tick() {
        bufferInventory.restockAll();
        for (Integer bufferSlot : this.bufferInventory.getInvAvailableSlots(null)) {
            controllerLabels.get(bufferSlot).setText(new LiteralText(Integer.toString(this.bufferInventory.getStored(bufferSlot))));
        }
    }

    public void setWidgets() {
        controllerSlots.set(0, bufferInventory.new WVoidSlot(this.bufferInventory, 0, 1, 1, playerInventory));
        controllerSlots.set(1, bufferInventory.new WVoidSlot(this.bufferInventory, 1, 1, 1, playerInventory));
        controllerSlots.set(2, bufferInventory.new WVoidSlot(this.bufferInventory, 2, 1, 1, playerInventory));
        controllerSlots.set(3, bufferInventory.new WVoidSlot(this.bufferInventory, 3, 1, 1, playerInventory));
        controllerSlots.set(4, bufferInventory.new WVoidSlot(this.bufferInventory, 4, 1, 1, playerInventory));
        controllerSlots.set(5, bufferInventory.new WVoidSlot(this.bufferInventory, 5, 1, 1, playerInventory));

        for (Integer bufferSlot : bufferInventory.getInvAvailableSlots(null)) {
            controllerLabels.get(bufferSlot).setText(new LiteralText(Integer.toString(bufferInventory.getStored(bufferSlot))));    
        }

        switch (bufferInventory.getTier()) {
            case 1:
                rootPanel.add(controllerSlots.get(0), sectionX * 2 - 27, sectionY - 12);
                rootPanel.add(controllerLabels.get(0), sectionX * 2 - 27, sectionY + 10);
                break;
            case 2:
                rootPanel.add(controllerSlots.get(0), sectionX * 2 + 1, sectionY - 12);
                rootPanel.add(controllerSlots.get(1), sectionX * 1 - 7, sectionY - 12);
                rootPanel.add(controllerLabels.get(0), sectionX * 2 + 1, sectionY + 10);
                rootPanel.add(controllerLabels.get(1), sectionX * 1 - 7, sectionY + 10);
                break;
            case 3:
                rootPanel.add(controllerSlots.get(0), sectionX * 1 - 36, sectionY - 12);
                rootPanel.add(controllerSlots.get(1), sectionX * 2 - 27, sectionY - 12);
                rootPanel.add(controllerSlots.get(2), sectionX * 3 - 18, sectionY - 12);
                rootPanel.add(controllerLabels.get(0), sectionX * 1 - 36, sectionY + 10);
                rootPanel.add(controllerLabels.get(1), sectionX * 2 - 27, sectionY + 10);
                rootPanel.add(controllerLabels.get(2), sectionX * 3 - 18, sectionY + 10);
                break;
            case 4:
                rootPanel.add(controllerSlots.get(0), sectionX * 1 - 36, sectionY - 12);
                rootPanel.add(controllerSlots.get(1), sectionX * 2 - 27, sectionY - 12);
                rootPanel.add(controllerSlots.get(2), sectionX * 3 - 18, sectionY - 12);
                rootPanel.add(controllerSlots.get(3), sectionX * 2 - 27, sectionY * 2 + 4);
                rootPanel.add(controllerLabels.get(0), sectionX * 1 - 36, sectionY + 10);
                rootPanel.add(controllerLabels.get(1), sectionX * 2 - 27, sectionY + 10);
                rootPanel.add(controllerLabels.get(2), sectionX * 3 - 18, sectionY + 10);
                rootPanel.add(controllerLabels.get(3), sectionX * 2 - 27, sectionY * 2 + 26);
                break;
            case 5:
                rootPanel.add(controllerSlots.get(0), sectionX * 1 - 36, sectionY - 12);
                rootPanel.add(controllerSlots.get(1), sectionX * 2 - 27, sectionY - 12);
                rootPanel.add(controllerSlots.get(2), sectionX * 3 - 18, sectionY - 12);
                rootPanel.add(controllerSlots.get(3), sectionX * 1 - 7, sectionY * 2 + 4);
                rootPanel.add(controllerSlots.get(4), sectionX * 2 + 1, sectionY * 2 + 4);
                rootPanel.add(controllerLabels.get(0), sectionX * 1 - 36, sectionY + 10);
                rootPanel.add(controllerLabels.get(1), sectionX * 2 - 27, sectionY  + 10);
                rootPanel.add(controllerLabels.get(2), sectionX * 3 - 18, sectionY + 10);
                rootPanel.add(controllerLabels.get(3), sectionX * 1 - 7, sectionY * 2 + 26);
                rootPanel.add(controllerLabels.get(4), sectionX * 2 + 1, sectionY * 2 + 26);
                break;
            case 6:
                rootPanel.add(controllerSlots.get(0), sectionX * 1 - 36, sectionY - 12);
                rootPanel.add(controllerSlots.get(1), sectionX * 2 - 27, sectionY - 12);
                rootPanel.add(controllerSlots.get(2), sectionX * 3 - 18, sectionY - 12);
                rootPanel.add(controllerSlots.get(3), sectionX * 1 - 36, sectionY * 2 + 4);
                rootPanel.add(controllerSlots.get(4), sectionX * 2 - 27, sectionY * 2 + 4);        
                rootPanel.add(controllerSlots.get(5), sectionX * 3 - 18, sectionY * 2 + 4);  
                rootPanel.add(controllerLabels.get(0), sectionX * 1 - 36, sectionY + 10);
                rootPanel.add(controllerLabels.get(1), sectionX * 2 - 27, sectionY  + 10);
                rootPanel.add(controllerLabels.get(2), sectionX * 3 - 18, sectionY + 10);
                rootPanel.add(controllerLabels.get(3), sectionX * 1 - 36, sectionY * 2 + 26);
                rootPanel.add(controllerLabels.get(4), sectionX * 2 - 27, sectionY * 2 + 26);        
                rootPanel.add(controllerLabels.get(5), sectionX * 3 - 18, sectionY * 2 + 26);  
                break;  
        }

        this.rootPanel.add(this.createPlayerInventoryPanel(), 0, sectionY * 4);
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