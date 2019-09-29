package buffer.registry;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.container.BlockContext;
import net.minecraft.util.Identifier;
import buffer.screen.BufferEntityController;
import buffer.screen.BufferItemController;

public class ScreenRegistryServer {
    public static final Identifier BUFFER_BLOCK_CONTAINER = new Identifier("buffer", "buffer_block");
    public static final Identifier BUFFER_ITEM_CONTAINER = new Identifier("buffer", "buffer_item");

    public static void registerScreens() {
        ContainerProviderRegistry.INSTANCE.registerFactory(BUFFER_BLOCK_CONTAINER, (syncId, id, player, buf) -> new BufferEntityController(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())));
        ContainerProviderRegistry.INSTANCE.registerFactory(BUFFER_ITEM_CONTAINER, (syncId, id, player, buf) -> new BufferItemController(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())));
    }
}