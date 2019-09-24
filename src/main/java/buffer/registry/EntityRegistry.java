package buffer.registry;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import buffer.entity.BufferEntity;

public class EntityRegistry {
    public static final BlockEntityType<BufferEntity> ENTITY_TESSERACT = BlockEntityType.Builder.create(BufferEntity::new, BlockRegistry.BLOCK_TESSERACT).build(null);

    public static void registerBlocks() {
        Registry.register(Registry.BLOCK_ENTITY, new Identifier("buffer", "buffer"), ENTITY_TESSERACT);
    }
}