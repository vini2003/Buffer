package buffer.item;

import java.util.Random;
import java.util.stream.IntStream;

import buffer.entity.BufferEntity;
import buffer.inventory.BufferInventory;
import buffer.inventory.BufferInventory.BufferStack;
import buffer.registry.ItemRegistry;
import buffer.utility.BufferPacket;
import buffer.utility.BufferUsageContext;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BufferItem extends BlockItem {
    public static ItemStack stackToDraw = ItemStack.EMPTY;
    public static Integer lockTick = 5;
    @Override
    public boolean canMine(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1) {
        // TODO Auto-generated method stub
        return super.canMine(blockState_1, world_1, blockPos_1, playerEntity_1);
    }
    
    public BufferItem(Block block, Item.Settings properties) {
        super(block, properties);
        this.addPropertyGetter(new Identifier("tier"), (itemStack_1, world_1, livingEntity_1) -> {
            CompoundTag itemTag = itemStack_1.getTag();
            if (itemTag == null || !itemTag.containsKey("tier")) {
                itemTag = new CompoundTag();
                itemTag.putInt("tier", 1);
                itemStack_1.setTag(itemTag);
            }
            Integer tier = itemTag.getInt("tier");
            return tier;
        });
    }

    @Override
    public ActionResult place(ItemPlacementContext placementContext) {
        if (placementContext.getStack().getItem() == ItemRegistry.BUFFER_ITEM) {
            if (super.place(placementContext) == ActionResult.SUCCESS) {
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
        if (itemContext.getPlayer().isSneaking()) {
            BufferInventory bufferInventory = BufferInventory.fromTag(itemContext.getPlayer().getMainHandStack().getTag());
            if (bufferInventory.selectedSlot == -1) {
                return super.useOnBlock(itemContext);
            } else {
                ItemUsageContext bufferContext = new BufferUsageContext(itemContext.getWorld(), itemContext.getPlayer(), itemContext.getHand(), bufferInventory.getSlot(bufferInventory.selectedSlot).getStack(), new BlockHitResult(itemContext.getHitPos(), itemContext.getSide(), itemContext.getBlockPos(), true));
                ActionResult useResult;
                BufferStack bufferStack = bufferInventory.getSlot(bufferInventory.selectedSlot);
                if (bufferContext.getStack().getItem().isDamageable() && bufferStack.getStored() == 1 && !itemContext.getWorld().isClient) {
                    ItemStack newStack = bufferStack.getStack().copy();
                    useResult = bufferContext.getStack().getItem().useOnBlock(bufferContext);
                    if (useResult == ActionResult.SUCCESS) {
                        newStack.damage(1, (Random)itemContext.getWorld().random, (ServerPlayerEntity)null);
                        bufferStack.setStack(newStack);
                        bufferStack.setTag(newStack.getTag());
                    }
                } else if (!bufferContext.getStack().getItem().isDamageable()) {
                    useResult = bufferContext.getStack().getItem().useOnBlock(bufferContext);
                } else {
                    useResult = ActionResult.FAIL;
                }
                itemContext.getPlayer().getMainHandStack().setTag(BufferInventory.toTag(bufferInventory, new CompoundTag()));
                return useResult;
            }
        } else {
            return ActionResult.FAIL;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        if (!world.isClient && playerEntity.isSneaking()) {
            ContainerProviderRegistry.INSTANCE.openContainer(new Identifier("buffer", "buffer_item"), playerEntity, (buffer)->{
                buffer.writeBlockPos(playerEntity.getBlockPos());
            });
        }
        return new TypedActionResult(ActionResult.SUCCESS, playerEntity.getMainHandStack());
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
                } else {
                    stackToDraw = bufferInventory.getSlot(bufferInventory.selectedSlot).getStack().copy();
                }
            } else {
                stackToDraw = ItemStack.EMPTY;
            }
        }
    }
}