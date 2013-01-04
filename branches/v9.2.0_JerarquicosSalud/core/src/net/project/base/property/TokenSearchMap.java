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

 package net.project.base.property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class provides a search map of tokens.
 * <p>
 * This search map maintains the context order and the token collections
 * corresponding to each context.
 * The token collections have been fetched from the token collection cache.
 * </p>
 *
 * @author Vishwajeet Lohakarey
 * @author Tim Morrow
 * @since 7.4
 */
class TokenSearchMap implements Serializable {

    /**
     * The order in which the contexts should be searched for a token.
     * Each element is a <code>Context</code>.
     * @since 7.4
     */
    private final List contextPrecedenceOrder;

    /**
     * A map of Context-TokenCollection pairs.
     * @since 7.4
     */
    private final Map tokenCollectionMap;

    private static final char OPEN_DELIMITER = '{';
    private static final char CLOSE_DELIMITER = '}';

    /**
     * Indicates whether this search map is modifiable.
     */
    private final boolean isModifiable;

    /**
     * Consrtucts a TokenSearchMap for a given precedence order and a tokencollection map.
     * The TokenSearchMap is unmodifiable.
     * @param contextPrecedenceOrder a list where each element is a <code>Context</code>
     * @param tokenCollectionMap a map where each key is a Context and each
     * value is a <code>TokenCollection</code>
     */
    TokenSearchMap(List contextPrecedenceOrder, Map tokenCollectionMap) {
        this(contextPrecedenceOrder, tokenCollectionMap, false);
    }

    /**
     * Consrtucts a TokenSearchMap for a given precedence order and a tokencollection map.
     * @param contextPrecedenceOrder a list where each element is a <code>Context</code>
     * @param tokenCollectionMap a map where each key is a Context and each
     * value is a <code>TokenCollection</code>
     * @param isModifiable true if this search map must be modifiable; this allows
     * the tokens in the search map to be added / removed.  A search map should
     * only be made modifiable if the token collection map that it is instantiated
     * from was not populated from the token cache; otherwise, it will allow
     * modification of cached tokens
     */
    TokenSearchMap(List contextPrecedenceOrder, Map tokenCollectionMap, boolean isModifiable) {
        this.contextPrecedenceOrder = contextPrecedenceOrder;
        this.tokenCollectionMap = tokenCollectionMap;
        this.isModifiable = isModifiable;
    }

    /* -------------------------------  Getter/Setter Methods  ------------------------------- */


    /**
     * Searches for the token with the specified name.
     * Contexts are searched in order.
     * The method returns <code>null</code> if the property is not found.
     *
     * @param key the name of the token to get
     * @return the token for that name or null if there is none defined
     */
    private Token getToken(String key) {
        return getToken(key, 0);
    }

    /**
     * Searches for the token with the specified name starting at the specified
     * context position.
     * @param key the name of the token to get
     * @param startAtContextPosition the context number to start at, where
     * contexts are numbered from zero
     * @return the token for that name or null if there is none defined
     */
    private Token getToken(String key, int startAtContextPosition) {
        Token token = null;

        int count = 0;
        for (Iterator it = contextPrecedenceOrder.iterator(); it.hasNext(); count++) {

            if (count >= startAtContextPosition) {
                Context nextContext = (Context) it.next();
                TokenCollection tokenCollection = (TokenCollection) this.tokenCollectionMap.get(nextContext);

                Object oval = tokenCollection.getToken(key);
                token = (oval instanceof Token) ? (Token) oval : null;
                if (token != null) {
                    break;
                }

            }

        }

        return token;
    }

    /**
     * Returns a token value in the base context.
     * This returns the token with matching name without hierarchically search
     * the contexts.
     * @param key the name of the token to get
     * @return the token or null if no token was found with the name
     */
    Token getTokenNoDefaults(String key) {
        Context context = (Context) this.contextPrecedenceOrder.get(0);
        Object oval = ((TokenCollection) this.tokenCollectionMap.get(context)).getToken(key);
        return (oval instanceof Token) ? (Token) oval : null;
    }


    /**
     * Returns the value for the token with specified key, recursively checking
     * defaults.
     * @param key the property key (or name)
     * @return the token value or null if the token is not found; if the token
     * is found but has a null value then the empty string is returned
     */
    String getTokenValue(String key) {
        Token token = getToken(key);
        return (token != null) ? ((token.getValue() != null) ? token.getValue() : "") : null;
    }


    /**
     * Returns the collection of resolved token values for the token with specified key.
     * The values are returned from each level of the search hierarchy.
     * There is one value for level in the hierarchy.
     * @param key the property key (or name)
     * @return the ordered collection of resolved String values
     */
    Collection getTokenValuesResolved(String key) {
        List values = new ArrayList();
        String value = null;
        Token token = null;

        int contextPosition = 0;
        for (Iterator it = contextPrecedenceOrder.iterator(); it.hasNext(); contextPosition++) {
            Context nextContext = (Context) it.next();
            TokenCollection tokenCollection = (TokenCollection) tokenCollectionMap.get(nextContext);

            Object oval = tokenCollection.getToken(key);
            token = (oval instanceof Token) ? (Token) oval : null;
            if (token != null) {
                value = getResolvedValue(token, contextPosition);
                values.add(value);
            }

        }

        return values;
    }


    /**
     * Resolves the token's value.
     * This resolves all referenced tokens (and any tokens they may reference)
     * and returns the resultant value.  All referenced tokens are resolved
     * from a specified context and higher.
     * @param token the token to get the resolved value for
     * @param startAtContextPosition the context position to start resolving
     * from
     * @return the resolved value
     */
    private String getResolvedValue(Token token, int startAtContextPosition) {
        String tokenValue = token.getValue();

        int startPosition = -1;
        int endPosition = -1;
        int currentPosition = 0;
        String resolvedValue = null;
        StringBuffer tokenBuff = null;
        StringBuffer resolvedBuff = null;

        // First check that there is a value and it at least has an OPEN_DELIMETER
        if (tokenValue != null && tokenValue.indexOf(OPEN_DELIMITER) >= 0) {
            tokenBuff = new StringBuffer(tokenValue);
            resolvedBuff = new StringBuffer();

            currentPosition = 0;
            while (currentPosition < tokenBuff.length()) {

                if (tokenBuff.charAt(currentPosition) == OPEN_DELIMITER) {
                    // Found an open delimeter
                    // We may be in a token name
                    startPosition = currentPosition;
                    endPosition = -1;

                    // Now search for end position
                    while (++currentPosition < tokenBuff.length()) {

                        if (tokenBuff.charAt(currentPosition) == CLOSE_DELIMITER) {
                            endPosition = currentPosition;
                            break;
                        }
                    }

                    if (endPosition == -1) {
                        // Hit end of string while looking for CLOSE delimeter
                        // Append remainder to resolved buffer
                        resolvedBuff.append(tokenBuff.substring(startPosition));
                        break;

                    } else {
                        // We found a token.  Its name is between open and
                        // close delimeters
                        // Example:  {@prm.global.brand.abbreviation}
                        // Token name is: prm.global.brand.abbreviation
                        String tokenName = tokenBuff.substring((startPosition + 2), endPosition);

                        // Look up this token value from specified context position
                        // onwards
                        Token referencedToken = getToken(tokenName, startAtContextPosition);
                        if (referencedToken != null) {
                            // Ensure this referenced value is resolved too
                            resolvedBuff.append(getResolvedValue(referencedToken, startAtContextPosition));

                        } else {
                            // No value for found token, so add the unresolved
                            // name, including delimeters
                            resolvedBuff.append(tokenBuff.substring(startPosition, endPosition + 1));

                        }

                    }

                } else {
                    // Not an OPEN delimeter.  Simply add it to the resolved
                    // buffer
                    resolvedBuff.append(tokenBuff.charAt(currentPosition));

                }

                currentPosition++;
            } // end while


            resolvedValue = resolvedBuff.toString();

        } else {
            // no delimeter
            resolvedValue = tokenValue;

        }

        return resolvedValue;
    }


    /**
     * Adds the token collections in the specified map to this map, adding
     * the context order to this search map's context order.
     * @param tokenSearchMap the token search map from which to retrieve the defaults to set on this search map.
     */
    void setTailDefaults(TokenSearchMap tokenSearchMap) {
        this.contextPrecedenceOrder.addAll(tokenSearchMap.contextPrecedenceOrder);
        this.tokenCollectionMap.putAll(tokenSearchMap.tokenCollectionMap);
    }

    /**
     * Updates a single token for the specified context.
     * Adds the token if it does not exist and updates it if it does.
     * @param token the token to add or update
     * @param context the context the context to add or update in
     * @throws IllegalStateException if this TokenSearchMap was constructed
     * to be unmodifiable
     */
    void setTokenForContext(Token token, Context context) {
        if (!isModifiable) {
            throw new IllegalStateException("TokenSearchMap is not modifiable");
        }

        TokenCollection tokenCollection = (TokenCollection) this.tokenCollectionMap.get(context);
        tokenCollection.addToken(token);
    }


    /* ------------------------------- IXMLPersistence  ------------------------------- */


    /**
     Converts the object to XML node representation without the xml header tag.
     This method returns the object as XML text.
     @return XML node representation
     */
    String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        Token token = null;

        List tokenList = new ArrayList();
        for (Iterator it = iterator(); it.hasNext(); ) {
            tokenList.add(it.next());
        }

        java.util.Collections.sort(tokenList, new TokenComparator());
        Iterator iter = tokenList.iterator();

        while (iter.hasNext()) {
            token = (Token) iter.next();
            xml.append(token.getXMLBody());

        }
        return xml.toString();
    }

    /**
    Converts the object to XML node representation without the xml header tag.
    This method returns the object as XML text.
    @return XML node representation
    */
	public Object getXMLBody(String tokenPresentationSize) {
		int tokensPresented = 0;
		int tokensTobePresented  = Integer.parseInt(tokenPresentationSize);
		StringBuffer xml = new StringBuffer();
        Token token = null;

        List tokenList = new ArrayList();
        for (Iterator it = iterator(); it.hasNext(); ) {
            tokenList.add(it.next());
        }

        java.util.Collections.sort(tokenList, new TokenComparator());
        Iterator iter = tokenList.iterator();

        while (iter.hasNext() && tokensPresented++ < tokensTobePresented) {
            token = (Token) iter.next();
            xml.append(token.getXMLBody());
        }
        return xml.toString();
	}
	
    /**
     * Provides an iterator over all tokens in the map.
     * The tokens are returned in order of context.
     * If this TokenSearchMap was constructed to be modifiable, then the <code>remove</code>
     * method on the iterator is available; if this TokenSearchMap is not modifiable
     * then calling the <code>remove</code> method will throw an <code>IllegalStateException</code>
     * @return the iterator where each element is a <code>Token</code>
     */
    Iterator iterator() {
        return new TokenIterator();
    }

    /**
     * Provides an iterator over all tokens for all contexts in context order.
     */
    private class TokenIterator implements Iterator {

        private final Iterator contextOrderIterator;
        private Iterator tokenCollectionIterator = null;

        private TokenIterator() {
            contextOrderIterator = TokenSearchMap.this.contextPrecedenceOrder.iterator();
        }

        /**
         * Returns <tt>true</tt> if the iteration has more elements. (In other
         * words, returns <tt>true</tt> if <tt>next</tt> would return an element
         * rather than throwing an exception.)
         *
         * @return <tt>true</tt> if the iterator has more elements.
         */
        public boolean hasNext() {

            if (tokenCollectionIterator != null && tokenCollectionIterator.hasNext()) {
                // We have a token in the current token collection iterator
                return true;

            } else {
                // Token Collection iterator is not initialized
                // or we ran out of tokens in this token collection
                // Try to see if we have another for a new context
                return hasNextNewContext();

            }

        }

        private boolean hasNextNewContext() {

            if (contextOrderIterator.hasNext()) {
                // We have another context
                // Now get the token collection iterator for the next context
                Context nextContext = (Context) contextOrderIterator.next();
                tokenCollectionIterator = ((TokenCollection) TokenSearchMap.this.tokenCollectionMap.get(nextContext)).iterator();

                if (!tokenCollectionIterator.hasNext()) {
                    // No tokens in token collection
                    // Try next context
                    return hasNextNewContext();

                } else {
                    // Got tokens in token collection
                    return true;

                }

            } else {
                // No more contexts
                // No more tokens
                return false;

            }
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration.
         * @exception NoSuchElementException iteration has no more elements.
         */
        public Object next() {
            if (hasNext()) {
                return tokenCollectionIterator.next();
            } else {
                throw new NoSuchElementException();
            }
        }

        /**
         *
         * Removes from the underlying collection the last element returned by the
         * iterator (optional operation).  This method can be called only once per
         * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
         * the underlying collection is modified while the iteration is in
         * progress in any way other than by calling this method.
         *
         * @exception UnsupportedOperationException if the <tt>remove</tt>
         *		  operation is not supported by this Iterator.

         * @exception IllegalStateException if the <tt>next</tt> method has not
         *		  yet been called, or the <tt>remove</tt> method has already
         *		  been called after the last call to the <tt>next</tt>
         *		  method.
         * @throws IllegalStateException if this token search map is
         * not modifiable
         */
        public void remove() {
            if (contextOrderIterator == null) {
                // Special case
                // hasNext has never been called which means next has never been called
                throw new IllegalStateException("Next has not been called");
            }

            if (!isModifiable) {
                throw new IllegalStateException("TokenSearchMap is not modifiable");
            }

            tokenCollectionIterator.remove();
        }

    }
}