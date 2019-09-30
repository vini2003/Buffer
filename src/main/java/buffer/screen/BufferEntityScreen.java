package buffer.screen;

import io.github.cottonmc.cotton.gui.client.CottonScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tickable;

public class BufferEntityScreen extends CottonScreen<BufferEntityController> implements Tickable {
	public BufferEntityScreen(BufferEntityController newController, PlayerEntity player) {
		super(newController, player);
	}
}