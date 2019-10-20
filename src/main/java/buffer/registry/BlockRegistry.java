package buffer.registry;

import buffer.BufferMod;
import buffer.block.BufferBlock;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Commonside Block registry.
 */
public class BlockRegistry {
	public static BufferBlock BLOCK_BUFFER;

	/**
	 * Register all of Buffer's Blocks.
	 */
	public static void registerBlocks() {
		BLOCK_BUFFER = Registry.register(Registry.BLOCK, new Identifier(BufferMod.MOD_ID, "buffer"), new BufferBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 0.0F).sounds(BlockSoundGroup.METAL).build()));
	}
}