package groovyfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.logging.Level;

public class CustomXMLHandler {

    private static String decodedPathCommunity;
    private static String decodedPathCustom;

    public static void createCustomDatabase(){
        try{
            if(getCustomPath() == null){
                DebugLogger.log("INIT: Creating custom database file...", Level.INFO);
                new File(decodedPathCustom).createNewFile();
                prepareFile();
                DebugLogger.log("INIT :Custom database file created!", Level.INFO);
            }
        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
    }

    public static String getCommunityPath(){
        try{
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            path = URLDecoder.decode(path, "UTF-8");

            decodedPathCommunity = path.substring(0, path.lastIndexOf("/")) + "/community.xml";
            decodedPathCustom = path.substring(0, path.lastIndexOf("/")) + "/custom.xml";

            if (new File(decodedPathCommunity).exists()) {
                return decodedPathCommunity;
            }

        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
        return null;
    }

    public static String getCustomPath(){
        try{
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            path = URLDecoder.decode(path, "UTF-8");

            decodedPathCustom = path.substring(0, path.lastIndexOf("/")) + "/custom.xml";

            if (new File(decodedPathCustom).exists()) {
                return decodedPathCustom;
            }

        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
        return null;
    }

    public static void resetCustomDatabase(){
        try{
            DebugLogger.log("Resetting custom database...", Level.INFO);
            new File(decodedPathCustom).delete();
            new File(decodedPathCustom).createNewFile();
            prepareFile();
            DebugLogger.log("Custom database resetted!", Level.INFO);
        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
    }

    private static void prepareFile() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("database");
        doc.appendChild(rootElement);

        //EMTPY ENTRY
        Element ticket = doc.createElement("Ticket");
        rootElement.appendChild(ticket);

        Element name = doc.createElement("name");
        name.appendChild(doc.createTextNode("Empty"));
        ticket.appendChild(name);

        Element region = doc.createElement("region");
        region.appendChild(doc.createTextNode("Empty"));
        ticket.appendChild(region);

        Element serial = doc.createElement("serial");
        serial.appendChild(doc.createTextNode("Empty"));
        ticket.appendChild(serial);

        Element titleid = doc.createElement("titleid");
        titleid.appendChild(doc.createTextNode("Empty"));
        ticket.appendChild(titleid);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(decodedPathCustom));

        transformer.transform(source, result);
    }

    public static void writeIntoXML(ObservableList<Ticket> entrylist) throws Exception{
        ObservableList<Ticket> newEntryList = FXCollections.observableArrayList();

        outerloop:
        for(Ticket ticket:entrylist){
            String titleid = ticket.getTitleID();
            if(newEntryList.size() > 0){
                for(int i = 0; i < newEntryList.size(); i++){
                    Ticket tiktik = newEntryList.get(i);
                    if(ticket.getTitleID().equals(tiktik.getTitleID())){

                        if(ticket.getName().length() > 0)
                            tiktik.setName(ticket.getName());
                        if(ticket.getRegion().length() > 0)
                            tiktik.setRegion(ticket.getRegion());
                        if(ticket.getSerial().length() > 0)
                            tiktik.setSerial(ticket.getSerial());

                        continue outerloop;
                    }
                }
                newEntryList.add(ticket);
            }else{
                newEntryList.add(ticket);
            }
        }

        DebugLogger.log("Writing into custom database...", Level.INFO);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(decodedPathCustom);

        Node database = doc.getFirstChild();

        for(Ticket tiktik:newEntryList){
            Element ticket = doc.createElement("Ticket");
            database.appendChild(ticket);

            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode(tiktik.getName()));
            ticket.appendChild(name);

            Element region = doc.createElement("region");
            region.appendChild(doc.createTextNode(tiktik.getRegion()));
            ticket.appendChild(region);

            Element serial = doc.createElement("serial");
            serial.appendChild(doc.createTextNode(tiktik.getSerial()));
            ticket.appendChild(serial);

            Element titleid = doc.createElement("titleid");
            titleid.appendChild(doc.createTextNode(tiktik.getTitleID()));
            ticket.appendChild(titleid);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(decodedPathCustom));

        transformer.transform(source, result);
        DebugLogger.log("Writing complete!", Level.INFO);

    }

}
