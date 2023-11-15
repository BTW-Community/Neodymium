package makamys.neodymium.mixin;

import net.minecraft.src.PlayerUsageSnooper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PlayerUsageSnooper.class)
public interface PlayerUsageSnooperAccessor {
    @Accessor
    Map getDataMap();
}
