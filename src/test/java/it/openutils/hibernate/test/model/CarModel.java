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

import javax.persistence.CascadeType;
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
public class CarModel implements Cloneable
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private CarMaker make;

    @Column
    private String name;

    @Column
    private Integer launchYear;

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
     * @return the make
     */
    public CarMaker getMake()
    {
        return make;
    }

    /**
     * @param make the make to set
     */
    public void setMake(CarMaker make)
    {
        this.make = make;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the launchYear
     */
    public Integer getLaunchYear()
    {
        return launchYear;
    }

    /**
     * @param launchYear the launchYear to set
     */
    public void setLaunchYear(Integer launchYear)
    {
        this.launchYear = launchYear;
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
        result = prime * result + ((make == null) ? 0 : make.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((launchYear == null) ? 0 : launchYear.hashCode());
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
        if (!(obj instanceof CarModel))
        {
            return false;
        }
        CarModel other = (CarModel) obj;
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
        if (make == null)
        {
            if (other.make != null)
            {
                return false;
            }
        }
        else if (!make.equals(other.make))
        {
            return false;
        }
        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }
        if (launchYear == null)
        {
            if (other.launchYear != null)
            {
                return false;
            }
        }
        else if (!launchYear.equals(other.launchYear))
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
        builder
            .append("CarModel [id=")
            .append(id)
            .append(", name=")
            .append(name)
            .append(", launchYear=")
            .append(launchYear)
            .append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public CarModel clone()
    {
        try
        {
            return (CarModel) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(e.getMessage());
        }
    }

}
