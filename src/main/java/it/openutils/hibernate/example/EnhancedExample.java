/**
 *
 * openutils base Spring-Hibernate DAO for java 5.0 (http://www.openmindlab.com/lab/products/bshd5.html)
 *
 * Copyright(C) ${project.inceptionYear}-2011, Openmind S.r.l. http://www.openmindonline.it
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package it.openutils.hibernate.example;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;


/**
 * @author Fabrizio Giustina
 * @version $Id: $
 */
public final class EnhancedExample
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(EnhancedExample.class);

    private Map<String, ? extends FilterMetadata> metadata;

    private EnhancedExample(
        final Criteria crit,
        final Object filter,
        final Map<String, ? extends FilterMetadata> metadata)
    {
        this.metadata = metadata == null ? Collections.<String, FilterMetadata> emptyMap() : metadata;
        fillCriteria(null, crit, filter);
    }

    /**
     * Fills a criteria object calling addCondition() for any non-null property or for any component in collections.
     * @param crit Criteria
     * @param filter javabean which will be analyzed for non-null properties
     * @param metadata Map of property names - filter metadata
     * @throws HibernateException exception while building the criteria
     */
    public static void create(final Criteria crit, final Object filter,
        final Map<String, ? extends FilterMetadata> metadata) throws HibernateException
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
    private void addCondition(final Criteria crit, final String propertyName, final Object value,
        final Object parentObject) throws HibernateException
    {

        String simplePropertyName = StringUtils.contains(propertyName, ".") ? StringUtils.substringAfterLast(
            propertyName,
            ".") : propertyName;

        if (isSimpleType(value) || value.getClass().isEnum())
        {

            // don't filter on empty strings!
            if (value instanceof String && StringUtils.isBlank((String) value))
            {
                return;
            }

            FilterMetadata fmd = metadata.get(propertyName);

            if (fmd == null)
            {
                fmd = FilterMetadata.EQUAL;
            }

            fmd.createFilter(crit, simplePropertyName, value);

        }
        else
        {
            if (containsSomething(value))
            {
                // @todo handle multiple associations in lists?
                // see http://opensource2.atlassian.com/projects/hibernate/browse/HHH-879
                if ((value instanceof Set< ? > || value instanceof List< ? >) && !((Collection< ? >) value).isEmpty())
                {
                    // collection: the new criteria has already been created, now we only need to analize content

                    for (Object element : ((Collection< ? >) value))
                    {

                        log.debug("crit.createCriteria({})", simplePropertyName);
                        Criteria childrenCriteria = crit.createCriteria(simplePropertyName);
                        fillCriteria(propertyName, childrenCriteria, element);
                    }
                }
                else if ((value instanceof Map< ? , ? >) && !((Map< ? , ? >) value).isEmpty())
                {
                    FilterMetadata fmd = metadata.get(propertyName);

                    if (fmd != null)
                    {
                        fmd.createFilter(crit, simplePropertyName, value);
                    }
                    else
                    {
                        log.warn(
                            "Maps are not handled without a FilterMetadata. Property is {} and value is {}.",
                            propertyName,
                            value);
                    }
                }
                else
                {
                    log.debug("crit.createCriteria({})", simplePropertyName);
                    Criteria childrenCriteria = crit.createCriteria(simplePropertyName);
                    fillCriteria(propertyName, childrenCriteria, value);
                }
            }
        }
    }

    /**
     * Check if the bean contains at least a valid property.
     * @param bean javabean
     * @return <code>true</code> if the bean contains at least a valid property
     */
    @SuppressWarnings("unchecked")
    private boolean containsSomething(final Object bean)
    {

        if (bean == null)
        {
            return false;
        }
        if (isSimpleType(bean) || bean.getClass().isEnum())
        {
            return true;
        }

        if (bean instanceof Collection< ? >)
        {

            Collection< ? > coll = ((Collection< ? >) bean);
            if (coll.isEmpty())
            {
                return false;
            }

            if (containsSomething(coll.iterator().next()))
            {
                return true;
            }
        }
        else if (bean instanceof Map< ? , ? >)
        {
            Map< ? , ? > coll = ((Map< ? , ? >) bean);
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

        return false;
    }

    /**
     * Fills a criteria object calling addCondition() for any non-null property or for any component in collections.
     * @param crit Criteria
     * @param filter javabean which will be analyzed for non-null properties
     * @throws HibernateException exception while building the criteria
     */
    @SuppressWarnings("unchecked")
    private void fillCriteria(final String parentPropertyName, final Criteria crit, final Object filter)
        throws HibernateException
    {
        if ((filter instanceof Set< ? > || filter instanceof List< ? >) && !((Collection< ? >) filter).isEmpty())
        {
            // collection: the new criteria has already been created, now we only need to analize content
            for (Object element : ((Collection< ? >) filter))
            {
                fillCriteria(parentPropertyName, crit, element);
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
                String composedPropertyName = (parentPropertyName == null) ? propertyName : parentPropertyName
                    + "."
                    + propertyName;
                addCondition(crit, composedPropertyName, value, filter);
            }
        }
    }

    /**
     * Check if the given object is a simple java type
     * @param object object to check
     * @return <code>true</code>if the given object is a simple type
     */
    private boolean isSimpleType(final Object object)
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
            || Calendar.class.isAssignableFrom(objClass);
    }

}
