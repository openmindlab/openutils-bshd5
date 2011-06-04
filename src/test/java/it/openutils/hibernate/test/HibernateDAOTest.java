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

package it.openutils.hibernate.test;

import it.openutils.hibernate.test.dao.PersonDAO;
import it.openutils.hibernate.test.model.Address;
import it.openutils.hibernate.test.model.FullName;
import it.openutils.hibernate.test.model.Person;
import it.openutils.hibernate.test.model.Wish;

import java.util.Collections;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author gcatania
 */
@ContextConfiguration(locations = "/spring-tests.xml")
public class HibernateDAOTest extends AbstractTransactionalTestNGSpringContextTests
{

    /*
     * TODO tests to perform: 1) find filtered with collection with zero, one or more elements 2) find filtered with
     * additional criteria 3) filter metadata support
     */

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

    @Test
    public void testFindFilteredFirst()
    {
        personDAO.save(fifteenYearsOld());
        List<Person> found = personDAO.findFiltered(new Person(new FullName(null, "Jones"), null, null, null));
        Assert.assertTrue(found.size() > 0, "No persons found.");
    }

    @Test
    public void testExampleBasic()
    {
        personDAO.save(fifteenYearsOld());
        Person outsideFilter = fifteenYearsOld();
        outsideFilter.getName().setFamilyName("Mahoney");
        personDAO.save(outsideFilter);
        Person filter = new Person(new FullName(null, "Jones"), null, null, null);
        List<Person> foundByExample = personDAO.find(Collections.singletonList(Example.create(filter)));
        Assert.assertNotNull(foundByExample);
        Assert.assertEquals(foundByExample.size(), 1);
        Assert.assertEquals(foundByExample.get(0).getName().getFamilyName(), "Jones");

        List<Person> found = personDAO.findFiltered(filter);
        Assert.assertNotNull(found);
        Assert.assertEquals(found.size(), 1);
        Assert.assertEquals(found.get(0).getName().getFamilyName(), "Jones");
    }

    @Test
    public void testExampleAssociations()
    {
        Person fifteenYearsOld = fifteenYearsOld();
        fifteenYearsOld.setWish(new Wish(fifteenYearsOld, "because he's young"));
        personDAO.save(fifteenYearsOld);
        Person another = fifteenYearsOld();
        another.setWish(new Wish(another, "because he's a nerd"));
        personDAO.save(another);

        Person filter = new Person();
        filter.setWish(new Wish(null, "because he's young"));
        List<Person> foundByExample = personDAO.find(Collections.singletonList(Example.create(filter)));
        Hibernate.initialize(foundByExample);
        Assert.assertNotNull(foundByExample);
        Assert.assertEquals(foundByExample.size(), 2);

        List<Person> found = personDAO.findFiltered(filter);
        Hibernate.initialize(found);
        Assert.assertNotNull(found);
        Assert.assertEquals(found.size(), 1);
        Assert.assertEquals(found.get(0).getWish().getReason(), "because he's young");
    }

}
