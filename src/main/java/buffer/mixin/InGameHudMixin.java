package buffer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import buffer.item.BufferItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

    @Inject(method = "render(F)V", at = @At("RETURN"))
    protected void render(float c, CallbackInfo info) {
        GuiLighting.enableForItems();
        itemRenderer.renderGuiItem(BufferItem.stackToDraw, 16, MinecraftClient.getInstance().window.getScaledHeight() - 32);
        GuiLighting.disable();
    }
}