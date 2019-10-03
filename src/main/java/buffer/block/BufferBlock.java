package buffer.block;

import blue.endless.jankson.annotation.Nullable;
import buffer.entity.BufferEntity;
import buffer.inventory.BufferInventory;
import buffer.registry.BlockRegistry;
import buffer.registry.ItemRegistry;
import buffer.registry.ScreenRegistryServer;
import buffer.screen.BufferEntityController;
import buffer.utility.BufferTier;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * BufferBlock extension of Block which implements a block entity provider.
 */
public class BufferBlock extends Block implements BlockEntityProvider {
    /**
     * Default constructor with default state.
     * @param settings
     */
    public BufferBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateFactory.getDefaultState()
            .with(BufferTier.bufferTier, 1));
    }

    /**
     * Override vanilla 'createBlockEntity'.
     */
    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new BufferEntity();
    }

    /**
     * Override vanilla 'activate' implemeting custom container.
     */
    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (!world.isClient) {
            ContainerProviderRegistry.INSTANCE.openContainer(ScreenRegistryServer.BUFFER_BLOCK_CONTAINER, playerEntity, (buffer)-> buffer.writeBlockPos(blockPos));
            BufferEntity bufferEntity = ((BufferEntity)world.getBlockEntity(blockPos));
            bufferEntity.bufferController = (BufferEntityController)playerEntity.container;
        }
        return true;
    }
    
    /**
     * Override vanilla 'onPlaced' implementing special code to configure created BufferEntity.
     */
    @Override
    public void onPlaced(World world, BlockPos blockPosition, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        if (itemStack.getItem() == ItemRegistry.BUFFER_ITEM) {
            CompoundTag itemTag = itemStack.getTag();
            BufferEntity bufferEntity = (BufferEntity)world.getBlockEntity(blockPosition);
            BufferInventory inventoryMirror = bufferEntity.bufferInventory;
            int bufferTier = itemTag.getInt(BufferInventory.TIER_RETRIEVER);
            inventoryMirror.setTier(bufferTier);
            world.setBlockState(blockPosition, BlockRegistry.BLOCK_BUFFER.getDefaultState().with(BufferTier.bufferTier, inventoryMirror.getTier()));
        }
        super.onPlaced(world, blockPosition, blockState, livingEntity, itemStack);
    }

    /**
     * Override vanilla 'afterBreak' implementing special code to configure drop result.
     */
    @Override
    public void afterBreak(World world, PlayerEntity playerEntity, BlockPos blockPosition, BlockState blockState, @Nullable BlockEntity blockEntity_1, ItemStack itemStack_1) {
        playerEntity.incrementStat(Stats.MINED.getOrCreateStat(this));
        playerEntity.addExhaustion(0.005F);
        ItemStack itemStack = new ItemStack(ItemRegistry.BUFFER_ITEM, 1);
        itemStack.setTag(BufferInventory.toTag(((BufferEntity)blockEntity_1).bufferInventory, new CompoundTag()));
        dropStack(world, blockPosition, itemStack);
    }
    
    /**
     * Override vanilla 'appendProperties' implementing BufferTier.
     */
    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(BufferTier.bufferTier);
    }

}