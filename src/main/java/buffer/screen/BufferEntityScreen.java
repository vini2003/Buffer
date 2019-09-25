package buffer.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class BufferEntityScreen extends CottonInventoryScreen<BufferEntityController> {
	BufferEntityController controller;

	public BufferEntityScreen(BufferEntityController newController, PlayerEntity player) {
		super(newController, player);
		this.controller = newController;
	}

	@Override
	public void tick() {
		super.tick();
		controller.screenTick();
	}
}
