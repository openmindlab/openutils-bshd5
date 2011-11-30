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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;


/**
 * @author gcatania
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "personType")
public class Person
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Embedded
    private FullName name;

    @Column(nullable = false)
    private Calendar birthDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    private Address currentAddress;

    @ManyToOne(cascade = CascadeType.ALL)
    private Address fiscalAddress;

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
    public FullName getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(FullName name)
    {
        this.name = name;
    }

    /**
     * @return the birthDate
     */
    public Calendar getBirthDate()
    {
        return birthDate;
    }

    /**
     * @param birthDate the birthDate to set
     */
    public void setBirthDate(Calendar birthDate)
    {
        this.birthDate = birthDate;
    }

    /**
     * @return the currentAddress
     */
    public Address getCurrentAddress()
    {
        return currentAddress;
    }

    /**
     * @param currentAddress the currentAddress to set
     */
    public void setCurrentAddress(Address currentAddress)
    {
        this.currentAddress = currentAddress;
    }

    /**
     * @return the fiscalAddress
     */
    public Address getFiscalAddress()
    {
        return fiscalAddress;
    }

    /**
     * @param fiscalAddress the fiscalAddress to set
     */
    public void setFiscalAddress(Address fiscalAddress)
    {
        this.fiscalAddress = fiscalAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
        result = prime * result + ((currentAddress == null) ? 0 : currentAddress.hashCode());
        result = prime * result + ((fiscalAddress == null) ? 0 : fiscalAddress.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        if (getClass() != obj.getClass())
        {
            return false;
        }
        Person other = (Person) obj;
        if (birthDate == null)
        {
            if (other.birthDate != null)
            {
                return false;
            }
        }
        else if (!birthDate.equals(other.birthDate))
        {
            return false;
        }
        if (currentAddress == null)
        {
            if (other.currentAddress != null)
            {
                return false;
            }
        }
        else if (!currentAddress.equals(other.currentAddress))
        {
            return false;
        }
        if (fiscalAddress == null)
        {
            if (other.fiscalAddress != null)
            {
                return false;
            }
        }
        else if (!fiscalAddress.equals(other.fiscalAddress))
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
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "Person [currentAddress="
            + currentAddress
            + ", fiscalAddress="
            + fiscalAddress
            + ", id="
            + id
            + ", name="
            + name
            + "]";
    }

}
