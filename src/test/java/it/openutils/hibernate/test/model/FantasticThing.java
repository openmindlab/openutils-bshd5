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

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;


/**
 * @author gcatania
 */
@Entity
public class FantasticThing
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String thingName;

    private Chance chanceOfAchievement;

    @ManyToMany(mappedBy = "objectsOfWish", cascade = CascadeType.ALL)
    private Set<Wish> wishes;

    public FantasticThing()
    {
    }

    public FantasticThing(String thingName, Chance chanceOfAchievement)
    {
        this.thingName = thingName;
        this.chanceOfAchievement = chanceOfAchievement;
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
     * @return the thingName
     */
    public String getThingName()
    {
        return thingName;
    }

    /**
     * @param thingName the thingName to set
     */
    public void setThingName(String thingName)
    {
        this.thingName = thingName;
    }

    /**
     * @return the chanceOfAchievement
     */
    public Chance getChanceOfAchievement()
    {
        return chanceOfAchievement;
    }

    /**
     * @param chanceOfAchievement the chanceOfAchievement to set
     */
    public void setChanceOfAchievement(Chance chanceOfAchievement)
    {
        this.chanceOfAchievement = chanceOfAchievement;
    }

    /**
     * @return the wishes
     */
    public Set<Wish> getWishes()
    {
        return wishes;
    }

    /**
     * @param wishes the wishes to set
     */
    public void setWishes(Set<Wish> wishes)
    {
        this.wishes = wishes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((chanceOfAchievement == null) ? 0 : chanceOfAchievement.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((thingName == null) ? 0 : thingName.hashCode());
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
        FantasticThing other = (FantasticThing) obj;
        if (chanceOfAchievement == null)
        {
            if (other.chanceOfAchievement != null)
            {
                return false;
            }
        }
        else if (!chanceOfAchievement.equals(other.chanceOfAchievement))
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
        if (thingName == null)
        {
            if (other.thingName != null)
            {
                return false;
            }
        }
        else if (!thingName.equals(other.thingName))
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
        return "FantasticThing [chanceOfAchievement="
            + chanceOfAchievement
            + ", id="
            + id
            + ", thingName="
            + thingName
            + "]";
    }

}
