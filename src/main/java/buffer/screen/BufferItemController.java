package buffer.screen;

import java.util.ArrayList;
import java.util.List;

import buffer.entity.EntityBuffer;
import buffer.inventory.InventoryBuffer;
import buffer.inventory.InventoryBuffer.VoidStack;
import buffer.inventory.InventoryBuffer.WVoidSlot;
import buffer.utility.BufferProvider;
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
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Tickable;

public class BufferItemController extends CottonCraftingController implements Tickable {
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

    public InventoryBuffer bufferInventory = new InventoryBuffer();

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
                    VoidStack voidStack = bufferEntity.bufferInventory.getSlot(slotNumber);
                    if (slot.inventory instanceof InventoryBuffer) {
                        voidStack.restockStack(false);
                        final ItemStack wrappedStack = voidStack.getWrappedStack().copy();
                        Boolean success = player.inventory.insertStack(wrappedStack.copy());
                        if (success) {
                            voidStack.setWrappedStack(ItemStack.EMPTY);
                            return ItemStack.EMPTY;
                        } else {
                            return wrappedStack.copy();
                        }
                    } else {
                        quickStack = bufferEntity.bufferInventory.insertStack(slot.getStack().copy());
                        this.setStackInSlot(slotNumber, quickStack.copy());
                    }
                    return quickStack;
                } else if (action == SlotActionType.PICKUP) {
                    if (slot.inventory instanceof InventoryBuffer) {
                        VoidStack voidStack = bufferEntity.bufferInventory.getSlot(slotNumber);
                        if (player.inventory.getCursorStack().isEmpty() && !voidStack.getPreviousStack().isEmpty() ||
                            player.inventory.getCursorStack().isEmpty() && !voidStack.getWrappedStack().isEmpty()) {
                                voidStack.restockStack(false);
                                final ItemStack wrappedStack = voidStack.getWrappedStack().copy();
                                player.inventory.setCursorStack(wrappedStack.copy());
                                voidStack.setWrappedStack(ItemStack.EMPTY);
                        } else if (!player.inventory.getCursorStack().isEmpty() && !slot.hasStack()) {
                            bufferEntity.bufferInventory.getSlot(slotNumber).setWrappedStack(player.inventory.getCursorStack().copy());
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

    public void tick() {
        //this.bufferEntity.bufferInventory.restockAll();

        for (int slot : this.bufferEntity.bufferInventory.getInvMaxSlotAmount()) {
            labels.get(slot).setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(slot))));
        }
    }

    @Override
    public void close(PlayerEntity playerEntity) {
        bufferEntity.bufferInventory = this.bufferEntity.bufferInventory;
        super.close(playerEntity);
    }

    public EntityBuffer getBlockEntity(BlockContext context) {
        EntityBuffer lambdaBypass[] = { null };

        context.run((world, blockPosition) -> {
            EntityBuffer temporaryEntity = (EntityBuffer)world.getBlockEntity(blockPosition);
            lambdaBypass[0] = temporaryEntity;
        });

        return lambdaBypass[0];
    }

    public BufferItemController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(RecipeType.CRAFTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));
    
        this.playerInventory = playerInventory;
        this.bufferEntity = (EntityBuffer)this.getBlockEntity(context);
        this.rootPanel = new WPlainPanel();

        this.setRootPanel(rootPanel);

        this.initInterface();
        this.postInterface();

        this.rootPanel.validate(this);
    }

    public void initInterface() {
        slotOne = bufferEntity.bufferInventory.new WVoidSlot(this.bufferEntity.bufferInventory, 0, 1, 1, playerInventory);
        slotTwo = bufferEntity.bufferInventory.new WVoidSlot(this.bufferEntity.bufferInventory, 1, 1, 1, playerInventory);
        slotThree = bufferEntity.bufferInventory.new WVoidSlot(this.bufferEntity.bufferInventory, 2, 1, 1, playerInventory);
        slotFour = bufferEntity.bufferInventory.new WVoidSlot(this.bufferEntity.bufferInventory, 3, 1, 1, playerInventory);
        slotFive = bufferEntity.bufferInventory.new WVoidSlot(this.bufferEntity.bufferInventory, 4, 1, 1, playerInventory);
        slotSix = bufferEntity.bufferInventory.new WVoidSlot(this.bufferEntity.bufferInventory, 5, 1, 1, playerInventory);

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

        if (bufferEntity.bufferInventory.getType() == BufferType.ONE) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(0))));

            this.rootPanel.add(slotOne, sectionX * 2 - 27, sectionY - 12);

            this.rootPanel.add(labelOne, sectionX * 2 - 27, sectionY + 10);
        }
        if (bufferEntity.bufferInventory.getType() == BufferType.TWO) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(1))));
            
            this.rootPanel.add(slotOne, sectionX * 2 + 1, sectionY - 12);
            this.rootPanel.add(slotTwo, sectionX * 1 - 7, sectionY - 12);

            this.rootPanel.add(labelOne, sectionX * 2 + 1, sectionY + 10);
            this.rootPanel.add(labelTwo, sectionX * 1 - 7, sectionY + 10);
        }
        if (bufferEntity.bufferInventory.getType() == BufferType.THREE) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(2))));

            this.rootPanel.add(slotOne, sectionX * 1 - 36, sectionY - 12);
            this.rootPanel.add(slotTwo, sectionX * 2 - 27, sectionY - 12);
            this.rootPanel.add(slotThree, sectionX * 3 - 18, sectionY - 12);

            this.rootPanel.add(labelOne, sectionX * 1 - 36, sectionY + 10);
            this.rootPanel.add(labelTwo, sectionX * 2 - 27, sectionY + 10);
            this.rootPanel.add(labelThree, sectionX * 3 - 18, sectionY + 10);
        }
        if (bufferEntity.bufferInventory.getType() == BufferType.FOUR) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(2))));
            labelFour.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(3))));

            this.rootPanel.add(slotOne, sectionX * 1 - 36, sectionY - 12);
            this.rootPanel.add(slotTwo, sectionX * 2 - 27, sectionY - 12);
            this.rootPanel.add(slotThree, sectionX * 3 - 18, sectionY - 12);
            this.rootPanel.add(slotFour, sectionX * 2 - 27, sectionY * 2 + 4);
        
            this.rootPanel.add(labelOne, sectionX * 1 - 36, sectionY + 10);
            this.rootPanel.add(labelTwo, sectionX * 2 - 27, sectionY + 10);
            this.rootPanel.add(labelThree, sectionX * 3 - 18, sectionY + 10);
            this.rootPanel.add(labelFour, sectionX * 2 - 27, sectionY * 2 + 26);
        }
        if (bufferEntity.bufferInventory.getType() == BufferType.FIVE) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(2))));
            labelFour.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(3))));
            labelFive.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(4))));

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
        if (bufferEntity.bufferInventory.getType() == BufferType.SIX) {
            labelOne.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(0))));
            labelTwo.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(1))));
            labelThree.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(2))));
            labelFour.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(3))));
            labelFive.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(4))));
            labelSix.setText(new LiteralText(Integer.toString(this.bufferEntity.bufferInventory.getStored(5))));

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