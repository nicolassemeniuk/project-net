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

 package net.project.crossspace;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

/**
 * Generates a list of all objects that the user has permission to share.
 *
 * @author Matthew Flower
 * @since Version 8.0.0
 */
public class ObjectListGenerator implements IXMLPersistence {
    private List topLevelShareableObjects = new ArrayList();
    private Map objectMap = new HashMap();
    private List supportedObjectTypes = new ArrayList();

    public void loadObjectList(String currentUserID, String currentSpaceID) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            int OBJECT_ID_COL = 1;
            int OBJECT_NAME_COL = 2;
            int OBJECT_TYPE_COL = 3;
            int CONTAINER_ID_COL = 4;
            int PERMISSION_TYPE_COL = 5;
            int ALLOWABLE_ACTIONS_COL = 6;

            db.setQuery(
                "select "+
                "  s.object_id, "+
                "  pon.name, "+
                "  o.object_type, "+
                "  s.container_id, "+
                "  s.permission_type, "+
                "  s.allowable_actions "+
                "from "+
                "  pn_object o, "+
                "  pn_object_name pon, "+
                "  pn_shareable s "+
                "where "+
                "  o.object_id = s.object_id "+
                "  and o.object_id = pon.object_id "+
                "  and o.object_type in ('project', 'plan', 'task') "+
                "  and o.record_status = 'A' "+
                "  and s.permission_type <> 0"+
                "start with "+
                "  s.object_id = s.space_id "+
                "connect by "+
                "  s.container_id = prior s.object_id "
            );
            //db.pstmt.setString(1, SessionManager.getUser().getID());
            db.executeQuery();

            topLevelShareableObjects.clear();
            objectMap.clear();

            Set mustFindPermissions = new HashSet();

            while (db.result.next()) {
                ExportedObject exportedObject = new ExportedObject();
                exportedObject.setID(db.result.getString(OBJECT_ID_COL));
                exportedObject.setName(db.result.getString(OBJECT_NAME_COL));
                exportedObject.setObjectType(db.result.getString(OBJECT_TYPE_COL));
                exportedObject.setPermissionType(TradeAgreement.getForID(db.result.getString(PERMISSION_TYPE_COL)));
                exportedObject.setAllowableActions(AllowableActionCollection.construct(db.result.getInt(ALLOWABLE_ACTIONS_COL)));

                if (exportedObject.getPermissionType().equals(TradeAgreement.SPECIFIED_ACCESS)) {
                    mustFindPermissions.add(exportedObject.getID());
                }

                //Create the tree node for this object and make it possible to look up.
                String parentContainer = db.result.getString(CONTAINER_ID_COL);
                TreeNode node = new TreeNode();
                node.object = exportedObject;
                node.parentID = parentContainer;
                objectMap.put(exportedObject.getID(), node);

                if (!Validator.isBlankOrNull(parentContainer)) {
                    //Now add this tree node to its parents list of children
                    TreeNode parentNode = (TreeNode)objectMap.get(parentContainer);

                    if (parentNode == null)
                        continue;

                    if (parentNode.children == null) {
                        parentNode.children = new ArrayList();
                    }
                    parentNode.children.add(node);
                } else {
                    topLevelShareableObjects.add(node);
                }
            }

            if (!mustFindPermissions.isEmpty()) {
                String sql = "select " +
                    "  object_id " +
                    "from " +
                    "  pn_shareable_permissions " +
                    "where " +
                    "  (permitted_object_id = " + currentUserID + " " +
                    "  or permitted_object_id = " + currentSpaceID + ") ";

                sql += " and (";
                for (Iterator it = mustFindPermissions.iterator(); it.hasNext();) {
                    String objectID = (String) it.next();
                    sql += " object_id = " + objectID;

                    if (it.hasNext()) {
                        sql += " or ";
                    } else {
                        sql += ")";
                    }
                }
                db.executeQuery(sql);

                while (db.result.next()) {
                    mustFindPermissions.remove(db.result.getString(1));
                }

                //Anything left in the set doesn't have permissions.
                List objectsToRemove = new ArrayList();
                for (Iterator it = mustFindPermissions.iterator(); it.hasNext();) {
                    String id = (String) it.next();

                    //Traverse up and down the tree, if no permissions were found
                    //The we will remove everything in this branch -- there is
                    //no reason to keep empty branches.
                    TreeNode currentNode = (TreeNode)objectMap.get(id);
                    while (currentNode != null) {
                        TradeAgreement permission = currentNode.object.getPermissionType();
                        boolean anySharesFound = permission.equals(TradeAgreement.ALL_ACCESS) ||
                            (permission.equals(TradeAgreement.SPECIFIED_ACCESS) && !mustFindPermissions.contains(currentNode.object.getID()));
                        if (!anySharesFound) {
                            objectsToRemove.add(currentNode.object.getID());
                        } else {
                            break;
                        }

                        currentNode = (TreeNode)objectMap.get(currentNode.parentID);
                    }

                    for (Iterator it2 = objectsToRemove.iterator(); it2.hasNext();) {
                        String objectID = (String) it2.next();
                        TreeNode object = (TreeNode)objectMap.get(objectID);
                        if (object != null) {
                            TreeNode parent = (TreeNode)objectMap.get(object.parentID);

                            if (object.children == null || object.children.size() == 0) {
                                objectMap.remove(objectID);

                                if (parent != null && parent.children != null) {
                                    parent.children.remove(object);
                                }

                                if (object.children != null && object.children.size() == 0) {
                                    topLevelShareableObjects.remove(object);
                                }
                                
                            } else {
                                object.object.setPermissionType(TradeAgreement.NO_ACCESS);
                            }
                        }
                    }
                }
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to load ShareableObjects", sqle);
        } finally {
            db.release();
        }
    }

    public List getSupportedObjectTypes() {
        return supportedObjectTypes;
    }

    public void setSupportedObjectTypes(List supportedObjectTypes) {
        this.supportedObjectTypes = supportedObjectTypes;
    }

    /**
     * Returns this object's XML representation, including the XML version tag.
     *
     * @return XML representation of this object
     * @see net.project.persistence.IXMLPersistence#getXMLBody
     * @see net.project.persistence.IXMLPersistence#XML_VERSION
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Returns this object's XML representation, without the XML version tag.
     *
     * @return XML representation of this object
     * @see net.project.persistence.IXMLPersistence#getXML
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<objectInfo>");
        xml.append("<mySpaces>");

        int childrenInsertPoint = xml.toString().length();
        StringBuffer childString = new StringBuffer().append("<children>");

        for (Iterator it = topLevelShareableObjects.iterator(); it.hasNext();) {
            TreeNode topLevelNode = (TreeNode)it.next();
            childString.append(topLevelNode.object.getID() + (it.hasNext() ? "," : ""));
            xml.append(printObjectAndChildren(topLevelNode));
        }

        childString.append("</children>");
        xml.insert(childrenInsertPoint, childString.toString());

        xml.append("</mySpaces>");
        xml.append("</objectInfo>");

        return xml.toString();
    }

    public String printObjectAndChildren(TreeNode node) {
        StringBuffer xml = new StringBuffer();

        //Start building the string for the current object
        Object[] params = new Object[] {
            node.object.getObjectType(),
            node.object.getID(),
            XMLUtils.escape(node.object.getName()),
            (node.object.canBeShared() ? "1" : "0"),
            String.valueOf(node.object.getAllowableActions().getDatabaseID())
        };
        String objectString = MessageFormat.format("<object type=\"{0}\" " +
            "id=\"{1}\" name=\"{2}\" enabled=\"{3}\" actionsAllowed=\"{4}\"", params);
        xml.append(objectString);

        //Now build the string for the children and close the object tag
        if (node.children != null && node.children.size() > 0) {
            xml.append(">");
            int appendPoint = objectString.length()+1;
            String childString = "<children>";
            for (Iterator it = node.children.iterator(); it.hasNext();) {
                TreeNode childNode = (TreeNode)it.next();
                childString += (childNode.object.getID() + (it.hasNext() ? "," : ""));
                xml.append(printObjectAndChildren(childNode));
            }
            childString += "</children>";
            xml.insert(appendPoint, childString);
            xml.append("</object>");
        } else {
            xml.append("/>");
        }

        return xml.toString();
    }

}

class TreeNode {
    public ExportedObject object;
    public List children;
    public String parentID;

    boolean isLeaf() {
        return children == null || children.size() == 0;
    }
}