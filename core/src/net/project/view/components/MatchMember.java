/**
 * 
 */
package net.project.view.components;

import net.project.util.StringUtils;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;


public class MatchMember {
	
	private static Logger log;
	
	@Parameter(required = true)
	@Property
	private String memberId;
	
	@Parameter(required = true)
	@Property
	private String participantList;
	
	@Parameter(required = true)
	@Property
	private String projectName;
	
	@Parameter(required = true)
	@Property
	private String roleOptionList;
	
	@Parameter(required = true)
	@Property
	private Integer cntProject;
	
	@Property
	private boolean present = false;
	
	@SetupRender
	void initializeMatchMember() {
		log = Logger.getLogger(MatchMember.class);
		String[] participants = participantList.split(",");
		if(StringUtils.isNotEmpty(participantList) && StringUtils.isNotEmpty(memberId)){
			for(int index = 0; index < participants.length; index++){
				if(participants[index].equals(memberId)){
					present = true;
					break;	
				} else {
					present = false;
				}
			}
		}
	}

}
