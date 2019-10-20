package buffer.registry;

import buffer.screen.BufferEntityController;
import buffer.screen.BufferEntityScreen;
import buffer.screen.BufferItemController;
import buffer.screen.BufferItemScreen;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.BlockContext;

/**
 * Clientside Screen & Container registry.
 */
public class ScreenRegistryClient {
	/**
	 * Register clientside Screen(s) & Container(s).
	 */
	public static void registerScreens() {
		ScreenProviderRegistry.INSTANCE.registerFactory(ScreenRegistryServer.BUFFER_BLOCK_CONTAINER,
				(syncId, identifier, player, buf) -> new BufferEntityScreen(new BufferEntityController(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
		ScreenProviderRegistry.INSTANCE.registerFactory(ScreenRegistryServer.BUFFER_ITEM_CONTAINER,
				(syncId, identifier, player, buf) -> new BufferItemScreen(new BufferItemController(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
	}
}