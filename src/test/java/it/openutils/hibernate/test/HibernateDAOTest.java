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

package it.openutils.hibernate.test;

import it.openutils.hibernate.test.dao.PersonDAO;
import it.openutils.hibernate.test.model.Address;
import it.openutils.hibernate.test.model.FullName;
import it.openutils.hibernate.test.model.Person;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author gcatania
 */
@ContextConfiguration(locations = "/spring-tests.xml")
public class HibernateDAOTest extends AbstractTestNGSpringContextTests
{

    @Autowired
    private PersonDAO personDAO;

    private Person fifteenYearsOld()
    {
        return new Person(
            new FullName("Troy", "Jones"),
            15,
            new Address("Long road", 15, "Smalltown", "MI", 14352),
            null);
    }

    /**
     * basic save/evict/get test.
     */
    @Test
    public void testSaveAndRetrievePerson()
    {
        Person person = fifteenYearsOld();
        Long savedId = personDAO.save(person);
        Assert.assertNotNull(savedId);
        Long personId = person.getId();
        Assert.assertEquals(savedId, personId);
        personDAO.evict(person);
        Person savedPerson = personDAO.get(personId);
        Assert.assertEquals(person, savedPerson);
    }

    /**
     * fails because of BSHD-5
     */
    @Test(enabled = false)
    public void testFindFilteredFirst()
    {
        personDAO.save(fifteenYearsOld());
        List<Person> found = personDAO.findFiltered(new Person(new FullName(null, "Jones"), null, null, null));
        Assert.assertNotNull(found);
        if (found.size() == 0)
        {
            Assert.fail("No persons found");
        }
        for (Person p : found)
        {
            System.out.println(p.toString());
        }
    }

}
