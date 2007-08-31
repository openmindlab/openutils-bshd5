package it.openutils.dao.hibernate;

import it.openutils.hibernate.example.EnhancedExample;
import it.openutils.hibernate.example.FilterMetadata;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * Base Hibernate DAO.
 * @author Fabrizio Giustina
 * @version $Id$
 * @param <T> Persistence class
 * @param <K> Object Key
 */
public abstract class HibernateDAOImpl<T extends Object, K extends Serializable> extends HibernateDaoSupport
    implements
    HibernateDAO<T, K>
{

    /**
     * @author carone
     * @version $Id$
     */
    private final class HibernateCallbackForExecution implements HibernateCallback
    {

        /**
         *
         */
        private final T filter;

        /**
         *
         */
        private final int page;

        /**
         *
         */
        private final int maxResults;

        /**
         *
         */
        private final Map<String, FilterMetadata> metadata;

        /**
         *
         */
        private final Order[] orderProperties;

        private List<Criterion> additionalCriteria;

        /**
         * @param filter
         * @param page
         * @param maxResults
         * @param metadata
         * @param orderProperties
         */
        private HibernateCallbackForExecution(
            T filter,
            int page,
            int maxResults,
            Map<String, FilterMetadata> metadata,
            Order[] orderProperties,
            List<Criterion> additionalCriteria)
        {
            this.filter = filter;
            this.page = page;
            this.maxResults = maxResults;
            this.metadata = metadata;
            this.orderProperties = orderProperties;
            this.additionalCriteria = additionalCriteria;
        }

        public Object doInHibernate(Session ses) throws HibernateException
        {
            Criteria crit = ses.createCriteria(filter.getClass());
            crit.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
            crit.setMaxResults(maxResults);
            crit.setFirstResult(maxResults * page);

            if (orderProperties != null && orderProperties.length > 0)
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
            if (additionalCriteria != null && !additionalCriteria.isEmpty())
            {
                for (Criterion criterion : additionalCriteria)
                {
                    crit.add(criterion);
                }
            }
            return crit.list();
        }
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
    public List<T> findAll()
    {
        return findAll(getDefaultOrder());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll(final Order[] orderProperties)
    {

        return (List<T>) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session ses) throws HibernateException
            {
                Criteria crit = ses.createCriteria(getReferenceClass());
                if (null != orderProperties)
                {
                    for (int j = 0; j < orderProperties.length; j++)
                    {
                        crit.addOrder(orderProperties[j]);
                    }

                }
                return crit.list();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll(final Order[] orderProperties, final List<Criterion> criteria)
    {
        return (List<T>) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session ses) throws HibernateException
            {
                Criteria crit = ses.createCriteria(getReferenceClass());
                if (null != orderProperties)
                {
                    for (int j = 0; j < orderProperties.length; j++)
                    {
                        crit.addOrder(orderProperties[j]);
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
    public List<T> find(String query, Object obj, Type type)
    {
        return find(query, new Object[]{obj }, new Type[]{type });
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> find(final String query, final Object[] obj, final Type[] type)
    {
        return (List<T>) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session ses) throws HibernateException
            {
                // hibernate 3
                return ses.createQuery(query).setParameters(obj, type).list();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T load(K key)
    {
        T result = (T) getHibernateTemplate().load(getReferenceClass(), key);
        Hibernate.initialize(result);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T loadIfAvailable(K key)
    {
        T result;
        try
        {
            result = (T) getHibernateTemplate().load(getReferenceClass(), key);
            Hibernate.initialize(result);
        }
        catch (ObjectNotFoundException e)
        {
            // during lazy initialization
            return null;
        }
        catch (HibernateObjectRetrievalFailureException e)
        {
            // during load
            if (e.getCause() instanceof ObjectNotFoundException)
            {
                return null;
            }
            throw e;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void saveOrUpdate(final T obj)
    {
        getHibernateTemplate().saveOrUpdate(obj);
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
    public boolean delete(final K key)
    {

        return (Boolean) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session ses) throws HibernateException
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
    @SuppressWarnings("unchecked")
    public T merge(final T obj)
    {
        return (T) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session ses) throws HibernateException
            {
                return ses.merge(obj);
            }
        });

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
    public T findFilteredFirst(final T filter)
    {
        return getFirstInCollection(findFiltered(filter, 1, 0));
    }

    /**
     * {@inheritDoc}
     */
    public T findFilteredFirst(final T filter, List<Criterion> criteria)
    {
        return getFirstInCollection(findFiltered(filter, null,  getDefaultFilterMetadata(), 1, 0, criteria));
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findFiltered(final T filter)
    {
        return findFiltered(filter, getDefaultFilterMetadata());
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findFiltered(T filter, Order[] orderProperties)
    {
        return findFiltered(filter, orderProperties, getDefaultFilterMetadata(), Integer.MAX_VALUE, 0);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findFiltered(final T filter, final Map<String, FilterMetadata> metadata)
    {
        return findFiltered(filter, metadata, Integer.MAX_VALUE, 0);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findFiltered(final T filter, final int maxResults, final int page)
    {
        return findFiltered(filter, getDefaultFilterMetadata(), maxResults, page);
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findFiltered(final T filter, final Map<String, FilterMetadata> metadata, final int maxResults,
        final int page)
    {
        return findFiltered(filter, null, metadata, maxResults, page);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findFiltered(final T filter, final Order[] customOrder, final Map<String, FilterMetadata> metadata,
        final int maxResults, final int page)
    {
        return findFiltered(filter, customOrder, metadata, maxResults, page, null);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findFiltered(T filter, Order[] customOrder, Map<String, FilterMetadata> metadata, int maxResults,
        int page, List<Criterion> additionalCriteria)
    {
        final Order[] orderProperties = customOrder != null && customOrder.length > 0 ? customOrder : this
            .getDefaultOrder();

        return (List<T>) getHibernateTemplate().execute(
            new HibernateCallbackForExecution(filter, page, maxResults, metadata, orderProperties, additionalCriteria));
    }

    /**
     * Return the specific Object class that will be used for class-specific implementation of this DAO.
     * @return the reference Class
     */
    protected abstract Class<T> getReferenceClass();

    /**
     * Return a list of <code>Order</code> object to be used for the default ordering of the collection.
     * @return the property name
     */
    protected Order[] getDefaultOrder()
    {
        return new Order[0];
    }

    /**
     * Obtain an instance of Query for a named query string defined in the mapping file.
     * @param name the name of a query defined externally
     * @param maxResults max number of results
     * @return Query
     */
    protected List< ? > getNamedQuery(final String name, final int maxResults)
    {
        return (List< ? >) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session ses) throws HibernateException
            {
                Query q = ses.getNamedQuery(name);
                q.setMaxResults(maxResults);
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
     */
    protected List< ? > getNamedQuery(final String name, final Serializable[] params, final int maxResults)
    {

        return (List< ? >) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session ses) throws HibernateException
            {
                Query q = ses.getNamedQuery(name);
                q.setMaxResults(maxResults);
                if (null != params)
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
    protected List< ? > getNamedQuery(final String name, final Map<String, Object> params, final int maxResults)
    {
        return (List< ? >) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session ses) throws HibernateException
            {
                Query q = ses.getNamedQuery(name);
                q.setMaxResults(maxResults);
                if (params != null)
                {
                    for (Map.Entry<String, Object> entry : params.entrySet())
                    {
                        setParameterValue(q, entry.getKey(), entry.getValue());
                    }
                }
                return q.list();
            }
        });
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
    protected Map<String, FilterMetadata> getDefaultFilterMetadata()
    {
        return new HashMap<String, FilterMetadata>(0);
    }

    /**
     * Returns the first object in the collection or null if the collection is null or empty.
     * @param list collection
     * @return first element in the list
     */
    @SuppressWarnings("unchecked")
    private T getFirstInCollection(Collection<T> list)
    {
        if (list != null && !list.isEmpty())
        {
            Object result = list.iterator().next();
            Hibernate.initialize(result);
            return (T) result;
        }
        return null;
    }

}