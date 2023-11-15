package makamys.neodymium.mixin;

import net.minecraft.src.ServerConfigurationManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerConfigurationManager.class)
public interface ServerConfigurationManagerAccessor {
    @Accessor
    void setViewDistance(int viewDistance);
}
