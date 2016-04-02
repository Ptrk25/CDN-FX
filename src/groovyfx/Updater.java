package groovyfx;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.logging.Level;

public class Updater {

    private final String version = "v1.04";
    private final String ostype = "x64";
    private String updateSite = "http://ptrk25.github.io/GroovyFX/program/";
    private String updateURL = "http://ptrk25.github.io/GroovyFX/program/";
    private String changelogURL = "http://ptrk25.github.io/GroovyFX/program/change.log";

    public Updater(){
        if(ostype.equals("x64")){
            updateSite = updateSite + "check64.txt";
            updateURL = updateURL + "gfx64.jar";
        }else{
            updateSite = updateSite + "check32.txt";
            updateURL = updateURL + "gfx32.jar";
        }
    }

    public boolean checkForUpdates(){
        DebugLogger.log("Checking for Update...", Level.INFO);
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(updateSite).openStream()));
            String inputLine, content="";

            while((inputLine = in.readLine()) != null){
                content = inputLine;
            }

            if(version.equals(content)){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            DebugLogger.log("Error while searching for updates!", Level.WARNING);
        }
        return false;
    }

    public boolean update(){
        try{
            DebugLogger.log("Updating...", Level.INFO);
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            ReadableByteChannel in = Channels.newChannel(new URL(updateURL).openStream());
            FileChannel out = new FileOutputStream(path).getChannel();

            out.transferFrom(in, 0, Long.MAX_VALUE);
            in.close();
            out.close();

            DebugLogger.log("Update successful!", Level.INFO);
            return true;

        }catch(Exception e){
            DebugLogger.log("Update failed!", Level.INFO);
            return false;
        }
    }

    public String getChangelog(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(changelogURL).openStream()));
            String inputLine, updatemessage = "";
            ArrayList<String> content = new ArrayList<>();

            while((inputLine = in.readLine()) != null){
                content.add(inputLine);
            }

            for(String n:content){
                updatemessage = updatemessage + n + "\n";
            }
            return updatemessage;

        }catch(Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
            return null;
        }
    }

}
