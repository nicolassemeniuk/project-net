package net.project.portfolio;

import java.io.Serializable;
import java.util.Iterator;

import net.project.business.BusinessSpaceFinder;
import net.project.financial.FinancialSpace;
import net.project.financial.FinancialSpaceFinder;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

public class FinancialPortfolio extends Portfolio implements IJDBCPersistence, IXMLPersistence, Serializable {
	
    /**
     * The current user context.
     */
    private User user = null;
    
    /**
     * Indicates whether the portfolio entries have been loaded.
     */
    private boolean entriesLoaded = false;
    
    /**
     * Creates an empty BusinessPortfolio.
     */
    public FinancialPortfolio() {
        super();
        setContentType(net.project.space.ISpaceTypes.FINANCIAL_SPACE);
    }
    
    /**
     * Specifies the current user context.
     *
     * @param user the current user context
     * @see #getUser
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the current user context.
     *
     * @return the current user context
     * @see #setUser
     */
    public User getUser() {
        return this.user;
    }
    
    public void load() throws PersistenceException{
    	if ((this.user != null) && (this.user.getID() != null)) {
            // Loads Financial Spaces that the user is a member of
            // There is no stored portfolio containing their membership,
            // it is performed by lookin in PN_SPACE_HAS_PERSON
            loadForUser();

        } else {
            // Silently do nothing
            // It turns out that in some instances no id or space list is
            // provided
            // It is expected that an empty portfolio results from this
            // scenario
            // Example:  Listing related business for a business.  If a business
            // has no related businesses, it has no portfolio to load
        }
    }
    
    /**
     * Load the financial portfolio for the current user from the database. Uses the current record status for loading.
     *
     * @throws PersistenceException if there is a problem loading
     * @throws IllegalStateException if the current user context is null
     * @see #setUser
     */
    private void loadForUser() throws PersistenceException {

        if (getUser() == null) {
            throw new IllegalStateException("User is null");
        }

        addAll(new FinancialSpaceFinder().findByUser(this.user));
        this.entriesLoaded = true;
    }
    
    /**
     * Returns a tree-style portfolio view.
     *
     * @return a tree view view
     */
    public IPortfolioView getTreeView() {
        IPortfolioView view = null;

        try {
            view = new PersonalFinancialTreeView(this);
        } catch (PersistenceException pe) {
            // Do nothing; a null tree view is returned
        }

        return view;
    }
    
    /**
     * Converts the object to XML representation. This method returns the object as XML text.
     *
     * @return XML representation of the object
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }


    /**
     * Converts the object to XML node representation without the xml header tag. This method returns the object as XML
     * text.
     *
     * @return XML node representation
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns the XML for this portfolio. Includes portfolio properties and all entries.
     *
     * @return the XML document
     */
    protected XMLDocument getXMLDocument() {
        XMLDocument doc = new XMLDocument();

        try {
            doc.startElement("FinancialPortfolio");
            addXMLProperties(doc);
            doc.endElement();

        } catch (XMLDocumentException e) {
            // Return partially built exception
        }

        return doc;
    }

    /**
     * Adds all XML properties to the specified document. Assumes an element is started.
     *
     * @param doc the XMLDocument to which to add properties
     * @throws XMLDocumentException if there is a problem adding elements
     */
    protected void addXMLProperties(XMLDocument doc) throws XMLDocumentException {

        // Add Portfolio properties
        doc.addElement("PortfolioID", getID());
        doc.addElement("ParentSpaceID", getParentSpaceID());
        doc.addElement("Name", getName());
        doc.addElement("Description", getDescription());
        doc.addElement("Type", getType());
        doc.addElement("ContentType", getContentType());

        // Add all entries
        for (Iterator it = iterator(); it.hasNext();) {
            doc.addXMLString(((FinancialSpace) it.next()).getXMLBody());
        }

    }

}
