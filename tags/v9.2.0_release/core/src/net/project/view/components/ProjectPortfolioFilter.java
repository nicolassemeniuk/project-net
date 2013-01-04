/**
 * 
 */
package net.project.view.components;

import java.util.Iterator;

import net.project.base.finder.FinderIngredientHTMLProducer;
import net.project.portfolio.view.PersonalPortfolioViewBuilder;
import net.project.portfolio.view.ViewBuilderFilterPage;
import net.project.util.VisitException;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

/**
 * This component is used to get project portfolio filters presentation. Internally this component uses
 * FinderIngredientHTMLProducer class to get the html presentation. To use this following parameters should be specified -
 * 
 * @param viewBuilder -
 *            object of PersonalPortfolioViewBuilder class Tag to use this component: <t:ProjectPortfolioFilter
 *            viewBuilder = "view"/>
 */
public class ProjectPortfolioFilter {

	/* reference of PersonalPortfolioViewBuilder class */
	@Parameter(required = true)
	private PersonalPortfolioViewBuilder viewBuilder;

	@Property
	private String htmlString = "";

	@SetupRender
	void initializeProjectPortfolioFilter() {
		for (Iterator it = viewBuilder.getFilterPages().iterator(); it.hasNext();) {
			ViewBuilderFilterPage nextPage = (ViewBuilderFilterPage) it.next();
			FinderIngredientHTMLProducer filterListProducer = new FinderIngredientHTMLProducer();
			try {
				nextPage.getFinderFilterList().accept(filterListProducer);
			} catch (VisitException e) {
				Logger.getLogger(ProjectPortfolioFilter.class).error("Error occurred while generating Project Portfolio Filter: " + e.getMessage());
			}
			htmlString += filterListProducer.getHTML() + "<br/>";
		}
	}
}
