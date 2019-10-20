package buffer.registry;

import buffer.BufferMod;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * Commonside ItemGroup registry.
 */
public class ItemGroupRegistry {
	public static ItemGroup BUFFER_GROUP;

	/**
	 * Register all of Buffer's ItemGroups.
	 */
	public static void registerGroups() {
		BUFFER_GROUP = FabricItemGroupBuilder.build(new Identifier(BufferMod.MOD_ID, "buffer"), () -> new ItemStack(ItemRegistry.BUFFER_ITEM));
	}
}