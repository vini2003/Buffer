package buffer;

import buffer.registry.KeybindRegistry;
import buffer.registry.ScreenRegistryClient;
import net.fabricmc.api.ClientModInitializer;

/**
 * Clientside mod initialization.
 */
public class BufferModClient implements ClientModInitializer {
	/**
	 * Override Fabric 'onInitializeClient' to register Buffer's things.
	 */
	@Override
	public void onInitializeClient() {
		ScreenRegistryClient.registerScreens();
		KeybindRegistry.registerKeybinds();
	}
}
