package groovyfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Collections;
import java.util.logging.Level;

public class XMLHandler {

    private final String DB_URL = "http://3dsdb.com/xml.php";

    private String pathToDB;
    private boolean dsdb = false;
    private ObservableList<Ticket> ticketlist;

    public XMLHandler(ObservableList<Ticket> ticketlist){
        this.ticketlist = ticketlist;

        if(PropertiesHandler.getProperties("disable3dsdbsupport") != null){
            if(PropertiesHandler.getProperties("disable3dsdbsupport").equals("yes"))
                dsdb = true;
            else
                dsdb = false;
        }

    }

    public void getXMLFileFromServer(){
        try{
            if(!dsdb){
                DebugLogger.log("INIT: Downloading 3dsdb newest XML database...", Level.INFO);
                File tempFile = File.createTempFile("3dsdb", Long.toString(System.nanoTime()));
                ReadableByteChannel in = Channels.newChannel(new URL(DB_URL).openStream());
                FileOutputStream fos = new FileOutputStream(tempFile);
                FileChannel out = fos.getChannel();

                out.transferFrom(in, 0, Long.MAX_VALUE);
                in.close();
                out.close();
                fos.close();

                pathToDB = tempFile.getPath();
                DebugLogger.log("INIT: Download completed!", Level.INFO);
            }
        }catch (Exception e){
            DebugLogger.log("INIT: Download failed! Using local file", Level.WARNING);
            getXMLFileFromJar();
        }
    }

    private void getXMLFileFromJar(){
        pathToDB = Main.class.getResource("/resources/3dsreleases.xml").getPath();
    }

    public void setTicketList(ObservableList<Ticket> ticketlist){
        this.ticketlist = ticketlist;
    }

    public ObservableList<Ticket> readXMLFile(){

        if(!dsdb){
            DebugLogger.log("Reading 3dsdb database...", Level.INFO);
            try{
                File xmlFile = new File(pathToDB);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);

                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("release");

                Ticket tickets[] = new Ticket[ticketlist.size()];

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;

                        String name = eElement.getElementsByTagName("name").item(0).getTextContent();
                        String region = eElement.getElementsByTagName("region").item(0).getTextContent();
                        String serial = eElement.getElementsByTagName("serial").item(0).getTextContent();
                        String titleid = eElement.getElementsByTagName("titleid").item(0).getTextContent().toLowerCase();

                        //dsdbticketlist.add(new Ticket(name, region, serial, titleid));

                        int i = 0;

                        for(Ticket tiktik:ticketlist){

                            if(tiktik.getTitleID().toLowerCase().contains(titleid) && titleid.length() > 1){
                                if(region.equals("WLD"))
                                    region = "ALL";
                                tiktik.setName(name);
                                tiktik.setRegion(region);
                                tiktik.setSerial(serial);
                                tickets[i] = tiktik;
                                i++;
                            }else{
                                tickets[i] = tiktik;
                                i++;
                            }
                        }
                    }
                }

                DebugLogger.log("Database processed!", Level.INFO);
                ObservableList<Ticket> ticketlist = FXCollections.observableArrayList(tickets);
                ticketlist.removeAll(Collections.singleton(null));
                return ticketlist;

            }catch (Exception e){
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                DebugLogger.log(errors.toString(), Level.SEVERE);
            }
            return null;
        }
        return null;
    }

    public ObservableList<Ticket> readCommunityXMLFile(){
        DebugLogger.log("Reading community database...", Level.INFO);
        try{
            if(CustomXMLHandler.getCommunityPath() == null){
                DebugLogger.log("No community database found!", Level.WARNING);
                return null;
            }

            File xmlFile = new File(CustomXMLHandler.getCommunityPath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Ticket");

            Ticket tickets[] = new Ticket[ticketlist.size()];

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String name = eElement.getElementsByTagName("name").item(0).getTextContent();
                    String region = eElement.getElementsByTagName("region").item(0).getTextContent();
                    String serial = eElement.getElementsByTagName("serial").item(0).getTextContent();
                    String titleid = eElement.getElementsByTagName("titleid").item(0).getTextContent().toLowerCase();

                    //dsdbticketlist.add(new Ticket(name, region, serial, titleid));

                    int i = 0;

                    for(Ticket tiktik:ticketlist){

                        if(tiktik.getTitleID().toLowerCase().contains(titleid) && titleid.length() > 1){
                            if(region.equals("WLD"))
                                region = "ALL";
                            tiktik.setName(name);
                            tiktik.setRegion(region);
                            tiktik.setSerial(serial);
                            tickets[i] = tiktik;
                            i++;
                        }else{
                            tickets[i] = tiktik;
                            i++;
                        }
                    }

                }
            }

            DebugLogger.log("Database processed!", Level.INFO);
            ObservableList<Ticket> ticketlist = FXCollections.observableArrayList(tickets);
            ticketlist.removeAll(Collections.singleton(null));
            return ticketlist;

        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
        return null;
    }

    public ObservableList<Ticket> readCustomXMLFile(){
        DebugLogger.log("Reading custom database...", Level.INFO);
        try{
            if(CustomXMLHandler.getCustomPath() == null){
                DebugLogger.log("No custom database found!", Level.WARNING);
                return null;
            }

            File xmlFile = new File(CustomXMLHandler.getCustomPath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Ticket");

            Ticket tickets[] = new Ticket[ticketlist.size()];

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String name = eElement.getElementsByTagName("name").item(0).getTextContent();
                    String region = eElement.getElementsByTagName("region").item(0).getTextContent();
                    String serial = eElement.getElementsByTagName("serial").item(0).getTextContent();
                    String titleid = eElement.getElementsByTagName("titleid").item(0).getTextContent().toLowerCase();

                    //dsdbticketlist.add(new Ticket(name, region, serial, titleid));

                    int i = 0;

                    for(Ticket tiktik:ticketlist){

                        if(tiktik.getTitleID().toLowerCase().contains(titleid) && titleid.length() > 1){
                            if(region.equals("WLD"))
                                region = "ALL";
                            tiktik.setName(name);
                            tiktik.setRegion(region);
                            tiktik.setSerial(serial);
                            tickets[i] = tiktik;
                            i++;
                        }else{
                            tickets[i] = tiktik;
                            i++;
                        }
                    }

                }
            }

            DebugLogger.log("Database processed!", Level.INFO);
            ObservableList<Ticket> ticketlist = FXCollections.observableArrayList(tickets);
            ticketlist.removeAll(Collections.singleton(null));
            return ticketlist;

        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
        return null;
    }

}
