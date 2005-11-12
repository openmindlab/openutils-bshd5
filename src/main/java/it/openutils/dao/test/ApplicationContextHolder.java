package it.openutils.dao.test;

import org.dbunit.dataset.IDataSet;
import org.springframework.context.ApplicationContext;


/**
 * @author fgiust
 * @version $Revision$ ($Author$)
 */
public class ApplicationContextHolder
{

    private static ApplicationContext SPRING_CONTEXT;

    private static IDataSet truncateAllDataSet;

    public static ApplicationContext getCtx()
    {
        return SPRING_CONTEXT;
    }

    public static void setCtx(ApplicationContext ctx)
    {
        SPRING_CONTEXT = ctx;
    }

    /**
     * Getter for <code>truncateAllDataSet</code>.
     * @return Returns the truncateAllDataSet.
     */
    public static IDataSet getTruncateAllDataSet()
    {
        return truncateAllDataSet;
    }

    /**
     * Setter for <code>truncateAllDataSet</code>.
     * @param truncateAllDataSet The truncateAllDataSet to set.
     */
    public static void setTruncateAllDataSet(IDataSet truncateAllDataSet)
    {
        ApplicationContextHolder.truncateAllDataSet = truncateAllDataSet;
    }

    public static void reset()
    {
        SPRING_CONTEXT = null;
        truncateAllDataSet = null;
    }

}
