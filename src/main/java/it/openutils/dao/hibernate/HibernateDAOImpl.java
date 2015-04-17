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
package it.openutils.dao.hibernate;

import it.openutils.hibernate.example.ExampleTree;
import it.openutils.hibernate.example.FilterMetadata;
import it.openutils.hibernate.example.FilterMetadataSupport;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.aopalliance.aop.AspectException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;


/**
 * Base Hibernate DAO.
 * @author Fabrizio Giustina
 * @version $Id$
 * @param <T> Persistence class
 * @param <K> Object Key
 */
public abstract class HibernateDAOImpl<T, K extends Serializable> implements HibernateDAO<T, K>
{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SessionFactory sessionFactory;

    private Class<T> referenceClass;

    private boolean aopenabled;

    /**
     * Instantiates a new DAO instance, will try to infer reference class from parameterized types from the class
     * hierarchy
     */
    public HibernateDAOImpl()
    {
        // tries to iterate on class or subclasses until a parameterized type is found, otherwise leaves the superclass
        // to be specified by the implementor calling setReferenceClass()
        java.lang.reflect.Type genericSuperclass = getClass().getGenericSuperclass();
        while (genericSuperclass != null && !(genericSuperclass instanceof java.lang.reflect.ParameterizedType))
        {
            genericSuperclass = ((Class< ? >) genericSuperclass).getGenericSuperclass();
        }
        if (genericSuperclass != null)
        {
            java.lang.reflect.Type[] typeArguments = ((java.lang.reflect.ParameterizedType) genericSuperclass)
                .getActualTypeArguments();
            // type arguments is guaranteed to be non-empty since the class is a ParameterizedType
            @SuppressWarnings("unchecked")
            Class<T> referenceClass = (Class<T>) typeArguments[0];
            setReferenceClass(referenceClass);
        }
    }

    /**
     * Sets the class of the persistent bean managed by this DAO
     * @param referenceClass the class for the bean managed by this DAO
     */
    public HibernateDAOImpl(Class<T> referenceClass)
    {
        this.referenceClass = referenceClass;
    }

    private Session getCurrentSession()
    {
        return sessionFactory.getCurrentSession();
    }

    /**
     * creates a criteria for this dao's reference class, associated to the current session
     * @return a new criteria for the reference class
     */
    private Criteria createCriteria()
    {
        return getCurrentSession().createCriteria(getReferenceClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll()
    {
        return getThis().find(Collections.<Criterion> emptyList(), getDefaultOrder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll(Order... orders)
    {

        return getThis().find(Collections.<Criterion> emptyList(), orders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> find(final List< ? extends Criterion> criteria, final Order... orders)
    {
        Criteria crit = createCriteria();
        if (criteria != null)
        {
            for (Criterion c : criteria)
            {
                crit.add(c);
            }
        }
        if (orders != null)
        {
            for (Order o : orders)
            {
                crit.addOrder(o);
            }
        }
        return crit.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> find(String query)
    {
        return getCurrentSession().createQuery(query).list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> find(String query, Object paramValue, Type paramType)
    {
        return getThis().find(query, new Object[]{paramValue }, new Type[]{paramType });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> find(final String query, final Object[] paramValues, final Type[] paramTypes)
    {
        return getCurrentSession().createQuery(query).setParameters(paramValues, paramTypes).list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findFiltered(T filter)
    {
        return getThis().findFiltered(defaultExample(filter), Integer.MAX_VALUE, 0, getDefaultOrder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findFiltered(ExampleTree exampleTree)
    {
        return getThis().findFiltered(exampleTree, Integer.MAX_VALUE, 0, getDefaultOrder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findFiltered(T filter, Order... orders)
    {
        return getThis().findFiltered(defaultExample(filter), Integer.MAX_VALUE, 0, orders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findFiltered(ExampleTree exampleTree, Order... orders)
    {
        return getThis().findFiltered(exampleTree, Integer.MAX_VALUE, 0, orders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findFiltered(T filter, int maxResults, int page)
    {
        return getThis().findFiltered(defaultExample(filter), maxResults, page, getDefaultOrder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findFiltered(ExampleTree exampleTree, int maxResults, int page)
    {
        return getThis().findFiltered(exampleTree, maxResults, page, getDefaultOrder());
    }

    /**
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)} {@inheritDoc}
     */
    @Override
    @Deprecated
    public List<T> findFiltered(T filter, Map<String, ? extends FilterMetadata> metadata)
    {
        return getThis().findFiltered(
            filter,
            metadata,
            Integer.MAX_VALUE,
            0,
            Collections.<Criterion> emptyList(),
            getDefaultOrder());
    }

    /**
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)} {@inheritDoc}
     */
    @Override
    @Deprecated
    public List<T> findFiltered(T filter, Map<String, ? extends FilterMetadata> metadata, int maxResults, int page)
    {
        return getThis().findFiltered(
            filter,
            metadata,
            maxResults,
            page,
            Collections.<Criterion> emptyList(),
            getDefaultOrder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findFilteredFirst(T filter)
    {
        return findFilteredFirst(defaultExample(filter));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findFilteredFirst(ExampleTree exampleTree)
    {
        return findFilteredFirst(exampleTree, new Order[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findFilteredFirst(T filter, final Order... orders)
    {
        return findFilteredFirst(defaultExample(filter), orders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findFilteredFirst(T filter, List< ? extends Criterion> criteria)
    {
        ExampleTree exampleTree = defaultExample(filter);
        appendToRoot(exampleTree, criteria);
        return findFilteredFirst(exampleTree, new Order[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T findFilteredFirst(ExampleTree exampleTree, final Order... orders)
    {
        List<T> found = getThis().findFiltered(exampleTree, 1, 0, orders);
        if (found.isEmpty())
        {
            return null;
        }
        T result = found.get(0);
        Hibernate.initialize(result);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T load(K key)
    {
        T result = (T) sessionFactory.getCurrentSession().load(getReferenceClass(), key);
        Hibernate.initialize(result);
        return result;
    }

    /**
     * @deprecated same as {@link #get(Serializable)};
     */
    @Override
    @Deprecated
    public T loadIfAvailable(K key)
    {
        return get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(K key)
    {
        return (T) sessionFactory.getCurrentSession().get(getReferenceClass(), key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public K save(T obj)
    {
        return (K) sessionFactory.getCurrentSession().save(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(T obj)
    {
        sessionFactory.getCurrentSession().update(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveOrUpdate(T obj)
    {
        sessionFactory.getCurrentSession().saveOrUpdate(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(final K key)
    {
        Session s = getCurrentSession();
        Object toDelete = s.load(getReferenceClass(), key);
        s.delete(toDelete);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh(T obj)
    {
        getCurrentSession().refresh(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evict(T obj)
    {
        getCurrentSession().evict(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T merge(final T obj)
    {
        return (T) getCurrentSession().merge(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public List<T> findAll(Order[] orders, List< ? extends Criterion> criteria)
    {
        return getThis().find(criteria, orders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findFiltered(ExampleTree exampleTree, int maxResults, int page, Order... orders)
    {
        return new ExampleTreeCallback<T>(exampleTree, maxResults, page, orders).doInHibernate(getCurrentSession());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> findFilteredProperties(T filter, int maxResults, int page, List<String> properties,
        Order... orders)
    {
        return getThis().findFilteredProperties(defaultExample(filter), maxResults, page, properties, orders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> findFilteredProperties(ExampleTree exampleTree, int maxResults, int page,
        List<String> properties, Order... orders)
    {
        return new ExampleTreePropertiesCallback(exampleTree, maxResults, page, properties, orders)
            .doInHibernate(getCurrentSession());
    }

    /**
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)} {@inheritDoc}
     */
    @Override
    @Deprecated
    public List<T> findFiltered(T filter, Order[] customOrder, Map<String, ? extends FilterMetadata> metadata,
        int maxResults, int page)
    {
        return getThis().findFiltered(
            filter,
            metadata,
            maxResults,
            page,
            Collections.<Criterion> emptyList(),
            getDefaultOrder());
    }

    /**
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)} {@inheritDoc}
     */
    @Override
    @Deprecated
    public List<T> findFiltered(T filter, Map<String, ? extends FilterMetadata> metadata, int maxResults, int page,
        Order... orders)
    {
        return getThis().findFiltered(filter, metadata, maxResults, page, Collections.<Criterion> emptyList(), orders);
    }

    /**
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)} {@inheritDoc}
     */
    @Override
    @Deprecated
    public List<T> findFiltered(T filter, Map<String, ? extends FilterMetadata> metadata, int maxResults, int page,
        List< ? extends Criterion> criteria, Order... orders)
    {
        BaseCallback<T> callback;
        if (MapUtils.isEmpty(metadata) && CollectionUtils.isEmpty(criteria))
        {
            callback = new ExampleTreeCallback<T>(defaultExample(filter), maxResults, page, orders);
        }
        else
        {
            callback = new LegacySupportCallback<T>(filter, maxResults, page, metadata, criteria, orders);
        }
        return callback.doInHibernate(getCurrentSession());
    }

    /**
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)} {@inheritDoc}
     */
    @Override
    @Deprecated
    public List<T> findFiltered(T filter, Order[] orders, Map<String, ? extends FilterMetadata> metadata,
        int maxResults, int page, List< ? extends Criterion> criteria)
    {
        return getThis().findFiltered(filter, metadata, maxResults, page, criteria, orders);
    }

    /**
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)} {@inheritDoc}
     */
    @Override
    @Deprecated
    public List<Object> findFilteredProperties(T filter, Order[] orders,
        Map<String, ? extends FilterMetadata> metadata, int maxResults, int page, List< ? extends Criterion> criteria,
        List<String> properties)
    {
        BaseCallback<Object> callback;
        if (MapUtils.isEmpty(metadata) && CollectionUtils.isEmpty(criteria))
        {
            callback = new ExampleTreePropertiesCallback(defaultExample(filter), maxResults, page, properties, orders);
        }
        else
        {
            callback = new LegacySupportPropertiesCallback(
                filter,
                maxResults,
                page,
                metadata,
                criteria,
                properties,
                orders);
        }
        return callback.doInHibernate(getCurrentSession());
    }

    /**
     * Sets the aopenabled.
     * @param aopenabled the aopenabled to set
     */
    public void setAopenabled(boolean aopenabled)
    {
        this.aopenabled = aopenabled;
    }

    public void setReferenceClass(Class<T> referenceClass)
    {
        this.referenceClass = referenceClass;
    }

    /**
     * Return the specific Object class that will be used for class-specific implementation of this DAO.
     * @return the reference Class
     */
    protected Class<T> getReferenceClass()
    {
        return referenceClass;
    }

    /**
     * Return a list of <code>Order</code> object to be used for the default ordering of the collection.
     * @return the property name
     */
    @SuppressWarnings("static-method")
    protected Order[] getDefaultOrder()
    {
        return new Order[0];
    }

    /**
     * Obtain an instance of Query for a named query string defined in the mapping file. Use the parameters given.
     * @param name the name of a query defined externally
     * @param params the parameter array
     * @param maxResults max number of results
     * @return Query
     */
    protected List<Object> findByNamedQuery(final String name, final Serializable[] params, final Integer maxResults)
    {
        Query q = getCurrentSession().getNamedQuery(name);
        if (maxResults != null)
        {
            q.setMaxResults(maxResults);
        }
        if (params != null)
        {
            for (int i = 0; i < params.length; i++)
            {
                q.setParameter(i, params[i]);
            }
        }
        return q.list();
    }

    /**
     * Obtain an instance of Query for a named query string defined in the mapping file. Use the parameters given.
     * @param name the name of a query defined externally
     * @param params the parameter Map
     * @param maxResults max number of results
     * @return Query
     */
    protected List<Object> findByNamedQuery(final String name, final Map<String, ? > params, final Integer maxResults)
    {
        Query q = getCurrentSession().getNamedQuery(name);
        if (maxResults != null)
        {
            q.setMaxResults(maxResults);
        }

        if (params != null)
        {
            for (Map.Entry<String, ? > entry : params.entrySet())
            {
                setParameterValue(q, entry.getKey(), entry.getValue());
            }
        }
        return q.list();
    }

    /**
     * Obtain an instance of Query for a named query string defined in the mapping file. Use the parameters given.
     * @param name the name of a query defined externally
     * @param params the parameter array
     * @param maxResults max number of results
     * @return Query
     * @deprecated use the better named <code>findByNamedQuery</code> method
     */
    @Deprecated
    protected List<Object> getNamedQuery(String name, Serializable[] params, int maxResults)
    {
        return findByNamedQuery(name, params, maxResults > 0 ? maxResults : Integer.MAX_VALUE);
    }

    /**
     * Obtain an instance of Query for a named query string defined in the mapping file. Use the parameters given.
     * @param name the name of a query defined externally
     * @param params the parameter Map
     * @param maxResults max number of results
     * @return Query
     * @deprecated use the better named <code>findByNamedQuery</code> method
     */
    @Deprecated
    protected List<Object> getNamedQuery(String name, Map<String, ? > params, int maxResults)
    {
        return findByNamedQuery(name, params, maxResults > 0 ? maxResults : Integer.MAX_VALUE);
    }

    /**
     * Convenience method to set paramers in the query given based on the actual object type in passed in as the value.
     * You may need to add more functionaly to this as desired (or not use this at all).
     * @param query the Query to set
     * @param key the key name
     * @param value the object to set as the parameter
     */
    protected static void setParameterValue(Query query, String key, Object value)
    {
        if (null == key || null == value)
        {
            return;
        }
        query.setParameter(key, value);
    }

    /**
     * Returns the default example tree that will be applied to a filtered search by entity. The default implementation
     * just returns an {@link ExampleTree}, subclasses may override.
     * @param entity the example (filter) entity
     * @return an example for the entity
     */
    protected ExampleTree defaultExample(T entity)
    {
        return new ExampleTree(entity);
    }

    /**
     * Returns the default set of FilterMetadata that will be applied to any query. The default implementation doesn't
     * set any default filter, subclasses may override this.
     * @return map of property name - filter metadata
     * @deprecated this method uses the deprecated class {@link FilterMetadata}, use {@link #defaultExample(Object)}
     * instead
     */
    @SuppressWarnings("static-method")
    @Deprecated
    protected Map<String, ? extends FilterMetadata> getDefaultFilterMetadata()
    {
        return Collections.emptyMap();
    }

    /**
     * appends the input criterions to the input example tree as root entity criterions
     * @param exampleTree the example tree
     * @param criterions the criterions to append
     */
    protected static void appendToRoot(ExampleTree exampleTree, List< ? extends Criterion> criterions)
    {
        if (criterions != null)
        {
            for (Criterion crit : criterions)
            {
                exampleTree.add(StringUtils.EMPTY, crit);
            }
        }
    }

    /**
     * @return This is needed as for https://jira.spring.io/browse/SPR-2226
     */
    private HibernateDAO<T, K> getThis()
    {
        try
        {
            if (aopenabled)
            {
                return (HibernateDAO<T, K>) AopContext.currentProxy();
            }
        }
        catch (AspectException exc)
        {
            logger.debug("Not running inside an AOP proxy, so no proxy can be returned: {}", exc.getMessage());
        }
        catch (IllegalStateException e)
        {
            logger.warn("Cannot access proxy: {}", e.getMessage());
            aopenabled = false;
        }
        return this;
    }

    /**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    /**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    /**
     * @author gcatania
     */
    @SuppressWarnings("deprecation")
    private class LegacySupportCallback<R> extends BaseCallback<R>
    {

        private final T rootEntity;

        private final Map<String, ? extends FilterMetadata> filterMetadata;

        private final List< ? extends Criterion> additionalCriteria;

        private LegacySupportCallback(
            T rootEntity,
            int maxResults,
            int page,
            Map<String, ? extends FilterMetadata> metadata,
            List< ? extends Criterion> additionalCriteria,
            Order... orders)
        {
            super(maxResults, page, orders);
            this.rootEntity = rootEntity;
            this.filterMetadata = metadata;
            this.additionalCriteria = additionalCriteria;
        }

        @Override
        protected Criteria createCriteria(Session session)
        {
            Criteria crit = new FilterMetadataSupport(rootEntity, filterMetadata).create(session).setResultTransformer(
                CriteriaSpecification.DISTINCT_ROOT_ENTITY);
            if (additionalCriteria != null)
            {
                for (Criterion c : additionalCriteria)
                {
                    crit.add(c);
                }
            }
            return crit;
        }

    }

    /**
     * @author gcatania
     */
    @SuppressWarnings("deprecation")
    private final class LegacySupportPropertiesCallback extends LegacySupportCallback<Object>
    {

        private final List<String> properties;

        private LegacySupportPropertiesCallback(
            T rootEntity,
            int maxResults,
            int page,
            Map<String, ? extends FilterMetadata> metadata,
            List< ? extends Criterion> additionalCriteria,
            List<String> properties,
            Order... orders)
        {
            super(rootEntity, maxResults, page, metadata, additionalCriteria, orders);
            this.properties = properties;
        }

        @Override
        protected Criteria createCriteria(Session session)
        {
            Criteria crit = super.createCriteria(session);
            if (CollectionUtils.isNotEmpty(properties))
            {
                ProjectionList projectionList = Projections.projectionList();
                for (String property : properties)
                {
                    projectionList.add(Property.forName(property));
                }

                crit.setProjection(projectionList);
            }
            return crit;
        }
    }
}


/**
 * @author gcatania
 * @param R the result class
 */
abstract class BaseCallback<R>
{

    private final Order[] orders;

    private final int maxResults;

    private final int page;

    protected BaseCallback(int maxResults, int page, Order... orders)
    {
        this.maxResults = maxResults;
        this.page = page;
        this.orders = orders;
    }

    /**
     * internal method that creates the query criteria.
     * @param session the hibernate session
     * @return the hibernate criteria
     */
    protected abstract Criteria createCriteria(Session session);

    public final List<R> doInHibernate(Session session)
    {
        Criteria crit = createCriteria(session);
        crit.setMaxResults(maxResults);
        crit.setFirstResult(maxResults * page);
        if (orders != null)
        {
            for (Order o : orders)
            {
                crit.addOrder(o);
            }
        }
        return crit.list();
    }
}


/**
 * @author gcatania
 * @param R the result class
 */
class ExampleTreeCallback<R> extends BaseCallback<R>
{

    private final ExampleTree exampleTree;

    protected ExampleTreeCallback(ExampleTree exampleTree, int maxResults, int page, Order... orders)
    {
        super(maxResults, page, orders);
        this.exampleTree = exampleTree;
    }

    /**
     * internal method that creates the query criteria. Subclasses may override.
     * @param session the hibernate session
     * @return the hibernate criteria
     */
    @Override
    protected Criteria createCriteria(Session session)
    {
        return exampleTree.create(session).setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
    }
}


class ExampleTreePropertiesCallback extends ExampleTreeCallback<Object>
{

    private final List<String> properties;

    protected ExampleTreePropertiesCallback(
        ExampleTree exampleTree,
        int maxResults,
        int page,
        List<String> properties,
        Order... orders)
    {
        super(exampleTree, maxResults, page, orders);
        this.properties = properties;
    }

    @Override
    protected Criteria createCriteria(Session session)
    {
        Criteria crit = super.createCriteria(session);
        if (CollectionUtils.isNotEmpty(properties))
        {
            ProjectionList projectionList = Projections.projectionList();
            for (String property : properties)
            {
                projectionList.add(Property.forName(property));
            }

            crit.setProjection(projectionList);
        }
        return crit;
    }
}
