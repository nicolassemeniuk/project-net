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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.form.report.formitemtimeseries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Contains summary deltas of form data over time.  As an example, it would hold
 * the following data about a "Clothing Location" form:
 *
 * <pre>
 * Date         Field Value (Clothing Location)     Count
 * ----         ------------------------------      -----
 * 01/01/2002   On the Floor                        3
 *              In the Hamper                       9
 *              In the Closet                       2
 * 01/08/2002   On The Floor                        0
 *              In the Hamper                       0
 *              In the Closet                       14
 * </pre>
 *
 * This data is populated by giving an initial count for a field value then
 * adding deltas for each date.  For example, in the example about, the field
 * value "On the Floor" would be given an initial value of 3 on 01/01/2002.  A
 * delta of -3 would be applied on 01/08/2002.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class FormItemTimeSeriesData {
    private Map initialValues = new HashMap();
    /**
     * A set of all unique dates that appear in this data set.  This information
     * is kept separate of the map to make it easier to return a unique set of
     * the dates kept in this map.  If we didn't do this, we'd have to iterate
     * through the map each time and dissect the keys to product a set.  I
     * decided to keep the set prebuilt instead.
     */
    private SortedSet dates = new TreeSet(
        new Comparator() {
            public int compare(Object o1, Object o2) {
                Date d1 = (Date)o1;
                Date d2 = (Date)o2;

                return d1.compareTo(d2);
            }
        });
    /**
     * A set of all unique field values that appear in this data set.  This
     * information is kept separate of the map to make it easier to return a
     * unique set of the field values kept in this map.  If we didn't do this,
     * we'd have to iterate through the <code>deltas</code> map each time the
     * field values were requested and dissect the <code>deltas</code> map keys
     * to produce a set.  I decided to keep the set prebuilt instead.
     */
    private SortedSet fieldValues = new TreeSet();
    /**
     * A map containing fieldValues that were selected or unselected on a given
     * day.  It is a map between a FormItemTimeSeriesKey object and an
     * java.lang.Integer object.
     */
    private Map deltas = new HashMap();
    /**
     * This map of data is designed to make it much faster to calculate the
     * count of a field value on a given date just as long as the user is requesting
     * that data in sequential order.
     */
    private Map cachedFieldCount = new HashMap();

    /**
     * Add the absolute number of instances of a form item value that existed on
     * a given day.
     *
     * @param date a <code>Date</code> object which indicates the date on which
     * the form items had this value.
     * @param fieldValue a <code>String</code> value indicating which field value
     * we are reporting on.
     * @param count a <code>Integer</code> object indicating how many form items
     * had this field value on this date.
     */
    public void addInitialValue(Date date, String fieldValue, Integer count) {
        //Nulls look bad on output and throw an error when inserted into a treeset
        if (fieldValue == null) {
            fieldValue = "";
        }

        initialValues.put(fieldValue, count);
        addDelta(date, fieldValue, new Integer(0));
    }

    /**
     * Add a count of how many instances of a certain field value were added or
     * removed on a given date.  If this count already exists in the
     * collection, the new value passed through the <code>count</code> will be
     * added to the existing value.
     *
     * @param date a <code>Date</code> value containing the date or beginning of
     * date range of the count we are going to show.
     * @param fieldValue a <code>String</code> value containing a field value
     * which is unique to the date parameter.
     * @param count a <code>Integer</code> object containing the number of
     * instances of the <code>fieldValue</code> parameter that were selected or
     * unselected on the <code>Date</code> indentified by the <code>date</code>
     * parameter.
     */
    public void addDelta(Date date, String fieldValue, Integer count) {
        //Nulls look bad on output and throw an error when inserted into a treeset
        if (fieldValue == null) {
            fieldValue = "";
        }

        FormItemTimeSeriesKey key = new FormItemTimeSeriesKey(date, fieldValue);
        Integer oldCount = (Integer)deltas.get(key);

        if (oldCount != null) {
            //There was already a delta for that time.  Add the two together to
            //make a aggregate delta
            count = new Integer(count.intValue() + oldCount.intValue());
        }

        deltas.put(key, count);
        if (!initialValues.containsKey(fieldValue)) {
            initialValues.put(fieldValue, new Integer(0));
        }

        fieldValues.add(fieldValue);
        dates.add(date);
    }

    /**
     * Clear out any data that exists in this object, returning it to the state
     * it was in immediately after creation.
     */
    public void clear() {
        dates.clear();
        fieldValues.clear();
        initialValues.clear();
        deltas.clear();
    }

    /**
     * Get the unique set of date values that exist in this object sorter in
     * least to greatest date value.
     *
     * @return a <code>Set</code> of <code>Date</code> values ordered least to
     * greatest.
     */
    public SortedSet getAllDatesInSeries() {
        return dates;
    }

    /**
     * Get a <code>Set</code> containing all of the unique values that have been
     * stored in this object.
     *
     * @return a <code>Set</code> containing all of the field values stored in
     * this object.
     */
    public SortedSet getAllFieldValues() {
        return fieldValues;
    }

    /**
     * Get the number of times a field value was found on a given date.
     *
     * @param date a <code>Date</code> value indentifying the date or beginning
     * of date range for which we want to receive a count of field value.
     * @param fieldValue a <code>String</code> containing the field value for
     * which we want to retrieve a frequency count.
     * @return a <code>Integer</code> object which indicates the number of times
     * the field value appear in a form item on the given date.
     */
    public Integer getCountOnDate(Date date, String fieldValue) {
        //Get the previous date out of the cache, if it can be found
        CacheValue cachedValue = findCachedValue(date, fieldValue);

        Integer count;
        if (cachedValue == null) {
            //Either no requests have been made for this value or the user is
            //requesting data out of order.  Compute the value from the start.
            Integer initialValue = (Integer)initialValues.get(fieldValue);
            if (initialValue == null) {
                initialValue = new Integer(0);
            }
            count = applyDeltas(fieldValue, initialValue, (Date)dates.first(), date);
        } else {
            //Some data was already cached for this value.  Apply the deltas to
            //get the correct value
            count = applyDeltas(fieldValue, cachedValue.count, cachedValue.date, date);
        }

        //Store this field in the cache so we don't have to look it up next time.
        cachedFieldCount.put(fieldValue, new CacheValue(date, count));

        return count;
    }

    private CacheValue findCachedValue(Date cannotBeLaterThan, String fieldValue) {
        CacheValue cv = (CacheValue)cachedFieldCount.get(fieldValue);

        if ((cv != null) && (cv.date.compareTo(cannotBeLaterThan)>0)) {
            //The user is requesting dates out of order.  We cannot use the cached value
            cv = null;
        }

        return cv;
    }


    private Integer applyDeltas(String fieldValue, Integer initialCount, Date beginningOfRange, Date endOfRange) {
        SortedSet dateSubset = dates.subSet(beginningOfRange, endOfRange);
        //Currently treeset.subset returns the subset that includes that beginning
        //of the range but omits the end of the range.  Normally that would be
        //great, but in our case, we have sorted the dates backwards.
        ArrayList datesToFindDeltas = new ArrayList(dateSubset);
        datesToFindDeltas.remove(beginningOfRange);
        datesToFindDeltas.add(endOfRange);

        int currentCount = initialCount.intValue();
        for (Iterator it = datesToFindDeltas.iterator(); it.hasNext();) {
            Date deltaDate = (Date)it.next();
            FormItemTimeSeriesKey key = new FormItemTimeSeriesKey(deltaDate, fieldValue);
            Integer deltaCount = (Integer)deltas.get(key);
            if (deltaCount != null) {
                currentCount += deltaCount.intValue();
            }
        }

        return new Integer(currentCount);
    }
}

/**
 * A private class used to form a unique key for <code>FormItemTimeSeriesData</code>
 * hash map.  It is a combination of the date on which a form field took a value
 * and a string representing the field value.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class FormItemTimeSeriesKey {
    /** Date on which the field value was used. */
    private Date date;
    /** Field value which a field took on that date. */
    private String fieldValue;

    /**
     * Standard constructor.
     *
     * @param date a <code>date</code> on which a field value was used.
     * @param fieldValue a <code>String</code> value that a form item took on a
     * specific date.
     */
    public FormItemTimeSeriesKey(Date date, String fieldValue) {
        this.date = date;
        this.fieldValue = fieldValue;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormItemTimeSeriesKey)) return false;

        final FormItemTimeSeriesKey formItemTimeSeriesKey = (FormItemTimeSeriesKey)o;

        if (date != null ? !date.equals(formItemTimeSeriesKey.date) : formItemTimeSeriesKey.date != null) return false;
        if (fieldValue != null ? !fieldValue.equals(formItemTimeSeriesKey.fieldValue) : formItemTimeSeriesKey.fieldValue != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (date != null ? date.hashCode() : 0);
        result = 29 * result + (fieldValue != null ? fieldValue.hashCode() : 0);
        return result;
    }
}

class CacheValue {
    public Date date;
    public Integer count;

    public CacheValue(Date date, Integer count) {
        this.date = date;
        this.count = count;
    }
}