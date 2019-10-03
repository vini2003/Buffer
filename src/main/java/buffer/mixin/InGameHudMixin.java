package buffer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import buffer.item.BufferItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Clientside Mixin into InGameHude to add Buffer HUD rendering code.
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    private ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    private TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    /**
     * Intercept 'render' and draw Buffer HUD data.
     * @param value Float used by Minecraft internally.
     * @param info Mixin CallbackInfo.
     */
    @Inject(method = "render(F)V", at = @At("RETURN"))
    private void render(float value, CallbackInfo info) {
        PlayerEntity playerEntity = MinecraftClient.getInstance().player;
        if (BufferItem.isBeingHeld(playerEntity) == ItemStack.EMPTY) {
            BufferItem.amountToDraw = 0;
            BufferItem.stackToDraw = ItemStack.EMPTY;
        } else if (BufferItem.stackToDraw != ItemStack.EMPTY && BufferItem.amountToDraw > 0) {
            GuiLighting.enableForItems();
            itemRenderer.renderGuiItem(BufferItem.stackToDraw, 16, MinecraftClient.getInstance().window.getScaledHeight() - 32);
            GuiLighting.disable();
            textRenderer.draw(Integer.toString(BufferItem.amountToDraw), 36, MinecraftClient.getInstance().window.getScaledHeight() - 27, 0xFFFFFF);
        }
    }
}