package com.Webtrekk.SDKTest;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Created by vartbaronov on 23.06.17.
 */

public class TestClassRunner extends BlockJUnit4ClassRunner {
    public TestClassRunner(Class<?> aClass) throws InitializationError {
        super(aClass);
    }

    @Override
    protected Statement methodBlock(FrameworkMethod frameworkMethod) {
        BaseTest.mTestName = frameworkMethod.getName();
        return super.methodBlock(frameworkMethod);
    }

}
