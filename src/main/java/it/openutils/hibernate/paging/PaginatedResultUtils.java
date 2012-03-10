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

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public final class PaginatedResultUtils
{

    /**
     * Logger.
     */
    static Logger log = LoggerFactory.getLogger(PaginatedResultUtils.class);

    /**
     * Executes a criteria query and returns a paginated result.
     * @param <T> element type
     * @param criteria criteria query
     * @param pagesize number of elements per page
     * @param pageNumberStartingFromOne page number, starting from 1
     * @return an instance of Paginated result
     */
    @SuppressWarnings("unchecked")
    public static <T> PaginatedResult<T> search(Criteria criteria, int pagesize, int pageNumberStartingFromOne)
    {
        if (pagesize > 0)
        {
            criteria.setMaxResults(pagesize);
        }

        if (pageNumberStartingFromOne > 1 && pagesize > 0)
        {
            int firstresult = (pageNumberStartingFromOne - 1) * pagesize;
            log.debug("Setting first result {}", firstresult);
            criteria.setFirstResult(firstresult);
        }

        Collection<T> results = criteria.list();

        // reset firstresult for count
        criteria.setFirstResult(0);
        Number count = (Number) criteria.setProjection(Projections.count("id")).uniqueResult();

        if (count == null)
        {
            count = 0;
        }

        PaginatedResultImpl<T> result = new PaginatedResultImpl<T>(
            pagesize,
            pageNumberStartingFromOne,
            count.intValue());

        result.setResults(results);

        return result;
    }
}
