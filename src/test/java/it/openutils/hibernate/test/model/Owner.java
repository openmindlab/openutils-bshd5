/**
 *
 * openutils base Spring-Hibernate DAO (http://www.openmindlab.com/lab/products/bshd5.html)
 *
 * Copyright(C) 2005-2011, Openmind S.r.l. http://www.openmindonline.it
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

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;


/**
 * @author gcatania
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "personType")
@DiscriminatorValue("owner")
public class Owner extends Person
{

    @OneToMany
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

}
