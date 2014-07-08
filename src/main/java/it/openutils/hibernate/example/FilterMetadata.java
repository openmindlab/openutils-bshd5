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
package it.openutils.hibernate.example;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * FilterMetadata can be used to alter how properties are handled.
 * @deprecated in favor of {@link ExampleTree#add(String, Criterion)} and
 * {@link ExampleTree#overridePropertyFilter(String, String, Criterion)}
 * @author Fabrizio Giustina
 * @version $Id: $
 */
@Deprecated
public interface FilterMetadata
{

    /**
     * A filter metadata that adds a LIKE condition.
     */
    FilterMetadata LIKE = new FilterMetadata()
    {

        private final Logger log = LoggerFactory.getLogger(FilterMetadata.class);

        /**
         * {@inheritDoc}
         */
        public void createFilter(Criteria crit, String propertyName, Object propertyValue)
        {
            crit.add(Restrictions.ilike(propertyName, (String) propertyValue, MatchMode.ANYWHERE));

            if (log.isDebugEnabled())
            {
                log.debug("crit.add(Expression.like(" + propertyName + ", '%" + propertyValue + "%' ))");
            }
        }
    };

    /**
     * A filter metadata that adds an EQUAL condition.
     */
    FilterMetadata EQUAL = new FilterMetadata()
    {

        private final Logger log = LoggerFactory.getLogger(FilterMetadata.class);

        /**
         * {@inheritDoc}
         */
        public void createFilter(Criteria crit, String propertyName, Object propertyValue)
        {
            if (log.isDebugEnabled())
            {
                log.debug("crit.add(Expression.eq(" + propertyName + ", " + propertyValue + "))");
            }

            crit.add(Restrictions.eq(propertyName, propertyValue));
        }
    };

    /**
     * The createFilter method can alter an existing criteria adding restrictions.
     * @param criteria the parent criteria
     * @param propertyName current property name
     * @param propertyValue property value
     */
    void createFilter(Criteria criteria, String propertyName, Object propertyValue);
}
