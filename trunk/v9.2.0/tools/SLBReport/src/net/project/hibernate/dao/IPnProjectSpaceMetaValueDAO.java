package net.project.hibernate.dao;

import net.project.hibernate.model.PnProjectSpaceMetaValue;
import net.project.hibernate.model.PnProjectSpaceMetaValuePK;

import java.math.BigDecimal;
import java.util.List;


public interface IPnProjectSpaceMetaValueDAO extends IDAO<PnProjectSpaceMetaValue, PnProjectSpaceMetaValuePK> {
    public List<PnProjectSpaceMetaValue> getMetaValuesByProjectId(final BigDecimal projectId);
}
