package it.openutils.dao.test;

import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.lang.ClassUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.filter.SequenceTableFilter;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.ext.mssql.InsertIdentityOperation;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;


/**
 * Base class for running DAO tests.
 * @author fgiust
 * @version $Revision $ ($Author $)
 */
public abstract class BaseDAOTestCase extends TestCase
{

    /**
     * Spring application context.
     */
    protected ApplicationContext ctx;

    /**
     * logger. Not static so it can be reuser in tests
     */
    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Resource bundle.
     */
    protected ResourceBundle rb;

    /**
     * Hibernate session factory.
     */
    private SessionFactory sessionFactory;

    private static final String BASETEST_DELETE = "/_BaseDAOTest-delete.xml";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        ctx = ApplicationContextHolder.getCtx();

        if (ctx == null)
        {
            // load Spring's BeanFactory
            // not using static since it often locks resources on filesystem after test is completed
            String[] paths = {"/spring-daotests.xml"};
            ctx = new ClassPathXmlApplicationContext(paths);

            ApplicationContextHolder.setCtx(ctx);
        }

        // Since a ResourceBundle is not required for each class, just do a simple check to see if one exists
        String className = this.getClass().getName();

        try
        {
            rb = ResourceBundle.getBundle(className);
        }
        catch (MissingResourceException mre)
        {
            // ignore
        }

        // insert values
        IDataSet dataSet = null;

        String datesetFileName = "/" + ClassUtils.getShortClassName(getClass()) + "-load.xml";
        InputStream testData = getClass().getResourceAsStream(datesetFileName);

        if (testData != null)
        {
            if (log.isDebugEnabled())
            {
                log.debug("loading dataset {}", datesetFileName);
            }

            dataSet = new XmlDataSet(testData);
        }
        else
        {
            // check for excel
            datesetFileName = "/" + ClassUtils.getShortClassName(getClass()) + "-load.xls";
            testData = getClass().getResourceAsStream(datesetFileName);

            if (testData != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("loading dataset {}", datesetFileName);
                }

                dataSet = new XlsDataSet(testData);
            }
        }

        if (dataSet == null)
        {
            log.debug("No test data found with name [{}]", datesetFileName);
        }
        else
        {

            DataSource dataSource = (DataSource) ctx.getBean("dataSource");
            IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());

            // truncate common tables
            IDataSet truncateDataSet = ApplicationContextHolder.getTruncateAllDataSet();

            if (truncateDataSet == null)
            {
                log.debug("Generating sorted dataset for initial cleanup");
                IDataSet unsortedTruncateDataSet = connection.createDataSet();
                ITableFilter filter = new DatabaseSequenceFilter(connection);
                truncateDataSet = new FilteredDataSet(filter, unsortedTruncateDataSet);
                ApplicationContextHolder.setTruncateAllDataSet(truncateDataSet);
                log.debug("Sorted dataset generated");
            }

            IDataSet orderedDataset = dataSet;

            // if a sorted dataset is available, use table sequence for sorting
            if (truncateDataSet == null)
            {
                ITableFilter filter = new SequenceTableFilter(truncateDataSet.getTableNames());
                orderedDataset = new FilteredDataSet(filter, dataSet);
            }

            try
            {
                if (truncateDataSet != null)
                {
                    DatabaseOperation.DELETE_ALL.execute(connection, truncateDataSet);
                }
                if (dataSet != null)
                {
                    InsertIdentityOperation.INSERT.execute(connection, orderedDataset);
                }
            }
            finally
            {
                connection.close();
            }
        }

        // mimic the Spring OpenSessionInViewFilter
        this.sessionFactory = (SessionFactory) ctx.getBean("sessionFactory");

        // SessionFactoryUtils.getSession(sessionFactory, true).setFlushMode(FlushMode.NEVER);
        SessionFactoryUtils.initDeferredClose(this.sessionFactory);

        // @todo find a way to disable cache
        // this.sessionFactory.openSession().setCacheMode(CacheMode.IGNORE);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        // close open hibernate sessions, mimic the OpenSessionInViewFilter
        SessionFactoryUtils.processDeferredClose(this.sessionFactory);

        // regenerate db initial state
        String datesetFileName = "/initial-load.xml";
        InputStream testData = getClass().getResourceAsStream(datesetFileName);

        if (testData != null)
        {
            log.debug("Restoring db state");

            IDataSet dataSet = new XmlDataSet(testData);

            DataSource dataSource = (DataSource) ctx.getBean("dataSource");
            IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());

            try
            {
                DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
            }
            finally
            {
                connection.close();
            }
        }

        super.tearDown();
    }

    /**
     * Returns the full test name.
     * @see junit.framework.TestCase#getName()
     */
    public String getName()
    {
        return ClassUtils.getShortClassName(this.getClass()) + "::" + super.getName();
    }

    /**
     * return the current Hibernate SessionFactory
     * @return SessionFactory object
     */
    public SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }
}