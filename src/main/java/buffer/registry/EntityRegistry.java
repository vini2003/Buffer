package buffer.registry;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import buffer.entity.EntityBuffer;

public class EntityRegistry {
    public static final BlockEntityType<EntityBuffer> ENTITY_TESSERACT = BlockEntityType.Builder.create(EntityBuffer::new, BlockRegistry.BLOCK_TESSERACT).build(null);

    public static void registerBlocks() {
        Registry.register(Registry.BLOCK_ENTITY, new Identifier("buffer", "buffer"), ENTITY_TESSERACT);
    }
}