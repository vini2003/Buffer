package buffer.item;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BufferItem extends BlockItem {
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
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext_1) {
        World world = itemUsageContext_1.getWorld();
        BlockPos blockPosition = itemUsageContext_1.getBlockPos();
        BlockState stateHit = world.getBlockState(blockPosition);
        Block blockHit = stateHit.getBlock();
        PlayerEntity playerEntity = itemUsageContext_1.getPlayer();
        ItemStack itemStack = itemUsageContext_1.getStack();
        CompoundTag itemTag = itemStack.getTag();
        Integer tier = itemTag.getInt("tier");

        if (blockHit == Blocks.COAL_BLOCK && !playerEntity.isSneaking() && tier == 1) {
            itemStack.getTag().putInt("tier", 2);
            world.breakBlock(blockPosition, false);
            return ActionResult.SUCCESS;
        } 
        if (blockHit == Blocks.IRON_BLOCK && !playerEntity.isSneaking() && tier == 2) {
            itemStack.getTag().putInt("tier", 3);
            world.breakBlock(blockPosition, false);
            return ActionResult.SUCCESS;
        }
        if (blockHit == Blocks.GOLD_BLOCK && !playerEntity.isSneaking() && tier == 3) {
            itemStack.getTag().putInt("tier", 4);
            world.breakBlock(blockPosition, false);
            return ActionResult.SUCCESS;
        }
        if (blockHit == Blocks.DIAMOND_BLOCK && !playerEntity.isSneaking() && tier == 4) {
            itemStack.getTag().putInt("tier", 5);
            world.breakBlock(blockPosition, false);
            return ActionResult.SUCCESS;
        }
        if (blockHit == Blocks.EMERALD_BLOCK && !playerEntity.isSneaking() && tier == 5) {
            itemStack.getTag().putInt("tier", 6);
            world.breakBlock(blockPosition, false);
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(itemUsageContext_1);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        if (!world.isClient && playerEntity.isSneaking()) {
            ContainerProviderRegistry.INSTANCE.openContainer(new Identifier("buffer", "buffer_item"), playerEntity, (buffer)->{
                buffer.writeBlockPos(playerEntity.getBlockPos());
            });
        }
        return new TypedActionResult(ActionResult.PASS, playerEntity.getMainHandStack(), false);
    }
}