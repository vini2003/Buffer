package buffer.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tickable;

public class BufferItemScreen extends CottonInventoryScreen<BufferItemController> implements Tickable {
	BufferItemController controller;

	public BufferItemScreen(BufferItemController newController, PlayerEntity player) {
		super(newController, player);
		this.controller = newController;
	}

	@Override
	public void tick() {
		super.tick();
		controller.screenTick();
	}
}
