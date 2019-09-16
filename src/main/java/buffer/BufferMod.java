package buffer;

import net.fabricmc.api.ModInitializer;
import buffer.registry.BlockRegistry;
import buffer.registry.EntityRegistry;
import buffer.registry.ItemRegistry;
import buffer.registry.ScreenRegistryServer;

public class BufferMod implements ModInitializer {
	@Override
	public void onInitialize() {
		BlockRegistry.registerBlocks();
		ItemRegistry.registerItems();
		EntityRegistry.registerBlocks();
		ScreenRegistryServer.registerScreens();
	}
}
