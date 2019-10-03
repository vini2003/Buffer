package buffer.utility;

import blue.endless.jankson.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

/**
 * Wrapper extension of ItemUsageContext.
 */
public class BufferUsageContext extends ItemUsageContext {
    public BufferUsageContext(World world, @Nullable PlayerEntity playerEntity, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult) {
        super(world, playerEntity, hand, itemStack, blockHitResult);
    }
}