/**
 * 
 */
package net.project.gui.extjs;

import java.util.Collection;
import java.util.Iterator;

import net.project.gui.html.IHTMLOption;
import net.project.util.HTMLUtils;

/**
 * 
 *
 */
public class ExtJSOptionList {
	
	 /**
     * Convenience method to create an option list with at most a single value
     * selected.
     * @param optionCollection the collection of <code>IHTMLOption</code>s.
     * @param selectOption the option who's value to select in the list, if found
     * @return the ExtJS option list
     */
    public static String makeExtJSOptionList(Collection optionCollection) {
		StringBuffer result = new StringBuffer();
		result.append("[");
		//[["equals","Equal To"],['notequals','Not Equal To'],['contains','Contains']]
		// Iterate over all options, building an option list
		for (Iterator it = optionCollection.iterator(); it.hasNext();) {
			IHTMLOption nextOption = (IHTMLOption) it.next();
			if(!result.toString().equals("[")){
				result.append(",");
			}
			result.append("[");
			result.append("\"");
			result.append(nextOption.getHtmlOptionValue());
			result.append("\"");
			result.append(",");
			result.append("\"");
			result.append(HTMLUtils.escape(nextOption.getHtmlOptionDisplay().replaceAll("\"", "``")).replaceAll("'", "&acute;"));
			result.append("\"");
			result.append("]");
		}
		result.append("]");
		return result.toString();
	}

}
