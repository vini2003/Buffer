package buffer.registry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ItemGroupRegistry {
    public static final Identifier IDENTIFIER_GROUP = new Identifier("buffer", "general");

    public static ItemGroup BUFFER_GROUP;

    public static void registerGroups() {
        BUFFER_GROUP = FabricItemGroupBuilder.build(new Identifier("buffer", "general"), () -> new ItemStack(ItemRegistry.BUFFER_ITEM));
   }
}