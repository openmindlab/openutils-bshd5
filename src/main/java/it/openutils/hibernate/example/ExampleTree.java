/**
 *
 * openutils base Spring-Hibernate DAO (http://www.openmindlab.com/lab/products/bshd5.html)
 *
 * Copyright(C) 2005-2011, Openmind S.r.l. http://www.openmindonline.it
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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Example.PropertySelector;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;


/**
 * facility to create subcriteria and examples for associations of an input entity
 * @author gcatania
 */
public class ExampleTree implements Serializable
{

    private static final long serialVersionUID = 4331039089117321853L;

    private final Object rootEntity;

    private Character escapeCharacter;

    private PropertySelector selector;

    private DefaultPropertySelector defaultSelector = DefaultPropertySelector.NOT_NULL;

    private MatchMode matchMode;

    private boolean isIgnoreCaseEnabled;

    private Map<String, Set<String>> excludedProperties = new HashMap<String, Set<String>>();

    private Map<String, List<Criterion>> additionalConditions = new HashMap<String, List<Criterion>>();

    /**
     * builds an instance of {@code ExampleTree} associated with the input entity
     * @param entity the example entity
     */
    public ExampleTree(Object entity)
    {
        if (entity == null)
        {
            throw new NullPointerException("Null entity.");
        }
        rootEntity = entity;
    }

    /**
     * creates a criteria with the input session, adds to it an example for the input entity, and creates subcriteria
     * and related examples for non-null property values on the filter entity that correspond to associations
     * @param ses the session
     * @return a criteria for this example tree
     */
    public Criteria create(Session ses)
    {
        return appendTo(ses.createCriteria(Hibernate.getClass(rootEntity)), ses);
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
        return new ExampleTreeWalker(ses).walk(crit);
    }

    /**
     * Set escape character for "like" clause
     * @param escapeCharacter the escape character
     * @return this, for method concatenation
     */
    public ExampleTree setEscapeCharacter(Character escapeCharacter)
    {
        this.escapeCharacter = escapeCharacter;
        return this;
    }

    /**
     * Set the property selector
     * @param selector the property selector
     * @return this, for method concatenation
     */
    public ExampleTree setPropertySelector(PropertySelector selector)
    {
        this.selector = selector;
        defaultSelector = null;
        return this;
    }

    /**
     * Exclude zero-valued properties
     * @return this, for method concatenation
     */
    public ExampleTree excludeZeroes()
    {
        return setDefaultSelector(DefaultPropertySelector.NOT_NULL_OR_ZERO);
    }

    /**
     * Don't exclude null or zero-valued properties
     * @return this, for method concatenation
     */
    public ExampleTree excludeNone()
    {
        return setDefaultSelector(DefaultPropertySelector.ALL);
    }

    /**
     * Use the "like" operator for all string-valued properties
     * @param matchMode the match mode
     * @return this, for method concatenation
     */
    public ExampleTree enableLike(MatchMode matchMode)
    {
        this.matchMode = matchMode;
        return this;
    }

    /**
     * Use the "like" operator for all string-valued properties
     * @return this, for method concatenation
     */
    public ExampleTree enableLike()
    {
        return enableLike(MatchMode.EXACT);
    }

    /**
     * Ignore case for all string-valued properties
     * @return this, for method concatenation
     */
    public ExampleTree ignoreCase()
    {
        isIgnoreCaseEnabled = true;
        return this;
    }

    /**
     * add an additional criterion for properties of the subentity at the given path
     * @param associationPath the association path with respect to the filter entity
     * @param criterion the criterion to add
     * @return this, for method concatenation
     */
    public ExampleTree add(String associationPath, Criterion criterion)
    {
        List<Criterion> criteriaForPath = additionalConditions.get(associationPath);
        if (criteriaForPath == null)
        {
            criteriaForPath = new ArrayList<Criterion>();
            additionalConditions.put(associationPath, criteriaForPath);
        }
        criteriaForPath.add(criterion);
        return this;
    }

    /**
     * exclude a property from the default filter (note that additional conditions may still be applied for this
     * property)
     * @param associationPath the association path of the property with respect to the filter entity
     * @param propertyName the property name
     * @return this, for method concatenation
     */
    public ExampleTree excludeProperty(String associationPath, String propertyName)
    {
        Set<String> excludedPropertiesForPath = excludedProperties.get(associationPath);
        if (excludedPropertiesForPath == null)
        {
            excludedPropertiesForPath = new HashSet<String>();
            excludedProperties.put(associationPath, excludedPropertiesForPath);
        }
        excludedPropertiesForPath.add(propertyName);
        return this;
    }

    /**
     * override filter behavior for a given property with the input criterion.
     * @param associationPath the association path of the property
     * @param propertyName the property name
     * @param override the new filter behavior for the property
     * @return this, for method concatenation
     */
    public ExampleTree overridePropertyFilter(String associationPath, String propertyName, Criterion override)
    {
        excludeProperty(associationPath, propertyName);
        return add(associationPath, override);
    }

    private static enum DefaultPropertySelector {
        NOT_NULL, NOT_NULL_OR_ZERO, ALL;
    }

    private ExampleTree setDefaultSelector(DefaultPropertySelector defaultSelector)
    {
        this.defaultSelector = defaultSelector;
        selector = null;
        return this;
    }

    private class ExampleTreeWalker implements Serializable
    {

        private static final long serialVersionUID = -5606122272375864522L;

        private final SessionFactory sessionFactory;

        private EntityMode entityMode;

        public ExampleTreeWalker(Session session)
        {
            sessionFactory = session.getSessionFactory();
            entityMode = session.getEntityMode();
        }

        public Criteria walk(Criteria rootCriteria)
        {
            createSubExamples(rootCriteria, rootEntity, new String[0]);
            return rootCriteria;
        }

        private void createSubExamples(Criteria crit, Object entity, String[] walkedProperties)
        {
            String associationPath = getAssociationPath(walkedProperties);
            crit.add(example(entity, associationPath));
            for (Criterion c : getAdditionalConditions(associationPath))
            {
                crit.add(c);
            }
            ClassMetadata classMetadata = sessionFactory.getClassMetadata(Hibernate.getClass(entity));
            Type[] types = classMetadata.getPropertyTypes();
            String[] names = classMetadata.getPropertyNames();
            for (int i = 0; i < types.length; i++)
            {
                Type propertyType = types[i];
                if (!propertyType.isAssociationType())
                {
                    // handled by Example.create()
                    continue;
                }
                String propertyName = names[i];
                if (alreadyWalked(walkedProperties, propertyName))
                {
                    continue;
                }

                Object propertyValue = classMetadata.getPropertyValue(entity, propertyName, entityMode);
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

        private String getAssociationPath(String[] walkedProperties)
        {
            return walkedProperties.length > 0 ? StringUtils.join(walkedProperties, '.') : StringUtils.EMPTY;
        }

        private Example example(Object entity, String associationPath)
        {
            Example ex = Example.create(entity);
            if (escapeCharacter != null)
            {
                ex.setEscapeCharacter(escapeCharacter);
            }
            if (matchMode != null)
            {
                ex.enableLike(matchMode);
            }
            if (isIgnoreCaseEnabled)
            {
                ex.ignoreCase();
            }
            if (selector != null)
            {
                ex.setPropertySelector(selector);
            }
            else
            {
                switch (defaultSelector)
                {
                    case NOT_NULL_OR_ZERO :
                        ex.excludeZeroes();
                        break;
                    case ALL :
                        ex.excludeNone();
                        break;
                    default :
                        break;
                }
            }
            Set<String> excludedPropertiesForPath = excludedProperties.get(associationPath);
            if (excludedPropertiesForPath != null)
            {
                for (String propertyName : excludedPropertiesForPath)
                {
                    ex.excludeProperty(propertyName);
                }
            }
            return ex;
        }

        private List<Criterion> getAdditionalConditions(String path)
        {
            List<Criterion> result = additionalConditions.get(path);
            if (result == null)
            {
                return Collections.emptyList();
            }
            return result;
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

        // see http://opensource2.atlassian.com/projects/hibernate/browse/HHH-879
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
