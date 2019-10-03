package buffer.screen;

import java.util.Arrays;
import java.util.List;

import buffer.inventory.BufferInventory;
import buffer.inventory.BufferInventory.BufferStack;
import io.github.cottonmc.cotton.gui.CottonScreenController;
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

/**
 * Base Container/Controller for usage with Buffer, implements default methods for GUI widgets
 * and slot behavior.
 */
public class BufferBaseController extends CottonScreenController {
	public BufferInventory bufferInventory = new BufferInventory(1);

	protected WPlainPanel rootPanel = new WPlainPanel();

	protected List<WLabel> controllerLabels = Arrays.asList(new WLabel(""),new WLabel(""), new WLabel(""), new WLabel(""), new WLabel(""), new WLabel(""));
	protected List<WItemSlot> controllerSlots = Arrays.asList(null, null, null, null, null, null);

	protected static final int SECTION_X = 48;
	protected static final int SECTION_Y = 20;

	/** 
	 * Base constructor which sets root panel.
	 * @param syncID ID for Container/Controller synchronization.
	 * @param playerInventory PlayerInventory from player who opened container.
	 * @param context BlockContext for opened container.
	 */
	public BufferBaseController(int syncId, PlayerInventory playerInventory, BlockContext context) {
		super(RecipeType.CRAFTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));
		setRootPanel(rootPanel);
	}

	/**
	 * Converts number into metric representation of with rounding.
	 * @param value Number to be converted.
	 * @return String with converted number.
	 */
	public static String withSuffix(long value) {
		if (value < 1000) return "" + value;
		int exp = (int) (Math.log(value) / Math.log(1000));
		return String.format("%.1f %c", value / Math.pow(1000, exp), "KMGTPE".charAt(exp-1));
	}

	/**
	 * Defines custom behavior for item slot clicks in Container/Controller.
	 * @param slot Slot clicked.
	 * @param button Button clicked.
	 * @param action Action type.
	 * @param player Player entity which did action.
	 * @return Item stack remaining from insertion.
	 */
	@Override
	public ItemStack onSlotClick(int slot, int button, SlotActionType action, PlayerEntity player) {
		Slot inventorySlot;
		if (slot < 0 || slot >= super.slotList.size()) {
			return ItemStack.EMPTY;
		} else {
			inventorySlot = super.slotList.get(slot);
		}
		if (inventorySlot == null || !inventorySlot.canTakeItems(player)) {
			return ItemStack.EMPTY;
		} else {
			if (action == SlotActionType.QUICK_MOVE) {
				ItemStack quickStack;
				if (inventorySlot.inventory instanceof BufferInventory) {
					BufferStack bufferStack = bufferInventory.getSlot(slot);
					bufferStack.restock(false);
					ItemStack wrappedStack = bufferStack.getStack();
					int amountToRemove = wrappedStack.getMaxCount();
					if (amountToRemove > wrappedStack.getCount()) {
						amountToRemove = wrappedStack.getCount();
					}
					ItemStack insertStack = wrappedStack.copy();
					wrappedStack.decrement(amountToRemove);
					insertStack.setCount(amountToRemove);
					if (player.inventory.insertStack(insertStack)) {
						return ItemStack.EMPTY;
					} else {
						return insertStack.copy();
					}
				} else {
					quickStack = bufferInventory.insertStack(inventorySlot.getStack().copy());
					this.setStackInSlot(slot, quickStack.copy());
				}
				return quickStack;
			} else if (action == SlotActionType.PICKUP) {
				if (inventorySlot.inventory instanceof BufferInventory) {
					BufferStack bufferStack = bufferInventory.getSlot(slot);
					bufferStack.restock(false);
					if (player.inventory.getCursorStack().isEmpty() && !bufferStack.getStack().isEmpty()) {
							final ItemStack wrappedStack = bufferStack.getStack().copy();
							player.inventory.setCursorStack(wrappedStack.copy());
							bufferStack.setStack(ItemStack.EMPTY);
					} else if (!player.inventory.getCursorStack().isEmpty() && !inventorySlot.hasStack()) {
						bufferInventory.getSlot(slot).setStack(player.inventory.getCursorStack().copy());
						player.inventory.setCursorStack(ItemStack.EMPTY);
					} else if (!player.inventory.getCursorStack().isEmpty() && inventorySlot.hasStack()) {
						final ItemStack cursorStack = player.inventory.getCursorStack();
						ItemStack cursedStack = bufferInventory.insertStack(cursorStack.copy());
						player.inventory.setCursorStack(cursedStack);
					}
					return ItemStack.EMPTY;
				} else {
					return super.onSlotClick(slot, button, action, player);
				}
			} else {
				return super.onSlotClick(slot, button, action, player);
			}
		}
	}

	/**
	 * Update labels on Container/Controller with BufferStack's total stored value.
	 */
	public void tickLabels() {
		for (int bufferSlot : this.bufferInventory.getInvAvailableSlots(null)) {
			controllerLabels.get(bufferSlot).setText(new LiteralText(withSuffix(this.bufferInventory.getStored(bufferSlot))));
		}
	}

	/**
	 * Update BufferInventory's slot, and update Container/Controller's labels.
	 */
	public void tick() {
		bufferInventory.restockAll();
		tickLabels();
	}

	/**
	 * Add base widget(s) used by both BufferItem and BufferEntity to Container/Controller.
	 */
	public void addBaseWidgets() {
		controllerSlots.set(0, new WItemSlot(this.bufferInventory, 0, 1, 1, false, false));
		controllerSlots.set(1, new WItemSlot(this.bufferInventory, 1, 1, 1, false, false));
		controllerSlots.set(2, new WItemSlot(this.bufferInventory, 2, 1, 1, false, false));
		controllerSlots.set(3, new WItemSlot(this.bufferInventory, 3, 1, 1, false, false));
		controllerSlots.set(4, new WItemSlot(this.bufferInventory, 4, 1, 1, false, false));
		controllerSlots.set(5, new WItemSlot(this.bufferInventory, 5, 1, 1, false, false));

		tickLabels();

		switch (bufferInventory.getTier()) {
			case 1:
				rootPanel.add(controllerSlots.get(0), SECTION_X * 2 - 27, SECTION_Y - 12);
				rootPanel.add(controllerLabels.get(0), SECTION_X * 2 - 27, SECTION_Y + 10);
				break;
			case 2:
				rootPanel.add(controllerSlots.get(0), SECTION_X * 2 + 1, SECTION_Y - 12);
				rootPanel.add(controllerSlots.get(1), SECTION_X * 1 - 7, SECTION_Y - 12);
				rootPanel.add(controllerLabels.get(0), SECTION_X * 2 + 1, SECTION_Y + 10);
				rootPanel.add(controllerLabels.get(1), SECTION_X * 1 - 7, SECTION_Y + 10);
				break;
			case 3:
				rootPanel.add(controllerSlots.get(0), SECTION_X * 1 - 36, SECTION_Y - 12);
				rootPanel.add(controllerSlots.get(1), SECTION_X * 2 - 27, SECTION_Y - 12);
				rootPanel.add(controllerSlots.get(2), SECTION_X * 3 - 18, SECTION_Y - 12);
				rootPanel.add(controllerLabels.get(0), SECTION_X * 1 - 36, SECTION_Y + 10);
				rootPanel.add(controllerLabels.get(1), SECTION_X * 2 - 27, SECTION_Y + 10);
				rootPanel.add(controllerLabels.get(2), SECTION_X * 3 - 18, SECTION_Y + 10);
				break;
			case 4:
				rootPanel.add(controllerSlots.get(0), SECTION_X * 1 - 36, SECTION_Y - 12);
				rootPanel.add(controllerSlots.get(1), SECTION_X * 2 - 27, SECTION_Y - 12);
				rootPanel.add(controllerSlots.get(2), SECTION_X * 3 - 18, SECTION_Y - 12);
				rootPanel.add(controllerSlots.get(3), SECTION_X * 2 - 27, SECTION_Y * 2 + 4);
				rootPanel.add(controllerLabels.get(0), SECTION_X * 1 - 36, SECTION_Y + 10);
				rootPanel.add(controllerLabels.get(1), SECTION_X * 2 - 27, SECTION_Y + 10);
				rootPanel.add(controllerLabels.get(2), SECTION_X * 3 - 18, SECTION_Y + 10);
				rootPanel.add(controllerLabels.get(3), SECTION_X * 2 - 27, SECTION_Y * 2 + 26);
				break;
			case 5:
				rootPanel.add(controllerSlots.get(0), SECTION_X * 1 - 36, SECTION_Y - 12);
				rootPanel.add(controllerSlots.get(1), SECTION_X * 2 - 27, SECTION_Y - 12);
				rootPanel.add(controllerSlots.get(2), SECTION_X * 3 - 18, SECTION_Y - 12);
				rootPanel.add(controllerSlots.get(3), SECTION_X * 1 - 7, SECTION_Y * 2 + 4);
				rootPanel.add(controllerSlots.get(4), SECTION_X * 2 + 1, SECTION_Y * 2 + 4);
				rootPanel.add(controllerLabels.get(0), SECTION_X * 1 - 36, SECTION_Y + 10);
				rootPanel.add(controllerLabels.get(1), SECTION_X * 2 - 27, SECTION_Y  + 10);
				rootPanel.add(controllerLabels.get(2), SECTION_X * 3 - 18, SECTION_Y + 10);
				rootPanel.add(controllerLabels.get(3), SECTION_X * 1 - 7, SECTION_Y * 2 + 26);
				rootPanel.add(controllerLabels.get(4), SECTION_X * 2 + 1, SECTION_Y * 2 + 26);
				break;
			case 6:
				rootPanel.add(controllerSlots.get(0), SECTION_X * 1 - 36, SECTION_Y - 12);
				rootPanel.add(controllerSlots.get(1), SECTION_X * 2 - 27, SECTION_Y - 12);
				rootPanel.add(controllerSlots.get(2), SECTION_X * 3 - 18, SECTION_Y - 12);
				rootPanel.add(controllerSlots.get(3), SECTION_X * 1 - 36, SECTION_Y * 2 + 4);
				rootPanel.add(controllerSlots.get(4), SECTION_X * 2 - 27, SECTION_Y * 2 + 4);        
				rootPanel.add(controllerSlots.get(5), SECTION_X * 3 - 18, SECTION_Y * 2 + 4);  
				rootPanel.add(controllerLabels.get(0), SECTION_X * 1 - 36, SECTION_Y + 10);
				rootPanel.add(controllerLabels.get(1), SECTION_X * 2 - 27, SECTION_Y  + 10);
				rootPanel.add(controllerLabels.get(2), SECTION_X * 3 - 18, SECTION_Y + 10);
				rootPanel.add(controllerLabels.get(3), SECTION_X * 1 - 36, SECTION_Y * 2 + 26);
				rootPanel.add(controllerLabels.get(4), SECTION_X * 2 - 27, SECTION_Y * 2 + 26);        
				rootPanel.add(controllerLabels.get(5), SECTION_X * 3 - 18, SECTION_Y * 2 + 26);  
				break;  
		}
	}
	
	/**
	 * Always return -1 because this interface does not implement crafting.
	 */
	@Override   
	public int getCraftingResultSlotIndex() {
		return -1;
	}
	
	/**
	 * Always return true to allow interface usage.
	 */
	@Override
	public boolean canUse(PlayerEntity entity) {
		return true;
	}
}