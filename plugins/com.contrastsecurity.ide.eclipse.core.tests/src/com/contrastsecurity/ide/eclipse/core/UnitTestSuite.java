package com.contrastsecurity.ide.eclipse.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.contrastsecurity.ide.eclipse.core.unit.ContrastCoreActivatorTest;
import com.contrastsecurity.ide.eclipse.core.unit.UtilTest;

@RunWith(Suite.class)
@SuiteClasses({
	ContrastCoreActivatorTest.class,
	UtilTest.class
})
public class UnitTestSuite {
}
