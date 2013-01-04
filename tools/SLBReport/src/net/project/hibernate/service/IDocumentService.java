package net.project.hibernate.service;

import java.math.BigDecimal;

public interface IDocumentService {
	
	public BigDecimal createDocumentSpace(BigDecimal spaceId, BigDecimal personId);

}
