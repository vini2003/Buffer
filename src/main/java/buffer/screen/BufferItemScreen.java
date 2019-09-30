package buffer.screen;

import io.github.cottonmc.cotton.gui.client.CottonScreen;
import net.minecraft.entity.player.PlayerEntity;

public class BufferItemScreen extends CottonScreen<BufferItemController> {
	BufferItemController controller;

	public BufferItemScreen(BufferItemController newController, PlayerEntity player) {
		super(newController, player);
		this.controller = newController;
	}

	@Override
	public void tick() {
		super.tick();
		controller.tick();
	}
}