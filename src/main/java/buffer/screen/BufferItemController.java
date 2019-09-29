package buffer.screen;

import buffer.inventory.BufferInventory;
import buffer.item.BufferItem;
import buffer.registry.NetworkRegistry;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class BufferItemController extends BufferBaseController {
    protected final static Identifier offImage = new Identifier("buffer:textures/gui/toggle_off.png");
	protected final static Identifier onImage  = new Identifier("buffer:textures/gui/toggle_on.png");

    public BufferItemController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(syncId, playerInventory, context);
        super.playerInventory = playerInventory;
        super.bufferInventory = BufferInventory.fromTag(playerInventory.getMainHandStack().getTag());
        super.setBaseWidgets();
        this.setItemWidgets();
        super.rootPanel.validate(this);
    }

    public void setItemWidgets() {
        Text voidText;

        WToggleButton togglePickup = new WToggleButton(onImage, offImage);
        WToggleButton toggleVoid = new WToggleButton(onImage, offImage);

        WLabel pickupLabel = new WLabel(new TranslatableText("buffer.gui.pickup"), WLabel.DEFAULT_TEXT_COLOR);
        WLabel voidLabel = new WLabel(voidText = new TranslatableText("buffer.gui.void"), WLabel.DEFAULT_TEXT_COLOR);

        togglePickup.setToggle(super.bufferInventory.isPickup);
        toggleVoid.setToggle(super.bufferInventory.isVoid);

        togglePickup.setOnToggle(() -> {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(NetworkRegistry.createBufferPickupPacket(togglePickup.getToggle()));
            bufferInventory.isPickup = togglePickup.getToggle();
            super.playerInventory.getMainHandStack().getTag().putBoolean(BufferInventory.PICKUP_RETRIEVER, togglePickup.getToggle());
        });
        toggleVoid.setOnToggle(() -> {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(NetworkRegistry.createBufferVoidPacket(toggleVoid.getToggle()));
            bufferInventory.isVoid = toggleVoid.getToggle();
            super.playerInventory.getMainHandStack().getTag().putBoolean(BufferInventory.VOID_RETRIEVER, toggleVoid.getToggle());
        });
        
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        if (super.bufferInventory.getTier() <= 3) {
            this.rootPanel.add(this.createPlayerInventoryPanel(), 0, sectionY * 4);
            super.rootPanel.add(togglePickup, 5, sectionY * 2 + 9);
            super.rootPanel.add(toggleVoid, 139, sectionY * 2 + 9);
            super.rootPanel.add(pickupLabel, 27, sectionY * 2 + 15);
            super.rootPanel.add(voidLabel, 135 - (textRenderer.getStringWidth(voidText.asString())), sectionY * 2 + 15);
        } else {
            this.rootPanel.add(this.createPlayerInventoryPanel(), 0, sectionY * 5 + 18);
            super.rootPanel.add(togglePickup, 5, sectionY * 3 + 8 + 18);
            super.rootPanel.add(toggleVoid, 139, sectionY * 3 + 8 + 18);
            super.rootPanel.add(pickupLabel, 27, sectionY * 3 + 8 + 18 + 6);
            super.rootPanel.add(voidLabel, 135 - (textRenderer.getStringWidth(voidText.asString())), sectionY * 3 + 8 + 18 + 6);       
        }
    }

    @Override
    public void close(PlayerEntity playerEntity) {
        ItemStack itemStack = playerEntity.getMainHandStack();
        itemStack.setTag(BufferInventory.toTag(bufferInventory, new CompoundTag()));
        BufferItem.stackToDraw = ItemStack.EMPTY;
        BufferItem.amountToDraw = 0;   
        super.close(playerEntity);
    }
}