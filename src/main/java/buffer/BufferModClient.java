package buffer;

import net.fabricmc.api.ClientModInitializer;
import buffer.registry.KeybindRegistry;
import buffer.registry.ScreenRegistryClient;

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
