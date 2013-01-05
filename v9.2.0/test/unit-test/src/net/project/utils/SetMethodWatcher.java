/*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 11611 $
|       $Date: 2003-08-28 22:40:09 +0530 (Thu, 28 Aug 2003) $
|     $Author: matt $
|
+-----------------------------------------------------------------------------*/
package net.project.utils;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Method;

/**
 * This class is designed to keep track of calls to mutator methods on classes.
 * More specifically, it is designed to tell which of all the mutator methods
 * have not been called yet.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class SetMethodWatcher {
    private Set methodList = new HashSet();

    /**
     * Public constructor which initializes the list of mutator methods.
     *
     * @param classToWatch a <code>Class</code> object which allows us to look
     * up the mutator methods.
     */
    public SetMethodWatcher(Class classToWatch) {
        createMethodList(classToWatch);
    }

    /**
     * Indicates that a method with the given name has been called.
     *
     * @param methodName a <code>String</code> containing a method name.  This
     * <code>String</code> does not contain return type, parameters, or
     * parentheses.  For example, "public void setName(String name)" would
     * just simply have "setName" as this String.
     */
    public void methodCalled(String methodName) {
        methodList.remove(methodName);
    }

    /**
     * Indicates that a given method is not going to be called, or shouldn't
     * be considered for the list of the methods that have not yet been called.
     *
     * @param methodName a <code>String</code> containing a method name.  This
     * <code>String</code> does not contain return type, parameters, or
     * parentheses.  For example, "public void setName(String name)" would
     * just simply have "setName" as this String.
     */
    public void skipMethod(String methodName) {
        methodList.remove(methodName);
    }

    /**
     * Indicates if all of the set methods in the class have either been called
     * or have been excluded using the {@link #skipMethod(String)} method.
     *
     * @return a <code>boolean</code> indicating if all the methods have been
     * called.
     */
    public boolean allMethodsCalled() {
        return methodList.size() == 0;
    }

    /**
     * Returns the set of methods that have not yet been called or requested to
     * be skipped.
     *
     * @return a <code>Set</code> of methods that have not yet been called.
     */
    public Set getUncalledMethod() {
        return methodList;
    }

    /**
     * Creates the list of methods that need to be called by using reflection.
     *
     * @param classToWatch a <code>Class</code> object that we are going to use
     * to figure out which methods we are watching for.
     */
    private void createMethodList(Class classToWatch) {
        Method[] methods = classToWatch.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().matches("set.*") && method.getParameterTypes().length > 0) {
                methodList.add(method.getName());
            }
        }

    }
}
