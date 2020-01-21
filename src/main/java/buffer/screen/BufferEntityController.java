package buffer.screen;

import buffer.entity.BufferEntity;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerInventory;

/**
 * Extended Container/Controller for usage with BufferEntity, implements custom methods and
 * widgets.
 */
public class BufferEntityController extends BufferBaseController {
	BufferEntity bufferEntity;

	/**
	 * Customized constructor which configures the Container/Controller for a BufferEntity.
	 * Sets custom widgets, obtains BufferEntity, PlayerInventory, and creates a BufferInventory.
	 *
	 * @param syncId          ID for Container/Controller synchronization.
	 * @param playerInventory PlayerInventory from player who opened container.
	 * @param context         BlockContext for opened container.
	 */
	public BufferEntityController(int syncId, PlayerInventory playerInventory, BlockContext context) {
		super(syncId, playerInventory, context);
		super.playerInventory = playerInventory;
		getBufferEntity(context);
		super.bufferInventory = bufferEntity.bufferInventory;
		addBaseWidgets();
		addEntityWidgets();
		super.rootPanel.validate(this);
	}

	/**
	 * Gets BufferEntity based on BlockContext.
	 *
	 * @param context Block context of opened interface.
	 * @return BufferEntity of opened interface.
	 */
	public void getBufferEntity(BlockContext context) {
		BufferEntity[] lambdaBypass = new BufferEntity[1];

		context.run((world, blockPosition) -> {
			BufferEntity temporaryEntity = (BufferEntity) world.getBlockEntity(blockPosition);
			lambdaBypass[0] = temporaryEntity;
		});

		this.bufferEntity = lambdaBypass[0];
	}

	/**
	 * Add base widget(s) used by BufferEntity to Container/Controller.
	 */
	public void addEntityWidgets() {
		super.rootPanel.add(super.createPlayerInventoryPanel(), 0, super.bufferInventory.getTier() <= 3 ? SECTION_Y * 3 : SECTION_Y * 4 + 18);
	}
}