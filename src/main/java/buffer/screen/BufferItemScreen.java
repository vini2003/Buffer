package buffer.screen;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.cottonmc.cotton.gui.client.CottonScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tickable;

public class BufferItemScreen extends CottonScreen<BufferItemController> {
	BufferItemController bufferController;

	public BufferItemScreen(BufferItemController newController, PlayerEntity player) {
		super(newController, player);
		this.bufferController = newController;
	}
}
