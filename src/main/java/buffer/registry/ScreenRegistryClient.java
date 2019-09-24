package buffer.registry;

import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.BlockContext;
import net.minecraft.util.Identifier;
import buffer.screen.BufferEntityController;
import buffer.screen.BufferEntityScreen;
import buffer.screen.BufferItemController;
import buffer.screen.BufferItemScreen;

public class ScreenRegistryClient {
    public static void registerScreens() {
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier("buffer", "buffer"), (syncId, identifier, player, buf) -> new BufferEntityScreen(new BufferEntityController(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier("buffer", "buffer_item"), (syncId, identifier, player, buf) -> new BufferItemScreen(new BufferItemController(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
    }
}