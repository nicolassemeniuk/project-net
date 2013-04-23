package net.project.material;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.Finder;
import net.project.persistence.PersistenceException;

public class MaterialFinder extends Finder {
	
	public static final ColumnDefinition NAME_COLUMN = new ColumnDefinition("m.material_name", "prm.material.columndefs.material.name");
	public static final ColumnDefinition DESCRIPTION_COLUMN = new ColumnDefinition("m.material_description", "prm.material.columndefs.material.description");
	public static final ColumnDefinition COST_COLUMN = new ColumnDefinition("m.material_cost", "prm.material.columndefs.material.cost");
	public static final ColumnDefinition TYPE_ID_COLUMN = new ColumnDefinition("m.material_type_id", "prm.material.columndefs.material.typeid");
	public static final ColumnDefinition CONSUMABLE_COLUMN = new ColumnDefinition("m.material_consumable", "prm.material.columndefs.material.consumable");
	public static final ColumnDefinition TYPE_NAME_COLUMN = new ColumnDefinition("mt.material_type_name", "prm.material.columndefs.material.type");
	
	
	private String BASE_SQL_STATEMENT = "select " + "  m.material_id, shm.space_id, m.material_name, m.material_description, m.material_cost, m.material_type_id, mt.material_type_name, m.material_consumable  " +
										" from pn_material m, pn_space_has_material shm, pn_material_type mt " +
										" where m.material_id = shm.material_id AND m.material_type_id = mt.material_type_id ";
	
	private static int index = 0;
	private static int MATERIAL_ID_COL_ID = ++index;
	private static int SPACE_ID_COL_ID = ++index;
	private static int MATERIAL_NAME_COL_ID = ++index;
	private static int MATERIAL_DESCRIPTION_COL_ID = ++index;
	private static int MATERIAL_COST_COL_ID = ++index;
	private static int MATERIAL_TYPE_ID = ++index;
	private static int MATERIAL_TYPE_NAME_ID = ++index;
	private static int MATERIAL_CONSUMABLE_ID = ++index;

	@Override
	protected String getBaseSQLStatement() {
		return BASE_SQL_STATEMENT;
	}

	@Override
	protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException {
		MaterialBean material = new MaterialBean();
		populateMaterial(databaseResults, material);
		return material;
	}
	
	private void populateMaterial(ResultSet result, MaterialBean material) throws SQLException{
		material.setID(result.getString(MATERIAL_ID_COL_ID));
		material.setName(result.getString(MATERIAL_NAME_COL_ID));
		material.setDescription(result.getString(MATERIAL_DESCRIPTION_COL_ID));
		material.setCost(result.getString(MATERIAL_COST_COL_ID));
		material.setMaterialTypeId(result.getString(MATERIAL_TYPE_ID));
		material.setSpaceID(result.getString(SPACE_ID_COL_ID));
		material.setMaterialTypeName(result.getString(MATERIAL_TYPE_NAME_ID));
		material.setConsumable(Boolean.valueOf(result.getString(MATERIAL_CONSUMABLE_ID)));
	}
	
	public List findBySpaceId(String spaceID) throws PersistenceException{
		addWhereClause(" shm.space_id = " + spaceID);	
		return loadFromDB();	
	}

}
