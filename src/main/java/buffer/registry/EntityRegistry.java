package buffer.registry;

import buffer.BufferMod;
import buffer.entity.BufferEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Commonside Entity registry.
 */
public class EntityRegistry {
	public static BlockEntityType<BufferEntity> ENTITY_BUFFER;

	/**
	 * Register all of Buffer's Entities.
	 */
	public static void registerEntities() {
		ENTITY_BUFFER = Registry.register(Registry.BLOCK_ENTITY, new Identifier(BufferMod.MOD_ID, "buffer"), BlockEntityType.Builder.create(BufferEntity::new, BlockRegistry.BLOCK_BUFFER).build(null));
	}
}