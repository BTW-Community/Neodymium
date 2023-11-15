package makamys.neodymium.mixin;

import net.minecraft.src.EnumOptions;
import net.minecraft.src.GameSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class)
public abstract class GameSettingsMixin {
    @Final
    @Mutable
    @Shadow
    private static final String[] RENDER_DISTANCES = new String[] {"options.renderDistance.ultrafar", "options.renderDistance.veryfar", "options.renderDistance.far", "options.renderDistance.normal", "options.renderDistance.short", "options.renderDistance.tiny"};

    @Shadow public int renderDistance;

    @Shadow public abstract void saveOptions();

    @Inject(method = "setOptionValue", at = @At("HEAD"), cancellable = true)
    private void onSetOptionValue(EnumOptions par1EnumOptions, int par2, CallbackInfo ci) {
        if (par1EnumOptions == EnumOptions.RENDER_DISTANCE)
        {
            this.renderDistance = (this.renderDistance + par2) % RENDER_DISTANCES.length;
            this.saveOptions();
            ci.cancel();
        }
    }
}
