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

import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


/**
 * @author gcatania
 */
@Entity
@DiscriminatorValue("employee")
public class Employee extends Person
{

    @ManyToOne(cascade = CascadeType.ALL)
    private CarMaker employer;

    @Column
    private CurrencyAmount grossAnnualSalary;

    @Column
    private String qualification;

    @Column
    private String department;

    @Column
    private Calendar employedSince;

    /**
     * @return the employer
     */
    public CarMaker getEmployer()
    {
        return employer;
    }

    /**
     * @param employer the employer to set
     */
    public void setEmployer(CarMaker employer)
    {
        this.employer = employer;
    }

    /**
     * @return the grossAnnualSalary
     */
    public CurrencyAmount getGrossAnnualSalary()
    {
        return grossAnnualSalary;
    }

    /**
     * @param grossAnnualSalary the grossAnnualSalary to set
     */
    public void setGrossAnnualSalary(CurrencyAmount grossAnnualSalary)
    {
        this.grossAnnualSalary = grossAnnualSalary;
    }

    /**
     * @return the qualification
     */
    public String getQualification()
    {
        return qualification;
    }

    /**
     * @param qualification the qualification to set
     */
    public void setQualification(String qualification)
    {
        this.qualification = qualification;
    }

    /**
     * @return the department
     */
    public String getDepartment()
    {
        return department;
    }

    /**
     * @param department the department to set
     */
    public void setDepartment(String department)
    {
        this.department = department;
    }

    /**
     * @return the employedSince
     */
    public Calendar getEmployedSince()
    {
        return employedSince;
    }

    /**
     * @param employedSince the employedSince to set
     */
    public void setEmployedSince(Calendar employedSince)
    {
        this.employedSince = employedSince;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((department == null) ? 0 : department.hashCode());
        result = prime * result + ((employedSince == null) ? 0 : employedSince.hashCode());
        result = prime * result + ((employer == null) ? 0 : employer.hashCode());
        result = prime * result + ((grossAnnualSalary == null) ? 0 : grossAnnualSalary.hashCode());
        result = prime * result + ((qualification == null) ? 0 : qualification.hashCode());
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
        if (!super.equals(obj))
        {
            return false;
        }
        if (!(obj instanceof Employee))
        {
            return false;
        }
        Employee other = (Employee) obj;
        if (department == null)
        {
            if (other.department != null)
            {
                return false;
            }
        }
        else if (!department.equals(other.department))
        {
            return false;
        }
        if (employedSince == null)
        {
            if (other.employedSince != null)
            {
                return false;
            }
        }
        else if (employedSince.compareTo(other.employedSince) != 0)
        {
            return false;
        }
        if (employer == null)
        {
            if (other.employer != null)
            {
                return false;
            }
        }
        else if (!employer.equals(other.employer))
        {
            return false;
        }
        if (grossAnnualSalary == null)
        {
            if (other.grossAnnualSalary != null)
            {
                return false;
            }
        }
        else if (!grossAnnualSalary.equals(other.grossAnnualSalary))
        {
            return false;
        }
        if (qualification == null)
        {
            if (other.qualification != null)
            {
                return false;
            }
        }
        else if (!qualification.equals(other.qualification))
        {
            return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Employee clone()
    {
        Employee clone = (Employee) super.clone();
        if (employer != null)
        {
            clone.employer = employer.clone();
        }
        if (grossAnnualSalary != null)
        {
            clone.grossAnnualSalary = grossAnnualSalary.clone();
        }
        if (employedSince != null)
        {
            clone.employedSince = (Calendar) employedSince.clone();
        }
        return clone;
    }

}
