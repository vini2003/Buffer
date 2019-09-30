package buffer.screen;

import io.github.cottonmc.cotton.gui.client.CottonScreen;
import net.minecraft.entity.player.PlayerEntity;

public class BufferItemScreen extends CottonScreen<BufferItemController> {
	public BufferItemScreen(BufferItemController newController, PlayerEntity player) {
		super(newController, player);
	}
}
