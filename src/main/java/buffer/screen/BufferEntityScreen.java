package buffer.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tickable;

public class BufferEntityScreen extends CottonInventoryScreen<BufferEntityController> implements Tickable {
	BufferEntityController bufferController;

	public BufferEntityScreen(BufferEntityController newController, PlayerEntity player) {
		super(newController, player);
		this.bufferController = newController;
	}

	@Override
	public void tick() {
		super.tick();
		bufferController.tick();
	}
}
