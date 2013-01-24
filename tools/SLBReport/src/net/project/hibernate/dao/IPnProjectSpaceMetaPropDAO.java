package net.project.hibernate.dao;

import net.project.hibernate.model.PnProjectSpaceMetaProp;

import java.math.BigDecimal;


public interface IPnProjectSpaceMetaPropDAO extends IDAO<PnProjectSpaceMetaProp, BigDecimal> {
    public PnProjectSpaceMetaProp getProjectSpaceMetaPropByName(String propertyName);
}
