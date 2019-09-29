package buffer.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import buffer.item.BufferItem;

public class ItemRegistry {
    public static final Identifier IDENTIFIER_BUFFER = new Identifier("buffer", "buffer");

    public static BufferItem BUFFER_ITEM;

    public static void registerItems() {
        BUFFER_ITEM = Registry.register(Registry.ITEM, IDENTIFIER_BUFFER, new BufferItem(BlockRegistry.BLOCK_BUFFER, new Item.Settings().group(ItemGroup.MISC)));
   }
}