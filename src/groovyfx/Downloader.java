package groovyfx;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class Downloader extends Thread{

    //Datafields
    private final int tk;
    private final int ATTEMPS;
    private final int BUFFER_SIZE;
    private final byte[] MAGIC;

    private boolean error;
    private boolean isInterrupted;

    private boolean patch_dlc, patch_demo, blank_id, build_cia;

    private String TitleID, TMD, path;

    private ObservableList<Ticket> downloadList;

    private long content,
                final_content,
                title,
                final_title,
                downloaded_bytes,
                final_bytes,
                failed;

    private Label lblTitleCount, lblAttemptCount, lblFailedCount, lblTitleName, lblTitleID, lblTMD, lblFilesCount, lblDownloadStats;
    private ProgressBar progressDownload;
    private Button btnDownload;
    private MenuItem add, rem, rebuild, addall, removeall;
    private TextField search;
    private TableView table;
    private boolean isDownload = true;

    /**
     * Constructor for class Downloader
     * Initialize all Datafields
     */
    public Downloader(ObservableList<Ticket> downloadList, String path){
        tk = 0x140;
        ATTEMPS = 10;
        BUFFER_SIZE = 4096;
        MAGIC = ConvertingTools.hexStringToByteArray("00010004919EBE464AD0F552CD1B72E7884910CF55A9F02E50789641D896683DC005BD0AEA87079D8AC284C675065F74C8BF37C88044409502A022980BB8AD48383F6D28A79DE39626CCB2B22A0F19E41032F094B39FF0133146DEC8F6C1A9D55CD28D9E1C47B3D11F4F5426C2C780135A2775D3CA679BC7E834F0E0FB58E68860A71330FC95791793C8FBA935A7A6908F229DEE2A0CA6B9B23B12D495A6FE19D0D72648216878605A66538DBF376899905D3445FC5C727A0E13E0E2C8971C9CFA6C60678875732A4E75523D2F562F12AABD1573BF06C94054AEFA81A71417AF9A4A066D0FFC5AD64BAB28B1FF60661F4437D49E1E0D9412EB4BCACF4CFD6A3408847982000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000526F6F742D43413030303030303033000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000158533030303030303063000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000137A0894AD505BB6C67E2E5BDD6A3BEC43D910C772E9CC290DA58588B77DCC11680BB3E29F4EABBB26E98C2601985C041BB14378E689181AAD770568E928A2B98167EE3E10D072BEEF1FA22FA2AA3E13F11E1836A92A4281EF70AAF4E462998221C6FBB9BDD017E6AC590494E9CEA9859CEB2D2A4C1766F2C33912C58F14A803E36FCCDCCCDC13FD7AE77C7A78D997E6ACC35557E0D3E9EB64B43C92F4C50D67A602DEB391B06661CD32880BD64912AF1CBCB7162A06F02565D3B0ECE4FCECDDAE8A4934DB8EE67F3017986221155D131C6C3F09AB1945C206AC70C942B36F49A1183BCD78B6E4B47C6C5CAC0F8D62F897C6953DD12F28B70C5B7DF751819A9834652625000100010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010003704138EFBBBDA16A987DD901326D1C9459484C88A2861B91A312587AE70EF6237EC50E1032DC39DDE89A96A8E859D76A98A6E7E36A0CFE352CA893058234FF833FCB3B03811E9F0DC0D9A52F8045B4B2F9411B67A51C44B5EF8CE77BD6D56BA75734A1856DE6D4BED6D3A242C7C8791B3422375E5C779ABF072F7695EFA0F75BCB83789FC30E3FE4CC8392207840638949C7F688565F649B74D63D8D58FFADDA571E9554426B1318FC468983D4C8A5628B06B6FC5D507C13E7A18AC1511EB6D62EA5448F83501447A9AFB3ECC2903C9DD52F922AC9ACDBEF58C6021848D96E208732D3D1D9D9EA440D91621C7A99DB8843C59C1F2E2C7D9B577D512C166D6F7E1AAD4A774A37447E78FE2021E14A95D112A068ADA019F463C7A55685AABB6888B9246483D18B9C806F474918331782344A4B8531334B26303263D9D2EB4F4BB99602B352F6AE4046C69A5E7E8E4A18EF9BC0A2DED61310417012FD824CC116CFB7C4C1F7EC7177A17446CBDE96F3EDD88FCD052F0B888A45FDAF2B631354F40D16E5FA9C2C4EDA98E798D15E6046DC5363F3096B2C607A9D8DD55B1502A6AC7D3CC8D8C575998E7D796910C804C495235057E91ECD2637C9C1845151AC6B9A0490AE3EC6F47740A0DB0BA36D075956CEE7354EA3E9A4F2720B26550C7D394324BC0CB7E9317D8A8661F42191FF10B08256CE3FD25B745E5194906B4D61CB4C2E000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000526F6F7400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001434130303030303030330000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000007BE8EF6CB279C9E2EEE121C6EAF44FF639F88F078B4B77ED9F9560B0358281B50E55AB721115A177703C7A30FE3AE9EF1C60BC1D974676B23A68CC04B198525BC968F11DE2DB50E4D9E7F071E562DAE2092233E9D363F61DD7C19FF3A4A91E8F6553D471DD7B84B9F1B8CE7335F0F5540563A1EAB83963E09BE901011F99546361287020E9CC0DAB487F140D6626A1836D27111F2068DE4772149151CF69C61BA60EF9D949A0F71F5499F2D39AD28C7005348293C431FFBD33F6BCA60DC7195EA2BCC56D200BAF6D06D09C41DB8DE9C720154CA4832B69C08C69CD3B073A0063602F462D338061A5EA6C915CD5623579C3EB64CE44EF586D14BAAA8834019B3EEBEED3790001000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
        error = false;
        isInterrupted = false;
        this.patch_dlc = true;
        this.patch_demo = true;
        this.blank_id = true;
        this.build_cia = true;
        TitleID = "000000000000000";
        TMD = "-";
        content = 0;
        final_content = 0;
        title = 1;
        final_title = 0;
        downloaded_bytes = 0;
        final_bytes = 0;
        failed = 0;
        this.downloadList = downloadList;
        this.path = path;
    }

    /**
     * Set the Parameter to patch DLC Content
     *
     * @param patch         true/false
     */
    public void setPatchDLC(boolean patch){
        patch_dlc = patch;
    }

    /**
     * Set the Parameter to patch Demo Content
     *
     * @param patch         true/false
     */
    public void setPatchDemo(boolean patch){
        patch_demo = patch;
    }

    /**
     * Set the Parameter to remove the Unique Console ID
     *
     * @param blank         true/false
     */
    public void setBlankID(boolean blank){
        blank_id = blank;
    }

    /**
     * Set the Parameter if you want to build the CIAs
     *
     * @param build         true/false
     */
    public void setBuildCIA(boolean build){
        build_cia = build;
    }

    public void setDownload(boolean dl){
        this.isDownload = dl;
    }

    public void setComponents(Label lblTitleCount, Label lblAttemptCount, Label lblFailedCount, Label lblTitleName, Label lblTitleID, Label lblTMD, Label lblFilesCount, Label lblDownloadStats, ProgressBar progressDownload, Button btnDownload){
        this.lblTitleCount = lblTitleCount;
        this.lblAttemptCount = lblAttemptCount;
        this.lblFailedCount = lblFailedCount;
        this.lblTitleName = lblTitleName;
        this.lblTitleID = lblTitleID;
        this.lblTMD = lblTMD;
        this.lblFilesCount = lblFilesCount;
        this.lblDownloadStats = lblDownloadStats;
        this.progressDownload = progressDownload;
        this.btnDownload = btnDownload;
    }

    public void setXtraComponents(MenuItem add, MenuItem rem, MenuItem rebuild, MenuItem addall, MenuItem removeall,TextField search, TableView table){
        this.add = add;
        this.rem = rem;
        this.rebuild = rebuild;
        this.search = search;
        this.table = table;
        this.addall = addall;
        this.removeall = removeall;
    }

    /**
     * Check if Directory exists
     *
     * @param path          Path to Folder
     * @return              true/false
     */
    private boolean createDirectory(String path){
        if(new File(path).mkdirs()) {
            DebugLogger.log("Creating direcory: " + path, Level.INFO);
            return true;
        }
        return false;
    }

    /**
     * Creates /Raw and /Cia Directory
     *
     * @param path          Path to Folder
     * @return              true/false
     */
    private boolean createRawCia(String path){
        if(!(Files.exists(Paths.get(path + "/raw")) && Files.exists(Paths.get(path + "/cia")))){
            if(new File(path + "/raw").mkdir() && new File(path + "/cia").mkdir())
                return true;
        }else
            return true;
        return false;
    }

    /**
     * Patches the Ticketdata
     *
     * @param data          Ticketdata
     * @param type          Tickettyp
     * @return              Patched data
     */
    public byte[] patchData(byte[] data, String type){
        if(blank_id){
            data = ConvertingTools.connectByteArray(Arrays.copyOfRange(data, 0x00, tk+0x98), ConvertingTools.hexStringToByteArray("00000000"), Arrays.copyOfRange(data, tk+0x9C, data.length));
            data = ConvertingTools.connectByteArray(Arrays.copyOfRange(data, 0x00, tk+0xDC), ConvertingTools.hexStringToByteArray("00000000"), Arrays.copyOfRange(data, tk+0xE0, data.length));
        }

        if(type == "Demo" && patch_demo){
            data = ConvertingTools.connectByteArray(Arrays.copyOfRange(data, 0x00, tk+0x124), ConvertingTools.hexStringToByteArray("00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"), Arrays.copyOfRange(data, tk+0x164, data.length));
        }else if(type == "DLC" && patch_dlc){
            data = ConvertingTools.connectByteArray(Arrays.copyOfRange(data, 0x00, tk+0x164), ConvertingTools.hexStringToByteArray("00010014000000ac000000140001001400000000000000280000000100000084000000840003000000000000ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"), Arrays.copyOfRange(data, tk+0x210, data.length));
        }

        return data;
    }

    private String bytes2human(long bytes, long bytes2){
        String output;
        output = (bytes/1000000) + " MB / " + (bytes2/1000000) + " MB";
        double progress = (double)bytes/(double)bytes2;
        if(bytes != 0)
            Platform.runLater(() -> progressDownload.setProgress(progress));
        else
            Platform.runLater(() -> progressDownload.setProgress(0));
        return output;
    }

    public void setInterrupted(boolean inter){
        isInterrupted = inter;
    }

    private void download() throws Exception{
        final_title = downloadList.size();
        failed = 0;

        createRawCia(path);
        Platform.runLater(() -> lblTitleCount.setText(title + "/" + final_title));

        for(Ticket tiktik:downloadList){

            if(isInterrupted)
                break;

            String type = tiktik.getType();
            String titleid = tiktik.getTitleID();
            String name = tiktik.getName();
            byte[] data = patchData(tiktik.getData(), type);

            createDirectory(path + "/raw/" + type + "/" + titleid);
            createDirectory(path + "/cia/" + type + "/" + titleid);

            FileOutputStream fos = new FileOutputStream(path + "/raw/" + type + "/" + titleid + "/cetk");
            fos.write(data);
            fos.write(MAGIC);
            fos.close();

            if(name.length() > 25){
                name = name.substring(0, 22) + "...";
            }
            String nm = name;

            Platform.runLater(() -> lblTitleName.setText(nm));
            Platform.runLater(() -> lblTitleID.setText(titleid));

            Boolean isDownloaded = downloadTitle(titleid, type);

            if(isDownloaded){
                title++;
                if(title <= final_title){
                    Platform.runLater(() -> lblTitleCount.setText(title + "/" + final_title));
                }
            }else{
                title++;
                failed++;
                Platform.runLater(() -> lblTitleCount.setText(title + "/" + final_title));
                Platform.runLater(() -> lblFailedCount.setText(failed+""));
            }
        }

        final_bytes = 0;
        final_content = 0;
        final_title = 0;
        content = 0;
        downloaded_bytes = 0;
        downloadList.clear();
        TMD = "-";
        TitleID = "";
        title = 0;

        Platform.runLater(() -> progressDownload.setProgress(0));
        Platform.runLater(() -> lblDownloadStats.setText("0 MB / 0 MB"));
        Platform.runLater(() -> lblTitleCount.setText("0/0"));
        Platform.runLater(() -> lblFailedCount.setText("0"));
        Platform.runLater(() -> lblFilesCount.setText("0/0"));
        Platform.runLater(() -> lblTitleID.setText(TitleID));
        Platform.runLater(() -> lblTitleName.setText(""));
        Platform.runLater(() -> lblTMD.setText(TMD));
        Platform.runLater(() -> lblAttemptCount.setText("0/0"));
        Platform.runLater(() -> btnDownload.setText("Download"));

        if(isInterrupted){
            Platform.runLater(() ->{
                Alert warning = new Alert(Alert.AlertType.INFORMATION);
                Stage stage = (Stage)warning.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("/resources/gciaicon.png"));
                warning.setTitle("Information");
                warning.setHeaderText("Download cancled!");
                warning.setContentText("Download cancled by user.");
                warning.showAndWait();
                    });
            isInterrupted = false;
        }else{
            Platform.runLater(() ->{
                Alert warning = new Alert(Alert.AlertType.INFORMATION);
                Stage stage = (Stage)warning.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("/resources/gciaicon.png"));
                warning.setTitle("Information");
                warning.setHeaderText("Download complete!");
                warning.setContentText("Failed: " + failed);
                warning.showAndWait();
                    });
            //CLEAR SELECTED DOWNLOAD
            for(int i = 0; i < TableFilter.filteredTickets.size(); i++){
                Ticket tiktik = TableFilter.filteredTickets.get(i);
                if(tiktik.getDownload()){
                    tiktik.setDownload(false);
                    try{
                        TableFilter.filteredTickets.set(i, tiktik);
                    }catch (Exception e){
                        //IGNORE
                    }
                }
            }
            //ENABLE GUI
            Platform.runLater(() -> rebuild.setDisable(false));
            Platform.runLater(() -> table.setEditable(true));
            Platform.runLater(() -> add.setDisable(false));
            Platform.runLater(() -> rem.setDisable(false));
            Platform.runLater(() -> addall.setDisable(false));
            Platform.runLater(() -> removeall.setDisable(false));
            Platform.runLater(() -> search.setDisable(false));
        }

    }

    private boolean downloadTitle(String titleID, String titleType) throws Exception{
        String baseurl = "http://ccs.cdn.c.shop.nintendowifi.net/ccs/download/" + titleID;

        DebugLogger.log("TitleID: " + titleID,Level.INFO);
        DebugLogger.log("Downloading TMD...", Level.INFO);
        for(int attempt = 0; attempt < ATTEMPS; attempt++){
            try{
                if(attempt >= 0){
                    int att = attempt+1;
                    Platform.runLater(() -> lblAttemptCount.setText((att) + "/" + ATTEMPS));
                }
                ReadableByteChannel rbc = Channels.newChannel(new URL(baseurl + "/tmd").openStream());
                FileOutputStream fos = new FileOutputStream(path + "/raw/" + titleType + "/" + titleID + "/" + "tmd");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                Platform.runLater(() -> lblTMD.setText("OK"));
                fos.close();

            }catch(Exception e){
                Platform.runLater(() -> lblTMD.setText("Error"));

                DebugLogger.log("Error while downloading TMD", Level.WARNING);
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                DebugLogger.log(errors.toString(), Level.SEVERE);

                Thread.sleep(100);
                error = true;
                continue;
            }

            error = false;
            break;
        }

        if(!error){
            Path tmd_path = Paths.get(path + "/raw/" + titleType + "/" + titleID + "/" + "tmd");
            byte[] tmd = Files.readAllBytes(tmd_path);

            int contentCount = Math.toIntExact(Long.parseLong(ConvertingTools.bytesToHex(Arrays.copyOfRange(tmd, tk+0x9e, tk+0xa0)), 16));
            final_content = contentCount;

            Platform.runLater(() -> lblFilesCount.setText(content + "/" + final_content));

            for(int i = 0; i < contentCount; i++){
                if(!error){
                    int cOffs = 0xB04 + (0x30*i);
                    String cID = ConvertingTools.bytesToHex(Arrays.copyOfRange(tmd, cOffs, cOffs + 0x04));

                    content = i+1;
                    Platform.runLater(() -> lblFilesCount.setText(content + "/" + final_content));
                    DebugLogger.log("Downloading content " + content + " of " + final_content + "...", Level.INFO);
                    final_bytes = Math.toIntExact(Long.parseLong(ConvertingTools.bytesToHex(Arrays.copyOfRange(tmd, cOffs+0x08, cOffs+0x10)), 16));
                    Platform.runLater(() -> lblDownloadStats.setText(bytes2human(downloaded_bytes,final_bytes)));

                    for(int attempt = 0; attempt < ATTEMPS; attempt++){
                        try{
                            if(attempt >= 0){
                                int att = attempt+1;
                                Platform.runLater(() -> lblAttemptCount.setText((att) + "/" + ATTEMPS));
                            }

                            if(!downloadFile(baseurl + "/" + cID, path + "/raw/" + titleType + "/" + titleID + "/" + cID)){
                                DebugLogger.log("Couldn't download content file...", Level.WARNING);
                                error = true;
                                continue;
                            }
                            File f = new File(path + "/raw/" + titleType + "/" + titleID + "/" + cID);
                            if(f.length() != final_bytes){
                                DebugLogger.log("Content download not correct size", Level.WARNING);
                                failed++;
                                if(isInterrupted){
                                    return false;
                                }
                                error = true;
                                continue;
                            }

                        }catch(Exception e){
                            DebugLogger.log("Couldn't download content file...", Level.WARNING);
                            StringWriter errors = new StringWriter();
                            e.printStackTrace(new PrintWriter(errors));
                            DebugLogger.log(errors.toString(), Level.SEVERE);

                            error = true;
                            continue;
                        }
                        error = false;
                        break;
                    }
                }
                if(error){
                    DebugLogger.log("ERROR: Could not download content file... Skipping title", Level.WARNING);
                }

            }

            if(!error){
                if(build_cia){
                    if(createCIA(path + "/raw/" + titleType + "/" + titleID + "/",path + "/cia/" + titleType + "/" + titleID + "/", titleID)){
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    return true;
                }

            }

        }
        return false;
    }

    public boolean downloadFile(String url, String outPath){
        try{

            HTTPDownloadUtil util = new HTTPDownloadUtil();
            util.downloadFile(url);

            InputStream is = util.getInputStream();
            FileOutputStream os = new FileOutputStream(outPath);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            long totalBytesRead = 0;

            Platform.runLater(() -> lblDownloadStats.setText("0 MB / 0 MB"));

            while(((bytesRead = is.read(buffer)) != -1) && !isInterrupted){
                os.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                downloaded_bytes = totalBytesRead;
                Platform.runLater(() -> lblDownloadStats.setText(bytes2human(downloaded_bytes, final_bytes)));
                //PROGRESSBAR
            }
            os.close();
            util.disconnect();
            return true;

        }catch(Exception e){
            return false;
        }
    }

    private boolean createCIA(String rawDir, String ciaDir, String titleid) throws IOException, InterruptedException{

        String makecmd = " \"" + rawDir + "\" \"" + ciaDir + titleid +".cia\"";
        String cdn_path = MakeCDN.getPath();


        if(!DetectOS.isWindows()){
            cdn_path = "/" + MakeCDN.getPath();
            Runtime.getRuntime().exec("chmod +x " + cdn_path);
            makecmd = " " + rawDir + " " + ciaDir + titleid +".cia";
        }

        DebugLogger.log("Creating CIA... (TitleID: " + titleid + ")", Level.INFO);
        DebugLogger.log("MakeCDN command: " + makecmd, Level.INFO);

        Runtime.getRuntime().exec(cdn_path + makecmd);

        Thread.sleep(100);
        if(new File(ciaDir + titleid + ".cia").isFile()){
            DebugLogger.log("CIA created!", Level.INFO);
            return true;
        }

        DebugLogger.log("CIA not created...",Level.WARNING);
        return false;

    }

    public void generateCIAFromRaw() throws IOException, InterruptedException{
        String Path = path + "/raw";
        File file = new File(Path);
        List<String> folders = new ArrayList<String>();
        List<Boolean> success = new ArrayList<Boolean>();
        int counter = 0;

        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        try{
            if(directories[0] == null)
                return;
        }catch(Exception e){
            Platform.runLater(() ->{
                Alert warning = new Alert(Alert.AlertType.ERROR);
                Stage stage = (Stage)warning.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("/resources/gciaicon.png"));
                warning.setTitle("Warning");
                warning.setHeaderText("Rebuild raw content");
                warning.setContentText("No raw content found. Did you select the right folder?");
                warning.showAndWait();
                Stage stage2 = (Stage) lblTitleCount.getScene().getWindow();
                stage2.close();
                    });
            return;
        }


        for(String folder:directories){
            file = new File(Path + "/" + folder);
            String[] directories2 = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                    return new File(current, name).isDirectory();
                }
            });
            folders.addAll(Arrays.asList(directories2));
        }

        //RebuildGUI

        for(String fod:directories){
            for(String folder:folders){
                file = new File(Path + "/" + fod + "/" + folder);
                if(file.exists()){
                    String tmp_path = Path + "/" + fod + "/" + folder + "/";
                    new File(tmp_path.replace("raw", "cia")).mkdirs();
                    success.add(createCIA(Path + "/" + fod + "/" + folder, tmp_path.replace("raw", "cia"), folder));
                    Platform.runLater(() -> lblTitleCount.setText("Completed "+success.size() + " of " + folders.size()));
                    Platform.runLater(() -> progressDownload.setProgress((double)success.size()/(double)folders.size()));
                }
            }
        }

        for(boolean count:success){
            if(count == false)
                counter++;
        }

        int c = counter;
        Platform.runLater(() ->{
            Alert warning = new Alert(Alert.AlertType.INFORMATION);
            Stage stage = (Stage)warning.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("/resources/gciaicon.png"));
            warning.setTitle("Done");
            warning.setHeaderText("Done");
            warning.setContentText("Succeded: " + (success.size()-c) + ("\nFailed: " + c));
            warning.showAndWait();
            Stage stage2 = (Stage) lblTitleCount.getScene().getWindow();
            stage2.close();
        });

    }

    public void run(){
        try{
            if(isDownload)
                download();
            else
                generateCIAFromRaw();
        }catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            DebugLogger.log(errors.toString(), Level.SEVERE);
        }
    }

}