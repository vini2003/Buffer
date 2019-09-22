package buffer.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class BufferScreen extends CottonInventoryScreen<BufferController> {
	BufferController controller;

	public BufferScreen(BufferController newController, PlayerEntity player) {
		super(newController, player);
		this.controller = newController;
	}

	@Override
	public void tick() {
		super.tick();
		controller.tick();
	}
}
