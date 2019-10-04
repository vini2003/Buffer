package buffer.screen;

import buffer.inventory.BufferInventory;
import buffer.item.BufferItem;
import buffer.registry.ItemRegistry;
import buffer.registry.NetworkRegistry;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;

/**
 * Extended Container/Controller for usage with BufferItem, implements custom methods and
 * widgets.
 */
public class BufferItemController extends BufferBaseController {
    protected Hand hand = Hand.MAIN_HAND;

    /**
     * Customized constructor which configures the Container/Controller for a BufferEntity.
     * Sets custom widgets, obtains Hand, PlayerInventory, and creates a BufferInventory.
	 * @param syncID ID for Container/Controller synchronization.
	 * @param playerInventory PlayerInventory from player who opened container.
	 * @param context BlockContext for opened container.
     */
    public BufferItemController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(syncId, playerInventory, context);
        super.playerInventory = playerInventory;
        this.hand = playerInventory.player.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM ? Hand.MAIN_HAND : Hand.OFF_HAND;
        super.bufferInventory = BufferInventory.fromTag(playerInventory.player.getStackInHand(hand).getTag());
        addBaseWidgets();
        addItemWidgets();
        super.rootPanel.validate(this);
    }

    /**
	 * Add base widget(s) used by BufferEntity to Container/Controller.
	 */
    public void addItemWidgets() {
        Text voidText;

        WToggleButton togglePickup = new WToggleButton();
        WToggleButton toggleVoid = new WToggleButton();

        WLabel pickupLabel = new WLabel(new TranslatableText("buffer.gui.pickup"), WLabel.DEFAULT_TEXT_COLOR);
        WLabel voidLabel = new WLabel(voidText = new TranslatableText("buffer.gui.void"), WLabel.DEFAULT_TEXT_COLOR);

        togglePickup.setToggle(bufferInventory.isPickup);
        toggleVoid.setToggle(bufferInventory.isVoid);

        togglePickup.setOnToggle(() -> {
            playerInventory.player.getStackInHand(hand).getTag().putBoolean(BufferInventory.PICKUP_RETRIEVER, togglePickup.getToggle());
            super.bufferInventory.isPickup = togglePickup.getToggle();
            ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.BUFFER_PICKUP_PACKET, NetworkRegistry.createBufferPickupPacket(super.bufferInventory.isPickup));
        });
        toggleVoid.setOnToggle(() -> {
            playerInventory.player.getStackInHand(hand).getTag().putBoolean(BufferInventory.VOID_RETRIEVER, toggleVoid.getToggle());
            super.bufferInventory.isVoid = toggleVoid.getToggle();
            ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.BUFFER_VOID_PACKET, NetworkRegistry.createBufferVoidPacket(super.bufferInventory.isVoid));
        });

        if (super.bufferInventory.getTier() <= 3) {
            super.rootPanel.add(togglePickup, 5, SECTION_Y * 2 + 9);
            super.rootPanel.add(toggleVoid, 139, SECTION_Y * 2 + 9);
            super.rootPanel.add(pickupLabel, 27, SECTION_Y * 2 + 15);
            super.rootPanel.add(voidLabel, 135 - (MinecraftClient.getInstance().textRenderer.getStringWidth(voidText.asString())), SECTION_Y * 2 + 15);
        } else {
            super.rootPanel.add(togglePickup, 5, SECTION_Y * 3 + 8 + 18);
            super.rootPanel.add(toggleVoid, 139, SECTION_Y * 3 + 8 + 18);
            super.rootPanel.add(pickupLabel, 27, SECTION_Y * 3 + 8 + 18 + 6);
            super.rootPanel.add(voidLabel, 135 - (MinecraftClient.getInstance().textRenderer.getStringWidth(voidText.asString())), SECTION_Y * 3 + 8 + 18 + 6);
        }

        super.rootPanel.add(super.createPlayerInventoryPanel(), 0, super.bufferInventory.getTier() <= 3 ? SECTION_Y * 4 : SECTION_Y * 5 + 18);
    }

    /**
     * Override close method to update hand ItemStack NBT, and reset BufferItem's drawing.
     * @param playerEntity Player who closed Container/Controller.
     */
    @Override
    public void close(PlayerEntity playerEntity) {
        playerEntity.getStackInHand(hand).setTag(BufferInventory.toTag(super.bufferInventory, new CompoundTag()));
        BufferItem.clear();
        super.close(playerEntity);
    }
}