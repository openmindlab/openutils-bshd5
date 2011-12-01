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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;


/**
 * @author gcatania
 */
@Entity
@DiscriminatorValue("designer")
public class Designer extends Employee
{

    @Column
    private int hipsterFactor;

    @ManyToMany
    Set<CarModel> designedModels;

    /**
     * @return the hipsterFactor
     */
    public int getHipsterFactor()
    {
        return hipsterFactor;
    }

    /**
     * @param hipsterFactor the hipsterFactor to set
     */
    public void setHipsterFactor(int hipsterFactor)
    {
        this.hipsterFactor = hipsterFactor;
    }

    /**
     * @return the designedModels
     */
    public Set<CarModel> getDesignedModels()
    {
        return designedModels;
    }

    /**
     * @param designedModels the designedModels to set
     */
    public void setDesignedModels(Set<CarModel> designedModels)
    {
        this.designedModels = designedModels;
    }

}
