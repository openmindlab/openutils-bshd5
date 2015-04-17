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
package it.openutils.hibernate.test.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


/**
 * @author gcatania
 */
@Entity
public class Car implements Cloneable
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private CarModel model;

    @ManyToOne(cascade = CascadeType.ALL)
    private Owner owner;

    @Column
    private Calendar registrationDate;

    @Column
    private CurrencyAmount marketValue;

    @OneToMany(cascade = CascadeType.ALL)
    // join table appears problematic, using single join column instead
    @JoinColumn(name = "carId", nullable = false)
    private List<Sticker> stickers;

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
     * @param model the model to set
     */
    public void setModel(CarModel model)
    {
        this.model = model;
    }

    /**
     * @return the stickers
     */
    public List<Sticker> getStickers()
    {
        return stickers;
    }

    /**
     * @param stickers the stickers to set
     */
    public void setStickers(List<Sticker> stickers)
    {
        this.stickers = stickers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((marketValue == null) ? 0 : marketValue.hashCode());
        result = prime * result + ((registrationDate == null) ? 0 : registrationDate.hashCode());
        result = prime * result + ((stickers == null) ? 0 : stickers.hashCode());
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
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof Car))
        {
            return false;
        }
        Car other = (Car) obj;
        if (id == null)
        {
            if (other.id != null)
            {
                return false;
            }
        }
        else if (!id.equals(other.id))
        {
            return false;
        }
        if (marketValue == null)
        {
            if (other.marketValue != null)
            {
                return false;
            }
        }
        else if (!marketValue.equals(other.marketValue))
        {
            return false;
        }
        if (registrationDate == null)
        {
            if (other.registrationDate != null)
            {
                return false;
            }
        }
        else if (registrationDate.compareTo(other.registrationDate) != 0)
        {
            return false;
        }
        if (stickers == null)
        {
            if (other.stickers != null)
            {
                return false;
            }
        }
        else if (!stickers.equals(other.stickers))
        {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Car [id=").append(id).append(", model=").append(model)
        // .append(", registrationDate=")
        // .append(registrationDate)
            .append(", marketValue=")
            .append(marketValue)
            .append(", stickers=")
            .append(stickers)
            .append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public Car clone()
    {
        Car clone;
        try
        {
            clone = (Car) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(e.getMessage());
        }
        if (model != null)
        {
            clone.model = model.clone();
        }
        if (registrationDate != null)
        {
            clone.registrationDate = (Calendar) registrationDate.clone();
        }
        if (stickers != null)
        {
            clone.stickers = new ArrayList<Sticker>();
            for (Sticker s : stickers)
            {
                clone.stickers.add(s.clone());
            }
        }
        return clone;
    }

}
