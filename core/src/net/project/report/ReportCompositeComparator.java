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
package net.project.report;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Composite bean comparator
 * Uses list of comparators for comparing two object
 * comparators are used by order they are put in list 
 */
public class ReportCompositeComparator implements Comparator{

	/**
	 * compartor list
	 */
	private List<Comparator> comparators = new ArrayList<Comparator>();
		
	/**
	 * Creates a new  Comparator using the property defined. 
	 * @param comparators the lsit of comparators to compare objects 
	 */
	public ReportCompositeComparator(List<Comparator> comparators){
		this.comparators.addAll(comparators);
	}
	
	/* 
	 *  Compare two objects using the comparators given.
	 * 
	 *  Compares using the comparator with smallest indec in list first. If they are
	 *  equal, then returns the comparison with the next  comparator from the list and so on.
	 *   
	 * @param o1 the object to compare
	 * @param o2 the object to compare to   
	 */
	public int compare(Object o1, Object o2) {
		
		int result = 0;
	    //flag when to stop comparing 	
		boolean endComparing = false;
		
		int comparatorIdx = 0;
		// comparing objects until first "non-equal" comparator found 
		while (!endComparing && comparatorIdx < comparators.size()){
			Comparator comparator = comparators.get(comparatorIdx);
			result = comparator.compare(o1, o2);			
			if (result != 0){
				endComparing = true;
			}
			comparatorIdx = comparatorIdx + 1;
		}		
		return result;
	}
	
	
	
	
}
