package buffer.registry;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.container.BlockContext;
import net.minecraft.util.Identifier;
import buffer.screen.BufferController;

public class ScreenRegistryServer {
    public static void registerScreens() {
        ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier("buffer", "buffer"), (syncId, id, player, buf) -> new BufferController(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())));
    }
}