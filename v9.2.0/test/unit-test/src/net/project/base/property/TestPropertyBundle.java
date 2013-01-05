package net.project.base.property;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.project.persistence.PersistenceException;

public class TestPropertyBundle extends PropertyBundle {

    protected TestPropertyBundle() {
        super();
    }
    
    Object lookup(String property) {
        return property;
    }

    Collection lookupAllResolved(String property) {
        return Arrays.asList(new String[] {property});
    }

    boolean isTokenDefined(String token) {
        return token != null ? true : false;
    }

    void load(javax.servlet.ServletContext application) throws PersistenceException {
        //do nothing
    }

    boolean isLoaded() {
        return true;
    }
}
