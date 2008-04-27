package it.openutils.dao.hibernate;

import it.openutils.hibernate.example.FilterMetadata;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.type.Type;


/**
 * @author Fabrizio Giustina
 * @version $Id$
 * @param <T> Persistence class
 * @param <K> Object Key
 */
public interface HibernateDAO<T extends Object, K extends Serializable>
{

    /**
     * Execute a query.
     * @param query a query expressed in Hibernate's query language
     * @return a distinct list of instances (or arrays of instances)
     */
    List<T> find(String query);

    /**
     * Return all objects related to the implementation of this DAO with no filter.
     * @return a list of all instances
     */
    List<T> findAll();

    /**
     * Return all objects related to the implementation of this DAO with no filter.
     * @param orderProperties <code>desc</code> or <code>asc</code>
     * @return a list of all instances
     */
    List<T> findAll(final Order[] orderProperties);

    /**
     * Return all objects related to the implementation of this DAO with no filter.
     * @param orderProperties <code>desc</code> or <code>asc</code>
     * @param criteria Additional Criterion conditions
     * @return a list of all instances
     */
    List<T> findAll(final Order[] orderProperties, List<Criterion> criteria);

    /**
     * Execute a query.
     * @param query a query expressed in Hibernate's query language
     * @param obj filter value
     * @param type filter type
     * @return a distinct list of instances (or arrays of instances)
     */
    List<T> find(String query, Object obj, Type type);

    /**
     * Execute a query.
     * @param query a query expressed in Hibernate's query language
     * @param obj filter values
     * @param type filter types
     * @return a distinct list of instances (or arrays of instances)
     */
    List<T> find(final String query, final Object[] obj, final Type[] type);

    /**
     * Re-read the state of the given instance from the underlying database. It is inadvisable to use this to implement
     * long-running sessions that span many business tasks. This method is, however, useful in certain special
     * circumstances. For example
     * <ul>
     * <li>where a database trigger alters the object state upon insert or update
     * <li>after executing direct SQL (eg. a mass update) in the same session
     * <li>after inserting a <tt>Blob</tt> or <tt>Clob</tt>
     * </ul>
     * @param obj Object
     */
    void refresh(T obj);

    /**
     * Remove the given object from the Session cache.
     * @param obj Object
     */
    void evict(T obj);

    /**
     * Copy the state of the given object onto the persistent object with the same identifier. If there is no persistent
     * instance currently associated with the session, it will be loaded. Return the persistent instance. If the given
     * instance is unsaved, save a copy of and return it as a newly persistent instance. The given instance does not
     * become associated with the session. This operation cascades to associated instances if the association is mapped
     * with <tt>cascade="merge"</tt>.<br>
     * <br>
     * The semantics of this method are defined by JSR-220.
     * @param obj a detached instance with state to be copied
     * @return an updated persistent instance
     */
    T merge(T obj);

    /**
     * Return all objects related to the implementation of this DAO filtered using properties of the provided instance.
     * @param filter an instance of the object with the properties you whish to filter on.
     * @param maxResults maximum number of results
     * @param page result page (first result is maxResults * page)
     * @return list of objects
     */
    List<T> findFiltered(final T filter, final int maxResults, final int page);

    /**
     * Return all objects related to the implementation of this DAO filtered using properties of the provided instance.
     * @param filter an instance of the object with the properties you whish to filter on.
     * @param metadata filter metadata
     * @param maxResults maximum number of results
     * @param page result page (first result is maxResults * page)
     * @return list of objects
     */
    List<T> findFiltered(final T filter, Map<String, FilterMetadata> metadata, final int maxResults, final int page);

    /**
     * Return all objects related to the implementation of this DAO filtered using properties of the provided instance.
     * @param filter an instance of the object with the properties you whish to filter on.
     * @param customOrder order criterias
     * @param metadata filter metadata
     * @param maxResults maximum number of results
     * @param page result page (first result is maxResults * page)
     * @return list of objects
     */
    List<T> findFiltered(final T filter, final Order[] customOrder, final Map<String, FilterMetadata> metadata,
        final int maxResults, final int page);

    /**
     * Return all objects related to the implementation of this DAO filtered using properties of the provided instance.
     * @param filter an instance of the object with the properties you whish to filter on.
     * @param customOrder order criterias
     * @param metadata filter metadata
     * @param maxResults maximum number of results
     * @param page result page (first result is maxResults * page)
     * @param additionalCriteria additional criteria
     * @return list of objects
     */
    List<T> findFiltered(final T filter, final Order[] customOrder, final Map<String, FilterMetadata> metadata,
        final int maxResults, final int page, List<Criterion> additionalCriteria);

    /**
     * Return properties from all objects related to the implementation of this DAO filtered using properties of the
     * provided instance.
     * @param filter an instance of the object with the properties you whish to filter on.
     * @param customOrder order criterias
     * @param metadata filter metadata
     * @param maxResults maximum number of results
     * @param page result page (first result is maxResults * page)
     * @param additionalCriteria additional criteria
     * @param properties properties to be returned
     * @return list of properties from all objects
     */
    List< ? > findFilteredProperties(final T filter, final Order[] customOrder,
        final Map<String, FilterMetadata> metadata, final int maxResults, final int page,
        List<Criterion> additionalCriteria, List<String> properties);

    /**
     * Return all objects related to the implementation of this DAO filtered using properties of the provided instance.
     * @param filter an instance of the object with the properties you whish to filter on.
     * @return list of objects
     */
    List<T> findFiltered(final T filter);

    /**
     * Return all objects related to the implementation of this DAO filtered using properties of the provided instance.
     * @param filter an instance of the object with the properties you whish to filter on.
     * @param orderProperties the name of the property used for ordering
     * @return list of objects
     */
    List<T> findFiltered(final T filter, final Order[] orderProperties);

    /**
     * Return all objects related to the implementation of this DAO filtered using properties of the provided instance.
     * @param filter an instance of the object with the properties you whish to filter on.
     * @param metadata filter metadata
     * @return list of objects
     */
    List<T> findFiltered(final T filter, Map<String, FilterMetadata> metadata);

    /**
     * Return the first object related to the implementation of this DAO filtered using properties of the provided
     * instance.
     * @param filter an instance of the object with the properties you whish to filter on.
     * @return first object in the collection
     */
    T findFilteredFirst(final T filter);

    /**
     * Return the first object related to the implementation of this DAO filtered using properties of the provided
     * instance.
     * @param filter an instance of the object with the properties you whish to filter on.
     * @param criteria additional criterion
     * @return first object in the collection
     */
    T findFilteredFirst(final T filter, final List<Criterion> criteria);

    /**
     * Used by the base DAO classes but here for your modification Remove a persistent instance from the datastore. The
     * argument may be an instance associated with the receiving Session or a transient instance with an identifier
     * associated with existing persistent state.
     * @param key key
     * @return true if the object was successfully deleted, false otherwise
     */
    boolean delete(final K key);

    /**
     * Load object matching the given key and return it. Throw an exception if not found.
     * @param key serializable key
     * @return Object
     */
    T load(K key);

    /**
     * Load object matching the given key and return it. Lazy object will be initialized.
     * @param key serializable key
     * @return Object
     */
    T loadIfAvailable(K key);

    /**
     * Load object matching the given key and return it. Lazy object will be initialized.
     * @param key serializable key
     * @return Object
     */
    T get(K key);

    /**
     * Used by the base DAO classes but here for your modification Either save() or update() the given instance,
     * depending upon the value of its identifier property.
     * @param obj Object
     */
    void saveOrUpdate(final T obj);

    /**
     * Used by the base DAO classes but here for your modification Update the persistent state associated with the given
     * identifier. An exception is thrown if there is a persistent instance with the same identifier in the current
     * session.
     * @param obj a transient instance containing updated state
     */
    void update(T obj);

    /**
     * Persist the given transient instance, first assigning a generated identifier. (Or using the current value of the
     * identifier property if the assigned generator is used.)
     * @param obj Object
     * @return generated id
     */
    K save(T obj);

}
