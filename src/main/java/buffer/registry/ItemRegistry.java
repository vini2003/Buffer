package buffer.registry;

import buffer.BufferMod;
import buffer.item.BufferItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Commonside Item registry.
 */
public class ItemRegistry {
	public static BufferItem BUFFER_ITEM;

	/**
	 * Register all of Buffer's Items.
	 */
	public static void registerItems() {
		BUFFER_ITEM = Registry.register(Registry.ITEM, new Identifier(BufferMod.MOD_ID, "buffer"),
				new BufferItem(BlockRegistry.BLOCK_BUFFER,
						new Item.Settings().group(ItemGroupRegistry.BUFFER_GROUP).maxCount(1)));
	}
}