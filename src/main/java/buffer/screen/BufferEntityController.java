package buffer.screen;

import buffer.entity.BufferEntity;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

public class BufferEntityController extends BufferBaseController {
    BufferEntity bufferEntity;

    public BufferEntity getBufferEntity(BlockContext context) {
        BufferEntity[] lambdaBypass = new BufferEntity[1];

        context.run((world, blockPosition) -> {
            BufferEntity temporaryEntity = (BufferEntity)world.getBlockEntity(blockPosition);
            lambdaBypass[0] = temporaryEntity;
        });

        return lambdaBypass[0];
    }

    public BufferEntityController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(syncId, playerInventory, context);
        super.playerInventory = playerInventory;
        this.bufferEntity = getBufferEntity(context);
        super.bufferInventory = bufferEntity.bufferInventory;
        super.setBaseWidgets();
        super.rootPanel.validate(this);
    }

    @Override
    public void close(PlayerEntity playerEntity) {
        bufferEntity.bufferController = null;
        super.close(playerEntity);
    }
}