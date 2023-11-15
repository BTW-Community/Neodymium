package makamys.neodymium.util;


public class OFUtil {
    private static final boolean isOptiFinePresent = false;//MixinConfigPlugin.class.getResource("/optifine/OptiFineTweaker.class") != null;
    
    public static boolean isOptiFinePresent() {
        return isOptiFinePresent;
    }
    
    public static boolean isFogOff() {
        return isOptiFinePresent;// && getIsFogOff();
    }
    
    private static boolean getIsFogOff() {
        return false;//((IMixinGameSettings_OptiFine)Minecraft.getMinecraft().gameSettings).getOfFogType() == 3;
    }
}
