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

package it.openutils.dao.hibernate;

import it.openutils.hibernate.example.ExampleTree;
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
 * @param <T> the persistence entity class that this DAO will handle
 * @param <K> the class of <code>T</code>'s id property
 */
public interface HibernateDAO<T, K extends Serializable>
{

    /**
     * Retrieve all entities handled by this DAO, in no particular order.
     * @return the list of all entity instances (never null)
     */
    List<T> findAll();

    /**
     * Retrieve all entities handled by this DAO, ordered according to the input <code>orders</code>.
     * @param orders the orders to apply with respect to entity class properties
     * @return the list of all entity instances (never null), ordered accordingly
     */
    List<T> findAll(Order... orders);

    /**
     * Retrieve all entities handled by this DAO that match the input query string.
     * @param query an HQL query
     * @return a list of distinct entity instances (never null)
     */
    List<T> find(String query);

    /**
     * Retrieve all entities handled by this DAO that match the input query string, accepting one query parameter and
     * the corresponding type.
     * @param query an HQL query
     * @param paramValue the value of a parameter to be set in the query
     * @param paramType the Hibernate type of <code>paramValue</code>
     * @return a list of distinct entity instances (never null)
     */
    List<T> find(String query, Object paramValue, Type paramType);

    /**
     * Retrieve all entities handled by this DAO that match the input query string, accepting one query parameter and
     * the corresponding type.
     * @param query an HQL query
     * @param paramValues the parameter values to be set in the query
     * @param paramTypes the Hibernate types of <code>paramValues</code>
     * @return a list of distinct entity instances (never null)
     */
    List<T> find(String query, Object[] paramValues, Type[] paramTypes);

    /**
     * Retrieve the entities handled by this DAO that match the input <code>criteria</code>, ordered according to the
     * input <code>orders</code>.
     * @param criteria a list of additional Hibernate criteria
     * @param orders the orders to apply with respect to entity class properties
     * @return a list of distinct entity instances (never null), matching the criteria and ordered accordingly
     */
    List<T> find(List< ? extends Criterion> criteria, Order... orders);

    /**
     * Retrieve the entities handled by this DAO whose property values match, via <code>equals()</code>,
     * <code>filter</code>'s non-null property values.
     * @param filter an instance of this DAO's entity class to be used as filter
     * @return a list of distinct entity instances (never null)
     */
    List<T> findFiltered(T filter);

    /**
     * Retrieve the entities handled by this DAO whose property values match, via <code>equals()</code>,
     * <code>filter</code>'s non-null property values. The result list is ordered according to the input
     * <code>orders</code> parameter.
     * @param filter an instance of this DAO's entity class to be used as filter
     * @param orders the orders to apply with respect to entity class properties
     * @return a list of distinct entity instances (never null)
     */
    List<T> findFiltered(T filter, Order... orders);

    /**
     * Retrieve the entities handled by this DAO whose property values match, via <code>equals()</code>,
     * <code>filter</code>'s non-null property values.
     * @param filter an instance of this DAO's entity class to be used as filter
     * @param maxResults the maximum number of results to be fetched
     * @param page the zero-based page number to use when displaying paginated results (the first entity returned is the
     * one at position <code>maxResults * page</code> in the complete list of results), or <code>0</code> for no
     * pagination
     * @return a list of distinct entity instances (never null)
     */
    List<T> findFiltered(T filter, int maxResults, int page);

    /**
     * Retrieve the entities handled by this DAO that match the input example tree
     * @param exampleTree the example tree criterion to match
     * @return a list of distinct entity instances (never null)
     */
    List<T> findFiltered(ExampleTree exampleTree);

    /**
     * Retrieve the entities handled by this DAO that match the input example tree
     * @param exampleTree the example tree criterion to match
     * @param orders the orders to apply with respect to entity class properties
     * @return a list of distinct entity instances (never null)
     */
    List<T> findFiltered(ExampleTree exampleTree, Order... orders);

    /**
     * Retrieve the entities handled by this DAO that match the input example tree
     * @param exampleTree the example tree criterion to match
     * @param maxResults the maximum number of results to be fetched
     * @param page the zero-based page number to use when displaying paginated results (the first entity returned is the
     * one at position <code>maxResults * page</code> in the complete list of results), or <code>0</code> for no
     * pagination
     * @return a list of distinct entity instances (never null)
     */
    List<T> findFiltered(ExampleTree exampleTree, int maxResults, int page);

    /**
     * Retrieve the entities handled by this DAO that match the input example tree
     * @param exampleTree the example tree criterion to match
     * @param maxResults the maximum number of results to be fetched
     * @param page the zero-based page number to use when displaying paginated results (the first entity returned is the
     * one at position <code>maxResults * page</code> in the complete list of results), or <code>0</code> for no
     * pagination
     * @param orders the orders to apply with respect to entity class properties
     * @return a list of distinct entity instances (never null)
     */
    List<T> findFiltered(ExampleTree exampleTree, int maxResults, int page, Order... orders);

    /**
     * Retrieve a set of properties from the entities returned by
     * {@link #findFiltered(Object, Order[], Map, int, int, List)}
     * @param filter an instance of this DAO's entity class to be used as filter
     * @param maxResults the maximum number of results to be fetched
     * @param page the zero-based page number to use when displaying paginated results (the first entity returned is the
     * one at position <code>maxResults * page</code> in the complete list of results), or <code>0</code> for no
     * pagination
     * @param properties the names of the properties to return
     * @param orders the orders to apply with respect to entity class properties
     * @return a list of distinct entity instances (never null)
     */
    List<Object> findFilteredProperties(T filter, int maxResults, int page, List<String> properties, Order... orders);

    /**
     * Retrieve a set of properties from the entities returned by
     * {@link #findFiltered(Object, Order[], Map, int, int, List)}
     * @param exampleTree the example tree criterion to match
     * @param maxResults the maximum number of results to be fetched
     * @param page the zero-based page number to use when displaying paginated results (the first entity returned is the
     * one at position <code>maxResults * page</code> in the complete list of results), or <code>0</code> for no
     * pagination
     * @param properties the names of the properties to return
     * @param orders the orders to apply with respect to entity class properties
     * @return a list of distinct entity instances (never null)
     */
    List<Object> findFilteredProperties(ExampleTree exampleTree, int maxResults, int page, List<String> properties,
        Order... orders);

    /**
     * Retrieve the entities handled by this DAO whose property values match, via <code>equals()</code> or via a
     * specified <code>FilterMetadata</code> object, <code>filter</code>'s non-null property values.
     * @param filter an instance of this DAO's entity class to be used as filter
     * @param metadata a map that matches names of entity class properties to <code>FilterMetadata</code> modifiers,
     * that will be used for comparing values of the corresponding property
     * @return a list of distinct entity instances (never null)
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)}
     */
    @Deprecated
    List<T> findFiltered(T filter, Map<String, ? extends FilterMetadata> metadata);

    /**
     * Retrieve the entities handled by this DAO whose property values match, via <code>equals()</code> or via a
     * specified <code>FilterMetadata</code> object, <code>filter</code>'s non-null property values.
     * @param filter an instance of this DAO's entity class to be used as filter
     * @param metadata a map that matches names of entity class properties to <code>FilterMetadata</code> modifiers,
     * that will be used for comparing values of the corresponding property
     * @param maxResults the maximum number of results to be fetched
     * @param page the zero-based page number to use when displaying paginated results (the first entity returned is the
     * one at position <code>maxResults * page</code> in the complete list of results), or <code>0</code> for no
     * pagination
     * @return a list of distinct entity instances (never null)
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)}
     */
    @Deprecated
    List<T> findFiltered(T filter, Map<String, ? extends FilterMetadata> metadata, int maxResults, int page);

    /**
     * Retrieve the entities handled by this DAO whose property values match, via <code>equals()</code> or via a
     * specified <code>FilterMetadata</code> object, <code>filter</code>'s non-null property values. The result list is
     * ordered according to the <code>orders</code> parameter.
     * @param filter an instance of this DAO's entity class to be used as filter
     * @param metadata a map that matches names of entity class properties to <code>FilterMetadata</code> modifiers,
     * that will be used for comparing values of the corresponding property
     * @param maxResults the maximum number of results to be fetched
     * @param page the zero-based page number to use when displaying paginated results (the first entity returned is the
     * one at position <code>maxResults * page</code> in the complete list of results), or <code>0</code> for no
     * pagination
     * @param orders the orders to apply with respect to entity class properties
     * @return a list of distinct entity instances (never null)
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)}
     */
    @Deprecated
    List<T> findFiltered(T filter, Map<String, ? extends FilterMetadata> metadata, int maxResults, int page,
        Order... orders);

    /**
     * Retrieve the entities handled by this DAO whose property values match, via <code>equals()</code> or via a
     * specified <code>FilterMetadata</code> object, <code>filter</code>'s non-null property values, and the input
     * <code>criteria</code>.
     * @param filter an instance of this DAO's entity class to be used as filter
     * @param metadata a map that matches names of entity class properties to <code>FilterMetadata</code> modifiers,
     * that will be used for comparing values of the corresponding property
     * @param maxResults the maximum number of results to be fetched
     * @param page the zero-based page number to use when displaying paginated results (the first entity returned is the
     * one at position <code>maxResults * page</code> in the complete list of results), or <code>0</code> for no
     * pagination
     * @param criteria a list of additional Hibernate criteria
     * @param orders the orders to apply with respect to entity class properties
     * @return a list of distinct entity instances (never null)
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)}
     */
    @Deprecated
    List<T> findFiltered(T filter, Map<String, ? extends FilterMetadata> metadata, int maxResults, int page,
        List< ? extends Criterion> criteria, Order... orders);

    /**
     * Retrieve the first entity instance that matches the input <code>filter</code>, if existing.
     * @param filter an instance of this DAO's entity class to be used as filter
     * @return the first matching instance of the entity class managed by this DAO, or <code>null</code> if none found
     * @see #findFiltered(T)
     */
    T findFilteredFirst(T filter);

    /**
     * Retrieve the first entity instance that matches the input example tree
     * @param exampleTree the example tree criterion to match
     * @return the first matching instance of the entity class managed by this DAO, or <code>null</code> if none found
     * @see #findFiltered(ExampleTree)
     */
    T findFilteredFirst(ExampleTree exampleTree);

    /**
     * Retrieve the first entity instance that matches the input <code>filter</code>, if existing.
     * @param filter an instance of this DAO's entity class to be used as filter
     * @param orders the orders to apply with respect to entity class properties
     * @return the first matching instance of the entity class managed by this DAO, or <code>null</code> if none found
     * @see #findFiltered(T, Order...)
     */
    T findFilteredFirst(T filter, Order... orders);

    /**
     * Retrieve the first entity instance that matches the input example tree
     * @param exampleTree the example tree criterion to match
     * @param orders the orders to apply with respect to entity class properties
     * @return the first matching instance of the entity class managed by this DAO, or <code>null</code> if none found
     * @see #findFiltered(ExampleTree, Order...)
     */
    T findFilteredFirst(ExampleTree exampleTree, final Order... orders);

    /**
     * Retrieve the first entity instance that matches the input <code>filter</code> and the additional input
     * <code>criteria</code>, if existing.
     * @param filter an instance of this DAO's entity class to be used as filter
     * @param criteria a list of additional Hibernate criteria
     * @return the first matching instance of the entity class managed by this DAO, or <code>null</code> if none found
     * @see #findFiltered(T, List)
     */
    T findFilteredFirst(T filter, List< ? extends Criterion> criteria);

    /**
     * Load object matching the given key and return it. Throw an exception if not found.
     * @param key the id of the entity instance to load
     * @return the found entity instance (never null)
     */
    T load(K key);

    /**
     * Load object matching the given key and return it. Lazy object will be initialized.
     * @param key the id of the entity instance to load
     * @return the found entity instance, or null if none found
     */
    T loadIfAvailable(K key);

    /**
     * Load object matching the given key and return it. Lazy object will be initialized.
     * @param key the id of the entity instance to load
     * @return the found entity instance (never null)
     */
    T get(K key);

    /**
     * Persist the given transient instance, first assigning a generated identifier. (Or using the current value of the
     * identifier property if the assigned generator is used.)
     * @param obj the entity instance to save
     * @return the id generated for the persisted instance
     */
    K save(T obj);

    /**
     * Used by the base DAO classes but here for your modification Update the persistent state associated with the given
     * identifier. An exception is thrown if there is a persistent instance with the same identifier in the current
     * session.
     * @param obj a transient instance containing updated state
     */
    void update(T obj);

    /**
     * Used by the base DAO classes but here for your modification Either save() or update() the given instance,
     * depending upon the value of its identifier property.
     * @param obj Object
     */
    void saveOrUpdate(T obj);

    /**
     * Used by the base DAO classes but here for your modification. Remove a persistent instance from the datastore. The
     * argument may be an instance associated with the receiving Session or a transient instance with an identifier
     * associated with existing persistent state.
     * @param key key
     * @return true if the object was successfully deleted, false otherwise
     */
    boolean delete(K key);

    /**
     * Re-reads the state of the given instance from the underlying database. This method is useful in certain special
     * circumstances, for example:
     * <ul>
     * <li>when a database trigger is known to alter the object state right after <code>INSERT</code> or
     * <code>UPDATE</code> statements
     * <li>after some operation (e.g. a mass update) has been executed via direct SQL in the same session
     * <li>after modifying a database field of type <tt>Blob</tt> or <tt>Clob</tt>
     * </ul>
     * However, use of this method in long-running sessions that span many business tasks is discouraged.
     * @param obj the entity instance to refresh
     */
    void refresh(T obj);

    /**
     * Remove the given object from the Session cache.
     * @param obj the entity instance to remove
     */
    void evict(T obj);

    /**
     * Copies the state of the given object onto the persistent object with the same identifier, and returns the updated
     * persistent instance. If there is no persistent instance currently associated with the session, a new one will be
     * loaded. If the given instance is unsaved, saves a copy of and return it as a newly persisted instance. The given
     * instance does not become associated with the session. This operation cascades to associated instances if the
     * association is mapped with <code>cascade="merge"</code>.<br>
     * <br>
     * The semantics of this method are defined by JSR-220.
     * @param obj a detached instance with state to be copied
     * @return an updated persistent instance
     */
    T merge(T obj);

    /**
     * Return all objects related to the implementation of this DAO with no filter.
     * @param orderProperties <code>desc</code> or <code>asc</code>
     * @param criteria Additional Criterion conditions
     * @return a list of all instances
     * @deprecated use the correctly named {@link #find(Order[], List)} instead
     */
    @Deprecated
    List<T> findAll(Order[] orderProperties, List< ? extends Criterion> criteria);

    /**
     * Retrieve the entities handled by this DAO whose property values match, via <code>equals()</code> or via a
     * specified <code>FilterMetadata</code> object, <code>filter</code>'s non-null property values. The result list is
     * ordered according to the <code>orders</code> parameter.
     * @param filter an instance of this DAO's entity class to be used as filter
     * @param orders the orders to apply with respect to entity class properties
     * @param metadata a map that matches names of entity class properties to <code>FilterMetadata</code> modifiers,
     * that will be used for comparing values of the corresponding property
     * @param maxResults the maximum number of results to be fetched
     * @param page the zero-based page number to use when displaying paginated results (the first entity returned is the
     * one at position <code>maxResults * page</code> in the complete list of results), or <code>0</code> for no
     * pagination
     * @return a list of distinct entity instances (never null)
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)}
     */
    @Deprecated
    List<T> findFiltered(T filter, Order[] orders, Map<String, ? extends FilterMetadata> metadata, int maxResults,
        int page);

    /**
     * Retrieve the entities handled by this DAO whose property values match, via <code>equals()</code> or via a
     * specified <code>FilterMetadata</code> object, <code>filter</code>'s non-null property values, and the input
     * <code>criteria</code>.
     * @param filter an instance of this DAO's entity class to be used as filter
     * @param orders the orders to apply with respect to entity class properties
     * @param metadata a map that matches names of entity class properties to <code>FilterMetadata</code> modifiers,
     * that will be used for comparing values of the corresponding property
     * @param maxResults the maximum number of results to be fetched
     * @param page the zero-based page number to use when displaying paginated results (the first entity returned is the
     * one at position <code>maxResults * page</code> in the complete list of results), or <code>0</code> for no
     * pagination
     * @param criteria a list of additional Hibernate criteria
     * @return a list of distinct entity instances (never null)
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)}
     */
    @Deprecated
    List<T> findFiltered(T filter, Order[] orders, Map<String, ? extends FilterMetadata> metadata, int maxResults,
        int page, List< ? extends Criterion> criteria);

    /**
     * Retrieve a set of properties from the entities returned by
     * {@link #findFiltered(Object, Order[], Map, int, int, List)}
     * @param filter an instance of this DAO's entity class to be used as filter
     * @param orders the orders to apply with respect to entity class properties
     * @param metadata a map that matches names of entity class properties to <code>FilterMetadata</code> modifiers,
     * that will be used for comparing values of the corresponding property
     * @param maxResults the maximum number of results to be fetched
     * @param page the zero-based page number to use when displaying paginated results (the first entity returned is the
     * one at position <code>maxResults * page</code> in the complete list of results), or <code>0</code> for no
     * pagination
     * @param criteria a list of additional Hibernate criteria
     * @param properties the names of the properties to return
     * @return a list of distinct entity instances (never null)
     * @deprecated {@link FilterMetadata} has been deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
     * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)}
     */
    @Deprecated
    List<Object> findFilteredProperties(T filter, Order[] orders, Map<String, ? extends FilterMetadata> metadata,
        int maxResults, int page, List< ? extends Criterion> criteria, List<String> properties);

}
