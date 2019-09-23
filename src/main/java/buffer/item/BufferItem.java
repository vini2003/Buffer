package buffer.item;

import buffer.inventory.BufferInventory;
import buffer.registry.BlockRegistry;
import buffer.utility.BufferProvider;
import buffer.utility.BufferType;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BufferItem extends BlockItem {
    public BufferInventory bufferInventory = null;

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

    // @Override
    // public ActionResult place(ItemPlacementContext placementContext) {
    //     if (super.place(placementContext) == ActionResult.SUCCESS) {
    //         World world = placementContext.getWorld();
    //         BlockPos blockPosition = placementContext.getBlockPos();
    //         CompoundTag itemTag = placementContext.getStack().getTag();
    //         bufferInventory.setType(itemTag);
    //         if (this.bufferInventory.getType() == null) {
    //             if (itemTag == null || !itemTag.containsKey("tier")) {
    //                 itemTag = new CompoundTag();
    //                 itemTag.putInt("tier", 1);
    //                 placementContext.getPlayer().getMainHandStack().setTag(itemTag);
    //             }
    //         }

    //         Integer tier = itemTag.getInt("tier");
    //         bufferInventory.setType(BufferType.fromInt(tier));

    //         world.setBlockState(blockPosition, BlockRegistry.BLOCK_TESSERACT.getDefaultState().with(BufferProvider.tier, bufferInventory.getType().asInt()));
    //         return ActionResult.SUCCESS;
    //     } else {
    //         return ActionResult.FAIL;
    //     }
    // }

    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int integer, boolean bool) {
        super.inventoryTick(itemStack, world, entity, integer, bool);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext_1) {
        World world = itemUsageContext_1.getWorld();
        BlockPos blockPosition = itemUsageContext_1.getBlockPos();
        BlockState stateHit = world.getBlockState(blockPosition);
        Block blockHit = stateHit.getBlock();
        PlayerEntity playerEntity = itemUsageContext_1.getPlayer();
        ItemStack itemStack = itemUsageContext_1.getStack();

        if (blockHit == Blocks.COAL_BLOCK && !playerEntity.isSneaking() && itemStack.getTag().getInt("tier") == 1) {
            //this.bufferInventory.setType(BufferType.TWO);
            itemStack.getTag().putInt("tier", 2);
            world.breakBlock(blockPosition, false);
            return ActionResult.SUCCESS;
        } 
        if (blockHit == Blocks.IRON_BLOCK && !playerEntity.isSneaking() && itemStack.getTag().getInt("tier") == 2) {
            //this.bufferInventory.setType(BufferType.THREE);
            itemStack.getTag().putInt("tier", 3);
            world.breakBlock(blockPosition, false);
            return ActionResult.SUCCESS;
        }
        if (blockHit == Blocks.GOLD_BLOCK && !playerEntity.isSneaking() && itemStack.getTag().getInt("tier") == 3) {
            //this.bufferInventory.setType(BufferType.FOUR);
            itemStack.getTag().putInt("tier", 4);
            world.breakBlock(blockPosition, false);
            return ActionResult.SUCCESS;
        }
        if (blockHit == Blocks.DIAMOND_BLOCK && !playerEntity.isSneaking() && itemStack.getTag().getInt("tier") == 4) {
            //this.bufferInventory.setType(BufferType.FIVE);
            itemStack.getTag().putInt("tier", 5);
            world.breakBlock(blockPosition, false);
            return ActionResult.SUCCESS;
        }
        if (blockHit == Blocks.EMERALD_BLOCK && !playerEntity.isSneaking() && itemStack.getTag().getInt("tier") == 5) {
            //this.bufferInventory.setType(BufferType.SIX);
            itemStack.getTag().putInt("tier", 6);
            world.breakBlock(blockPosition, false);
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(itemUsageContext_1);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        if (this.bufferInventory.getType() == null) {
            CompoundTag itemTag = playerEntity.getMainHandStack().getTag();
            if (itemTag == null || !itemTag.containsKey("tier")) {
                itemTag = new CompoundTag();
                itemTag.putInt("tier", 1);
                playerEntity.getMainHandStack().setTag(itemTag);
            }
            Integer tier = itemTag.getInt("tier");
            //this.bufferInventory = new InventoryBuffer(this.bufferInventory,)
            bufferInventory.setType(BufferType.fromInt(tier));
        }
        if (playerEntity.world.isClient && playerEntity.isSneaking()) {
            ContainerProviderRegistry.INSTANCE.openContainer(new Identifier("buffer", "buffer"), playerEntity, (buffer)->{
                buffer.writeBlockPos(playerEntity.getBlockPos());
            });
        }
        return super.use(world, playerEntity, hand);
    }
}