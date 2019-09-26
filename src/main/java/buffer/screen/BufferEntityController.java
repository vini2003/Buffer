package buffer.screen;

import buffer.entity.BufferEntity;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerInventory;

public class BufferEntityController extends BufferBaseController {
    public BufferEntity getBufferEntity(BlockContext context) {
        BufferEntity lambdaBypass[] = { null };

        context.run((world, blockPosition) -> {
            BufferEntity temporaryEntity = (BufferEntity)world.getBlockEntity(blockPosition);
            lambdaBypass[0] = temporaryEntity;
        });

        return lambdaBypass[0];
    }

    public BufferEntityController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(syncId, playerInventory, context);
        super.playerInventory = playerInventory;
        super.bufferInventory = ((BufferEntity)getBufferEntity(context)).bufferInventory;
        super.setWidgets();
        super.rootPanel.validate(this);
    }
}