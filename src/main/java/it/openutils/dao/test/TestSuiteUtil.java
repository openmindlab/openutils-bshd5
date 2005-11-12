package it.openutils.dao.test;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Dynamic test suite derived from http://www.javaworld.com/javaworld/jw-12-2000/jw-1221-junit.html. Runs all Java test
 * cases in the source tree that extend TestCase. This helps running tests faster with ant/maven since httpunit tests
 * requires forking and starting a new java process for each test is too slow.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */
public abstract class TestSuiteUtil
{

    /**
     * logger.
     */
    private static Log log = LogFactory.getLog(TestSuiteUtil.class);

    /**
     * Iterates over the classes accessible via the iterator and adds them to the test suite.
     * @param suite TestSuite empty test suite
     * @param classIterator iterator on loaded classes
     * @return int number of testcases added to suite
     */
    private static int addAllTests(TestSuite suite, Iterator classIterator)
    {
        int testClassCount = 0;
        while (classIterator.hasNext())
        {
            Class testCaseClass = (Class) classIterator.next();

            try
            {
                Method suiteMethod = testCaseClass.getMethod("suite", new Class[0]);
                Test test = (Test) suiteMethod.invoke(null, new Object[0]); // static method
                suite.addTest(test);
            }
            catch (NoSuchMethodException e)
            {
                suite.addTest(new TestSuite(testCaseClass));
            }
            catch (Exception e)
            {
                log.error("Failed to execute suite ()", e);
            }
            if (log.isDebugEnabled())
            {
                log.debug("Loaded test case: " + testCaseClass.getName());
            }
            testClassCount++;
        }
        return testClassCount;
    }

    /**
     * Dynamically create a test suite from a set of class files in a directory tree.
     * @throws Throwable in running the suite() method
     * @return TestSuite for all the found tests
     */
    public static Test generateTestSuite(Class caller, String packageRoot) throws Throwable
    {
        try
        {

            String className = caller.getName();
            URL testFile = caller.getResource(ClassUtils.getShortClassName(caller) + ".class");
            log.debug(testFile.getFile());
            File classRoot = new File(testFile.getFile()).getParentFile();
            while (className.indexOf(".") > -1)
            {
                classRoot = classRoot.getParentFile();
                className = className.substring(className.indexOf(".") + 1, className.length());
            }
            if (log.isDebugEnabled())
            {
                log.debug("Looking for classes in " + classRoot);
            }
            ClassFinder classFinder = new ClassFinder(classRoot, packageRoot);
            TestCaseLoader testCaseLoader = new TestCaseLoader();
            testCaseLoader.loadTestCases(classFinder.getClasses());
            TestSuite suite = new TestSuite();
            int numberOfTests = addAllTests(suite, testCaseLoader.getClasses());
            if (log.isDebugEnabled())
            {
                log.debug("Number of test classes found: " + numberOfTests);
            }
            return suite;
        }
        catch (Throwable t)
        {
            // This ensures we have extra information.
            // Otherwise all we get is a "Could not invoke the suite method." message.
            log.error("suite()", t);
            throw t;
        }
    }

    /**
     * Dynamically create a test suite from a set of class files in a directory tree.
     * @throws Throwable in running the suite() method
     * @return TestSuite for all the found tests
     */
    public static Test generateTestSuite(Class caller, String[] packageRoot) throws Throwable
    {
        TestSuite suite = new TestSuite();

        for (int j = 0; j < packageRoot.length; j++)
        {
            suite.addTest(generateTestSuite(caller, packageRoot[j]));
        }
        return suite;
    }
}


/**
 * This class is responsible for searching a directory for class files. It builds a list of fully qualified class names
 * from the class files in the directory tree.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */

class ClassFinder
{

    /**
     * List of found classes (names).
     */
    private List classNameList = new ArrayList();

    /**
     * length of the base package String.
     */
    private int startPackageLength;

    /**
     * Construct the class finder and locate all the classes in the directory structured pointed to by
     * <code>classPathRoot</code>. Only classes in the package <code>packageRoot</code> are considered.
     * @param classPathRoot classpath directory where to search for test cases
     * @param packageRoot root package for tests to be included
     */
    public ClassFinder(File classPathRoot, String packageRoot)
    {
        startPackageLength = classPathRoot.getAbsolutePath().length() + 1;
        String directoryOffset = packageRoot.replace('.', File.separatorChar);
        findAndStoreTestClasses(new File(classPathRoot, directoryOffset));
    }

    /**
     * Given a file name, guess the fully qualified class name.
     * @param file class file
     * @return class name
     */
    private String computeClassName(File file)
    {
        String absPath = file.getAbsolutePath();
        String packageBase = absPath.substring(startPackageLength, absPath.length() - 6);
        String className;
        className = packageBase.replace(File.separatorChar, '.');
        return className;
    }

    /**
     * This method does all the work. It runs down the directory structure looking for java classes.
     * @param currentDirectory directory to search class files in
     */
    private void findAndStoreTestClasses(File currentDirectory)
    {
        String[] files = currentDirectory.list();
        for (int i = 0; i < files.length; i++)
        {
            File file = new File(currentDirectory, files[i]);
            String fileBase = file.getName();
            int idx = fileBase.indexOf(".class");
            final int CLASS_EXTENSION_LENGTH = 6;

            if (idx != -1 && (fileBase.length() - idx) == CLASS_EXTENSION_LENGTH)
            {
                String className = computeClassName(file);
                classNameList.add(className);
            }
            else
            {
                if (file.isDirectory())
                {
                    findAndStoreTestClasses(file);
                }
            }
        }
    }

    /**
     * Return the found classes.
     * @return Iterator on classes names
     */
    public Iterator getClasses()
    {
        return classNameList.iterator();
    }
}


/**
 * Responsible for loading classes representing valid test cases.
 * @author Fabrizio Giustina
 * @version $Revision$ ($Author$)
 */

class TestCaseLoader
{

    /**
     * list containing laded classes.
     */
    private List classList = new ArrayList();

    /**
     * Load the classes that represent test cases we are interested.
     * @param classNamesIterator An iterator over a collection of fully qualified class names
     */
    public void loadTestCases(Iterator classNamesIterator)
    {
        while (classNamesIterator.hasNext())
        {
            String className = (String) classNamesIterator.next();
            try
            {
                Class candidateClass = Class.forName(className);
                addClassIfTestCase(candidateClass);
            }
            catch (ClassNotFoundException e)
            {
                System.err.println("Cannot load class: " + className + " " + e.getMessage());
            }
            catch (NoClassDefFoundError e)
            {
                System.err.println("Cannot load class that " + className + " is dependant on");
            }
        }
    }

    /**
     * Adds testCaseClass to the list of classes if the class extends TestCase and it's not abstract.
     * @param testCaseClass class to test
     */
    private void addClassIfTestCase(Class testCaseClass)
    {
        if (TestCase.class.isAssignableFrom(testCaseClass)
            && !TestSuiteUtil.class.isAssignableFrom(testCaseClass)
            && !Modifier.isAbstract(testCaseClass.getModifiers()))
        {
            try
            {
                testCaseClass.getMethod("suite", new Class[]{});
            }
            catch (Throwable e)
            {
                // don't add other test suites!
                classList.add(testCaseClass);
            }
        }
    }

    /**
     * Obtain an iterator over the collection of test case classes loaded by <code>loadTestCases</code>.
     * @return Iterator on loaded classes list
     */
    public Iterator getClasses()
    {
        return classList.iterator();
    }
}