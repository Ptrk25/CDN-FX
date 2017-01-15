package groovyfx;

import java.io.*;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.logging.Level;

public class PropertiesHandler {

    private static Properties p = new Properties();
    private static String decodedPath;

    public static void createFile(){
        try{
            if(getPath() == null){
                new File(decodedPath).createNewFile();
                DebugLogger.log("Properties file created!", Level.INFO);
            }
        }catch (IOException e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
    }

    public static String getInputPath(){
        return getProperties("ticketdb");
    }

    public static String getOutputPath(){
        return getProperties("outputfolder");
    }

    public static void setProperties(String data, String content){
        try{
            data = data.replaceAll(":", "!");
            p.setProperty(content, data);
        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
    }

    public static String getProperties(String content){
        try{
            if(getPath() != null){
                InputStream input = new FileInputStream(getPath());
                p.load(input);
                String data = p.getProperty(content);
                if(data != null){
                    data = data.replaceAll("!", ":");
                }
                return data;
            }
        }catch(Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
        return null;
    }

    public static void saveProperties(){
        try {
            p.store(new FileOutputStream(getPath()), "Config File");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            e.printStackTrace();
            DebugLogger.log(errors.toString(), Level.SEVERE);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            e.printStackTrace();
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            e.printStackTrace();
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
    }

    private static String getPath(){

        try{
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("GroovyFX.jar","");
            decodedPath = URLDecoder.decode(path, "UTF-8");

            decodedPath = decodedPath.substring(0, decodedPath.lastIndexOf("/")) + "/settings.properties";

            if(new File(decodedPath).exists()){
                if(DetectOS.returnOS() == "Windows")
                    return decodedPath;
                else if(DetectOS.returnOS() == "Unix")
                    return decodedPath;
                else if(DetectOS.returnOS() == "Mac")
                    return decodedPath;
                else
                    return null;
            }
            return null;
        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
            return null;
        }
    }

}
