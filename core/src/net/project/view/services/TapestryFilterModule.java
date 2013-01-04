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
package net.project.view.services;

import java.io.IOException;

import net.project.security.SessionManager;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.AliasContribution;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.RequestExceptionHandler;
import org.apache.tapestry5.services.ResponseRenderer;
import org.apache.tapestry5.services.URLEncoder;
import org.slf4j.Logger;

/**
 * Application global configurations
 */
public class TapestryFilterModule {

	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {

		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");

		configuration.add(SymbolConstants.SCRIPTS_AT_TOP, "false");
	}

	/**
	 * Decorate Error page
	 * @param logger
	 * @param renderer
	 * @param componentSource
	 * @param productionMode
	 * @param service
	 * @return
	 */
	public RequestExceptionHandler decorateRequestExceptionHandler(final Logger logger,
			final ResponseRenderer renderer, final ComponentSource componentSource,
			@Symbol(SymbolConstants.PRODUCTION_MODE)
			boolean productionMode, Object service) {
		if (!productionMode) {
			return null;
		}

		return new RequestExceptionHandler() {
			public void handleRequestException(Throwable exception) throws IOException {
				logger.error("Unexpected runtime exception: " + exception.getMessage(), exception);
				ExceptionReporter error = (ExceptionReporter) componentSource.getPage("Error");
				error.reportException(exception);
				renderer.renderPageMarkupResponse("Error");
			}
		};
	}

	/**
	 * Method to override Tapestry BaseURLSource to handle HTTP / HTTPS connections 
	 * @param configuration
	 */
	public static void contributeAliasOverrides(Configuration<AliasContribution> configuration) {
		BaseURLSource source = new BaseURLSource() {

			public String getBaseURL(boolean secure) {
				String protocol = SessionManager.getSiteScheme().toLowerCase().startsWith("https") ? "https" : "http";

				return String.format("%s://%s", protocol, SessionManager.getSiteHost());
			}
		};

		configuration.add(AliasContribution.create(BaseURLSource.class, source));

		// PnetURLEncoder service to override URLEncoder service of Tapestry
		// for supporting non English characters in url
		configuration.add(AliasContribution.create(URLEncoder.class, new PnetURLEncoderImpl()));
	}

}
