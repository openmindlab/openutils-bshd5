package it.openutils.dao.hibernate;

import it.openutils.hibernate.example.EnhancedExample;
import it.openutils.hibernate.example.FilterMetadata;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * Base Hibernate DAO.
 * @author Fabrizio Giustina
 * @version $Id$
 */
public abstract class HibernateDAOImpl<T extends Object, K extends Serializable> extends HibernateDaoSupport
    implements
    HibernateDAO<T, K>
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(HibernateDAOImpl.class);

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#find(java.lang.String)
     */
    public List<T> find(String query)
    {
        return getHibernateTemplate().find(query);
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#findAll()
     */
    public List<T> findAll()
    {
        return findAll(getDefaultOrder());
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#findAll(org.hibernate.criterion.Order[])
     */
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
     * @see it.openutils.dao.hibernate.HibernateDAO#find(java.lang.String, java.lang.Object, org.hibernate.type.Type)
     */
    public List<T> find(String query, Object obj, Type type)
    {
        return find(query, new Object[]{obj}, new Type[]{type});
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#find(String, Object[], Type[])
     */
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
     * @see it.openutils.dao.hibernate.HibernateDAO#load(java.io.Serializable)
     */
    public T load(K key)
    {
        T result = (T) getHibernateTemplate().load(getReferenceClass(), key);
        Hibernate.initialize(result);
        return result;
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#load(java.io.Serializable)
     */
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
     * @see it.openutils.dao.hibernate.HibernateDAO#saveOrUpdate(null)
     */
    public void saveOrUpdate(final T obj)
    {
        getHibernateTemplate().saveOrUpdate(obj);
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#update(null)
     */
    public void update(T obj)
    {
        getHibernateTemplate().update(obj);
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#delete(null)
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
     * @see it.openutils.dao.hibernate.HibernateDAO#refresh(null)
     */
    public void refresh(T obj)
    {
        getHibernateTemplate().refresh(obj);
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#merge(T)
     */
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
     * @see it.openutils.dao.hibernate.HibernateDAO#save(java.lang.Object)
     */
    public K save(T obj)
    {
        return (K) getHibernateTemplate().save(obj);
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#findFilteredFirst(T)
     */
    public T findFilteredFirst(final T filter)
    {
        return getFirstInCollection(findFiltered(filter, 1, 0));
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#findFiltered(T)
     */
    public List<T> findFiltered(final T filter)
    {
        return findFiltered(filter, new HashMap<String, FilterMetadata>(0));
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#findFiltered(java.lang.Object, org.hibernate.criterion.Order[])
     */
    public List<T> findFiltered(T filter, Order[] orderProperties)
    {
        return findFiltered(filter, orderProperties, new HashMap<String, FilterMetadata>(0), Integer.MAX_VALUE, 0);
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#findFiltered(T)
     */
    public List<T> findFiltered(final T filter, final Map<String, FilterMetadata> metadata)
    {
        return findFiltered(filter, metadata, Integer.MAX_VALUE, 0);
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#findFiltered(null, int, int)
     */
    public List<T> findFiltered(final T filter, final int maxResults, final int page)
    {
        return findFiltered(filter, new HashMap<String, FilterMetadata>(0), maxResults, page);
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#findFiltered(null, int, int)
     */
    public List<T> findFiltered(final T filter, final Map<String, FilterMetadata> metadata, final int maxResults,
        final int page)
    {
        return findFiltered(filter, null, metadata, maxResults, page);
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#findFiltered(null, int, int)
     */
    public List<T> findFiltered(final T filter, final Order[] customOrder, final Map<String, FilterMetadata> metadata,
        final int maxResults, final int page)
    {
        final Order[] orderProperties = customOrder != null && customOrder.length > 0 ? customOrder : this
            .getDefaultOrder();

        return (List<T>) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session ses) throws HibernateException
            {
                Criteria crit = ses.createCriteria(filter.getClass());
                crit.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
                crit.setMaxResults(maxResults);
                crit.setFirstResult(maxResults * page);

                if (null != orderProperties && orderProperties.length > 0)
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

                return crit.list();
            }
        });
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
     * @return Query
     */
    protected List< ? > getNamedQuery(final String name, final int marResults)
    {
        return (List< ? >) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session ses) throws HibernateException
            {
                Query q = ses.getNamedQuery(name);
                q.setMaxResults(marResults);
                return q.list();
            }
        });
    }

    /**
     * Obtain an instance of Query for a named query string defined in the mapping file. Use the parameters given.
     * @param name the name of a query defined externally
     * @param params the parameter array
     * @return Query
     */
    protected List< ? > getNamedQuery(final String name, final Serializable[] params, final int marResults)
    {

        return (List< ? >) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session ses) throws HibernateException
            {
                Query q = ses.getNamedQuery(name);
                q.setMaxResults(marResults);
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
     * @return Query
     */
    protected List< ? > getNamedQuery(final String name, final Map params, final int marResults)
    {
        return (List< ? >) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session ses) throws HibernateException
            {
                Query q = ses.getNamedQuery(name);
                q.setMaxResults(marResults);
                if (params != null)
                {
                    for (Iterator i = params.entrySet().iterator(); i.hasNext();)
                    {
                        Map.Entry entry = (Map.Entry) i.next();
                        setParameterValue(q, (String) entry.getKey(), entry.getValue());
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
     * Returns the first object in the collection or null if the collection is null or empty.
     * @param list collection
     * @return first element in the list
     */
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