package buffer.screen;

import buffer.inventory.BufferInventory;
import buffer.item.BufferItem;
import buffer.registry.ItemRegistry;
import buffer.registry.NetworkRegistry;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;

public class BufferItemController extends BufferBaseController {
    protected Hand hand = Hand.MAIN_HAND;

    public BufferItemController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(syncId, playerInventory, context);
        if (playerInventory.player.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.MAIN_HAND; } else { hand = Hand.OFF_HAND; };
        super.playerInventory = playerInventory;
        super.bufferInventory = BufferInventory.fromTag(playerInventory.player.getStackInHand(hand).getTag());
        super.setBaseWidgets();
        this.setItemWidgets();
        super.rootPanel.validate(this);
    }

    public void setItemWidgets() {
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
        
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        if (super.bufferInventory.getTier() <= 3) {
            super.rootPanel.add(super.createPlayerInventoryPanel(), 0, sectionY * 4);
            super.rootPanel.add(togglePickup, 5, sectionY * 2 + 9);
            super.rootPanel.add(toggleVoid, 139, sectionY * 2 + 9);
            super.rootPanel.add(pickupLabel, 27, sectionY * 2 + 15);
            super.rootPanel.add(voidLabel, 135 - (textRenderer.getStringWidth(voidText.asString())), sectionY * 2 + 15);
        } else {
            super.rootPanel.add(super.createPlayerInventoryPanel(), 0, sectionY * 5 + 18);
            super.rootPanel.add(togglePickup, 5, sectionY * 3 + 8 + 18);
            super.rootPanel.add(toggleVoid, 139, sectionY * 3 + 8 + 18);
            super.rootPanel.add(pickupLabel, 27, sectionY * 3 + 8 + 18 + 6);
            super.rootPanel.add(voidLabel, 135 - (textRenderer.getStringWidth(voidText.asString())), sectionY * 3 + 8 + 18 + 6);       
        }
    }

    @Override
    public void close(PlayerEntity playerEntity) {
        playerEntity.getStackInHand(hand).setTag(BufferInventory.toTag(super.bufferInventory, new CompoundTag()));
        BufferItem.reset();
        super.close(playerEntity);
    }
}