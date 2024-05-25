package makamys.neodymium.mixin;

import makamys.neodymium.Compat;
import makamys.neodymium.ElementType;
import makamys.neodymium.Neodymium;
import makamys.neodymium.Phase;
import net.minecraft.src.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Profiler;
import net.minecraft.src.WorldClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    public WorldClient theWorld;

    @Shadow public FontRenderer fontRenderer;

    @Shadow public int displayWidth;

    @Shadow public int displayHeight;

    @Shadow @Final public Profiler mcProfiler;

    @Shadow private String debugProfilerName;

    @Inject(method = "loadWorld(Lnet/minecraft/src/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void loadWorldMixin(WorldClient par1WorldClient, String par2Str, CallbackInfo ci) {
        if (par1WorldClient == null) {
            Neodymium.instance.onWorldUnload(this.theWorld);
        }
    }

    @Inject(method = "loadWorld(Lnet/minecraft/src/WorldClient;Ljava/lang/String;)V", at = @At("RETURN"))
    private void loadWorldNotNullMixin(WorldClient par1WorldClient, String par2Str, CallbackInfo ci) {
        if (par1WorldClient != null) {
            Neodymium.instance.onConnectToServer();
        }
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    private void runTickMixin(CallbackInfo ci) {
        Neodymium.instance.onClientTick();
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityRenderer;updateCameraAndRender(F)V"))
    private void runGameLoopMixin(CallbackInfo ci) {
        Neodymium.instance.onRenderTick(Phase.START);
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityRenderer;updateCameraAndRender(F)V", shift = At.Shift.AFTER))
    private void runGameLoopMixin2(CallbackInfo ci) {
        Neodymium.instance.onRenderTick(Phase.END);
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Minecraft;displayDebugInfo(J)V"))
    private void runGameLoopMixin3(CallbackInfo ci) {
        Neodymium.instance.onRenderOverlay(ElementType.DEBUG, null);
    }

    @Inject(method = "displayDebugInfo", at = @At(value = "RETURN"))
    private void displayDebugInfoMixin(long par1, CallbackInfo ci) {
        List var3 = this.mcProfiler.getProfilingData(this.debugProfilerName);
        var3.remove(0);
        String text = Neodymium.instance.onRenderOverlay(ElementType.TEXT, "");
        int var7 = this.displayWidth - 160 - 10;
        int var8 = this.displayHeight - 160 * 2;
        this.fontRenderer.drawStringWithShadow(text, var7 + 160 - this.fontRenderer.getStringWidth(text), var8 + 160 / 2 + var3.size() * 8 + 20, 0xFFFFFF);
    }

    @Inject(method = "startGame", at= @At("RETURN"))
    private void startGameMixin(CallbackInfo ci) {
        Compat.init();
    }

}
