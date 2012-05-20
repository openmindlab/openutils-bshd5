/**
 *
 * openutils base Spring-Hibernate DAO (http://www.openmindlab.com/lab/products/bshd5.html)
 *
 * Copyright(C) 2005-2012, Openmind S.r.l. http://www.openmindonline.it
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;


/**
 * legacy support for {@link FilterMetadata}.
 * @author gcatania
 */
@SuppressWarnings("deprecation")
public class FilterMetadataSupport
{

    private final Object entity;

    private final Map<String, ? extends FilterMetadata> filterMetadata;

    /**
     * builds an instance of {@code ExampleTree} associated with the input entity
     * @param entity the example entity
     */
    public FilterMetadataSupport(Object entity, Map<String, ? extends FilterMetadata> filterMetadata)
    {
        if (entity == null)
        {
            throw new NullPointerException("Null entity.");
        }
        this.entity = entity;
        this.filterMetadata = filterMetadata;
    }

    /**
     * creates a criteria with the input session, adds to it an example for the input entity, and creates subcriteria
     * and related examples for non-null property values on the filter entity that correspond to associations
     * @param ses the session
     * @return a criteria for this example tree
     */
    public Criteria create(Session ses)
    {
        return appendTo(ses.createCriteria(Hibernate.getClass(entity)), ses);
    }

    /**
     * appends examples and subcriteria, created for the entity this example tree was initialized with, to the input
     * criteria, and then returns it
     * @param crit the criteria to append to
     * @param ses the session
     * @return the input criteria with the added subcriteria and examples
     */
    public Criteria appendTo(Criteria crit, Session ses)
    {
        return new ExampleTreeWalker(ses).walk(crit, entity);
    }

    private class ExampleTreeWalker
    {

        private final SessionFactory sessionFactory;

        private final EntityMode entityMode;

        public ExampleTreeWalker(Session session)
        {
            sessionFactory = session.getSessionFactory();
            entityMode = session.getEntityMode();
        }

        public Criteria walk(Criteria rootCriteria, Object rootEntity)
        {
            if (MapUtils.isNotEmpty(filterMetadata))
            {
                createSubExamples(rootCriteria, rootEntity, new String[0]);
            }
            return rootCriteria;
        }

        private void createSubExamples(Criteria crit, Object entity, String[] walkedProperties)
        {
            String path = getPath(walkedProperties);
            Map<String, FilterMetadata> currFilterMetadata = getFilterMetadata(path);
            crit.add(example(entity, currFilterMetadata.keySet()));
            ClassMetadata classMetadata = sessionFactory.getClassMetadata(Hibernate.getClass(entity));
            Type[] types = classMetadata.getPropertyTypes();
            String[] names = classMetadata.getPropertyNames();
            for (int i = 0; i < types.length; i++)
            {
                String propertyName = names[i];
                if (alreadyWalked(walkedProperties, propertyName))
                {
                    continue;
                }
                Object propertyValue = classMetadata.getPropertyValue(entity, propertyName, entityMode);
                FilterMetadata fm = currFilterMetadata.get(propertyName);
                if (fm != null)
                {
                    fm.createFilter(crit, propertyName, propertyValue);
                    continue;
                }

                Type propertyType = types[i];
                if (!propertyType.isAssociationType())
                {
                    // handled by Example.create() or by filterMetadata
                    continue;
                }

                if (propertyType.isCollectionType())
                {
                    propertyValue = getValueFromCollection(propertyValue);
                }
                if (propertyValue == null)
                {
                    // skip null values
                    continue;
                }

                Criteria subCrit = crit.createCriteria(propertyName);
                String[] subProperties = append(walkedProperties, propertyName);
                createSubExamples(subCrit, propertyValue, subProperties);
            }
        }

        private Map<String, FilterMetadata> getFilterMetadata(String path)
        {
            Map<String, FilterMetadata> result = new HashMap<String, FilterMetadata>();
            for (String key : filterMetadata.keySet())
            {
                if (key.equals(path))
                {
                    continue;
                }
                if (!key.startsWith(path))
                {
                    continue;
                }
                String leftover = key.substring(path.length() + 1);
                // skip subproperties
                if (leftover.contains("."))
                {
                    continue;
                }
                result.put(leftover, filterMetadata.get(key));
            }
            return result;
        }

        private String getPath(String[] walkedProperties)
        {
            return walkedProperties.length > 0 ? StringUtils.join(walkedProperties, '.') : StringUtils.EMPTY;
        }

        private Example example(Object entity, Set<String> propertiesToExclude)
        {
            Example ex = Example.create(entity);
            for (String propertyName : propertiesToExclude)
            {
                // skip properties handled by filterMetadata
                ex.excludeProperty(propertyName);
            }
            return ex;
        }

        /**
         * check the property with the input name was already walked in the input path
         * @param path the current path
         * @param propertyName the property name about to be walked
         * @return true if the property with the input name was already walked in the input path
         */
        private boolean alreadyWalked(String[] walkedProperties, String propertyName)
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

        private Object getValueFromCollection(Object collectionValue)
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
                        throw new IllegalArgumentException("More than one element in filter collection is unsupported.");
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
                        throw new IllegalArgumentException("More than one element in filter array is unsupported.");
                    }
                }
                // TODO other cases?
            }
            return null;
        }

        private String[] append(String[] propertyNames, String propertyName)
        {
            String[] result = Arrays.copyOf(propertyNames, propertyNames.length + 1);
            result[propertyNames.length] = propertyName;
            return result;
        }
    }

}
