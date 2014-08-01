/**
 *
 * openutils base Spring-Hibernate DAO (http://www.openmindlab.com/lab/products/bshd5.html)
 *
 * Copyright(C) 2005-2013, Openmind S.r.l. http://www.openmindonline.it
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

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;


/**
 * @author gcatania
 * @version $Id$
 */
final class ExampleTreeUtils
{

    private ExampleTreeUtils()
    {
    }

    /**
     * check the property with the input name was already walked in the input path
     * @param path the current path
     * @param propertyName the property name about to be walked
     * @return true if the property with the input name was already walked in the input path
     */
    public static boolean alreadyWalked(String[] walkedProperties, String propertyName)
    {
        if (walkedProperties.length <= 2)
        {
            return false;
        }
        String parent = walkedProperties[walkedProperties.length - 1];
        boolean lastWasChild = false;
        for (int i = walkedProperties.length - 2; i > 0; i--)
        {
            String currPropertyName = walkedProperties[i];
            if (currPropertyName.equals(propertyName))
            {
                lastWasChild = true;
                continue;
            }
            if (lastWasChild)
            {
                if (currPropertyName.equals(parent))
                {
                    return true;
                }
                else
                {
                    lastWasChild = false;
                }
            }
        }
        return false;
    }

    /**
     * retrieves a value from a collection property
     * @param propertyName the property name (will be reported in the exception)
     * @param collectionValue the collection
     * @return a value
     * @see http://opensource2.atlassian.com/projects/hibernate/browse/HHH-879
     * @throws IllegalArgumentException if the input collection contains more than one value
     */
    public static Object getValueFromCollection(String propertyName, Object collectionValue)
        throws IllegalArgumentException
    {
        if (collectionValue != null)
        {
            if (collectionValue instanceof Collection< ? >)
            {
                Collection< ? > coll = (Collection< ? >) collectionValue;
                int size = coll.size();
                if (size == 1)
                {
                    return coll.iterator().next();
                }
                if (size > 1)
                {
                    throw new IllegalArgumentException(MessageFormat.format(
                        "More than one element in filter collection is unsupported.\nproperty: {0}, value: {1}",
                        propertyName,
                        coll));
                }
            }
            Class< ? extends Object> clazz = collectionValue.getClass();
            if (clazz.isArray())
            {
                int length = Array.getLength(collectionValue);
                if (length == 1)
                {
                    return Array.get(collectionValue, 0);
                }
                if (length > 1)
                {
                    throw new IllegalArgumentException(
                        MessageFormat
                            .format(
                                "More than one element in filter array is unsupported.\nproperty: {0}, value: {1} - length: {2}",
                                propertyName,
                                collectionValue,
                                length));
                }
            }
            // TODO other cases?
        }
        return null;
    }

    /**
     * obtains the hibernate class metadata for the input entity
     * @param entity the hibernate entity
     * @param sessionFactory the session factory to retrieve metadata from
     * @return the class metadata
     * @throws IllegalStateException if no class metadata is configured for the input entity
     */
    public static ClassMetadata getClassMetadata(Object entity, SessionFactory sessionFactory)
        throws IllegalStateException
    {
        Class< ? > cl = Hibernate.getClass(entity);
        ClassMetadata classMetadata = sessionFactory.getClassMetadata(cl);
        if (classMetadata == null)
        {
            throw new IllegalStateException("No hibernate class metadata found for: " + cl);
        }
        return classMetadata;
    }

    /**
     * @param strings an array of strings
     * @param s the string to append
     * @return a new array containing the input string array plus the input string at the end
     */
    public static String[] append(String[] strings, String s)
    {
        String[] result = Arrays.copyOf(strings, strings.length + 1);
        result[strings.length] = s;
        return result;
    }

    /**
     * constructs the association path from an array of property names
     * @param propertyNames the walked properties
     * @return the association path
     */
    public static String getPath(String[] propertyNames)
    {
        return propertyNames.length > 0 ? StringUtils.join(propertyNames, '.') : StringUtils.EMPTY;
    }

    /**
     * adds the identifier restriction to the input criteria, if required
     * @param crit the criteria
     * @param entity the entity to use as example
     * @param classMetadata the class metadata to use
     * @param ses the current session
     * @return true if the identifier restriction has been added, false otherwise
     * @see BSHD-11, BSHD-20
     */
    public static boolean addIdentifierRestriction(Criteria crit, Object entity, ClassMetadata classMetadata,
        Session ses)
    {
        String identifierName = classMetadata.getIdentifierPropertyName();
        if (identifierName != null)
        {
            // TODO is this cast really necessary? Will it fail in future hibernate versions?
            SessionImplementor si = (SessionImplementor) ses;

            Object idValue = classMetadata.getIdentifier(entity, si);
            if (idValue != null) // TODO should we use property selectors instead?
            {
                crit.add(Restrictions.idEq(idValue));
                return true;
            }
        }
        return false;
    }

}
