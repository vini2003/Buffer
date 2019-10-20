package buffer.screen;

import io.github.cottonmc.cotton.gui.client.CottonScreen;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Extended Screen for usage with BufferEntity.
 */
public class BufferEntityScreen extends CottonScreen<BufferEntityController> {
	BufferEntityController controller;

	/**
	 * Customized constructor which configures the Screen for a BufferItem.
	 * Sets cached Container/Controller.
	 *
	 * @param openContainer Container/Controller linked to Screen.
	 * @param playerEntity  Player who opened container.
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