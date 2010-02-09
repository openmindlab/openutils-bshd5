/**
 *
 * openutils base Spring-Hibernate DAO for java 5.0 (http://www.openmindlab.com/lab/products/bshd5.html)
 *
 * Copyright(C) null-2010, Openmind S.r.l. http://www.openmindonline.it
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

import it.openutils.hibernate.example.EnhancedExample;
import it.openutils.hibernate.example.FilterMetadata;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.aopalliance.aop.AspectException;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.type.Type;
import org.springframework.aop.framework.AopContext;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * Base Hibernate DAO.
 * @author Fabrizio Giustina
 * @version $Id$
 * @param <T> Persistence class
 * @param <K> Object Key
 */
public abstract class HibernateDAOImpl<T, K extends Serializable> extends HibernateDaoSupport
    implements
    HibernateDAO<T, K>
{

    private Class<T> referenceClass;

    private boolean aopenabled;

    /**
     * Instantiates a new DAO instance
     */
    public HibernateDAOImpl()
    {
    }

    /**
     * Sets the class of the persistent bean managed by this DAO
     * @param referenceClass the class for the bean managed by this DAO
     */
    public HibernateDAOImpl(Class<T> referenceClass)
    {
        this.referenceClass = referenceClass;
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findAll()
    {
        return findAll(getDefaultOrder());
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findAll(Order[] orders)
    {
        return getThis().findAll(orders, Collections.<Criterion> emptyList());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> find(String query)
    {
        return getHibernateTemplate().find(query);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> find(String query, Object paramValue, Type paramType)
    {
        return getThis().find(query, new Object[]{paramValue }, new Type[]{paramType });
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> find(final String query, final Object[] paramValues, final Type[] paramTypes)
    {
        return (List<T>) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(final Session ses) throws HibernateException
            {
                // hibernate 3
                return ses.createQuery(query).setParameters(paramValues, paramTypes).list();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findFiltered(T filter)
    {
        return findFiltered(filter, getDefaultFilterMetadata());
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findFiltered(T filter, Order[] orders)
    {
        return findFiltered(filter, orders, getDefaultFilterMetadata(), Integer.MAX_VALUE, 0);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findFiltered(T filter, int maxResults, int page)
    {
        return findFiltered(filter, getDefaultFilterMetadata(), maxResults, page);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findFiltered(T filter, Map<String, ? extends FilterMetadata> metadata)
    {
        return findFiltered(filter, metadata, Integer.MAX_VALUE, 0);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findFiltered(T filter, Map<String, ? extends FilterMetadata> metadata, int maxResults, int page)
    {
        return findFiltered(filter, getDefaultOrder(), metadata, maxResults, page);
    }

    /**
     * {@inheritDoc}
     */
    public T findFilteredFirst(T filter)
    {
        return getFirstInCollection(findFiltered(filter, 1, 0));
    }

    /**
     * {@inheritDoc}
     */
    public T findFilteredFirst(T filter, final Order[] orders)
    {
        return getFirstInCollection(findFiltered(filter, orders, getDefaultFilterMetadata(), 1, 0));
    }

    /**
     * {@inheritDoc}
     */
    public T findFilteredFirst(T filter, List< ? extends Criterion> criteria)
    {
        return getFirstInCollection(findFiltered(filter, getDefaultOrder(), getDefaultFilterMetadata(), 1, 0, criteria));
    }

    /**
     * {@inheritDoc}
     */
    public T load(K key)
    {
        @SuppressWarnings("unchecked")
        T result = (T) getHibernateTemplate().load(getReferenceClass(), key);
        Hibernate.initialize(result);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public T loadIfAvailable(K key)
    {
        try
        {
            @SuppressWarnings("unchecked")
            T result = (T) getHibernateTemplate().get(getReferenceClass(), key);
            if (result != null)
            {
                Hibernate.initialize(result);
                return result;
            }
        }
        catch (ObjectNotFoundException e)
        {
            // thrown by HibernateTemplate#get() if the object does not exist
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T get(K key)
    {
        return (T) getHibernateTemplate().get(getReferenceClass(), key);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public K save(T obj)
    {
        return (K) getHibernateTemplate().save(obj);
    }

    /**
     * {@inheritDoc}
     */
    public void update(T obj)
    {
        getHibernateTemplate().update(obj);
    }

    /**
     * {@inheritDoc}
     */
    public void saveOrUpdate(T obj)
    {
        getHibernateTemplate().saveOrUpdate(obj);
    }

    /**
     * {@inheritDoc}
     */
    public boolean delete(final K key)
    {

        return (Boolean) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(final Session ses) throws HibernateException
            {
                ses.delete(ses.load(getReferenceClass(), key));
                return true;
            }
        });

    }

    /**
     * {@inheritDoc}
     */
    public void refresh(T obj)
    {
        getHibernateTemplate().refresh(obj);
    }

    /**
     * {@inheritDoc}
     */
    public void evict(T obj)
    {
        getHibernateTemplate().evict(obj);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T merge(final T obj)
    {
        return (T) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(final Session ses) throws HibernateException
            {
                return ses.merge(obj);
            }
        });

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll(final Order[] orders, final List< ? extends Criterion> criteria)
    {
        return (List<T>) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(final Session ses) throws HibernateException
            {
                Criteria crit = ses.createCriteria(getReferenceClass());
                if (null != orders)
                {
                    for (int j = 0; j < orders.length; j++)
                    {
                        crit.addOrder(orders[j]);
                    }

                }
                if (criteria != null)
                {
                    for (Criterion criterion : criteria)
                    {
                        crit.add(criterion);
                    }
                }
                return crit.list();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findFiltered(T filter, Order[] customOrder, Map<String, ? extends FilterMetadata> metadata,
        int maxResults, int page)
    {
        return getThis().findFiltered(
            filter,
            customOrder,
            metadata,
            maxResults,
            page,
            Collections.<Criterion> emptyList());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findFiltered(T filter, Order[] orders, Map<String, ? extends FilterMetadata> metadata,
        int maxResults, int page, List< ? extends Criterion> criteria)
    {
        return (List<T>) getHibernateTemplate().execute(
            new HibernateCallbackForExecution(filter, page, maxResults, metadata, orders, criteria, Collections
                .<String> emptyList()));
    }

    /**
     * {@inheritDoc}
     */
    public List< ? > findFilteredProperties(T filter, Order[] orders, Map<String, ? extends FilterMetadata> metadata,
        int maxResults, int page, List< ? extends Criterion> criteria, List<String> properties)
    {
        return (List< ? >) getHibernateTemplate().execute(
            new HibernateCallbackForExecution(filter, page, maxResults, metadata, orders, criteria, properties));
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
    protected List< ? > findByNamedQuery(final String name, final Serializable[] params, final Integer maxResults)
    {
        return (List< ? >) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(final Session ses) throws HibernateException
            {
                Query q = ses.getNamedQuery(name);
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
        });
    }

    /**
     * Obtain an instance of Query for a named query string defined in the mapping file. Use the parameters given.
     * @param name the name of a query defined externally
     * @param params the parameter Map
     * @param maxResults max number of results
     * @return Query
     */
    protected List< ? > findByNamedQuery(final String name, final Map<String, ? > params, final Integer maxResults)
    {
        return (List< ? >) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(final Session ses) throws HibernateException
            {
                Query q = ses.getNamedQuery(name);
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
        });
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
    protected List< ? > getNamedQuery(String name, Serializable[] params, int maxResults)
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
    protected List< ? > getNamedQuery(String name, Map<String, ? > params, int maxResults)
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
    protected void setParameterValue(Query query, String key, Object value)
    {
        if (null == key || null == value)
        {
            return;
        }
        query.setParameter(key, value);
    }

    /**
     * Returns the default set of FilterMetadata that will be applied to any query. The default implementation doesn't
     * set any default filter, subclasses may override this.
     * @return map of property name - filter metadata
     */
    protected Map<String, ? extends FilterMetadata> getDefaultFilterMetadata()
    {
        return Collections.emptyMap();
    }

    /**
     * Returns the first object in the collection or null if the collection is null or empty.
     * @param list collection
     * @return first element in the list
     */
    private T getFirstInCollection(Collection< ? extends T> list)
    {
        if (list != null && !list.isEmpty())
        {
            T result = list.iterator().next();
            Hibernate.initialize(result);
            return result;
        }
        return null;
    }

    /**
     * @return This is needed as for http://opensource.atlassian.com/projects/spring/browse/SPR-2226
     */
    @SuppressWarnings("unchecked")
    private HibernateDAO<T, K> getThis()
    {
        if (!aopenabled)
        {
            return this;
        }

        try
        {
            return (HibernateDAO<T, K>) AopContext.currentProxy();
        }
        catch (AspectException exc)
        {
            logger.debug("Not running inside an AOP proxy, so no proxy can be returned: " + exc.getMessage());
            return this;
        }
        catch (IllegalStateException e)
        {
            logger.warn("Cannot access proxy: " + e.getMessage());
            aopenabled = false;
            return this;
        }
    }

    /**
     * @author carone
     * @version $Id$
     */
    private final class HibernateCallbackForExecution implements HibernateCallback
    {

        private T filter;

        private int page;

        private int maxResults;

        private Map<String, ? extends FilterMetadata> metadata;

        private List<String> properties;

        private Order[] orderProperties;

        private List< ? extends Criterion> additionalCriteria;

        private HibernateCallbackForExecution(
            T filter,
            int page,
            int maxResults,
            Map<String, ? extends FilterMetadata> metadata,
            Order[] orderProperties,
            List< ? extends Criterion> additionalCriteria,
            List<String> properties)
        {
            this.filter = filter;
            this.page = page;
            this.maxResults = maxResults;
            this.metadata = metadata;
            this.orderProperties = orderProperties;
            this.additionalCriteria = additionalCriteria;
            this.properties = properties;
        }

        public Object doInHibernate(Session ses) throws HibernateException
        {
            Criteria crit = ses.createCriteria(filter.getClass());
            crit.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
            crit.setMaxResults(maxResults);
            crit.setFirstResult(maxResults * page);

            if (orderProperties != null)
            {
                for (Order order : orderProperties)
                {
                    if (order != null)
                    {
                        crit.addOrder(order);
                    }
                }
            }
            EnhancedExample.create(crit, filter, metadata);
            if (additionalCriteria != null)
            {
                for (Criterion criterion : additionalCriteria)
                {
                    crit.add(criterion);
                }
            }
            if (properties != null)
            {
                ProjectionList projectionList = Projections.projectionList();

                for (String property : properties)
                {
                    projectionList.add(Property.forName(property));
                }

                crit.setProjection(projectionList);
            }
            return crit.list();
        }
    }

}