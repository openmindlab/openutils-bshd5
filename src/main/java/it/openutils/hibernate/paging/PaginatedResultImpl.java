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

package it.openutils.hibernate.paging;

import java.util.Collection;


/**
 * @author fgiust
 * @version $Id$
 */
public class PaginatedResultImpl<T> implements PaginatedResult<T>
{

    private final int itemsPerPage;

    private final int pageNumberStartingFromOne;

    private final int totalSize;

    private Collection<T> results;

    /**
     * Stable serialVersionUID.
     */
    private static final long serialVersionUID = 42L;

    /**
     * @param itemsPerPage
     * @param pageNumberStartingFromOne
     * @param totalSize
     */
    public PaginatedResultImpl(int itemsPerPage, int pageNumberStartingFromOne, int totalSize)
    {
        this.itemsPerPage = itemsPerPage;
        this.pageNumberStartingFromOne = pageNumberStartingFromOne;
        this.totalSize = totalSize;
    }

    /**
     * {@inheritDoc}
     */
    public int getItemsPerPage()
    {
        return itemsPerPage;
    }

    /**
     * {@inheritDoc}
     */
    public int getPage()
    {
        return pageNumberStartingFromOne;
    }

    /**
     * {@inheritDoc}
     */
    public int getTotalSize()
    {
        return totalSize;
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfPages()
    {
        return itemsPerPage > 0 ? (int) Math.round(Math.ceil(((float) totalSize / (float) itemsPerPage))) : 1;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<T> getItems()
    {
        return this.results;
    }

    /**
     * {@inheritDoc}
     */
    public T getFirstResult()
    {
        if (this.results != null && this.results.size() > 0)
        {
            return this.results.iterator().next();
        }
        return null;
    }

    public void setResults(Collection<T> results)
    {
        this.results = results;
    }

}
