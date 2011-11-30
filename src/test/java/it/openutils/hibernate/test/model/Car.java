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

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


/**
 * @author gcatania
 */
@Entity
public class Car
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private final CarModel model;

    @ManyToOne
    private final CarMaker maker;

    @ManyToOne
    private Owner owner;

    @Column
    private Calendar registrationDate;

    @Column
    private CurrencyAmount marketValue;

    public Car(Calendar registrationDate, CarModel model, CarMaker maker)
    {
        this.model = model;
        this.maker = maker;
    }

    /**
     * @return the id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return the owner
     */
    public Owner getOwner()
    {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(Owner owner)
    {
        this.owner = owner;
    }

    /**
     * @return the registrationDate
     */
    public Calendar getRegistrationDate()
    {
        return registrationDate;
    }

    /**
     * @param registrationDate the registrationDate to set
     */
    public void setRegistrationDate(Calendar registrationDate)
    {
        this.registrationDate = registrationDate;
    }

    /**
     * @return the marketValue
     */
    public CurrencyAmount getMarketValue()
    {
        return marketValue;
    }

    /**
     * @param marketValue the marketValue to set
     */
    public void setMarketValue(CurrencyAmount marketValue)
    {
        this.marketValue = marketValue;
    }

    /**
     * @return the model
     */
    public CarModel getModel()
    {
        return model;
    }

    /**
     * @return the maker
     */
    public CarMaker getMaker()
    {
        return maker;
    }

}
