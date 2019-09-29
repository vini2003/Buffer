package buffer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import buffer.item.BufferItem;
import buffer.registry.ItemRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    private ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    private TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    @Inject(method = "render(F)V", at = @At("RETURN"))
    private void render(float c, CallbackInfo info) {
        if (MinecraftClient.getInstance().player.getMainHandStack().getItem() != ItemRegistry.BUFFER_ITEM) {
            BufferItem.amountToDraw = 0;
            BufferItem.stackToDraw = ItemStack.EMPTY;
        }
        if (BufferItem.stackToDraw != ItemStack.EMPTY && BufferItem.amountToDraw > 0) {
            GuiLighting.enableForItems();
            itemRenderer.renderGuiItem(BufferItem.stackToDraw, 16, MinecraftClient.getInstance().window.getScaledHeight() - 32);
            GuiLighting.disable();
            textRenderer.draw(Integer.toString(BufferItem.amountToDraw), 36, MinecraftClient.getInstance().window.getScaledHeight() - 27, 0xFFFFFF);
        }
    }
}