package makamys.neodymium.mixin;

import net.minecraft.src.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Tessellator.class)
public interface TessellatorAccessor {
    @Accessor
    int[] getRawBuffer();

    @Accessor
    int getVertexCount();


    @Accessor
    boolean isHasColor();

    @Accessor
    boolean isHasTexture();

    @Accessor
    boolean isHasBrightness();

    @Accessor
    boolean isHasNormals();


    @Accessor
    int getDrawMode();

    @Accessor
    double getXOffset();

    @Accessor
    double getYOffset();

    @Accessor
    double getZOffset();

}
