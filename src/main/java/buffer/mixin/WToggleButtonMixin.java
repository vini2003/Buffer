package buffer.mixin;

import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// LibGUI, none of this would be necessary if you backported.
@Mixin(value = WToggleButton.class, remap = false)
public class WToggleButtonMixin {

	@Shadow
	protected Identifier onImage;

	@Shadow
	protected Identifier offImage;

	@Redirect(method = "paintBackground", at = @At(value = "FIELD", target = "Lio/github/cottonmc/cotton/gui/widget/WToggleButton;DEFAULT_ON_IMAGE:Lnet/minecraft/util/Identifier;"))
	private Identifier getOnImage() {
		return onImage;
	}

	@Redirect(method = "paintBackground", at = @At(value = "FIELD", target = "Lio/github/cottonmc/cotton/gui/widget/WToggleButton;DEFAULT_OFF_IMAGE:Lnet/minecraft/util/Identifier;"))
	private Identifier getOffImage() {
		return offImage;
	}
}
