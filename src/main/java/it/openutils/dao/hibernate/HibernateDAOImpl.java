package it.openutils.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * Base Hibernate DAO.
 * @author fgiust
 * @version $Id$
 */
public abstract class HibernateDAOImpl<T extends Object, K extends Serializable> extends HibernateDaoSupport
    implements
    HibernateDAO<T, K>
{

    /**
     * Logger.
     */
    protected static Logger log = Logger.getLogger(HibernateDAOImpl.class);

    public static final String LIKE_EXPRESSION = "${like}";

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
     * @see it.openutils.dao.hibernate.HibernateDAO#findFiltered(java.lang.String, java.lang.Object)
     */
    public List<T> findFiltered(String propName, Object filter)
    {
        return findFiltered(propName, filter, getDefaultOrder());
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#findFiltered(String, Object, Order[])
     */
    public List<T> findFiltered(final String propName, final Object filter, final Order[] orderProperties)
    {

        return (List<T>) getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session ses) throws HibernateException
            {
                Criteria crit = ses.createCriteria(getReferenceClass());
                crit.add(Restrictions.eq(propName, filter));

                for (int j = 0; j < orderProperties.length; j++)
                {
                    Order order = orderProperties[j];
                    crit.addOrder(order);
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
     * @see it.openutils.dao.hibernate.HibernateDAO#findFiltered(T)
     */
    public List<T> findFiltered(final T filter)
    {
        return findFiltered(filter, 500, 0);
    }

    /**
     * @see it.openutils.dao.hibernate.HibernateDAO#findFilteredFirst(T)
     */
    public T findFilteredFirst(final T filter)
    {
        return getFirstInCollection(findFiltered(filter, 1, 0));
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
     * @see it.openutils.dao.hibernate.HibernateDAO#findFiltered(null, int, int)
     */
    public List<T> findFiltered(final T filter, final int maxResults, final int page)
    {
        final Order[] orderProperties = this.getDefaultOrder();

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

                fillCriteria(crit, filter);

                return crit.list();
            }
        });
    }

    /**
     * Return the specific Object class that will be used for class-specific implementation of this DAO.
     * @return the reference Class
     */
    protected abstract Class getReferenceClass();

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
     * Fills a criteria object calling addCondition() for any non-null property or for any component in collections.
     * @param crit Criteria
     * @param filter javabean which will be analyzed for non-null properties
     * @throws HibernateException exception while building the criteria
     */
    protected void fillCriteria(Criteria crit, Object filter) throws HibernateException
    {
        if ((filter instanceof Set || filter instanceof List) && !((Collection) filter).isEmpty())
        {
            // collection: the new criteria has already been created, now we only nee to analize content
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
            if (value instanceof String)
            {

                // don't filter on empty strings!
                if (StringUtils.isBlank((String) value))
                {
                    return;
                }

                if (StringUtils.contains((String) value, LIKE_EXPRESSION))
                {
                    String valoreDescr = (String) value;
                    valoreDescr = StringUtils.replace(valoreDescr, "%", "");
                    valoreDescr = StringUtils.replace(valoreDescr, LIKE_EXPRESSION, "%");
                    crit.add(Restrictions.like(propertyName, valoreDescr));
                    log.debug("crit.add(Expression.like(" + propertyName + ", " + valoreDescr + "))");
                    return;
                }

            }

            if (log.isDebugEnabled())
            {
                log.debug("crit.add(Expression.eq(" + propertyName + ", " + value + "))");
            }

            crit.add(Restrictions.eq(propertyName, value));
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
                Criteria childrenCriteria = crit.createCriteria(propertyName);

                fillCriteria(childrenCriteria, value);
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