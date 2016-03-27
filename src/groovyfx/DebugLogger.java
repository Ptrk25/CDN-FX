package groovyfx;

import java.net.URLDecoder;
import java.util.logging.*;

public class DebugLogger {

    private static  Logger logger;
    private static  Handler handler;
    private static String path;

    public static  void init() throws Exception{
        path = URLDecoder.decode(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
        logger = Logger.getLogger("DebugLogger");
        handler = new FileHandler(path.substring(1, path.lastIndexOf("/")) + "/debug.log");
        logger.addHandler(handler);
        SimpleFormatter formatter = new SimpleFormatter();
        handler.setFormatter(formatter);

        log("Logger initialized!", Level.INFO);
    }

    public static void log(String message, Level lvl){
        logger.log(lvl, message);
    }

}
