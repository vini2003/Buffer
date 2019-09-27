package buffer.item;

import java.util.Iterator;
import java.util.stream.IntStream;

import com.mojang.realmsclient.gui.RealmsWorldSlotButton.Action;

import buffer.entity.BufferEntity;
import buffer.inventory.BufferInventory;
import buffer.registry.ItemRegistry;
import buffer.utility.BufferPacket;
import buffer.utility.BufferUsageContext;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class BufferItem extends BlockItem {
    public static ItemStack stackToDraw = ItemStack.EMPTY;
    public static Integer lockTick = 5;

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
            ActionResult placeResult;
            if (placementContext.canPlace()) {
                placeResult = ActionResult.SUCCESS;
            } else {
                placeResult = ActionResult.FAIL;
            }
            if (placeResult == ActionResult.SUCCESS) {
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
            ItemUsageContext bufferContext = new BufferUsageContext(itemContext.getWorld(), itemContext.getPlayer(), itemContext.getHand(), bufferInventory.getSlot(bufferInventory.selectedSlot).getStack(), new BlockHitResult(itemContext.getHitPos(), itemContext.getSide(), itemContext.getBlockPos(), true));
            if (bufferContext.getStack().getItem() == ItemRegistry.BUFFER_ITEM) {
                ActionResult result = super.useOnBlock(bufferContext);
                if (result == ActionResult.SUCCESS) {
                    bufferInventory.getSlot(bufferInventory.selectedSlot).getStack().decrement(1);
                    itemContext.getPlayer().getMainHandStack().setTag(BufferInventory.toTag(bufferInventory, new CompoundTag()));
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.FAIL;
                }
                
            } else {
                return bufferContext.getStack().getItem().useOnBlock(bufferContext);
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
            CompoundTag itemTag = playerEntity.getMainHandStack().getTag();
            for (Integer slotNumber : IntStream.rangeClosed(0, itemTag.getInt("tier") - 1).toArray()) {
                BufferPacket.sendPacket((ServerPlayerEntity)playerEntity, slotNumber, itemTag.getInt(Integer.toString(slotNumber)));
            }
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