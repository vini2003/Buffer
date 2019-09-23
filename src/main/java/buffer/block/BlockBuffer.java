package buffer.block;

import buffer.entity.EntityBuffer;
import buffer.inventory.InventoryBuffer;
import buffer.registry.BlockRegistry;
import buffer.utility.BufferProvider;
import buffer.utility.BufferType;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.StateFactory;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockBuffer extends Block implements BlockEntityProvider {
    public BlockBuffer(Block.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateFactory.getDefaultState()
            .with(BufferProvider.tier, 1));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new EntityBuffer();
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (!world.isClient) {
            ContainerProviderRegistry.INSTANCE.openContainer(new Identifier("buffer", "buffer"), playerEntity, (buffer)->{
                buffer.writeBlockPos(blockPos);
            });
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onPlaced(World world, BlockPos blockPosition, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        CompoundTag itemTag = itemStack.getTag();
        EntityBuffer bufferEntity = (EntityBuffer)world.getBlockEntity(blockPosition);
        InventoryBuffer inventoryMirror = bufferEntity.bufferInventory;
        inventoryMirror.setType(itemTag);
        if (inventoryMirror.getType() == null) {
            if (itemTag == null || !itemTag.containsKey("tier")) {
                itemTag = new CompoundTag();
                itemTag.putInt("tier", 1);
                itemStack.setTag(itemTag);
            }
        }

        Integer tier = itemTag.getInt("tier");
        inventoryMirror.setType(BufferType.fromInt(tier));

        world.setBlockState(blockPosition, BlockRegistry.BLOCK_TESSERACT.getDefaultState().with(BufferProvider.tier, inventoryMirror.getType().toInt()));
        super.onPlaced(world, blockPosition, blockState, livingEntity, itemStack);
    }
    
    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(BufferProvider.tier);
    }

}