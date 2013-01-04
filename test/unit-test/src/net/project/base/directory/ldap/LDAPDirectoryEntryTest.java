package net.project.base.directory.ldap;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.base.attribute.IAttribute;
import net.project.base.attribute.TextAttribute;
import net.project.resource.IPersonAttributes;

/**
 * Tests {@link LDAPDirectoryEntry}.
 * @author Tim Morrow
 */
public class LDAPDirectoryEntryTest extends TestCase {

    /**
     * Constructs a test case with the given name.
     * @param s a <code>String</code> containing the name of this test.
     */
    public LDAPDirectoryEntryTest(String s) {
        super(s);
    }

    /**
     * Direct route to unit test this object from the command line.
     * @param args a <code>String[]</code> value which contains the command line options.  (These will be unused.)
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Construct a <code>TestSuite</code> containing the test that this unit test believes it is comprised of.  In most
     * cases, it is only the current class, though it can include others.
     * @return a <code>Test</code> object which is really a TestSuite of unit tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(LDAPDirectoryEntryTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    /**
     * Tests {@link LDAPDirectoryEntry#getEmail()}.
     */
    public void testGetEmail() {

        LDAPDirectoryEntry entry;
        Map attributeValueMap;

        entry = new LDAPDirectoryEntry();

        // Email attribute present
        attributeValueMap = new HashMap();
        attributeValueMap.put(new TestAttribute(IPersonAttributes.EMAIL_ATTRIBUTE), "some email");
        entry.populate(attributeValueMap);
        assertEquals("some email", entry.getEmail());

        // Absent email attribute; null value
        attributeValueMap = new HashMap();
        entry.populate(attributeValueMap);
        assertNull(entry.getEmail());

    }

    /**
     * Tests {@link LDAPDirectoryEntry#isAttributeProvided(String)}.
     */
    public void testIsAttributeProvided() {

        // Null parameter
        try {
            new LDAPDirectoryEntry().isAttributeProvided(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        LDAPDirectoryEntry entry;
        Map attributeValueMap;

        entry = new LDAPDirectoryEntry();
        attributeValueMap = new HashMap();
        attributeValueMap.put(new TestAttribute("att1"), "value1");
        attributeValueMap.put(new TestAttribute("att2"), null);
        entry.populate(attributeValueMap);

        // Attribute present
        assertTrue(entry.isAttributeProvided("att1"));

        // Null attribute present
        assertTrue(entry.isAttributeProvided("att2"));

        // Attribute absent
        assertFalse(entry.isAttributeProvided("doesn't exist"));
    }

    /**
     * Tests {@link LDAPDirectoryEntry#getAttributeValue(String)}.
     */
    public void testGetAttributeValue() {

        LDAPDirectoryEntry entry;
        Map attributeValueMap;

        try {
            new LDAPDirectoryEntry().getAttributeValue(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        entry = new LDAPDirectoryEntry();
        attributeValueMap = new HashMap();
        attributeValueMap.put(new TestAttribute("att1"), "value1");
        attributeValueMap.put(new TestAttribute("att2"), null);
        entry.populate(attributeValueMap);

        // Missing value returns null
        assertNull(entry.getAttributeValue("doesn't exist"));

        // Found value returns value
        assertEquals("value1", entry.getAttributeValue("att1"));

        // Found null value returns empty string
        assertEquals("", entry.getAttributeValue("att2"));

    }

    private static class TestAttribute extends TextAttribute implements IAttribute {
        public TestAttribute(String id) {
            super(id, "Test Attribute");
        }
    }
}
