package groovyfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;

public class XMLUpdater {

    private final String updateSite = "http://ptrk25.github.io/GroovyFX/database/check.txt";
    private final String updateURL = "http://ptrk25.github.io/GroovyFX/database/community.xml";

    public XMLUpdater(){

    }

    public boolean checkForUpdates(){
        DebugLogger.log("Checking for XML update...", Level.INFO);
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(updateSite).openStream()));
            String inputLine, content = "", xmlversion;

            while((inputLine = in.readLine()) != null){
                content = inputLine;
            }
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            path = path.substring(1, path.lastIndexOf("/")) + "/";

            if(!new File(path+"community.xml").exists()){
                PropertiesHandler.setProperties(content, "xmlversion");
                PropertiesHandler.saveProperties();
                return true;
            }

            if(PropertiesHandler.getProperties("xmlversion") != null){
                xmlversion = PropertiesHandler.getProperties("xmlversion");
            }else{
                PropertiesHandler.setProperties(content, "xmlversion");
                PropertiesHandler.saveProperties();
                return true;
            }

            if(xmlversion.equals(content)){
                DebugLogger.log("No update found!", Level.INFO);
                return false;
            }else{
                PropertiesHandler.setProperties(content, "xmlversion");
                PropertiesHandler.saveProperties();
                return true;
            }

        }catch (Exception e){
            DebugLogger.log("Error while searching for updates!", Level.WARNING);
        }
        return false;
    }

    public boolean update(){
        try{
            DebugLogger.log("Updating XML...", Level.INFO);
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            path = path.substring(1, path.lastIndexOf("/")) + "/";
            ReadableByteChannel in = Channels.newChannel(new URL(updateURL).openStream());
            FileChannel out = new FileOutputStream((path+"community.xml")).getChannel();

            out.transferFrom(in, 0, Long.MAX_VALUE);
            in.close();
            out.close();

            DebugLogger.log("XML Update successful!", Level.INFO);
            return true;

        }catch(Exception e){
            DebugLogger.log("XML Update failed!", Level.INFO);
            return false;
        }
    }

}
