/**********************************************************************
 * Copyright (c) 2005-2009 ant4eclipse project team.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nils Hartmann, Daniel Kasmeroglu, Gerd Wuetherich
 **********************************************************************/
package org.ant4eclipse.core.logging;

import static org.junit.Assert.assertEquals;

import org.ant4eclipse.lib.core.logging.A4ELogging;
import org.ant4eclipse.lib.core.logging.Ant4EclipseLogger;
import org.ant4eclipse.lib.core.service.ConfigurationContext;
import org.ant4eclipse.lib.core.service.ServiceRegistry;
import org.ant4eclipse.lib.core.service.ServiceRegistryConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class LoggingUsageTest {

  private static final Class<?> SERVICE_TYPE = Ant4EclipseLogger.class;

  private ByteArrayOutputStream byteout      = new ByteArrayOutputStream();

  @Before
  public void configureServiceRegistry() {
    this.byteout.reset();
    ServiceRegistryConfiguration configuration = new ServiceRegistryConfiguration() {
      public void configure(ConfigurationContext context) {
        PrintStream printer = new PrintStream(LoggingUsageTest.this.byteout);
        context.registerService(new DefaultAnt4EclipseLogger(printer), SERVICE_TYPE.getName());
      }
    };
    ServiceRegistry.configure(configuration);
  }

  /**
   * Returns the current output generated by the logger. This output doesn't contain any cr character in order to
   * support testing o different platforms.
   * 
   * @return The current output without cr characters. Not <code>null</code>.
   */
  private String getCurrentOutput() {
    String result = new String(this.byteout.toByteArray());
    return result.replaceAll("\r", "");
  }

  @After
  public void disposeServiceRegistry() {
    ServiceRegistry.reset();
  }

  @Test
  public void testInfo() {
    A4ELogging.info("no args");
    A4ELogging.info("single arg is: %d", Integer.valueOf(12));
    A4ELogging.info("multiple args are: %d, '%s'", Integer.valueOf(45), "Fredo");
    assertEquals("[INFO] no args\n[INFO] single arg is: 12\n[INFO] multiple args are: 45, 'Fredo'\n",
        getCurrentOutput());
  }

  @Test
  public void testWarn() {
    A4ELogging.warn("no args");
    A4ELogging.warn("single arg is: %d", Integer.valueOf(12));
    A4ELogging.warn("multiple args are: %d, '%s'", Integer.valueOf(45), "Fredo");
    assertEquals("[WARN] no args\n[WARN] single arg is: 12\n[WARN] multiple args are: 45, 'Fredo'\n",
        getCurrentOutput());
  }

  @Test
  public void testError() {
    A4ELogging.error("no args");
    A4ELogging.error("single arg is: %d", Integer.valueOf(12));
    A4ELogging.error("multiple args are: %d, '%s'", Integer.valueOf(45), "Fredo");
    assertEquals("[ERROR] no args\n[ERROR] single arg is: 12\n[ERROR] multiple args are: 45, 'Fredo'\n",
        getCurrentOutput());
  }

  @Test
  public void testDebugEnabled() {
    A4ELogging.debug("no args");
    A4ELogging.debug("single arg is: %d", Integer.valueOf(12));
    A4ELogging.debug("multiple args are: %d, '%s'", Integer.valueOf(45), "Fredo");
    assertEquals("[DEBUG] no args\n[DEBUG] single arg is: 12\n[DEBUG] multiple args are: 45, 'Fredo'\n",
        getCurrentOutput());
  }

  @Test
  public void testDebugDisabled() {
    DefaultAnt4EclipseLogger loggerimpl = (DefaultAnt4EclipseLogger) ServiceRegistry.instance().getService(
        SERVICE_TYPE.getName());
    loggerimpl.setLogLevel(DefaultAnt4EclipseLogger.LOG_LEVEL_INFO);
    A4ELogging.debug("no args");
    A4ELogging.debug("single arg is: %d", Integer.valueOf(12));
    A4ELogging.debug("multiple args are: %d, '%s'", Integer.valueOf(45), "Fredo");
    assertEquals("", getCurrentOutput());
  }

  @Test
  public void testTracingEnabled() {
    A4ELogging.trace("no args");
    A4ELogging.trace("single arg is: %d", Integer.valueOf(12));
    A4ELogging.trace("multiple args are: %d, '%s'", Integer.valueOf(45), "Fredo");
    assertEquals("[TRACE] no args\n[TRACE] single arg is: 12\n[TRACE] multiple args are: 45, 'Fredo'\n",
        getCurrentOutput());
  }

  @Test
  public void testTracingDisabled() {
    DefaultAnt4EclipseLogger loggerimpl = (DefaultAnt4EclipseLogger) ServiceRegistry.instance().getService(
        SERVICE_TYPE.getName());
    loggerimpl.setLogLevel(DefaultAnt4EclipseLogger.LOG_LEVEL_INFO);
    A4ELogging.trace("no args");
    A4ELogging.trace("single arg is: %d", Integer.valueOf(12));
    A4ELogging.trace("multiple args are: %d, '%s'", Integer.valueOf(45), "Fredo");
    assertEquals("", getCurrentOutput());
  }

} /* ENDCLASS */