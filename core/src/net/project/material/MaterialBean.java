package net.project.material;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;

import net.project.persistence.IXMLPersistence;
import net.project.base.Module;
import net.project.space.Space;

public class MaterialBean extends Material implements Serializable, IXMLPersistence {
	
    /** Current space */
    private Space space;
    
    /**
     * Set the current space
     * @param space the current space
     */
    public void setSpace(Space space) {
        this.space = space;
    }
    
    public Space getSpace(){
    	return this.space;
    }

	@Override
	public String getXML() throws SQLException {
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" ?>\n\n");
		xml.append(getXMLBody());
		return xml.toString();
	}

	@Override
	public String getXMLBody() throws SQLException {
		StringBuffer xml = new StringBuffer();
		xml.append("<node>");
    	xml.append("<value>");
    	xml.append("<isowner>1</isowner>");   
    
    	xml.append("<materialspace>"); 
        	xml.append("<materialid>" + this.getMaterialId() + "</materialid>");
        	xml.append("<materialname>" + this.getName() + "</materialname>");
        	xml.append("<materialdescription>" + this.getDescription() + "</materialdescription>");
        	xml.append("<materialtype>" + this.getMaterialTypeName() + "</materialtype>");
        	xml.append("<materialcost>" + this.getCost() + "</materialcost>");
        	xml.append("<currentdate>" + String.valueOf((new Date()).getTime()) + "</currentdate>");
    	xml.append("</materialspace>");         
    
    	xml.append("</value>");     
    
    	xml.append("<children>");
    	xml.append("</children>");          
    	xml.append("</node>");
    	
    	return xml.toString();
	}
	
		public String getUrl()
	{
        return "/material/MaterialDetail.jsp?module=" + Module.MATERIAL + "&id=" + this.getMaterialId();		
	} 

}
