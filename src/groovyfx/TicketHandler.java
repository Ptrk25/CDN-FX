package groovyfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import groovyfx.Ticket.Type;

public class TicketHandler {

    //Datafield
    private final byte[] TICKET_HEADER;
    private byte[] ticketData;
    private final int tk;

    private List<Integer> ticketOffsets;
    private List<Integer> ticketCount;
    private List<Integer> apptypeCount;

    private ObservableList<Ticket> ticketlist;

    /**
     * Constructor for class TicketHandler
     */
    public TicketHandler(){
        TICKET_HEADER = ConvertingTools.hexStringToByteArray("526f6f742d434130303030303030332d58533030303030303063");
        ticketData = null;
        ticketOffsets = new ArrayList<>();
        ticketlist = FXCollections.observableArrayList();
        ticketCount = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0);
        apptypeCount = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        tk = 0x140;
    }

    /**
     * Returns the ticketlist
     *
     * @return              ticketlist
     */
    public ObservableList<Ticket> getTicketList(){
        return ticketlist;
    }

    /**
     * Override current ticketlist with another
     *
     * @param list          ticketlist
     */
    public void setTicketList(ObservableList<Ticket> list){
        ticketlist = list;
    }

    /**
     * Returns the ticketCount
     *
     * @return              ticketCount
     */
    public List<Integer> getTicketCount(){
        return ticketCount;
    }

    /**
     * Returns the apptypeCount
     *
     * @return              apptypeCount
     */
    public List<Integer> getApptypeCount(){
        return apptypeCount;
    }

    /**
     * Clears the Arraylists
     */
    public void resetData(){
        ticketData = null;
        ticketlist.clear();
        ticketOffsets.clear();
        ticketCount = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0);
        apptypeCount = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    /**
     * Opens a file and stores it into a byte array
     *
     * @param path              Filepath
     * @throws IOException      File not found Exception
     */
    public void openFile(String path) throws IOException{
        Path realPath = Paths.get(path);
        ticketData = Files.readAllBytes(realPath);
    }

    /**
     * Search for tickets in file and stores them into a list
     *
     * @return                  Contain tickets
     */
    public boolean addToTicketList(){
        int tmp = 0;
        int commonKeyIndex = 0;

        while(tmp != -1){
            tmp = ConvertingTools.indexOf(ticketData, TICKET_HEADER, tmp+1);
            if(!(tmp == -1)){
                ticketOffsets.add(tmp);
            }
        }

        if(ticketOffsets.size() == 0){
            return false;
        }

        for(int offs:ticketOffsets){
            commonKeyIndex = ticketData[offs+0xb1];
            if(ticketData[offs+0x7c] != 0x1){
                continue;
            }
            if(commonKeyIndex > 5){
                continue;
            }

            byte[] ticketData = Arrays.copyOfRange(this.ticketData, offs-0x140, offs+0x210);
            String titleid =  ConvertingTools.bytesToHex(Arrays.copyOfRange(ticketData, tk+0x9c, tk+0xa4));
            String consoleid =  ConvertingTools.bytesToHex(Arrays.copyOfRange(ticketData, tk+0x98, tk+0x9c));
            int common_keyindex = this.ticketData[offs+0xb1];

            ticketlist.add(new Ticket(ticketData, titleid, consoleid, common_keyindex));
        }
        return true;
    }

    /**
     * Counts the tickets in ticketlist and stores the counted values into ticketCount
     */
    public void countTickets(){
        ArrayList<String> counter = new ArrayList<>();
        ArrayList<Ticket> not_eshop_tickets = new ArrayList<>();
        ArrayList<Ticket> eshop_tickets = new ArrayList<>();

        int systik = 0;

        ticketCount.set(0, ticketlist.size());

        for(Ticket tiktik:ticketlist){
            counter.add(tiktik.getTitleID());
        }

        ticketCount.set(1, ConvertingTools.removeDuplicates(counter).size());
        ticketCount.set(2, counter.size() - ConvertingTools.removeDuplicates(counter).size());

        counter.clear();

        for(Ticket tiktik:ticketlist){
            String typecheck = tiktik.getTitleID().substring(4, 8);
            if(((Long.parseLong(typecheck, 16)) & 0x10) == 0x10){
                systik++;
            }else if(typecheck.equals("8005")){
                systik++;
            }else if(typecheck.equals("800F")){
                systik++;
            }
        }
        ticketCount.set(3, systik);

        for(Ticket tiktik:ticketlist){
            if(tiktik.getCommonKeyIndex() == 0){
                if(tiktik.getConsoleID().equals("00000000")){
                    not_eshop_tickets.add(tiktik);
                }else{
                    eshop_tickets.add(tiktik);
                    counter.add(tiktik.getTitleID());
                }
            }
        }
        ticketCount.set(4, eshop_tickets.size());
        ticketCount.set(5, ConvertingTools.removeDuplicates(counter).size());
        ticketCount.set(6, eshop_tickets.size() - ConvertingTools.removeDuplicates(counter).size());
        ticketCount.set(7, not_eshop_tickets.size());
    }

    /**
     * This will sort the tickets to the categories.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void sortTickets() throws IOException, InterruptedException{
        String titleid;
        String typecheck;

        Ticket[] ticketlistClone = new Ticket[ticketlist.size()];
        int i = 0;

        for(Ticket tiktik:ticketlist){
            titleid = tiktik.getTitleID();
            typecheck = titleid.substring(4, 8).toLowerCase();
            tiktik.setDownload(false);


            if(!ConvertingTools.bytesToHex(Arrays.copyOfRange(tiktik.getData(), 0x00, 0x04)).contains("00010004")){
                // Bad Ticket
                continue;
            }

            if(typecheck.equals("0000")){
                tiktik.setType(Type.ESHOP);
                ticketlistClone[i] = tiktik;
                apptypeCount.set(0, apptypeCount.get(0)+1);
            }else if(typecheck.equals("0001")){
                tiktik.setType(Type.DLP);
                ticketlistClone[i] = tiktik;
                apptypeCount.set(1, apptypeCount.get(1)+1);
            }else if(typecheck.equals("0002")){
                tiktik.setType(Type.DEMO);
                ticketlistClone[i] = tiktik;
                apptypeCount.set(2, apptypeCount.get(2)+1);
            }else if(typecheck.equals("000e")){
                tiktik.setType(Type.UPDATE);
                ticketlistClone[i] = tiktik;
                apptypeCount.set(3, apptypeCount.get(3)+1);
            }else if(typecheck.equals("008c")){
                tiktik.setType(Type.DLC);
                ticketlistClone[i] = tiktik;
                apptypeCount.set(4, apptypeCount.get(4)+1);
            }else if(typecheck.equals("8004")){
                tiktik.setType(Type.DSIWARE);
                ticketlistClone[i] = tiktik;
                apptypeCount.set(5, apptypeCount.get(5)+1);
            }else if(((Long.parseLong(typecheck, 16)) & 0x10) == 0x10){
                tiktik.setType(Type.SYSTEM);
                ticketlistClone[i] = tiktik;
                apptypeCount.set(8, apptypeCount.get(8)+1);
            }else if(typecheck.equals("8005")){
                tiktik.setType(Type.DSISYSAPP);
                ticketlistClone[i] = tiktik;
                apptypeCount.set(6, apptypeCount.get(6)+1);
            }else if(typecheck.equals("800f")){
                tiktik.setType(Type.DSISYSDAT);
                ticketlistClone[i] = tiktik;
                apptypeCount.set(7, apptypeCount.get(7)+1);
            }else{
                tiktik.setType(Type.MYSTERY);
                ticketlistClone[i] = tiktik;
                apptypeCount.set(9, apptypeCount.get(9)+1);
                continue;
            }
            i++;

        }

        ArrayList<Ticket> tmp_list = new ArrayList<>(Arrays.asList(ticketlistClone));
        ticketlist = FXCollections.observableArrayList(tmp_list);
        ticketlist.removeAll(Collections.singleton(null));
    }

}
