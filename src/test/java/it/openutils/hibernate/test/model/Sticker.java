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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * @author gcatania
 */
@Entity
public class Sticker implements Cloneable
{

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column
    private Double height;

    @Column
    private Double width;

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
     * @return the height
     */
    public Double getHeight()
    {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(Double height)
    {
        this.height = height;
    }

    /**
     * @return the width
     */
    public Double getWidth()
    {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(Double width)
    {
        this.width = width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((height == null) ? 0 : height.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((width == null) ? 0 : width.hashCode());
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
        if (!(obj instanceof Sticker))
        {
            return false;
        }
        Sticker other = (Sticker) obj;
        if (height == null)
        {
            if (other.height != null)
            {
                return false;
            }
        }
        else if (!height.equals(other.height))
        {
            return false;
        }
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
        if (width == null)
        {
            if (other.width != null)
            {
                return false;
            }
        }
        else if (!width.equals(other.width))
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
            .append("Sticker [id=")
            .append(id)
            .append(", name=")
            .append(name)
            .append(", height=")
            .append(height)
            .append(", width=")
            .append(width)
            .append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public Sticker clone()
    {
        try
        {
            return (Sticker) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(e.getMessage());
        }
    }
}
