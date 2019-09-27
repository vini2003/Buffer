package buffer;

import net.fabricmc.api.ClientModInitializer;
import buffer.registry.KeybindRegistry;
import buffer.registry.ScreenRegistryClient;

public class BufferModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ScreenRegistryClient.registerScreens();
		KeybindRegistry.registerKeybinds();
	}
}
