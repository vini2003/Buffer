package buffer.registry;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.container.BlockContext;
import net.minecraft.util.Identifier;
import buffer.screen.BufferEntityController;
import buffer.screen.BufferItemController;

public class ScreenRegistryServer {
    public static void registerScreens() {
        ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier("buffer", "buffer"), (syncId, id, player, buf) -> new BufferEntityController(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())));
        ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier("buffer", "buffer_item"), (syncId, id, player, buf) -> new BufferItemController(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())));
    }
}