package org.generationcp.breeding.manager.pojos;

public class ImportedGermplasm {
    
	private Integer gid;
    private Integer entryId;
    private String desig;
    private String cross;
    private String source;
    private String entryCode;
    private Double seedAmount;
    
    public ImportedGermplasm(){
        
    }
    
    public ImportedGermplasm(Integer entryId, String desig){
        this.entryId = entryId;
        this.desig = desig;
    }
    
    public Integer getEntryId(){
        return entryId;
    }
    
    public void setEntryId(Integer entryId){
        this.entryId = entryId;
    }
    
    public String getDesig(){
        return desig;
    }
    
    public void setDesig(String desig){
        this.desig = desig;
    }

	public void setGid(Integer gid) {
		this.gid = gid;
	}
	
	public Integer getGid(){
		return gid;
	}

	public void setCross(String cross) {
		this.cross = cross;
	}
	
	public String getCross(){
		return cross;
	}
    
	public void setSource(String source){
		this.source = source;
	}
	
	public String getSource(){
		return source;
	}
	
	public void setEntryCode(String entryCode){
		this.entryCode = entryCode;
	}
	
	public String getEntryCode(){
		return entryCode;
	}
	
    public void setSeedAmount(Double seedAmount){
        this.seedAmount = seedAmount;
	}
	
	public Double getSeedAmount(){
	    return seedAmount;
	}
	
};