package buffer.registry;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import buffer.block.BlockBuffer;

public class BlockRegistry {
    public static final BlockBuffer BLOCK_TESSERACT = new BlockBuffer(FabricBlockSettings.of(Material.METAL).strength(3.0F, 0.0F).sounds(BlockSoundGroup.METAL).build());

    public static void registerBlocks() {
        Registry.register(Registry.BLOCK, new Identifier("buffer", "buffer"), BLOCK_TESSERACT);
    }
}