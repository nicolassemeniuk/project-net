package net.project.hibernate.service;

import java.math.BigDecimal;

public interface ISecurityService {

	public BigDecimal createSpaceAdminGroup(BigDecimal creatorPersonId, BigDecimal spaceId, String description);
	
	public void createSecurityPermissions(BigDecimal objectId, String objectType, BigDecimal projectId, BigDecimal personId);

}
