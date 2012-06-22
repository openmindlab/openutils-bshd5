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
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * @author gcatania
 */
@Entity
public class Address implements Cloneable
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 50)
    private String street;

    @Column(nullable = false, scale = 4)
    private Integer no;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 2)
    private String county;

    @Column(nullable = false, scale = 5)
    private Integer zipCode;

    public Address()
    {
    }

    public Address(String street, Integer no, String city, String county, Integer zipCode)
    {
        this.street = street;
        this.no = no;
        this.city = city;
        this.county = county;
        this.zipCode = zipCode;
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
     * @return the street
     */
    public String getStreet()
    {
        return street;
    }

    /**
     * @param street the street to set
     */
    public void setStreet(String street)
    {
        this.street = street;
    }

    /**
     * @return the no
     */
    public Integer getNo()
    {
        return no;
    }

    /**
     * @param no the no to set
     */
    public void setNo(Integer no)
    {
        this.no = no;
    }

    /**
     * @return the city
     */
    public String getCity()
    {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city)
    {
        this.city = city;
    }

    /**
     * @return the county
     */
    public String getCounty()
    {
        return county;
    }

    /**
     * @param county the county to set
     */
    public void setCounty(String county)
    {
        this.county = county;
    }

    /**
     * @return the zipCode
     */
    public Integer getZipCode()
    {
        return zipCode;
    }

    /**
     * @param zipCode the zipCode to set
     */
    public void setZipCode(Integer zipCode)
    {
        this.zipCode = zipCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((county == null) ? 0 : county.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((no == null) ? 0 : no.hashCode());
        result = prime * result + ((street == null) ? 0 : street.hashCode());
        result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
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
        if (!(obj instanceof Address))
        {
            return false;
        }
        Address other = (Address) obj;
        if (city == null)
        {
            if (other.city != null)
            {
                return false;
            }
        }
        else if (!city.equals(other.city))
        {
            return false;
        }
        if (county == null)
        {
            if (other.county != null)
            {
                return false;
            }
        }
        else if (!county.equals(other.county))
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
        if (no == null)
        {
            if (other.no != null)
            {
                return false;
            }
        }
        else if (!no.equals(other.no))
        {
            return false;
        }
        if (street == null)
        {
            if (other.street != null)
            {
                return false;
            }
        }
        else if (!street.equals(other.street))
        {
            return false;
        }
        if (zipCode == null)
        {
            if (other.zipCode != null)
            {
                return false;
            }
        }
        else if (!zipCode.equals(other.zipCode))
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
        return "Address [city="
            + city
            + ", county="
            + county
            + ", id="
            + id
            + ", no="
            + no
            + ", street="
            + street
            + ", zipCode="
            + zipCode
            + "]";
    }

    /** {@inheritDoc} */
    @Override
    public Address clone()
    {
        try
        {
            return (Address) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(e.getMessage());
        }
    }

}
