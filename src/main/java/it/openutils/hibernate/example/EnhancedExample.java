package it.openutils.hibernate.example;

import it.openutils.dao.hibernate.MutableDateRange;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataRetrievalFailureException;


/**
 * @author Fabrizio Giustina
 * @version $Id$
 */
public class EnhancedExample
{

    private Map<String, FilterMetadata> metadata;

    private EnhancedExample(Criteria crit, Object filter, Map<String, FilterMetadata> metadata)
    {
        this.metadata = metadata == null ? new HashMap<String, FilterMetadata>(0) : metadata;
        fillCriteria(crit, filter);
    }

    /**
     * Logger.
     */
    private static Log log = LogFactory.getLog(EnhancedExample.class);

    /**
     * Fills a criteria object calling addCondition() for any non-null property or for any component in collections.
     * @param crit Criteria
     * @param filter javabean which will be analyzed for non-null properties
     * @throws HibernateException exception while building the criteria
     */
    public static void create(Criteria crit, Object filter, Map<String, FilterMetadata> metadata)
        throws HibernateException
    {
        new EnhancedExample(crit, filter, metadata);
    }

    /**
     * Adds contitions to an existing criteria or create sub-criteria for associations.
     * @param crit Criteria
     * @param propertyName property name in parent bean
     * @param value property value
     * @throws HibernateException exception while building the criteria
     */
    private void addCondition(Criteria crit, String propertyName, Object value, Object parentObject)
        throws HibernateException
    {
        if (isSimpleType(value))
        {

            // don't filter on empty strings!
            if (value instanceof String && StringUtils.isBlank((String) value))
            {
                return;
            }

            // @todo handle nested properties
            FilterMetadata fmd = metadata.get(propertyName);

            if (fmd == null)
            {
                fmd = FilterMetadata.EQUAL;
            }

            fmd.createFilter(crit, propertyName, value);

        }
        else if (value instanceof MutableDateRange)
        {
            Date from = ((MutableDateRange) value).getFrom();
            Date to = ((MutableDateRange) value).getTo();
            if (from != null && to != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("crit.add(Restrictions.between(" + propertyName + "," + from + ", " + to + ")");
                }
                crit.add(Restrictions.between(propertyName, from, to));
            }
            else if (from != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("crit.add(Restrictions.ge(" + propertyName + "," + from + ")");
                }
                crit.add(Restrictions.ge(propertyName, from));
            }
            else if (to != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("crit.add(Restrictions.le(" + propertyName + ", " + to + ")");
                }
                crit.add(Restrictions.le(propertyName, to));
            }
        }
        else
        {
            if (containsSomething(value))
            {
                if (log.isDebugEnabled())
                {
                    log.debug("crit.createCriteria(" + propertyName + ")");
                }

                // @todo handle multiple associations in lists?
                // see http://opensource2.atlassian.com/projects/hibernate/browse/HHH-879
                if ((value instanceof Set || value instanceof List) && !((Collection) value).isEmpty())
                {
                    // collection: the new criteria has already been created, now we only nee to analize content
                    Iterator iterator = ((Collection) value).iterator();

                    if (iterator.hasNext())
                    {
                        Object element = iterator.next();
                        Criteria childrenCriteria = crit.createCriteria(propertyName);
                        fillCriteria(childrenCriteria, element);
                    }
                }
                else
                {
                    Criteria childrenCriteria = crit.createCriteria(propertyName);
                    fillCriteria(childrenCriteria, value);
                }
            }
        }
    }

    /**
     * Check if the bean contains at least a valid property.
     * @param bean javabean
     * @return <code>true</code> if the bean contains at least a valid property
     */
    private boolean containsSomething(Object bean)
    {

        if (bean == null)
        {
            return false;
        }
        if (isSimpleType(bean))
        {
            return true;
        }
        else if (bean instanceof MutableDateRange)
        {
            return ((MutableDateRange) bean).isSet();
        }

        if (bean instanceof Collection)
        {
            // log.debug("**COL**");
            Collection coll = ((Collection) bean);
            if (coll.isEmpty())
            {
                return false;
            }

            if (containsSomething(coll.iterator().next()))
            {
                return true;
            }
        }
        else if (bean instanceof Map)
        {
            // log.debug("**MAP**");
            Map coll = ((Map) bean);
            if (coll.isEmpty())
            {
                return false;
            }

            if (containsSomething(coll.values().iterator().next()))
            {
                return true;
            }
        }

        Map<String, Object> properties;
        try
        {
            properties = PropertyUtils.describe(bean);
        }
        catch (Throwable e)
        {
            if (e instanceof InvocationTargetException)
            {
                e = ((InvocationTargetException) e).getTargetException();
            }

            log.error("Unable to build filter, PropertyUtils.describe throws an exception while analizing class "
                + ClassUtils.getShortClassName(bean, "NULL"), e);
            return false;
        }

        for (Map.Entry<String, Object> property : properties.entrySet())
        {

            if (!PropertyUtils.isWriteable(bean, property.getKey()))
            {
                // skip readonly properties
                continue;
            }

            Object propertyValue = property.getValue();
            if (propertyValue == null)
            {
                continue;
            }

            if (isSimpleType(propertyValue) || containsSomething(propertyValue))
            {
                return true;
            }
        }

        if (log.isDebugEnabled())
        {
            // log.debug(ClassUtils.getShortClassName(bean.getClass()) + " is empty");
        }

        return false;
    }

    /**
     * Fills a criteria object calling addCondition() for any non-null property or for any component in collections.
     * @param crit Criteria
     * @param filter javabean which will be analyzed for non-null properties
     * @throws HibernateException exception while building the criteria
     */
    private void fillCriteria(Criteria crit, Object filter) throws HibernateException
    {
        if ((filter instanceof Set || filter instanceof List) && !((Collection) filter).isEmpty())
        {
            // collection: the new criteria has already been created, now we only need to analize content
            Iterator iterator = ((Collection) filter).iterator();
            while (iterator.hasNext())
            {
                Object element = iterator.next();
                fillCriteria(crit, element);
            }
        }

        Map<String, Object> properties;
        try
        {
            properties = PropertyUtils.describe(filter);
        }
        catch (Throwable e)
        {
            if (e instanceof InvocationTargetException)
            {
                e = ((InvocationTargetException) e).getTargetException();
            }

            throw new DataRetrievalFailureException(
                "Unable to build filter, PropertyUtils.describe throws an exception while analizing class "
                    + ClassUtils.getShortClassName(filter, "NULL")
                    + ":"
                    + e.getClass(),
                e);
        }

        Iterator<String> iterator = properties.keySet().iterator();
        while (iterator.hasNext())
        {
            String propertyName = iterator.next();

            Object value = properties.get(propertyName);

            // add only non-null values, ignore read-only properties
            if (value != null && PropertyUtils.isWriteable(filter, propertyName))
            {
                addCondition(crit, propertyName, value, filter);
            }
        }
    }

    /**
     * Check if the given object is a simple java type
     * @param object object to check
     * @return <code>true</code>if the given object is a simple type
     */
    private boolean isSimpleType(Object object)
    {

        Class< ? extends Object> objClass = object.getClass();

        return objClass.isPrimitive()
            || objClass.equals(Integer.class)
            || objClass.equals(Long.class)
            || objClass.equals(Short.class)
            || objClass.equals(Boolean.class)
            || objClass.equals(String.class)
            || objClass.equals(Double.class)
            || objClass.equals(Float.class)
            || objClass.equals(Date.class)
            || objClass.equals(Byte.class)
            || objClass.equals(BigDecimal.class)
            || objClass.equals(Timestamp.class)
            || objClass.equals(Character.class)
            || objClass.equals(Calendar.class);
    }

}
