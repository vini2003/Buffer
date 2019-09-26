package buffer.screen;

import io.github.cottonmc.cotton.gui.client.CottonScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tickable;

public class BufferItemScreen extends CottonScreen<BufferItemController> implements Tickable {
	BufferItemController bufferController;

	public BufferItemScreen(BufferItemController newController, PlayerEntity player) {
		super(newController, player);
		this.bufferController = newController;
	}

	@Override
	public void tick() {
		super.tick();
		bufferController.tick();
	}
}
