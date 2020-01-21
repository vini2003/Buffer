package buffer.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Extended Screen for usage with BufferEntity.
 */
public class BufferEntityScreen extends CottonInventoryScreen<BufferEntityController> {
	BufferEntityController controller;

	/**
	 * Customized constructor which configures the Screen for a BufferItem.
	 * Sets cached Container/Controller.
	 *
	 * @param newController Container/Controller linked to Screen.
	 * @param player  Player who opened container.
	 */
	public BufferEntityScreen(BufferEntityController newController, PlayerEntity player) {
		super(newController, player);
		this.controller = newController;
	}

	/**
	 * Overrides default tick behaviour to update Container/Controller.
	 */
	@Override
	public void tick() {
		super.tick();
		controller.tick();
	}
}