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
package it.openutils.hibernate.paging;

import java.io.Serializable;
import java.util.Collection;


/**
 * @author fgiust
 * @version $Id$
 */
public interface PaginatedResult<T> extends Serializable
{

    /**
     * Gets the maximum number of results per page
     * @return the maximum number of results per page
     */
    int getItemsPerPage();

    /**
     * Gets the page number (1, 2, 3...)
     * @return the page number (1, 2, 3...)
     */
    int getPage();

    /**
     * Gets the total number of results that would be retrieved without pagination.
     * @return the total number of results that would be retrieved without pagination.
     */
    int getTotalSize();

    /**
     * Gets the total number of pages
     * @return total number of pages
     */
    int getNumberOfPages();

    /**
     * Gets an iterator over the results
     * @return an iterator over the results
     */
    Collection<T> getItems();

    /**
     * Returns the fist result if available, null otherwise.
     * @return the fist result if available, null otherwise.
     */
    T getFirstResult();

}
