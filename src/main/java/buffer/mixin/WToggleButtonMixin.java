package buffer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

// LibGUI, none of this would be necessary if you backported.

@Mixin(WToggleButton.class)
public class WToggleButtonMixin {
    @Shadow(remap=false)
    boolean isOn;

    @Shadow(remap=false)
    Identifier onImage;
    
    @Shadow(remap=false)
    Identifier offImage;
    
    @Shadow(remap=false)
    Text label;

    @Shadow(remap=false)
    int color;
    
    @Shadow(remap=false)
	int darkmodeColor;

    @Overwrite(remap=false)
    @Environment(EnvType.CLIENT)
    public void paintBackground(int x, int y) {
		ScreenDrawing.rect(isOn ? onImage : offImage, x, y, 18, 18, 0xFFFFFFFF);
		
		if (label!=null) {

			ScreenDrawing.drawString(label.asFormattedString(), x + 22, y+6, LibGuiClient.config.darkMode ? darkmodeColor : color);
        }
	}
    
}