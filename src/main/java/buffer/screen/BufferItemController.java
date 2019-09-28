package buffer.screen;

import buffer.inventory.BufferInventory;
import buffer.item.BufferItem;
import buffer.utility.BufferProvider;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class BufferItemController extends BufferBaseController {
    public BufferItemController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(syncId, playerInventory, context);
        super.playerInventory = playerInventory;
        super.bufferInventory = BufferInventory.fromTag(playerInventory.getMainHandStack().getTag());
        super.setWidgets();
        super.rootPanel.validate(this);
    }

    @Override
    public void close(PlayerEntity playerEntity) {
        ItemStack itemStack = playerEntity.getMainHandStack();
        itemStack.setTag(BufferInventory.toTag(bufferInventory, new CompoundTag()));
        BufferItem.stackToDraw = ItemStack.EMPTY;   
        super.close(playerEntity);
    }
}