package net.project.view.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class MatchRole {
	
	@Parameter(required = true)
	@Property
	private String uniqueId;
	
	@Parameter(required = true)
	@Property
	private String projectId;
	
	@Parameter(required = true)
	@Property
	private String groupId;
	
	@Parameter(required = true)
	@Property
	private String email;

	@Parameter(required = true)
	@Property
	private String[] inviteeRoles;
	
	@Parameter(required = true)
	@Property
	private boolean defaultRole;
	
	@Property
	private boolean present;
	
	@SetupRender
	void initializeMatchMember() {
		if(groupId != "0")
			for(int index =0 ;index < inviteeRoles.length ; index++){
				if(inviteeRoles[index].equals(groupId)){
					present = true;
					break;
				}else{
					present = false;
				}
			 }
		 }
}
