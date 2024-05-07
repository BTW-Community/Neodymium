package makamys.neodymium;

import static makamys.neodymium.Constants.LOGGER;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import makamys.neodymium.mixin.PlayerUsageSnooperAccessor;
import net.fabricmc.loader.gui.FabricGuiEntry;
import net.fabricmc.loader.launch.common.FabricLauncherBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.GameSettings;
import org.lwjgl.opengl.GLContext;

import makamys.neodymium.config.Config;
import makamys.neodymium.util.virtualjar.IVirtualJar;
import makamys.neodymium.util.virtualjar.VirtualJar;
import net.minecraft.client.Minecraft;

public class Compat {
    
    private static boolean isGL33Supported;
    
    private static boolean wasAdvancedOpenGLEnabled;
    
    private static int notEnoughVRAMAmountMB = -1;
    
    public static void init() {
        isGL33Supported = GLContext.getCapabilities().OpenGL33;
        if (!MinecraftServer.getIsServer() && !FabricLauncherBase.getLauncher().isDevelopment() && System.getProperty("os.name") != null && System.getProperty("os.name").contains("Windows")) {
            boolean found = false;
            Minecraft.getMinecraft().getPlayerUsageSnooper().startSnooper();
            Map map = ((PlayerUsageSnooperAccessor)Minecraft.getMinecraft().getPlayerUsageSnooper()).getDataMap();
            for (Object o : map.values()) {
                String s;
                try {
                    s = (String) o;
                } catch (ClassCastException e) {
                    continue;
                }
                //System.out.println(s);
                if (s.equals("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump")) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                FabricGuiEntry.displayCriticalError(new RuntimeException("Neodymium requires the JVM argument -XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump to be set. Please add it to your JVM arguments."), true);
            }
        }
        /*if (Loader.isModLoaded("triangulator")) {
            disableTriangulator();
        }*/
    }

    private static void disableTriangulator() {
        //((ToggleableTessellator) Tessellator.instance).disableTriangulator();
    }
    
    public static void getCompatibilityWarnings(List<Warning> warns, List<Warning> criticalWarns, boolean statusCommand){
        if (Minecraft.getMinecraft().gameSettings.advancedOpengl) {
            warns.add(new Warning("Advanced OpenGL is enabled, performance may be poor." + (statusCommand ? " Click here to disable it." : "")).chatAction("neodymium disable_advanced_opengl"));
        }
        
        try {
            Class<?> shaders = Class.forName("shadersmod.client.Shaders");
            try {
                String shaderPack = (String)shaders.getMethod("getShaderPackName").invoke(null);
                if(shaderPack != null) {
                    criticalWarns.add(new Warning("A shader pack is enabled, this is not supported."));
                }
            } catch(Exception e) {
                LOGGER.warn("Failed to get shader pack name");
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            
        }
        
        if(!isGL33Supported) {
            criticalWarns.add(new Warning("OpenGL 3.3 is not supported."));
        }
        if(detectedNotEnoughVRAM()) {
            criticalWarns.add(new Warning("Not enough VRAM"));
        }
    }

    public static boolean hasChanged() {
        boolean changed = false;
        
        boolean advGL = Minecraft.getMinecraft().gameSettings.advancedOpengl;
        if(advGL != wasAdvancedOpenGLEnabled) {
            changed = true;
        }
        wasAdvancedOpenGLEnabled = advGL;
        
        return changed;
    }
    
    public static void onNotEnoughVRAM(int amountMB) {
        notEnoughVRAMAmountMB = amountMB;
    }
    
    public static void reset() {
        notEnoughVRAMAmountMB = -1;
    }
    
    private static boolean detectedNotEnoughVRAM() {
        return Config.VRAMSize == notEnoughVRAMAmountMB;
    }

    public static void forceEnableOptiFineDetectionOfFastCraft() {
        if(Compat.class.getResource("/fastcraft/Tweaker.class") != null) {
            // If OptiFine is present, it's already on the class path at this point, so our virtual jar won't override it.
            LOGGER.info("FastCraft is present, applying hack to forcingly enable FastCraft's OptiFine compat");
            VirtualJar.add(new OptiFineStubVirtualJar());
        }
    }
    
    public static boolean disableAdvancedOpenGL() {
        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        
        if(gameSettings.advancedOpengl) {
            gameSettings.advancedOpengl = false;
            gameSettings.saveOptions();
            return true;
        }
        return false;
    }
    
    private static class OptiFineStubVirtualJar implements IVirtualJar {

        @Override
        public String getName() {
            return "optifine-stub";
        }

        @Override
        public InputStream getInputStream(String path) {
            if(path.equals("/optifine/OptiFineForgeTweaker.class")) {
                // Dummy file to make FastCraft think OptiFine is present.
                LOGGER.debug("Returning a dummy /optifine/OptiFineForgeTweaker.class to force FastCraft compat.");
                return new ByteArrayInputStream(new byte[0]);
            } else {
                return null;
            }
        }
        
    }
    
    public static class Warning {
        public String text;
        public String chatAction;
        
        public Warning(String text) {
            this.text = text;
        }
        
        public Warning chatAction(String command) {
            this.chatAction = command;
            return this;
        }
    }
}
