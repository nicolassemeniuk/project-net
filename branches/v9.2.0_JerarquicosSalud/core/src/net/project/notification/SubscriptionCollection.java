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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.project.persistence.IXMLPersistence;

/**
 * holds a collection of objects which are guaranteed to be Subscriptions. Permits the normal Colleciton activities aginst these objects.
 *
 * @author chad
 * @version 1
 * @see java.util.ArrayList
 * @see Subscription
 * @since 11/00 JDK 1.3
 */
class SubscriptionCollection extends ArrayList implements IXMLPersistence {

    public SubscriptionCollection() {
        super();
    }


    /**
     returns a String representing the XML representation of all the <code>Subscriptions</code> contained in this Collection appended together
     */
    public String getXML()     //clob
    {
        Iterator subscriptionIterator = iterator();
        Subscription subscription = null;
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(net.project.xml.IXMLTags.XML_VERSION_STRING);     //clob
        stringBuffer.append("<subscription_collection>");
        while (subscriptionIterator.hasNext()) {
            subscription = (Subscription) subscriptionIterator.next();
            stringBuffer.append(subscription.getXMLBody());      //clob
        }
        stringBuffer.append("</subscription_collection>");
        return stringBuffer.toString();
    }

    /**
     returns a String representing the XML bodies of all the <code>Subscriptions</code> contained in this Collection appended together . Each subscription if a WFF but not valid
     */
    public String getXMLBody()      //clob
    {
        Iterator subscriptionIterator = this.iterator();
        Subscription subscription = null;
        StringBuffer stringBuffer = new StringBuffer();

        while (subscriptionIterator.hasNext()) {
            subscription = (Subscription) subscriptionIterator.next();
            stringBuffer.append(subscription.getXMLBody());        //clob
        }
        return stringBuffer.toString();
    }

    /**
     This element must be a Subscription Object or a subclass thereof.
     */
    public Object set(int index, Object element) {
        if (!(element instanceof Subscription))
            throw new java.lang.IllegalArgumentException(" Only a Subscription object  can be set into a SubscriptionCollection");

        return super.set(index, element);
    }


    /**
     This element must be a Subscription Object or a subclass thereof.
     */
    public boolean add(Object o) {
        if (!(o instanceof Subscription))
            throw new java.lang.IllegalArgumentException(" Only a Subscription object  can be set inot a SubscriptionCollection");

        return super.add(o);
    }


    /**
     This element must be a Subscription Object or a subclass thereof.
     */
    public void add(int index, Object element) {
        if (!(element instanceof Subscription))
            throw new java.lang.IllegalArgumentException(" Only a Subscription object  can be set inot a SubscriptionCollection");

        super.set(index, element);
    }


    /**
     Each element in the Collection must be of type Subscription or a subclass thereof.
     */

    public boolean addAll(Collection collection) {
        Iterator collectionIterator = collection.iterator();
        while (collectionIterator.hasNext()) {
            if (!(collectionIterator.next() instanceof Subscription))
                throw new java.lang.IllegalArgumentException(" The elements  of the collection must all be Subscription type objects or subclasses thereof");
        }
        return super.addAll(collection);
    }


    /**
     Each element in the Colleciton must be of type Subscription or a subclass thereof.
     */
    public boolean addAll(int index, Collection collection) {
        Iterator collectionIterator = collection.iterator();
        while (collectionIterator.hasNext()) {
            if (!(collectionIterator.next() instanceof Subscription))
                throw new java.lang.IllegalArgumentException(" The elements  of the collection must all be Subscription type objects or subclasses thereof");
        }
        return super.addAll(index, collection);
    }


}