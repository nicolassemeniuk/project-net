package net.project.wiki.tags;

import info.bliki.htmlcleaner.TagNode;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.NowikiTag;

import java.io.IOException;
import java.util.Map;

import net.project.security.SessionManager;

/**
 * Form tag for Pnet Forms: &lt;form&gt;reference text...&lt;/form&gt;
 * 
 */
public class FormTag extends NowikiTag {
	private final static String FORM_HEADER = "<div id=\"pnetForm\">\n"
		+ "<a id=\"pnetFormGo\" ";

	private final static String FORM_FOOTER = ">Form List</a>\n" + "</div>";

	
	public FormTag() {
		super("form");
	}

	@Override
	public void renderHTML(ITextConverter converter, Appendable buf, IWikiModel model) throws IOException {
		TagNode node = this;
		Map<String, String> tagAtttributes = node.getAttributes();
		String jspRootURL = SessionManager.getJSPRootURL();
		
		buf.append(FORM_HEADER);

		// if url attribute is specified go to specific form
		String attValue = (String) tagAtttributes.get("url");
		System.out.println("url = " + attValue);
		if (attValue != null){
			buf.append(" href=\""+attValue+"\" ");
		} else {
			// otherwise link brings to Form List for current space
			// TODO: build link appropriately
			buf.append(" href=\"" + jspRootURL + "/form/Main.jsp?module=30\" ");
		}
		
		buf.append(FORM_FOOTER);
	}

	@Override
	public boolean isReduceTokenStack() {
		return true;
	}
}
