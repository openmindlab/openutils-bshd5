/**
 *
 * openutils base Spring-Hibernate DAO for java 5.0 (http://www.openmindlab.com/lab/products/bshd5.html)
 *
 * Copyright(C) null-2010, Openmind S.r.l. http://www.openmindonline.it
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
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;


/**
 * @author gcatania
 */
@Entity
public class Person
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Embedded
    private FullName name;

    @Column(nullable = false, scale = 3)
    private Integer age;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    private Address currentAddress;

    @ManyToOne(cascade = CascadeType.ALL)
    private Address fiscalAddress;

    @OneToOne
    private Wish wish;

    public Person()
    {
    }

    public Person(FullName name, Integer age, Address currentAddress, Address fiscalAddress)
    {
        this.name = name;
        this.age = age;
        this.currentAddress = currentAddress;
        this.fiscalAddress = fiscalAddress;
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
     * @return the wish
     */
    public Wish getWish()
    {
        return wish;
    }

    /**
     * @param wish the wish to set
     */
    public void setWish(Wish wish)
    {
        this.wish = wish;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((age == null) ? 0 : age);
        result = prime * result + ((currentAddress == null) ? 0 : currentAddress.hashCode());
        result = prime * result + ((fiscalAddress == null) ? 0 : fiscalAddress.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((wish == null) ? 0 : wish.hashCode());
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
        if (age == null)
        {
            if (other.age != null)
            {
                return false;
            }
        }
        else if (!age.equals(other.age))
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
        if (wish == null)
        {
            if (other.wish != null)
            {
                return false;
            }
        }
        else if (!wish.equals(other.wish))
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
        return "Person [age="
            + age
            + ", currentAddress="
            + currentAddress
            + ", fiscalAddress="
            + fiscalAddress
            + ", id="
            + id
            + ", name="
            + name
            + ", wish="
            + wish
            + "]";
    }

}
