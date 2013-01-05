/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 20173 $
|       $Date: 2009-12-08 13:36:27 -0300 (mar, 08 dic 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------*/
package net.project.base.property;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.project.base.RecordStatus;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

/**
 * Defines a single token.
 * A token has a name and value.
 *
 * @author unascribed
 * @author Tim Morrow
 */
public class Token implements java.io.Serializable, IXMLPersistence {

    private final String contextID;
    private final String name;
    private final String value;
    private final PropertyType type;
    private final String language;
    private final RecordStatus recordStatus;
    private final boolean isSystemProperty;
    private final boolean isTranslatableProperty;

    private boolean isDirty = false;
    private boolean isNewToken = false;

    /**
     * Creates a new token with a string type.
     *
     * @param contextID the contextID to which the token belongs
     * @param name the name of the token
     * @param value the value of the token
     * @param typeString the type of the token
     * @param language the language the token value is for
     * @param recordStatus the record status
     * @param isSystemProperty true if this token is a sytem property.  A system
     * property cannot be modified in any configuration other than the default
     * configuration
     * @param isTranslatableProperty true if this token is translatabe; this
     * is a classification that helps to differentiate "settings" (not translatable)
     * from "externalized text" (translatable)
     */
    public Token(String contextID, String name, String value, String typeString, String language, RecordStatus recordStatus, boolean isSystemProperty, boolean isTranslatableProperty) {
        this(contextID, name, value, PropertyType.findByID(typeString), language, recordStatus, isSystemProperty, isTranslatableProperty);
    }

    /**
     * Creates a new token.
     *
     * @param contextID the contextID to which the token belongs
     * @param name the name of the token
     * @param value the value of the token
     * @param type the type of the token
     * @param language the language the token value is for
     * @param recordStatus the record status
     * @param isSystemProperty true if this token is a sytem property.  A system
     * property cannot be modified in any configuration other than the default
     * configuration
     * @param isTranslatableProperty true if this token is translatabe; this
     * is a classification that helps to differentiate "settings" (not translatable)
     * from "externalized text" (translatable)
     */
    public Token(String contextID, String name, String value, PropertyType type, String language, RecordStatus recordStatus, boolean isSystemProperty, boolean isTranslatableProperty) {
        this.contextID = contextID;
        this.name = name;
        this.value = value;
        this.type = type;
        this.language = language;
        this.recordStatus = recordStatus;
        this.isSystemProperty = isSystemProperty;
        this.isTranslatableProperty = isTranslatableProperty;
    }

    public String getContextID() {
        return this.contextID;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public PropertyType getType() {
        return this.type;
    }

    public String getTypeString() {
        return (getType() == null ? null : getType().getID());
    }

    public String getLanguage() {
        return language;
    }

    public RecordStatus getRecordStatus() {
        return this.recordStatus;
    }

    public boolean isSystemProperty() {
        return isSystemProperty;
    }

    public boolean isTranslatableProperty() {
        return isTranslatableProperty;
    }

    /**
     * Indicates whether this token's value is null or equal to the empty string.
     * @return true if the token value is null or empty; false otherwise
     */
    public boolean isValueNull() {
        boolean flag = false;

        if (value == null || value.equals("")) {
            flag = true;
        }

        return flag;
    }

    public boolean equalsValue(Object object) {
        boolean flag;
        Token token = (object instanceof Token) ? (Token) object : null;

        if (token != null &&
                token.name != null && token.name.equals(this.name) &&
                token.value != null && token.value.equals(this.value) &&
                token.type != null && token.type.equals(this.type) &&
                token.contextID != null && token.contextID.equals(this.contextID) &&
                token.language != null && token.language.equals(this.language) &&
                token.recordStatus != null && token.recordStatus.equals(this.recordStatus) &&
                token.isSystemProperty == this.isSystemProperty &&
                token.isTranslatableProperty == this.isTranslatableProperty) {

            flag = true;
        } else
            flag = false;


        return flag;
    }


    public void setIsDirty() {
        isDirty = true;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void setIsNewToken() {
        isNewToken = true;
    }

    private boolean isNewToken() {
        return this.isNewToken;
    }


    /**
     * Stores this token.
     * @throws PersistenceException if there is a problem storing
     */
    public void store() throws PersistenceException {

        final DBBean db = new DBBean();

        try {

            db.setAutoCommit(false);

            if (isNewToken()) {
                createToken(db);

            } else {
                updateToken(db);

            }

            db.commit();

        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
                // We will release and throw original error
            }
            throw new PersistenceException("Token.store() threw an SQLException: " + sqle, sqle);

        } finally {
            db.release();

        }
    }

    /**
     * Updates an existing property.
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem executing the SQL statement
     */
    private void updateToken(DBBean db) throws SQLException, PersistenceException {

        if (getType().isClobStorage()) {
            // Property value stored in a clob
            StringBuffer updateStatement = new StringBuffer();
            updateStatement.append("update pn_property set ");
            updateStatement.append("property_value = null, property_value_clob = empty_clob(), ");
            updateStatement.append("property_type = ?, is_system_property = ?, is_translatable_property = ?,  record_status = ? ");
            updateStatement.append("where context_id = ? and language = ? and property = ? ");

            int index = 0;
            db.prepareStatement(updateStatement.toString());
            db.pstmt.setString(++index, getType().getID());
            db.pstmt.setInt(++index, net.project.util.Conversion.booleanToInt(isSystemProperty()));
            db.pstmt.setInt(++index, net.project.util.Conversion.booleanToInt(isTranslatableProperty()));
            db.pstmt.setString(++index, getRecordStatus().getID());
            db.pstmt.setString(++index, getContextID());
            db.pstmt.setString(++index, getLanguage());
            db.pstmt.setString(++index, getName());
            db.executePrepared();

            // Now stream the clob to the updated row
            writeValueToClob(db);

        } else {
            // Property value not stored in clob
            StringBuffer updateStatement = new StringBuffer();
            updateStatement.append("update pn_property set ");
            updateStatement.append("property_value = ?, property_value_clob = null, ");
            updateStatement.append("property_type = ?, is_system_property = ?, is_translatable_property = ?,  record_status = ? ");
            updateStatement.append("where context_id = ? and language = ? and property = ?");

            int index = 0;
            db.prepareStatement(updateStatement.toString());
            db.pstmt.setString(++index, getValue());
            db.pstmt.setString(++index, getType().getID());
            db.pstmt.setInt(++index, net.project.util.Conversion.booleanToInt(isSystemProperty()));
            db.pstmt.setInt(++index, net.project.util.Conversion.booleanToInt(isTranslatableProperty()));
            db.pstmt.setString(++index, getRecordStatus().getID());
            db.pstmt.setString(++index, getContextID());
            db.pstmt.setString(++index, getLanguage());
            db.pstmt.setString(++index, getName());
            db.executePrepared();

        }

    }

    /**
     * Inserts this token as a new property if its value is not empty.
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem executing the SQL statement
     */
    private void createToken(DBBean db) throws SQLException, PersistenceException {

        if (!this.isValueNull()) {

            if (getType().isClobStorage()) {
                // Property value stored in a clob
                StringBuffer insertStatement = new StringBuffer();
                insertStatement.append("insert into pn_property ( ");
                insertStatement.append("property, property_value, property_value_clob, property_type, context_id, ");
                insertStatement.append("language, is_system_property, is_translatable_property, record_status ");
                insertStatement.append(") values ( ");
                insertStatement.append("?, ?, empty_clob(), ?, ?, ?, ?, ?, ?)");

                int index = 0;
                db.prepareStatement(insertStatement.toString());
                db.pstmt.setString(++index, getName());
                db.pstmt.setNull(++index, java.sql.Types.VARCHAR);
                db.pstmt.setString(++index, getType().getID());
                db.pstmt.setString(++index, getContextID());
                db.pstmt.setString(++index, getLanguage());
                db.pstmt.setInt(++index, net.project.util.Conversion.booleanToInt(isSystemProperty()));
                db.pstmt.setInt(++index, net.project.util.Conversion.booleanToInt(isTranslatableProperty()));
                db.pstmt.setString(++index, getRecordStatus().getID());
                db.executePrepared();

                writeValueToClob(db);

            } else {
                // Property value not stored in clob
                // Property value stored in a clob
                StringBuffer insertStatement = new StringBuffer();
                insertStatement.append("insert into pn_property ( ");
                insertStatement.append("property, property_value, property_value_clob, property_type, context_id, ");
                insertStatement.append("language, is_system_property, is_translatable_property, record_status ");
                insertStatement.append(") values ( ");
                insertStatement.append("?, ?, ?, ?, ?, ?, ?, ?, ?)");

                int index = 0;
                db.prepareStatement(insertStatement.toString());
                db.pstmt.setString(++index, getName());
                db.pstmt.setString(++index, getValue());
                db.pstmt.setNull(++index, java.sql.Types.CLOB);
                db.pstmt.setString(++index, getType().getID());
                db.pstmt.setString(++index, getContextID());
                db.pstmt.setString(++index, getLanguage());
                db.pstmt.setInt(++index, net.project.util.Conversion.booleanToInt(isSystemProperty()));
                db.pstmt.setInt(++index, net.project.util.Conversion.booleanToInt(isTranslatableProperty()));
                db.pstmt.setString(++index, getRecordStatus().getID());
                db.executePrepared();

            }


        }


    }

    /**
     * Write the current value to the clob for the current token.
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException
     * @throws PersistenceException
     */
    private void writeValueToClob(DBBean db) throws SQLException, PersistenceException {

        StringBuffer selectStatement = new StringBuffer();
        selectStatement.append("select property_value_clob ");
        selectStatement.append("from pn_property ");
        selectStatement.append("where context_id = ? and language = ? and property = ? ");
        selectStatement.append("for update nowait ");

        int index = 0;
        db.prepareStatement(selectStatement.toString());
        db.pstmt.setString(++index, getContextID());
        db.pstmt.setString(++index, getLanguage());
        db.pstmt.setString(++index, getName());
        db.executePrepared();

        if (db.result.next()) {
            ClobHelper.write(db.result.getClob("property_value_clob"), getValue());

        } else {
            throw new PersistenceException("Error finding updated property for writing clob '" + getName() + "'");

        }

    }

    /**
     * Removes this property from the database.
     * @throws PersistenceException
     */
    public void remove() throws PersistenceException {

        final DBBean db = new DBBean();

        String qstrDeleteToken = "delete from pn_property where property = ? and context_id = ? and language = ?";

        try {

            db.prepareStatement(qstrDeleteToken);

            db.pstmt.setString(1, this.name);
            db.pstmt.setString(2, this.contextID);
            db.pstmt.setString(3, this.language);

            db.executePrepared();
        } catch (SQLException sqle) {
            throw new PersistenceException("Token.remove() threw an SQLException: " + sqle, sqle);

        } finally {
            db.release();

        }

    }


    /**
     Converts the object to XML representation
     This method returns the object as XML text.
     @return XML representation of the object
     */
    public String getXML() {

        StringBuffer xml = new StringBuffer();

        xml.append(net.project.xml.IXMLTags.XML_VERSION_STRING);
        xml.append(getXMLBody());

        return xml.toString();
    }


    /**
     Converts the object to XML node representation without the xml header tag.
     This method returns the object as XML text.
     @return XML node representation
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();

        xml.append("\n<token");
        xml.append(" contextID=\"" + XMLUtils.escape(getContextID()) + "\"");
        xml.append(" name=\"" + XMLUtils.escape(getName()) + "\"");
        xml.append(" type=\"" + XMLUtils.escape(getType().getID()) + "\"");
        xml.append(" language=\"" + XMLUtils.escape(getLanguage()) + "\"");
        xml.append(" recordStatus=\"" + XMLUtils.escape(getRecordStatus().getID()) + "\"");
        xml.append(" isSystemProperty=\"" + XMLUtils.escape(Conversion.booleanToString(isSystemProperty())) + "\"");
        xml.append(" isTranslatableProperty=\"" + XMLUtils.escape(Conversion.booleanToString(isTranslatableProperty())) + "\"");
        xml.append(">");
        // The value must be the element content since it may contain line breaks
        // convert windows-style CRLF to LF
        if (getValue() != null) {
            xml.append(XMLUtils.escape(getValue().replaceAll("\r\n", "\n")));
        }
        xml.append("</token>");


        return xml.toString();
    }
    
    // For cosign
	public static Map<String, String> getTokens(String context) {
		Map<String, String> tokenMap = new HashMap<String, String>();
		final DBBean db = new DBBean();
		String sql = "select property, property_value from pn_property where property like '" + context + "%'";
		try {
			db.setQuery(sql);
			db.executeQuery();
			while (db.result.next()) {
				tokenMap.put(db.result.getString("property"), db.result.getString("property_value"));
			}
		} catch (SQLException sqle) {
			Logger.getLogger(Token.class).error("Error while geting tokens for cosing ldap config : "+sqle.getMessage());
		} finally {
			db.release();
		}
		return tokenMap;
	}

}
