package buffer.block;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import buffer.entity.EntityBuffer;
import buffer.utility.BufferProvider;

public class BlockBuffer extends Block implements BlockEntityProvider {
    public BlockBuffer(Block.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateFactory.getDefaultState()
            .with(BufferProvider.one, false)
            .with(BufferProvider.two, false)
            .with(BufferProvider.three, false)
            .with(BufferProvider.four, false)
            .with(BufferProvider.five, false)
            .with(BufferProvider.six, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new EntityBuffer();
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (!world.isClient) {
            ContainerProviderRegistry.INSTANCE.openContainer(new Identifier("buffer", "buffer"), playerEntity, (buffer)->{
                buffer.writeBlockPos(playerEntity.getBlockPos());
            });
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(BufferProvider.one, BufferProvider.two, BufferProvider.three, BufferProvider.four, BufferProvider.five, BufferProvider.six);
    }

}