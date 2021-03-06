package org.ant4eclipse.ant.pde.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.ant4eclipse.lib.core.Assure;
import org.ant4eclipse.lib.core.ClassName;
import org.ant4eclipse.lib.core.util.Utilities;
import org.ant4eclipse.lib.jdt.ecj.ClassFile;
import org.ant4eclipse.lib.jdt.ecj.ClassFileLoader;
import org.ant4eclipse.lib.jdt.ecj.ClassFileLoaderFactory;
import org.ant4eclipse.lib.jdt.ecj.EcjAdapter;
import org.ant4eclipse.lib.jdt.model.project.JavaProjectRole;
import org.ant4eclipse.lib.jdt.tools.JdtResolver;
import org.ant4eclipse.lib.jdt.tools.ResolvedClasspath;
import org.ant4eclipse.lib.jdt.tools.container.JdtClasspathContainerArgument;
import org.ant4eclipse.lib.platform.model.resource.EclipseProject;
import org.objectweb.asm.ClassReader;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class TestClassAnalyser {

  /** the eclipse project to test */
  private EclipseProject  _eclipseProject;

  private ClassFileLoader _fileLoader;

  /**
   * <p>
   * Creates a new instance of type {@link TestClassAnalyser}.
   * </p>
   * 
   * @param eclipseProject
   *          the eclipse project to test.
   */
  public TestClassAnalyser(EclipseProject eclipseProject) {
    Assure.notNull("eclipseProject", eclipseProject);

    //
    this._eclipseProject = eclipseProject;

    ResolvedClasspath classpath = JdtResolver.resolveProjectClasspath(this._eclipseProject, false, false,
        new LinkedList<JdtClasspathContainerArgument>());

    this._fileLoader = ClassFileLoaderFactory.createClasspathClassFileLoader(this._eclipseProject.getFolder(),
        EcjAdapter.PROJECT, classpath.getClasspathFiles(), new File[0]);
  }

  /**
   * <p>
   * Returns the test classes as a string.
   * </p>
   * 
   * @return the test classes as a string.
   */
  public String getTestClassesAsString() {

    // create result
    StringBuilder builder = new StringBuilder();

    //
    Set<String> testClasses = getTestClasses();

    //
    if (testClasses.isEmpty()) {
      throw new RuntimeException(String.format("Project '%s' does not contain any test classes!",
          this._eclipseProject.getSpecifiedName()));
    }

    // iterate over all the
    for (Iterator<String> iterator = testClasses.iterator(); iterator.hasNext();) {
      builder.append(iterator.next());
      if (iterator.hasNext()) {
        builder.append("\n");
      }
    }

    // returns the result
    return builder.toString();
  }

  /**
   * <p>
   * Returns a set with the names of all contained test classes.
   * </p>
   * 
   * @return a set with the names of all contained test classes.
   */
  public Set<String> getTestClasses() {

    // create the result set
    Set<String> result = new HashSet<String>();

    // get the java projetc role
    JavaProjectRole javaProjectRole = this._eclipseProject.getRole(JavaProjectRole.class);

    // iterate over all the output folder names
    for (String outputFolderName : javaProjectRole.getAllOutputFolders()) {

      // get the output folder
      File outputFolder = this._eclipseProject.getChild(outputFolderName);

      // iterate over all contained children
      for (File file : Utilities.getAllChildren(outputFolder)) {

        // scan the class files
        try {
          String className = scanClass(file);
          if (className != null) {
            result.add(className);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    // return the result
    return result;
  }

  /**
   * <p>
   * Scans the class files.
   * </p>
   * 
   * @param classFile
   * @return the name of the class file if a the class file is a junit test class.
   * @throws FileNotFoundException
   * @throws IOException
   */
  private String scanClass(File classFile) throws FileNotFoundException, IOException {

    // return if file is no class file
    if (!classFile.getName().endsWith(".class")) {
      return null;
    }

    // return if file is an inner type
    if (classFile.getName().contains("$")) {
      return null;
    }

    // scan the file
    JUnitVisitor classVisitor = new JUnitVisitor();
    new ClassReader(new FileInputStream(classFile)).accept(classVisitor, 0);

    boolean isTestClass = classVisitor.isTestClass();

    if (!isTestClass) {
      String superClass = classVisitor.getSuperClassName();
      isTestClass = scanSuperClasses(superClass);
    }

    // return the result
    return isTestClass ? classVisitor.getClassName() : null;
  }

  /**
   * <p>
   * </p>
   * 
   * @param className
   * @return
   */
  private boolean scanSuperClasses(String className) {

    if (className == null || className.equals("java.lang.Object")) {
      return false;
    }

    ClassFile superClassFile = this._fileLoader.loadClass(ClassName.fromQualifiedClassName(className));

    JUnitVisitor classVisitor = new JUnitVisitor();
    new ClassReader(superClassFile.getBytes()).accept(classVisitor, 0);

    boolean isTestClass = classVisitor.hasTestAnnotations();

    if (!isTestClass) {
      String superClass = classVisitor.getSuperClassName();
      isTestClass = scanSuperClasses(superClass);
    }

    return isTestClass;
  }
}
