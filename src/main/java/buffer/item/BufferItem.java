package buffer.item;

import java.util.List;
import java.util.Random;

import buffer.entity.BufferEntity;
import buffer.inventory.BufferInventory;
import buffer.inventory.BufferInventory.BufferStack;
import buffer.registry.ItemRegistry;
import buffer.registry.ScreenRegistryServer;
import buffer.utility.BufferUsageContext;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class BufferItem extends Item {
    public static ItemStack stackToDraw = ItemStack.EMPTY;
    public static int amountToDraw = 0;

    public static int slotTick = 5;
    public static int voidTick = 5;
    public static int pickupTick = 5;

    public BufferItem(Block block, Item.Settings properties) {
        super(properties);
        this.addPropertyGetter(new Identifier(BufferInventory.TIER_RETRIEVER), (itemStack_1, world_1, livingEntity_1) -> {
            CompoundTag itemTag = itemStack_1.getTag();
            if (itemTag == null || !itemTag.containsKey(BufferInventory.TIER_RETRIEVER)) {
                itemTag = new CompoundTag();
                itemTag.putInt(BufferInventory.TIER_RETRIEVER, 1);
                itemStack_1.setTag(itemTag);
            }
            return itemTag.getInt(BufferInventory.TIER_RETRIEVER);
        });
    }

    public ActionResult place(ItemPlacementContext placementContext) {
        if (placementContext.getStack().getItem() == ItemRegistry.BUFFER_ITEM) {
            if (placementContext.canPlace()) {
                BufferEntity bufferEntity = ((BufferEntity)placementContext.getWorld().getBlockEntity(placementContext.getBlockPos()));
                bufferEntity.bufferInventory = BufferInventory.fromTag(placementContext.getStack().getTag());
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.FAIL;
            }
        } else {
            if (placementContext.getStack().getItem() instanceof BlockItem) {
                BlockItem blockToPlace = (BlockItem)placementContext.getStack().getItem();
                return blockToPlace.place(placementContext);
            } else {
                return ActionResult.FAIL;
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemContext) {
            BufferInventory bufferInventory = BufferInventory.fromTag(itemContext.getPlayer().getStackInHand(itemContext.getHand()).getTag());
            if (bufferInventory.selectedSlot == -1) {
                return super.useOnBlock(itemContext);
            } else {
                ItemUsageContext bufferContext = new BufferUsageContext(itemContext.getWorld(), itemContext.getPlayer(), itemContext.getHand(), bufferInventory.getSlot(bufferInventory.selectedSlot).getStack(), new BlockHitResult(itemContext.getHitPos(), itemContext.getSide(), itemContext.getBlockPos(), true));
                ActionResult blockUsageResult = ActionResult.FAIL;
                BufferStack bufferStack = bufferInventory.getSlot(bufferInventory.selectedSlot);
                if (bufferStack.getItem() == ItemRegistry.BUFFER_ITEM) {
                    return ActionResult.FAIL;
                } else {
                    if (bufferContext.getStack().getItem().isDamageable() && bufferStack.getStored() != 1) {
                        return ActionResult.FAIL;
                    }

                    Hand hand = itemContext.getHand();
                    World world = itemContext.getWorld();
                    PlayerEntity playerEntity = itemContext.getPlayer();
                    ItemStack bufferItemStack = playerEntity.getStackInHand(itemContext.getHand());
                    ActionResult usageResult;

                    playerEntity.setStackInHand(hand, bufferStack.getStack());   

                    usageResult = playerEntity.getStackInHand(itemContext.getHand()).getItem().useOnBlock(bufferContext);

                    if (usageResult == ActionResult.SUCCESS) {
                        if (bufferContext.getStack().getItem().isDamageable()) {
                            bufferStack.getStack().damage(1, (Random)itemContext.getWorld().random, (ServerPlayerEntity)null);
                            bufferStack.setTag(bufferStack.getStack().getTag());
                        }
                        bufferItemStack.setTag(BufferInventory.toTag(bufferInventory, new CompoundTag()));
                    } else {
                        TypedActionResult<ItemStack> useResult = playerEntity.getStackInHand(itemContext.getHand()).getItem().use(world, playerEntity, hand);
                        if (useResult.getResult() == ActionResult.SUCCESS) {
                            if (useResult.getValue() != bufferStack.getStack()) {
                                playerEntity.inventory.insertStack((ItemStack)useResult.getValue());
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
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        BufferInventory bufferInventory = BufferInventory.fromTag(playerEntity.getStackInHand(hand).getTag());
        if (!world.isClient && bufferInventory.selectedSlot == -1 && !playerEntity.isSneaking()) {
            ContainerProviderRegistry.INSTANCE.openContainer(ScreenRegistryServer.BUFFER_ITEM_CONTAINER, playerEntity, (buffer) -> buffer.writeBlockPos(playerEntity.getBlockPos()));
        } else {
            ItemStack bufferItemStack = playerEntity.getStackInHand(hand);
            if (bufferInventory.selectedSlot != -1) {
                BufferStack bufferStack = bufferInventory.getSlot(bufferInventory.selectedSlot);
                if (bufferStack.getStack().isFood()) {
                    return new TypedActionResult<>(ActionResult.PASS, bufferItemStack);
                } else {
                    playerEntity.setStackInHand(hand, bufferStack.getStack());                
                    TypedActionResult<ItemStack> usageResult = playerEntity.getStackInHand(hand).getItem().use(world, playerEntity, hand);
                    if (usageResult.getResult() == ActionResult.SUCCESS) {
                        if (usageResult.getValue() != bufferStack.getStack()) {
                            playerEntity.inventory.insertStack((ItemStack)usageResult.getValue());
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

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> text, TooltipContext tooltipContext) {
        if (itemStack.getTag() != null) {
            CompoundTag tag = itemStack.getTag();

            if(tag.containsKey(BufferInventory.TIER_RETRIEVER)) {
                text.add(new TranslatableText("buffer.tooltip.tier", Integer.toString(tag.getInt(BufferInventory.TIER_RETRIEVER))));
            }

            text.add(new TranslatableText("buffer.tooltip.pickup." + tag.getBoolean(BufferInventory.PICKUP_RETRIEVER)));
            text.add(new TranslatableText("buffer.tooltip.void." + tag.getBoolean(BufferInventory.VOID_RETRIEVER)));
        }
        super.appendTooltip(itemStack, world, text, tooltipContext);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int integer, boolean bool) {
        super.inventoryTick(itemStack, world, entity, integer, bool);
        if (entity instanceof PlayerEntity && world.isClient) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            if (playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM) {
                BufferInventory bufferInventory = BufferInventory.fromTag(playerEntity.getMainHandStack().getTag());
                if (bufferInventory.selectedSlot == -1) {
                    stackToDraw = playerEntity.getMainHandStack();
                    amountToDraw = playerEntity.getMainHandStack().getCount();
                } else {
                    stackToDraw = bufferInventory.getSlot(bufferInventory.selectedSlot).getStack().copy();
                    amountToDraw = bufferInventory.getStored(bufferInventory.selectedSlot);
                }
            } else {
                stackToDraw = ItemStack.EMPTY;
                amountToDraw = 0;
            }
        }
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items) {
        if(isIn(group)) {
            for(int i = 1; i <= 6; i++) {
                items.add(getTier(i));
            }
        }
    }

    private ItemStack getTier(int tier) {
        ItemStack stack = new ItemStack(this);
        CompoundTag tag = new CompoundTag();
        tag.putInt(BufferInventory.TIER_RETRIEVER, tier);
        stack.setTag(tag);
        return stack;
    }
}