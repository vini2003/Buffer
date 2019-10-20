package buffer.item;

import buffer.entity.BufferEntity;
import buffer.inventory.BufferInventory;
import buffer.inventory.BufferInventory.BufferStack;
import buffer.registry.ItemRegistry;
import buffer.registry.ScreenRegistryServer;
import buffer.utility.BufferTier;
import buffer.utility.BufferUsageContext;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * BufferItem extension of Item which implements base methods.
 */
public class BufferItem extends BlockItem {
	public static ItemStack stackToDraw = ItemStack.EMPTY;
	public static int amountToDraw = 0;

	public static int slotTick = 5;
	public static int voidTick = 5;
	public static int pickupTick = 5;

	/**
	 * Customized constructor which sets up variables for BufferItem usage.
	 *
	 * @param block      BufferBlock associated with BufferItem.
	 * @param properties Item.Settings properties.
	 */
	public BufferItem(Block block, Item.Settings properties) {
		super(block, properties);

		/**
		 * Add property 'tier' property getter for BufferItem rendering code.
		 */
		this.addPropertyGetter(new Identifier(BufferInventory.TIER_RETRIEVER), (itemStack, world, livingEntity) -> {
			CompoundTag itemTag = itemStack.getOrCreateTag();
			if (!itemTag.containsKey(BufferInventory.TIER_RETRIEVER)) {
				itemTag.putInt(BufferInventory.TIER_RETRIEVER, 1);
				itemStack.setTag(itemTag);
			}
			return itemTag.getInt(BufferInventory.TIER_RETRIEVER);
		});
	}

	/**
	 * Return if a BufferItem ItemStack is being held by specified player.
	 *
	 * @param playerEntity PlayerEntity to check.
	 * @return ItemStack.EMPTY if not found; else, BufferItem's ItemStack.
	 */
	public static ItemStack isBeingHeld(PlayerEntity playerEntity) {
		if (playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM
				|| playerEntity.getOffHandStack().getItem() == ItemRegistry.BUFFER_ITEM) {
			Hand hand = playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM ? Hand.MAIN_HAND : Hand.OFF_HAND;
			return playerEntity.getStackInHand(hand);
		} else {
			return ItemStack.EMPTY;
		}
	}

	/**
	 * Clear BufferItem GUI rendering data.
	 */
	public static void clear() {
		stackToDraw = ItemStack.EMPTY;
		amountToDraw = 0;
	}

	/**
	 * Custom BufferItem 'place' behaviour to correctly configure spawned BufferEntity.
	 *
	 * @param placementContext BufferItem's ItemPlacementContext.
	 * @return ActionResult.SUCCESS if successfully placed, ActionResult.FAIL if not.
	 */
	@Override
	public ActionResult place(ItemPlacementContext placementContext) {
		ActionResult placementResult = ActionResult.FAIL;
		if (placementContext.getStack().getItem() == ItemRegistry.BUFFER_ITEM) {
			placementResult = super.place(placementContext);
			if (placementResult == ActionResult.SUCCESS) {
				BufferEntity bufferEntity = ((BufferEntity) placementContext.getWorld().getBlockEntity(placementContext.getBlockPos()));
				bufferEntity.bufferInventory = BufferInventory.fromTag(placementContext.getStack().getTag());
			}
		}
		return placementResult;
	}

	/**
	 * Custom BufferItem 'useOnBlock' behaviour to allow for usage of selected BufferStack.
	 * TODO: Perhaps find a better way to do this, given it is currently very hacky.
	 *
	 * @param usageContext BufferItem's ItemUsageContext.
	 * @return ActionResult.SUCCESS if successfully placed, ActionResult.FAIL if not.
	 */
	@Override
	public ActionResult useOnBlock(ItemUsageContext usageContext) {
		BufferInventory bufferInventory = BufferInventory.fromTag(usageContext.getPlayer().getStackInHand(usageContext.getHand()).getTag());
		if (bufferInventory.selectedSlot == -1) {
			return super.useOnBlock(usageContext);
		} else {
			ItemUsageContext bufferContext = new BufferUsageContext(usageContext.getWorld(), usageContext.getPlayer(), usageContext.getHand(), bufferInventory.getSlot(bufferInventory.selectedSlot).getStack(), new BlockHitResult(usageContext.getHitPos(), usageContext.getSide(), usageContext.getBlockPos(), true));
			ActionResult blockUsageResult = ActionResult.FAIL;
			BufferStack bufferStack = bufferInventory.getSlot(bufferInventory.selectedSlot);
			bufferStack.restock(false);
			if (bufferStack.getItem() == ItemRegistry.BUFFER_ITEM) {
				return ActionResult.FAIL;
			} else {
				if (bufferContext.getStack().getItem().isDamageable() && bufferStack.getStored() != 1) {
					return ActionResult.FAIL;
				}

				Hand hand = usageContext.getHand();
				World world = usageContext.getWorld();
				PlayerEntity playerEntity = usageContext.getPlayer();
				ItemStack bufferItemStack = playerEntity.getStackInHand(usageContext.getHand());
				ActionResult usageResult;

				playerEntity.setStackInHand(hand, bufferStack.getStack());

				usageResult = playerEntity.getStackInHand(usageContext.getHand()).getItem().useOnBlock(bufferContext);

				if (usageResult == ActionResult.SUCCESS) {
					if (bufferContext.getStack().getItem().isDamageable()) {
						bufferStack.getStack().damage(1, usageContext.getWorld().random, null);
						bufferStack.setTag(bufferStack.getStack().getTag());
					} else {
						if (bufferStack.getStack().getCount() == bufferContext.getStack().getCount()) {
							bufferStack.setStack(bufferContext.getStack());
						} else {
							bufferStack.getStack().decrement(1);
						}
					}
					bufferItemStack.setTag(BufferInventory.toTag(bufferInventory, new CompoundTag()));
				} else {
					TypedActionResult<ItemStack> useResult = playerEntity.getStackInHand(usageContext.getHand()).getItem().use(world, playerEntity, hand);
					if (useResult.getResult() == ActionResult.SUCCESS) {
						if (useResult.getValue() != bufferStack.getStack()) {
							playerEntity.inventory.insertStack(useResult.getValue());
							bufferStack.getStack().decrement(1);
						}
					}
					bufferItemStack.setTag(BufferInventory.toTag(bufferInventory, new CompoundTag()));
				}

				playerEntity.setStackInHand(hand, bufferItemStack);
				return blockUsageResult;
			}
		}
	}

	/**
	 * Custom BufferItem 'use' behaviour to allow for usage of selected BufferStack.
	 * TODO: Perhaps find a better way to do this, given it is currently very hacky.
	 *
	 * @param world        World where BufferItem was used.
	 * @param playerEntity PlayerEntity who used the BufferItem.
	 * @param hand         Active Hand of PlayerEntity who used the BufferItem.
	 */
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		BufferInventory bufferInventory = BufferInventory.fromTag(playerEntity.getStackInHand(hand).getTag());
		if (!world.isClient && bufferInventory.selectedSlot == -1 && !playerEntity.isSneaking()) {
			ContainerProviderRegistry.INSTANCE.openContainer(ScreenRegistryServer.BUFFER_ITEM_CONTAINER, playerEntity, (buffer) -> {
				buffer.writeBlockPos(playerEntity.getBlockPos());
			});
		} else {
			ItemStack bufferItemStack = playerEntity.getStackInHand(hand);
			if (bufferInventory.selectedSlot != -1) {
				BufferStack bufferStack = bufferInventory.getSlot(bufferInventory.selectedSlot);
				bufferStack.restock(false);
				if (bufferStack.getStack().isFood()) {
					return new TypedActionResult<>(ActionResult.PASS, bufferItemStack);
				} else {
					playerEntity.setStackInHand(hand, bufferStack.getStack());
					TypedActionResult<ItemStack> usageResult = playerEntity.getStackInHand(hand).getItem().use(world, playerEntity, hand);
					if (usageResult.getResult() == ActionResult.SUCCESS) {
						if (usageResult.getValue() != bufferStack.getStack()) {
							playerEntity.inventory.insertStack(usageResult.getValue());
							bufferStack.getStack().decrement(1);
						} else {
							bufferStack.getStack().decrement(1);
							bufferStack.setStack(playerEntity.getStackInHand(hand));
						}
						bufferItemStack.setTag(BufferInventory.toTag(bufferInventory, new CompoundTag()));
					}
					playerEntity.setStackInHand(hand, bufferItemStack);
				}
			} else {

			}
		}
		return new TypedActionResult<>(ActionResult.PASS, playerEntity.getStackInHand(hand));
	}

	/**
	 * Custom BufferItem 'appendTooltip' behaviour to add custom tooltips with Buffer Item statisics.
	 *
	 * @param itemStack      ItemStack to whom tooltip will be added.
	 * @param world          World where tooltip will be added.
	 * @param text           List<Text> of lines for tooltip description.
	 * @param tooltipContext ItemStack's TooltipContext.
	 */
	@Override
	public void appendTooltip(ItemStack itemStack, World world, List<Text> text, TooltipContext tooltipContext) {
		boolean isSneaking = Screen.hasShiftDown();
		if (itemStack.getTag() != null) {
			BufferInventory bufferInventory = BufferInventory.fromTag(itemStack.getTag());

			text.add(new TranslatableText("buffer.tooltip.tier", Integer.toString(bufferInventory.getTier())));
			text.add(new TranslatableText("buffer.tooltip.pickup." + bufferInventory.isPickup));
			text.add(new TranslatableText("buffer.tooltip.void." + bufferInventory.isVoid));

			if (!isSneaking) {
				text.add(new TranslatableText("buffer.tooltip.sneaking_false"));
			} else {
				text.add(new TranslatableText("buffer.tooltip.sneaking_true"));
				Iterator<BufferStack> stackIterator = bufferInventory.bufferStacks.iterator();
				while (stackIterator.hasNext()) {
					BufferStack bufferStack = stackIterator.next();
					String name;
					if (bufferStack.getStack() == ItemStack.EMPTY) {
						name = "Empty";
					} else {
						name = "§f§o" + bufferStack.getStack().getName().getString();
					}
					String amount;
					if (bufferStack.getStack() == ItemStack.EMPTY) {
						amount = "";
					} else {
						amount = "§9" + bufferStack.getStored() + "x §f§o";
					}

					if (stackIterator.hasNext()) {
						text.add(new LiteralText("├─ " + amount + name));
					} else {
						text.add(new LiteralText("└─ " + amount + name));
					}
				}
			}
		}
		super.appendTooltip(itemStack, world, text, tooltipContext);
	}

	/**
	 * Custom BufferItem 'inventoryTick' behaviour to update BufferItem GUI rendering data.
	 *
	 * @param itemStack    ItemStack where tick was called.
	 * @param world        World where tick was called.
	 * @param entity       Entity in whose inventory tick was called.
	 * @param integerValue Integer used by Minecraft internally.
	 * @param booleanValue Boolean used by Minecraft internally.
	 */
	@Override
	public void inventoryTick(ItemStack itemStack, World world, Entity entity, int integerValue, boolean booleanValue) {
		super.inventoryTick(itemStack, world, entity, integerValue, booleanValue);
		if (entity instanceof PlayerEntity && world.isClient) {
			PlayerEntity playerEntity = (PlayerEntity) entity;
			if (BufferItem.isBeingHeld(playerEntity) != ItemStack.EMPTY) {
				Hand hand = playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM ? Hand.MAIN_HAND : Hand.OFF_HAND;
				BufferInventory bufferInventory = BufferInventory.fromTag(playerEntity.getStackInHand(hand).getTag());
				if (bufferInventory.selectedSlot == -1) {
					stackToDraw = playerEntity.getStackInHand(hand);
					amountToDraw = playerEntity.getStackInHand(hand).getCount();
				} else {
					stackToDraw = bufferInventory.getSlot(bufferInventory.selectedSlot).getStack().copy();
					amountToDraw = bufferInventory.getStored(bufferInventory.selectedSlot);
				}
			} else {
				BufferItem.clear();
			}
		}
	}

	/**
	 * Custom BufferItem 'appendStacks' behaviour to add BufferItem ItemStacks to Buffer ItemGroup.
	 *
	 * @param itemGroup  ItemGroup BufferItem is asked if it belongs in.
	 * @param itemStacks ItemStacks of ItemGroup.
	 */
	@Override
	public void appendStacks(ItemGroup itemGroup, DefaultedList<ItemStack> itemStacks) {
		if (isIn(itemGroup)) {
			for (int bufferTier = 1; bufferTier <= BufferTier.getMaximumTier(); bufferTier++) {
				itemStacks.add(getStackWithTier(bufferTier));
			}
		}
	}

	/**
	 * Get new BufferItem ItemStack with NBT data for specified tier.
	 *
	 * @param bufferTier Specified tier;
	 * @return ItemStack with specified tier.
	 */
	private ItemStack getStackWithTier(int bufferTier) {
		ItemStack itemStack = new ItemStack(this);
		CompoundTag itemTag = BufferInventory.toTag(new BufferInventory(bufferTier), new CompoundTag());
		itemStack.setTag(itemTag);
		return itemStack;
	}
}