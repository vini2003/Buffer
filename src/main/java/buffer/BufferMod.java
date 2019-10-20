package buffer;

import buffer.registry.BlockRegistry;
import buffer.registry.EntityRegistry;
import buffer.registry.ItemGroupRegistry;
import buffer.registry.ItemRegistry;
import buffer.registry.NetworkRegistry;
import buffer.registry.ScreenRegistryServer;
import net.fabricmc.api.ModInitializer;

/**
 * Commonside mod initialization.
 */
public class BufferMod implements ModInitializer {
	public static final String MOD_ID = "buffer";

	/**
	 * Override Fabric 'onInitialize' to register Buffer's things.
	 */
	@Override
	public void onInitialize() {
		ItemGroupRegistry.registerGroups();
		BlockRegistry.registerBlocks();
		ItemRegistry.registerItems();
		EntityRegistry.registerEntities();
		ScreenRegistryServer.registerScreens();
		NetworkRegistry.registerPackets();
	}
}
