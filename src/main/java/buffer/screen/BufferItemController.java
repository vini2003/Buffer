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
import net.minecraft.util.Identifier;

public class BufferItemController extends BufferBaseController {
    protected final static Identifier baseImage  = new Identifier("buffer:textures/gui/toggle_base.png");
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
        WToggleButton togglePickup = new WToggleButton(onImage, offImage);
        WToggleButton toggleVoid = new WToggleButton(onImage, offImage);
        WLabel pickupLabel = new WLabel("Collect");
        WLabel voidLabel = new WLabel("Void");

        rootPanel.add(controllerLabels.get(4), sectionX * 2 - 27, sectionY * 2 + 26);        
        rootPanel.add(controllerLabels.get(5), sectionX * 3 - 18, sectionY * 2 + 26);  

        togglePickup.setToggle(super.bufferInventory.isPickup);
        toggleVoid.setToggle(super.bufferInventory.isVoid);

        togglePickup.setOnToggle(() -> {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(NetworkRegistry.createBufferVoidPacket(togglePickup.getToggle()));
            bufferInventory.isPickup = togglePickup.getToggle();
            super.playerInventory.getMainHandStack().getTag().putBoolean(BufferInventory.PICKUP_RETRIEVER(), togglePickup.getToggle());
        });
        toggleVoid.setOnToggle(() -> {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(NetworkRegistry.createBufferVoidPacket(toggleVoid.getToggle()));
            bufferInventory.isVoid = toggleVoid.getToggle();
            super.playerInventory.getMainHandStack().getTag().putBoolean(BufferInventory.VOID_RETRIEVER(), toggleVoid.getToggle());
        });
        
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        if (super.bufferInventory.getTier() <= 3) {
            super.rootPanel.add(togglePickup, 5, sectionY * 2 + 18);
            super.rootPanel.add(toggleVoid, 139, sectionY * 2 + 18);
            super.rootPanel.add(pickupLabel, 27, sectionY * 2 + 24);
            super.rootPanel.add(voidLabel, 135 - (textRenderer.getStringWidth("Void")), sectionY * 2 + 24);
        } else {
            super.rootPanel.add(togglePickup, 5, sectionY * 2 + 18);
            super.rootPanel.add(toggleVoid, 139, sectionY * 2 + 18);
            super.rootPanel.add(pickupLabel, 27, sectionY * 2 + 24);
            super.rootPanel.add(voidLabel, 135 - (textRenderer.getStringWidth("Void")), sectionY * 2 + 24);
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