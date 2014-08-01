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

import it.openutils.hibernate.selectors.ExcludeBackrefPropertySelector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Example.PropertySelector;
import org.hibernate.criterion.MatchMode;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;
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

    private PropertySelector selector = new ExcludeBackrefPropertySelector(ExampleTreePropertySelectorSupport.NOT_NULL); // BSHD-15

    private MatchMode matchMode;

    private boolean isIgnoreCaseEnabled;

    private boolean jointPropertyAndIdentifierFiltering;

    private final Map<String, Set<String>> excludedProperties = new HashMap<String, Set<String>>();

    private final Map<String, List<Criterion>> additionalConditions = new HashMap<String, List<Criterion>>();

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
        if (selector == null)
        {
            throw new NullPointerException("Null selector specified");
        }
        this.selector = new ExcludeBackrefPropertySelector(selector); // BSHD-15
        return this;
    }

    /**
     * Exclude zero-valued properties
     * @return this, for method concatenation
     */
    public ExampleTree excludeZeroes()
    {
        return setPropertySelector(ExampleTreePropertySelectorSupport.NOT_NULL_OR_ZERO);
    }

    /**
     * Don't exclude null or zero-valued properties
     * @return this, for method concatenation
     */
    public ExampleTree excludeNone()
    {
        return setPropertySelector(ExampleTreePropertySelectorSupport.ALL);
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
     * The default behaviour of this class ignores properties set on a filter entity with a non-null identifier.
     * Invoking this method reverses the default behaviour by enabling property filtering regardless of identifier
     * presence.
     * @return this, for method concatenation
     */
    public ExampleTree enableJointPropertyAndIdentifierFiltering()
    {
        jointPropertyAndIdentifierFiltering = true;
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

    private class ExampleTreeWalker implements Serializable
    {

        private static final long serialVersionUID = -5606122272375864522L;

        private final SessionFactory sessionFactory;

        public ExampleTreeWalker(Session session)
        {
            sessionFactory = session.getSessionFactory();
        }

        public Criteria walk(Criteria rootCriteria)
        {
            createSubExamples(rootCriteria, rootEntity, new String[0]);
            return rootCriteria;
        }

        private void createSubExamples(Criteria crit, Object entity, String[] walkedProperties)
        {
            ClassMetadata classMetadata = ExampleTreeUtils.getClassMetadata(entity, sessionFactory);
            boolean isIdSet = ExampleTreeUtils.addIdentifierRestriction(
                crit,
                entity,
                classMetadata,
                sessionFactory.getCurrentSession()); // BSHD-11
            if (isIdSet && (HibernateProxy.class.isInstance(entity) || !jointPropertyAndIdentifierFiltering))
            {
                // BSHD-20 only impose the identifier conditions in the following cases:
                // 1) if the current entity is an hibernate proxy (because we assume the identifier restriction is
                // enough and the entity is already aligned
                // 2) if the corresponding flag has not been explicitly activated
                return;
            }

            String associationPath = ExampleTreeUtils.getPath(walkedProperties);
            crit.add(example(entity, associationPath));
            for (Criterion c : getAdditionalConditions(associationPath))
            {
                crit.add(c);
            }

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
                if (ExampleTreeUtils.alreadyWalked(walkedProperties, propertyName))
                {
                    continue;
                }

                Object propertyValue = classMetadata.getPropertyValue(entity, propertyName);
                if (propertyType.isCollectionType())
                {
                    propertyValue = ExampleTreeUtils.getValueFromCollection(propertyName, propertyValue);
                }
                if (propertyValue == null)
                {
                    // skip null values
                    continue;
                }

                Criteria subCrit = crit.createCriteria(propertyName);
                String[] subProperties = ExampleTreeUtils.append(walkedProperties, propertyName);
                createSubExamples(subCrit, propertyValue, subProperties);
            }
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
            ex.setPropertySelector(selector);
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

    }
}


/**
 * workaround to {@link Example} not exposing internal property selectors
 * @author gcatania
 */
@SuppressWarnings({"serial", "static-method" })
class ExampleTreePropertySelectorSupport
{

    static final PropertySelector NOT_NULL = new NotNullPropertySelector();

    static final PropertySelector ALL = new AllPropertySelector();

    static final PropertySelector NOT_NULL_OR_ZERO = new NotNullOrZeroPropertySelector();

    static final class AllPropertySelector implements PropertySelector
    {

        @Override
        public boolean include(Object object, String propertyName, Type type)
        {
            return true;
        }

        private Object readResolve()
        {
            return ALL;
        }
    }

    static final class NotNullPropertySelector implements PropertySelector
    {

        @Override
        public boolean include(Object object, String propertyName, Type type)
        {
            return object != null;
        }

        private Object readResolve()
        {
            return NOT_NULL;
        }
    }

    static final class NotNullOrZeroPropertySelector implements PropertySelector
    {

        @Override
        public boolean include(Object object, String propertyName, Type type)
        {
            return object != null && (!(object instanceof Number) || ((Number) object).longValue() != 0);
        }

        private Object readResolve()
        {
            return NOT_NULL_OR_ZERO;
        }
    }

}