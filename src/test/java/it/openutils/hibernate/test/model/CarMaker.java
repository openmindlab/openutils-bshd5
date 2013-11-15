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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;


/**
 * @author gcatania
 */
@Entity
public class CarMaker implements Cloneable
{

    @Id
    private String code;

    @Column
    private String name;

    @Column
    private CurrencyAmount capitalization;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "make")
    private List<CarModel> models;

    /**
     * @return the code
     */
    public String getCode()
    {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code)
    {
        this.code = code;
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
     * @return the capitalization
     */
    public CurrencyAmount getCapitalization()
    {
        return capitalization;
    }

    /**
     * @param capitalization the capitalization to set
     */
    public void setCapitalization(CurrencyAmount capitalization)
    {
        this.capitalization = capitalization;
    }

    /**
     * @return the models
     */
    public List<CarModel> getModels()
    {
        return models;
    }

    /**
     * @param models the models to set
     */
    public void setModels(List<CarModel> models)
    {
        this.models = models;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((capitalization == null) ? 0 : capitalization.hashCode());
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if (!(obj instanceof CarMaker))
        {
            return false;
        }
        CarMaker other = (CarMaker) obj;
        if (capitalization == null)
        {
            if (other.capitalization != null)
            {
                return false;
            }
        }
        else if (!capitalization.equals(other.capitalization))
        {
            return false;
        }
        if (code == null)
        {
            if (other.code != null)
            {
                return false;
            }
        }
        else if (!code.equals(other.code))
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
            .append("CarMaker [id=")
            .append(code)
            .append(", name=")
            .append(name)
            .append(", capitalization=")
            .append(capitalization)
            .append(", models=")
            .append(models)
            .append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public CarMaker clone()
    {
        CarMaker clone;
        try
        {
            clone = (CarMaker) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(e.getMessage());
        }
        if (capitalization != null)
        {
            clone.capitalization = capitalization.clone();
        }
        if (models != null)
        {
            clone.models = new ArrayList<CarModel>();
            for (CarModel m : models)
            {
                clone.models.add(m.clone());
            }
        }
        return clone;
    }

}
