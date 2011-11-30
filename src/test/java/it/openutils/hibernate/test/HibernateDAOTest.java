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

import it.openutils.hibernate.test.dao.CarDAO;
import it.openutils.hibernate.test.dao.PersonDAO;
import it.openutils.hibernate.test.model.Address;
import it.openutils.hibernate.test.model.Car;
import it.openutils.hibernate.test.model.CarMaker;
import it.openutils.hibernate.test.model.CarModel;
import it.openutils.hibernate.test.model.CurrencyAmount;
import it.openutils.hibernate.test.model.FullName;
import it.openutils.hibernate.test.model.Owner;
import it.openutils.hibernate.test.model.Person;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

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
     * additional criteria 3) filter metadata support 4) find filtered with id 5) find filtered with backref
     */

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CarDAO carDAO;

    private Person alice()
    {
        FullName fullName = new FullName("Alice", "McBeal");
        Calendar birthDate = new GregorianCalendar(1970, Calendar.MARCH, 7);
        Address address = new Address("Long road", 15, "Smalltown", "MI", 14352);
        Person p = new Person();
        p.setName(fullName);
        p.setBirthDate(birthDate);
        p.setFiscalAddress(address);
        p.setCurrentAddress(address);
        return p;
    }

    private Owner bob()
    {
        FullName fullName = new FullName("Bob", "Kelso");
        Calendar birthDate = new GregorianCalendar(1950, Calendar.MARCH, 7);
        Address address = new Address("Sacred Heart Lane", 3, "Smalltown", "CA", 11243);
        Owner o = new Owner();
        o.setName(fullName);
        o.setBirthDate(birthDate);
        o.setFiscalAddress(address);
        o.setCurrentAddress(address);
        return o;
    }

    private Owner chuck()
    {
        FullName fullName = new FullName("Chuck", "Palahniuk");
        Calendar birthDate = new GregorianCalendar(1962, Calendar.FEBRUARY, 21);
        Address address = new Address("Awesome Street", 2, "Pasco", "WA", 13121);
        Owner p = new Owner();
        p.setName(fullName);
        p.setBirthDate(birthDate);
        p.setFiscalAddress(address);
        p.setCurrentAddress(address);
        return p;
    }

    /**
     * basic save/evict/get test.
     */
    @Test
    public void testSaveAndRetrievePerson()
    {
        Person person = alice();
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
        Person alice = alice();
        personDAO.save(alice);
        Person filter = new Person();
        filter.setName(new FullName(null, "McBeal"));
        List<Person> found = personDAO.findFiltered(filter);

        Assert.assertEquals(found.size(), 1, "No persons found.");
        Assert.assertEquals(found.get(0), alice);
    }

    @Test
    public void testFindFilteredById()
    {
        Person alice = alice();
        Long savedId = personDAO.save(alice);
        Person filter = new Person();
        filter.setId(savedId);
        List<Person> found = personDAO.findFiltered(filter);

        Assert.assertEquals(found.size(), 1, "No persons found.");
        Assert.assertEquals(found.get(0), alice);
    }

    /*
     * disabled: filter by id on child objects isn't working yet (see
     * http://docs.jboss.org/hibernate/core/3.6/reference/en-US/html/querycriteria.html#querycriteria-examples and
     * https://forum.hibernate.org/viewtopic.php?f=9&t=1004833&view=next )
     */
    @Test(enabled = false)
    public void testFindFilteredByChildId()
    {
        Owner bob = bob();
        CarMaker toyota = new CarMaker();
        toyota.setName("Toyota");
        toyota.setCapitalization(new CurrencyAmount(12000, "YEN"));
        CarModel prius = new CarModel();
        prius.setName("Prius");
        prius.setYear(Integer.valueOf(2008));
        toyota.setModels(Collections.singletonList(prius));

        Car bobsPrius = new Car(new GregorianCalendar(2010, Calendar.OCTOBER, 12), prius, toyota);
        bob.setCars(Collections.singleton(bobsPrius));
        personDAO.save(bob);

        Owner filter = new Owner();
        Car carFilter = new Car(null, null, null);
        carFilter.setId(bobsPrius.getId());
        filter.setCars(Collections.singleton(carFilter));
        Person found = personDAO.findFilteredFirst(filter);
        Assert.assertEquals(found.getName(), bob.getName());
    }

    @Test
    public void testExampleBasic()
    {
        personDAO.save(alice());
        Person outsideFilter = alice();
        outsideFilter.getName().setFamilyName("Mahoney");
        personDAO.save(outsideFilter);
        Person filter = new Person();
        filter.setName(new FullName(null, "McBeal"));
        List<Person> foundByExample = personDAO.find(Collections.singletonList(Example.create(filter)));
        Assert.assertNotNull(foundByExample);
        Assert.assertEquals(foundByExample.size(), 1);
        Assert.assertEquals(foundByExample.get(0).getName().getFamilyName(), "McBeal");

        List<Person> found = personDAO.findFiltered(filter);
        Assert.assertNotNull(found);
        Assert.assertEquals(found.size(), 1);
        Assert.assertEquals(found.get(0).getName().getFamilyName(), "McBeal");
    }

    // @Test
    // public void testExampleAssociations()
    // {
    // Person fifteenYearsOld = fifteenYearsOld();
    // fifteenYearsOld.setWish(new Wish(fifteenYearsOld, "because he's young"));
    // personDAO.save(fifteenYearsOld);
    // Person another = fifteenYearsOld();
    // another.setWish(new Wish(another, "because he's a nerd"));
    // personDAO.save(another);
    //
    // Person filter = new Person();
    // filter.setWish(new Wish(null, "because he's young"));
    // List<Person> foundByExample = personDAO.find(Collections.singletonList(Example.create(filter)));
    // Hibernate.initialize(foundByExample);
    // Assert.assertNotNull(foundByExample);
    // Assert.assertEquals(foundByExample.size(), 2);
    //
    // List<Person> found = personDAO.findFiltered(filter);
    // Hibernate.initialize(found);
    // Assert.assertNotNull(found);
    // Assert.assertEquals(found.size(), 1);
    // Assert.assertEquals(found.get(0).getWish().getReason(), "because he's young");
    // }

}
