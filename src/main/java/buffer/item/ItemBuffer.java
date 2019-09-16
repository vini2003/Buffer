package buffer.item;

import buffer.inventory.InventoryBuffer;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ItemBuffer extends BlockItem implements BufferProvider {
    public InventoryBuffer bufferInventory = new InventoryBuffer();

    public ItemBuffer(Block block, Item.Settings properties) {
        super(block, properties);
    }

    @Override
    public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos) {
        return bufferInventory;
    }

    @Override
    public ActionResult place(ItemPlacementContext placementContext) {
        if (super.place(placementContext) == ActionResult.SUCCESS) {
            World world = placementContext.getWorld();
            BlockPos blockPosition = placementContext.getBlockPos();
            world.setBlockState(blockPosition, BlockRegistry.BLOCK_TESSERACT.getDefaultState().with(bufferInventory.getType().asProperty(), true));
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.FAIL;
        }
    }

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

        if (blockHit == Blocks.COAL_BLOCK && !playerEntity.isSneaking()) {
            this.bufferInventory.setType(BufferType.TWO);
        } 
        if (blockHit == Blocks.IRON_BLOCK && !playerEntity.isSneaking()) {
            this.bufferInventory.setType(BufferType.THREE);
        }
        if (blockHit == Blocks.GOLD_BLOCK && !playerEntity.isSneaking()) {
            this.bufferInventory.setType(BufferType.FOUR);
        }
        if (blockHit == Blocks.DIAMOND_BLOCK && !playerEntity.isSneaking()) {
            this.bufferInventory.setType(BufferType.FIVE);
        }
        if (blockHit == Blocks.EMERALD_BLOCK && !playerEntity.isSneaking()) {
            this.bufferInventory.setType(BufferType.SIX);
        }

        return super.useOnBlock(itemUsageContext_1);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        if (this.bufferInventory.getType() == null) {
            this.bufferInventory.setType(playerEntity.inventory.getMainHandStack().getTag());
        }
        if (world.isClient && playerEntity.isSneaking()) {
            ContainerProviderRegistry.INSTANCE.openContainer(new Identifier("buffer", "buffer"), playerEntity, (buffer)->{
                buffer.writeBlockPos(playerEntity.getBlockPos());
            });
        }
        return super.use(world, playerEntity, hand);
    }
}