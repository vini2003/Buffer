package buffer.mixin;

import buffer.inventory.BufferInventory;
import buffer.registry.ItemRegistry;
import buffer.screen.BufferItemController;
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

import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableList;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

	@Shadow
	private UUID owner;

	@Shadow
	private int age;

	@Shadow
	private int pickupDelay;

	@Inject(method = "onPlayerCollision", at = @At("HEAD"))
	private void onPlayerCollision(PlayerEntity playerEntity, CallbackInfo info) {
		ItemEntity itemEntity = (ItemEntity) (Object) this;
		ItemStack buffer = ItemStack.EMPTY;

		List<DefaultedList<ItemStack>> combinedInventory = ImmutableList.of(playerEntity.inventory.main, playerEntity.inventory.offHand);

		for (DefaultedList<ItemStack> list : combinedInventory) {
			if (buffer.getItem() == ItemRegistry.BUFFER_ITEM) {
				break;
			}
			for (ItemStack stack : list) {
				if (stack.getItem() == ItemRegistry.BUFFER_ITEM) {
					buffer = stack;
					break;
				}
			}
		}

		if (!itemEntity.world.isClient && buffer != ItemStack.EMPTY && !(playerEntity.container instanceof BufferItemController) && buffer.getTag().getBoolean("pickup")) {
			ItemStack itemStack = itemEntity.getStack();
			if (itemStack.getItem() != ItemRegistry.BUFFER_ITEM && pickupDelay == 0 && (owner == null || 6000 - age <= 200 || owner.equals(playerEntity.getUuid()))) {
				BufferInventory bufferInventory = BufferInventory.fromTag(buffer.getTag());
				ItemStack tryInsertStack = bufferInventory.insertStack(itemStack);
				itemEntity.setStack(tryInsertStack);
				buffer.setTag(BufferInventory.toTag(bufferInventory, new CompoundTag()));
				playerEntity.sendPickup(itemEntity, itemStack.getCount());
				playerEntity.increaseStat(Stats.PICKED_UP.getOrCreateStat(itemStack.getItem()), itemStack.getCount());
			}
		}
	}
}