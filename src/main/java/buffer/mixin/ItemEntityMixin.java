package buffer.mixin;

import buffer.inventory.BufferInventory;
import buffer.registry.ItemRegistry;
import buffer.screen.BufferItemController;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stats;
import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * Commonside Mixin into ItemEntity to try Buffer insertion before all else.
 */
@Mixin(ItemEntity.class)
public class ItemEntityMixin {
	@Shadow
	private UUID owner;

	@Shadow
	private int age;

	@Shadow
	private int pickupDelay;

	/**
	 * Intercept 'onPlayerCollision' and attempt insertion into Buffer.
	 *
	 * @param playerEntity PlayerEntity involved in collision.
	 * @param info         Mixin CallbackInfo.
	 */
	@Inject(method = "onPlayerCollision", at = @At("HEAD"))
	private void onPlayerCollision(PlayerEntity playerEntity, CallbackInfo info) {
		ItemEntity itemEntity = (ItemEntity) (Object) this;
		ItemStack resultStack = ItemStack.EMPTY;

		ImmutableList<DefaultedList<ItemStack>> inventory = ImmutableList.of(playerEntity.inventory.main, playerEntity.inventory.offHand);

		/**
		 * Attempt to find BufferItem.
		 */
		for (DefaultedList<ItemStack> list : inventory) {
			if (resultStack.getItem() == ItemRegistry.BUFFER_ITEM) {
				break;
			}
			for (ItemStack itemStack : list) {
				if (itemStack.getItem() == ItemRegistry.BUFFER_ITEM) {
					resultStack = itemStack;
					break;
				}
			}
		}

		/**
		 * If found and meets all criteria, perform insertion.
		 */
		if (!itemEntity.world.isClient && resultStack != ItemStack.EMPTY && !(playerEntity.container instanceof BufferItemController) && resultStack.getTag().getBoolean("pickup")) {
			ItemStack itemStack = itemEntity.getStack();
			if (itemStack.getItem() != ItemRegistry.BUFFER_ITEM && pickupDelay == 0 && (owner == null || 6000 - age <= 200 || owner.equals(playerEntity.getUuid()))) {
				BufferInventory bufferInventory = BufferInventory.fromTag(resultStack.getTag());
				ItemStack tryInsertStack = bufferInventory.insertStack(itemStack);
				itemEntity.setStack(tryInsertStack);
				resultStack.setTag(BufferInventory.toTag(bufferInventory, new CompoundTag()));
				playerEntity.sendPickup(itemEntity, itemStack.getCount());
				playerEntity.increaseStat(Stats.PICKED_UP.getOrCreateStat(itemStack.getItem()), itemStack.getCount());
			}
		}
	}
}