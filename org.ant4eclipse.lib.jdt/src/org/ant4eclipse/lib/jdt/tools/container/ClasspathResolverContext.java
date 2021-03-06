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
package org.ant4eclipse.lib.jdt.tools.container;


import org.ant4eclipse.lib.jdt.tools.ResolvedClasspathEntry;
import org.ant4eclipse.lib.platform.model.resource.EclipseProject;
import org.ant4eclipse.lib.platform.model.resource.Workspace;

import java.util.List;

/**
 * <p>
 * An instance of type {@link ClasspathResolverContext} is passed to a {@link ClasspathContainerResolver} to enable the
 * resolution of eclipse class path containers.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface ClasspathResolverContext {

  /**
   * <p>
   * Returns <code>true</code> if the the class path should be resolved relative to the workspace.
   * <p>
   * 
   * @return <code>true</code> if the the class path should be resolved relative to the workspace.
   */
  boolean isWorkspaceRelative();

  /**
   * <p>
   * Returns <code>true</code> if the the class path is a runtime class path.
   * </p>
   * 
   * @return <code>true</code> if the the class path is a runtime class path.
   */
  boolean isRuntime();

  /**
   * <p>
   * Returns the current {@link Workspace}.
   * </p>
   * 
   * @return the current {@link Workspace}.
   */
  Workspace getWorkspace();

  /**
   * <p>
   * Returns <code>true</code>, if a current project is set.
   * </p>
   * 
   * @return <code>true</code>, if a current project is set.
   */
  boolean hasCurrentProject();

  /**
   * <p>
   * Returns the current {@link EclipseProject}.
   * </p>
   * 
   * @return The current {@link EclipseProject}. Maybe null.
   */
  EclipseProject getCurrentProject();

  /**
   * <p>
   * Returns <code>true</code> if the current project is the root project.
   * </p>
   * 
   * @return <code>true</code> if the current project is the root project.
   */
  boolean isCurrentProjectRoot();

  /**
   * <p>
   * Returns the (optional) list of {@link JdtClasspathContainerArgument JdtClasspathContainerArguments}.
   * </p>
   * 
   * @return the (optional) list of {@link JdtClasspathContainerArgument JdtClasspathContainerArguments}.
   */
  List<JdtClasspathContainerArgument> getJdtClasspathContainerArguments();

  /**
   * <p>
   * Returns the {@link JdtClasspathContainerArgument} with the given key.
   * </p>
   * 
   * @param key
   *          the key.
   * @return the {@link JdtClasspathContainerArgument} with the given key.
   */
  JdtClasspathContainerArgument getJdtClasspathContainerArgument(String key);

  /**
   * <p>
   * Resolves the class path for a (java-)project.
   * </p>
   * 
   * @param project
   *          the (java-)project which class path should be resolved.
   */
  void resolveProjectClasspath(EclipseProject project);

  /**
   * <p>
   * Adds a referenced eclipse project to the class path <b>without</b> resolving it.
   * </p>
   * 
   * @param eclipseProject
   *          the eclipse project.
   */
  void addReferencedProjects(EclipseProject eclipseProject);

  /**
   * <p>
   * Adds a resolved class path entry to the class path.
   * </p>
   * 
   * @param entry
   *          the entry to add.
   */
  void addClasspathEntry(ResolvedClasspathEntry resolvedClasspathEntry);

  /**
   * <p>
   * Adds a resolved class path entry to the boot class path.
   * </p>
   * 
   * @param entry
   *          the entry to add.
   */
  void setBootClasspathEntry(ResolvedClasspathEntry resolvedClasspathEntry);
}