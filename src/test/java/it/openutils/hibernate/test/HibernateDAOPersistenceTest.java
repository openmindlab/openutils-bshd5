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

package it.openutils.hibernate.test;

import static it.openutils.hibernate.test.EntityBuilder.alice;
import static it.openutils.hibernate.test.EntityBuilder.bob;
import static it.openutils.hibernate.test.EntityBuilder.bobsPrius;
import static it.openutils.hibernate.test.EntityBuilder.chuck;
import static it.openutils.hibernate.test.EntityBuilder.chucksPrius;
import static it.openutils.hibernate.test.EntityBuilder.fiat;
import static it.openutils.hibernate.test.EntityBuilder.prius;
import static it.openutils.hibernate.test.EntityBuilder.priusDesigner;
import static it.openutils.hibernate.test.EntityBuilder.toyota;
import it.openutils.hibernate.test.dao.CarDAO;
import it.openutils.hibernate.test.dao.CarMakerDAO;
import it.openutils.hibernate.test.dao.PersonDAO;
import it.openutils.hibernate.test.dao.StickerDAO;
import it.openutils.hibernate.test.model.Address;
import it.openutils.hibernate.test.model.Car;
import it.openutils.hibernate.test.model.CarMaker;
import it.openutils.hibernate.test.model.CarModel;
import it.openutils.hibernate.test.model.CurrencyAmount;
import it.openutils.hibernate.test.model.Designer;
import it.openutils.hibernate.test.model.FullName;
import it.openutils.hibernate.test.model.Owner;
import it.openutils.hibernate.test.model.Person;
import it.openutils.hibernate.test.model.Sticker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author gcatania
 */
@ContextConfiguration(locations = "/spring-tests.xml")
public class HibernateDAOPersistenceTest extends AbstractTransactionalTestNGSpringContextTests
{

    /*
     * TODO tests to perform: 1) find filtered with collection with zero, one or more elements 2) find filtered with
     * additional criteria 3) filter metadata support
     */

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CarMakerDAO carMakerDAO;

    @Autowired
    private CarDAO carDAO;

    @Autowired
    private StickerDAO stickerDAO;

    /**
     * basic save/evict/get test.
     */
    @Test
    public void testSaveAndRetrieveBasic()
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
    public void testSaveAndRetrieveWithCascade()
    {
        CarMaker toyota = toyota();
        CarModel prius = prius(toyota);

        carMakerDAO.save(toyota);

        Designer designer = priusDesigner(prius);

        personDAO.save(designer);
        // evicting breaks equals() on persistent bags
        // personDAO.evict(designer);

        // cannot use load() with entity inheritance, see https://forum.hibernate.org/viewtopic.php?p=2418875
        // Person reloadedDesigner = personDAO.load(designer.getId());

        Person filter = new Person();
        filter.setBirthDate(designer.getBirthDate());
        Person reloadedDesigner = personDAO.findFilteredFirst(filter);
        Assert.assertNotNull(reloadedDesigner);
        Assert.assertEquals(reloadedDesigner.getClass(), Designer.class);
        Designer rd = (Designer) reloadedDesigner;
        Assert.assertEquals(rd.getEmployer(), toyota);
        Assert.assertEquals(prius.getMake(), toyota);
        Assert.assertEquals(rd.getHipsterFactor(), 97);
        Assert.assertEquals(rd.getDesignedModels().iterator().next().getYear(), Integer.valueOf(2008));
    }

    @Test
    public void testBasicFind()
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
    public void testFindFiltered()
    {
        personDAO.save(alice());
        personDAO.save(bob());
        personDAO.save(chuck());
        Person filter = new Person();
        filter.setName(new FullName(null, "Kelso"));
        List<Person> found = personDAO.findFiltered(filter);

        Assert.assertEquals(found.size(), 1, "Invalid number of persons found.");
        Person actualBob = bob();
        Person expectedBob = found.get(0);
        Assert.assertEquals(expectedBob.getName(), actualBob.getName());
        Assert.assertEquals(expectedBob.getCurrentAddress().getStreet(), actualBob.getCurrentAddress().getStreet());
        Assert.assertEquals(expectedBob.getBirthDate(), actualBob.getBirthDate());
    }

    @Test
    public void testFindFilteredInheritance()
    {
        personDAO.save(alice());
        personDAO.save(bob());
        personDAO.save(chuck());
        Person filter = new Person();
        filter.setName(new FullName(null, "Kelso"));
        List<Person> found = personDAO.findFiltered(filter);

        Assert.assertEquals(found.size(), 1, "Invalid number of persons found.");
        Person expectedBob = found.get(0);
        Assert.assertEquals(expectedBob.getClass(), Owner.class, "Inheritanche check failed");
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

    @Test
    public void testFindFilteredChild()
    {
        CarMaker toyota = toyota();
        CarModel prius = prius(toyota);

        Owner bob = bob();
        bobsPrius(bob, prius);
        personDAO.save(bob);

        Owner chuck = chuck();
        chucksPrius(chuck, prius);
        personDAO.save(chuck);

        personDAO.save(priusDesigner(prius));

        Car carFilter = new Car();
        carFilter.setMarketValue(new CurrencyAmount(1700, "USD"));
        Owner filter = new Owner();
        filter.setCars(Collections.singleton(carFilter));
        List<Person> found = personDAO.findFiltered(filter);
        Assert.assertEquals(found.size(), 1);
        Person shouldBeBob = found.get(0);
        Assert.assertEquals(shouldBeBob.getName(), bob.getName());
    }

    /**
     * @see BSHD-11
     */
    @Test
    public void testFindFilteredById()
    {
        Person alice = alice();
        Long alicesId = personDAO.save(alice);
        personDAO.save(bob());
        Person filter = new Person();
        filter.setId(alicesId);
        List<Person> found = personDAO.findFiltered(filter);

        Assert.assertEquals(found.size(), 1, "Invalid number of persons found.");
        Assert.assertEquals(found.get(0), alice);
    }

    /**
     * @see BSHD-11
     */
    @Test
    public void testFindFilteredByNonDefaultId()
    {
        CarMaker toyota = toyota();
        carMakerDAO.save(toyota);
        carMakerDAO.save(fiat());

        CarMaker filter = new CarMaker();
        filter.setCode("TYT");
        List<CarMaker> found = carMakerDAO.findFiltered(filter);

        Assert.assertEquals(found.size(), 1, "Wrong number of car makers found");
        CarMaker foundCarMaker = found.get(0);
        Assert.assertEquals(foundCarMaker.getName(), "Toyota");
    }

    /**
     * @see BSHD-11
     */
    @Test
    public void testFindFilteredByChildId()
    {
        Owner bob = bob();
        CarMaker toyota = toyota();
        CarModel prius = prius(toyota);

        Car bobsPrius = bobsPrius(bob, prius);
        personDAO.save(bob);
        personDAO.save(alice());
        personDAO.save(chuck());
        personDAO.save(priusDesigner(prius));

        Car carFilter = new Car();
        carFilter.setId(bobsPrius.getId());
        Owner filter = new Owner();
        filter.setCars(Collections.singleton(carFilter));
        List<Person> found = personDAO.findFiltered(filter);
        Assert.assertEquals(found.size(), 1);
        Person shouldBeBob = found.get(0);
        Assert.assertEquals(shouldBeBob.getName(), bob.getName());
    }

    @Test
    public void testFindFilteredChildEntity()
    {
        Sticker st1 = new Sticker();
        st1.setName("Warning! Baby on board!");
        st1.setHeight(20d);
        st1.setWidth(10d);
        Sticker st2 = new Sticker();
        st2.setName("Objects in the mirror are losing");
        st2.setHeight(5d);
        st2.setWidth(10d);
        Sticker st3 = new Sticker();
        st3.setName("(tribal tattoo sticker)");
        st3.setHeight(35d);
        st3.setWidth(18d);

        Car chucksPrius = chucksPrius(chuck(), prius(toyota()));
        chucksPrius.setStickers(Arrays.asList(st1, st2, st3));
        carDAO.save(chucksPrius);
        carDAO.evict(chucksPrius);
        Sticker filter = new Sticker();
        filter.setWidth(10d);
        List<Sticker> found = stickerDAO.findFiltered(filter);
        Assert.assertEquals(found.size(), 2);
    }

    @Test
    public void testFindFilteredProperties()
    {
        Person alice = alice();
        Person bob = bob();
        personDAO.save(alice);
        personDAO.save(bob);
        personDAO.save(chuck());

        Address addressFilter = new Address();
        addressFilter.setCity("Smalltown");
        Person filter = new Person();
        filter.setCurrentAddress(addressFilter);

        List<Object> foundProperties = personDAO.findFilteredProperties(
            filter,
            Integer.MAX_VALUE,
            0,
            Collections.singletonList("fiscalAddress"));

        Assert.assertEquals(foundProperties.size(), 2);

        Assert.assertEquals(foundProperties.get(0), alice.getFiscalAddress());
        Assert.assertEquals(foundProperties.get(1), bob.getFiscalAddress());

        foundProperties = personDAO.findFilteredProperties(
            filter,
            Integer.MAX_VALUE,
            0,
            Arrays.asList("name", "birthDate"),
            Order.desc("name.givenName"));

        Assert.assertEquals(foundProperties.size(), 2);

        Object[] bobsProperties = (Object[]) foundProperties.get(0);
        Assert.assertEquals(bobsProperties[0], bob.getName());
        Assert.assertEquals(bobsProperties[1], bob.getBirthDate());
        Object[] alicesProperties = (Object[]) foundProperties.get(1);
        Assert.assertEquals(alicesProperties[0], alice.getName());
        Assert.assertEquals(alicesProperties[1], alice.getBirthDate());

    }

    /**
     * BSHD-15 check backref property accessors
     */
    @Test
    public void testOneToMany()
    {
        Sticker st1 = new Sticker();
        st1.setName("Warning! Baby on board!");
        st1.setHeight(20d);
        st1.setWidth(10d);
        Sticker st2 = new Sticker();
        st2.setName("Objects in the mirror are losing");
        st2.setHeight(5d);
        st2.setWidth(10d);

        Car chucksPrius = chucksPrius(chuck(), prius(toyota()));
        chucksPrius.setStickers(Collections.singletonList(st1));
        Long savedId = carDAO.save(chucksPrius);
        chucksPrius = carDAO.load(savedId);
        // evicting breaks equals() on persistent bags
        // carDAO.evict(chucksPrius);

        Car filter = chucksPrius.clone();
        // filter.setOwner(null);
        // filter.setModel(null);
        filter.setStickers(null);
        // filter.setRegistrationDate(null);
        // filter.setMarketValue(null);
        // filter.setId(null);

        Car found = carDAO.findFilteredFirst(filter);
        Assert.assertEquals(found, chucksPrius);

        // found = carDAO.findFilteredFirst(chucksPrius);
        // Assert.assertEquals(found, chucksPrius);
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
