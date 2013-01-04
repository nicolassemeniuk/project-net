package net.project.gui.tml;

import java.util.Collection;
import java.util.Iterator;

import net.project.gui.html.IHTMLOption;

/**
 *
 */
public class TMLOptionList {
	
	 /**
     * Convenience method to create an option list with at most a single value
     * selected.
     * @param optionCollection the collection of <code>IHTMLOption</code>s.
     * @param selectOption the option who's value to select in the list, if found
     * @return the tml(Tapestry Markup language) option list
     */
    public static String makeTmlOptionList(Collection optionCollection) {
		StringBuffer result = new StringBuffer();
		// Iterate over all options, building an option list
		for (Iterator it = optionCollection.iterator(); it.hasNext();) {
			IHTMLOption nextOption = (IHTMLOption) it.next();
			result.append(nextOption.getHtmlOptionValue());
			result.append("=");
			result.append(nextOption.getHtmlOptionDisplay());
			result.append(",");
		}
		return result.toString();
	}
}
