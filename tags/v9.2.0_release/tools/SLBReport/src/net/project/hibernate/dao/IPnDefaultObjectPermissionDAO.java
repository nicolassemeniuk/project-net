package net.project.hibernate.dao;

import java.math.BigDecimal;
import java.util.Iterator;

import net.project.hibernate.model.PnDefaultObjectPermission;
import net.project.hibernate.model.PnDefaultObjectPermissionPK;

public interface IPnDefaultObjectPermissionDAO extends IDAO<PnDefaultObjectPermission, PnDefaultObjectPermissionPK> {

	public Iterator getObjectPermisions(final BigDecimal spaceId, final String objectType);
}
