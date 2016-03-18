package groovyfx;

public class Ticket {
	public enum Type {ESHOP,DLP,DEMO,UPDATE,DLC,DSIWARE,DSISYSAPP,DSISYSDAT,SYSTEM,MYSTERY,NONE}
	
	private String name, region, serial, titleid, consoleid;
	private int commonKeyIndex;
	private byte[] data;
	private Type type;
	private boolean download;
	
	public Ticket(){
		this.name = "";
		this.region = "";
		this.serial = "";
		this.type = Type.NONE;
		this.titleid = "";
		this.consoleid = "";
		this.data = null;
		this.commonKeyIndex = 0;
		this.download = false;
	}
	
	public Ticket(String name, String region, String serial, String titleid){
		this.name = name;
		this.region = region;
		this.serial = serial;
		this.titleid = titleid;

	}
	
	public Ticket(byte[] data, String titleid, String consoleid, int commonKeyIndex){
		this.data = data;
		this.titleid = titleid;
		this.consoleid = consoleid;
		this.commonKeyIndex = commonKeyIndex;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getRegion(){
		return this.region;
	}
	
	public String getSerial(){
		return this.serial;
	}
	
	public String getType(){
		if(this.type == Type.ESHOP)
			return "eShopApp";
		if(this.type == Type.DLP)
			return "DLP";
		if(this.type == Type.DEMO)
			return "Demo";
		if(this.type == Type.UPDATE)
			return "UpdatePatch";
		if(this.type == Type.DLC)
			return "DLC";
		if(this.type == Type.DSIWARE)
			return "DSiWare";
		if(this.type == Type.DSISYSAPP)
			return "DSiSysApp";
		if(this.type == Type.DSISYSDAT)
			return "DSiSysDat";
		if(this.type == Type.SYSTEM)
			return "System";
		if(this.type == Type.MYSTERY)
			return "Mystery";
		if(this.type == Type.NONE)
			return null;
		return null;
	}
	
	public String getTitleID(){
		return this.titleid;
	}
	
	public String getConsoleID(){
		return this.consoleid;
	}
	
	public boolean getDownload(){
		return this.download;
	}
	
	public int getCommonKeyIndex(){
		return this.commonKeyIndex;
	}
	
	public byte[] getData(){
		return this.data;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setRegion(String region){
		this.region = region;
	}
	
	public void setSerial(String serial){
		this.serial = serial;
	}
	
	public void setType(Type type){
		this.type = type;
	}
	
	public void setTitleID(String titleid){
		this.titleid = titleid;
	}
	
	public void setConsoleID(String consoleid){
		this.consoleid = consoleid;
	}
	
	public void setDownload(Boolean download){
		this.download = download;
	}
	
	public void setCommonKexIndex(int commonKeyIndex){
		this.commonKeyIndex = commonKeyIndex;
	}
	
	public void setData(byte[] data){
		this.data = data;
	}
	
}
