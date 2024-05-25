package makamys.neodymium.util.virtualjar.protocol.neodymiumvirtualjar;

import net.fabricmc.loader.impl.launch.FabricLauncherBase;

import static makamys.neodymium.Constants.MODID;
import static makamys.neodymium.Constants.PROTOCOL;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;


public class Handler extends URLStreamHandler {

    private static final IURLStreamHandlerImpl impl = (IURLStreamHandlerImpl) FabricLauncherBase.getProperties().get(MODID + "." + PROTOCOL + ".impl");

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return impl.openConnection(url);
    }

    public interface IURLStreamHandlerImpl {
        URLConnection openConnection(URL url) throws IOException;
    }
}
