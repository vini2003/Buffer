package buffer.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import buffer.item.BufferItem;

public class ItemRegistry {
    public static final Identifier ITEM_TESSERACT = new Identifier("buffer", "buffer");

    public static void registerItems() {
        Registry.register(Registry.ITEM, ITEM_TESSERACT, new BufferItem(BlockRegistry.BLOCK_TESSERACT, new Item.Settings().group(ItemGroup.MISC)));
   }
}