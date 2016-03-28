package groovyfx;

import java.net.URLDecoder;
import java.util.logging.*;

public class DebugLogger {

    private static  Logger logger;
    private static  Handler handler;
    private static String path;
    private static boolean disabled = false;

    public static  void init() throws Exception{
        if(PropertiesHandler.getProperties("debugmode") != null){
            if(PropertiesHandler.getProperties("debugmode").equals("yes")){
                path = URLDecoder.decode(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
                logger = Logger.getLogger("DebugLogger");
                handler = new FileHandler(path.substring(1, path.lastIndexOf("/")) + "/debug.log");
                logger.addHandler(handler);
                SimpleFormatter formatter = new SimpleFormatter();
                handler.setFormatter(formatter);

                log("Logger initialized!", Level.INFO);
            }else{
                disabled = true;
            }
        }
    }

    public static void log(String message, Level lvl){
        if(PropertiesHandler.getProperties("debugmode") != null){
            if(PropertiesHandler.getProperties("debugmode").equals("yes") && !disabled){
                logger.log(lvl, message);
            }
        }
    }

}
