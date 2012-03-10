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

package it.openutils.hibernate.test.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;


/**
 * @author gcatania
 */
@Entity
@DiscriminatorValue("owner")
public class Owner extends Person
{

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    private Set<Car> cars;

    @Transient
    private CurrencyAmount totalValueOfCars;

    /**
     * @return the cars
     */
    public Set<Car> getCars()
    {
        return cars;
    }

    /**
     * @param cars the cars to set
     */
    public void setCars(Set<Car> cars)
    {
        this.cars = cars;
    }

    /**
     * @param totalValueOfCars the totalValueOfCars to set
     */
    public void setTotalValueOfCars(CurrencyAmount totalValueOfCars)
    {
        this.totalValueOfCars = totalValueOfCars;
    }

    /**
     * @return the totalValueOfCars
     */
    public CurrencyAmount getTotalValueOfCars()
    {
        return totalValueOfCars;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((totalValueOfCars == null) ? 0 : totalValueOfCars.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (!(obj instanceof Owner))
        {
            return false;
        }
        Owner other = (Owner) obj;
        if (totalValueOfCars == null)
        {
            if (other.totalValueOfCars != null)
            {
                return false;
            }
        }
        else if (!totalValueOfCars.equals(other.totalValueOfCars))
        {
            return false;
        }
        return true;
    }

}
