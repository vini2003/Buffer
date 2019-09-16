package buffer.registry;

import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.BlockContext;
import net.minecraft.util.Identifier;
import buffer.screen.BufferController;
import buffer.screen.BufferScreen;

public class ScreenRegistryClient {
    public static void registerScreens() {
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier("buffer", "buffer"), (syncId, identifier, player, buf) -> new BufferScreen(new BufferController(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
    }
}