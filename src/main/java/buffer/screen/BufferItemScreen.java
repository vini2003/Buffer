package buffer.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Extended Screen for usage with BufferItem.
 */
public class BufferItemScreen extends CottonInventoryScreen<BufferItemController> {
	BufferItemController controller;

	/**
	 * Customized constructor which configures the Screen for a BufferItem.
	 * Sets cached Container/Controller.
	 *
	 * @param openContainer Container/Controller linked to Screen.
	 * @param playerEntity  Player who opened container.
	 */
	public BufferItemScreen(BufferItemController openContainer, PlayerEntity playerEntity) {
		super(openContainer, playerEntity);
		this.controller = openContainer;
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