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

package it.openutils.hibernate.selectors;

import java.io.Serializable;

import org.hibernate.criterion.Example.PropertySelector;
import org.hibernate.property.BackrefPropertyAccessor;
import org.hibernate.type.Type;


/**
 * utility selector to avoid class cast exceptions on {@link BackrefPropertyAccessor.UNKNOWN}
 * @see BSHD-15
 * @author gcatania
 * @version $Id$
 */
public class ExcludeBackrefPropertySelector implements PropertySelector, Serializable
{

    private static final long serialVersionUID = -2803322309158823550L;

    private final PropertySelector selector;

    public ExcludeBackrefPropertySelector(PropertySelector selector)
    {
        if (selector == null)
        {
            throw new NullPointerException("Null selector.");
        }
        this.selector = selector;
    }

    public boolean include(Object propertyValue, String propertyName, Type type)
    {
        if (BackrefPropertyAccessor.UNKNOWN.equals(propertyValue))
        {
            return false;
        }
        return selector.include(propertyValue, propertyName, type);
    }

}
