package net.project.base.compatibility.test;

import net.project.base.compatibility.AbstractCompatibilityProviderImpl;
import net.project.base.compatibility.ICompatibilityProvider;
import net.project.base.compatibility.modern.ModernClobProvider;

public class TestCompatabilityProviderImpl extends AbstractCompatibilityProviderImpl implements ICompatibilityProvider {
    public TestCompatabilityProviderImpl() {
        super(
            new TestSessionProvider(),
            new TestResourceProvider(),
            new TestContentProvider(),
            new TestConfigurationProvider(),
            new TestConnectionProvider(),
            new ModernClobProvider(),
            new TestMailSessionProvider()
        );
    }
}