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

import java.util.ArrayList;
import java.util.Arrays;
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
    private CarMakerDAO carMakerDAO;

    @Autowired
    private CarDAO carDAO;

    @Autowired
    private StickerDAO stickerDAO;

    private static Person alice()
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

    private static Owner bob()
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

    private static Owner chuck()
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

    private static CarMaker toyota()
    {
        CarMaker toyota = new CarMaker();
        toyota.setCode("TYT");
        toyota.setName("Toyota");
        toyota.setCapitalization(new CurrencyAmount(12000, "YEN"));
        return toyota;
    }

    private static CarMaker fiat()
    {
        CarMaker fiat = new CarMaker();
        fiat.setCode("FIA");
        fiat.setName("Fabbrica Italiana Automobili Torino");
        fiat.setCapitalization(new CurrencyAmount(80000, "EUR"));
        return fiat;
    }

    private static CarModel prius(CarMaker toyota)
    {
        CarModel prius = new CarModel();
        prius.setName("Prius");
        prius.setMake(toyota);
        prius.setYear(Integer.valueOf(2008));

        List<CarModel> toyotaModels = toyota.getModels();
        if (toyotaModels == null)
        {
            toyotaModels = new ArrayList<CarModel>();
        }
        toyotaModels.add(prius);
        toyota.setModels(toyotaModels);
        return prius;
    }

    private static Designer priusDesigner(CarModel prius)
    {
        FullName fullName = new FullName("Ken", "Shiro");
        Calendar birthDate = new GregorianCalendar(1981, Calendar.OCTOBER, 16);
        Address address = new Address("Khan avenue", 6, "Nagato", "TK", 99867);
        Designer p = new Designer();
        p.setName(fullName);
        p.setBirthDate(birthDate);
        p.setFiscalAddress(address);
        p.setCurrentAddress(address);
        p.setDepartment("design");
        p.setEmployer(prius.getMake());
        p.setDesignedModels(Collections.singleton(prius));
        p.setGrossAnnualSalary(new CurrencyAmount(60000, "YEN"));
        p.setHipsterFactor(97);
        return p;
    }

    private static Car bobsPrius(Owner bob, CarModel prius)
    {
        Car bobsPrius = new Car();
        bobsPrius.setModel(prius);
        bobsPrius.setRegistrationDate(new GregorianCalendar(2010, Calendar.OCTOBER, 28));
        bobsPrius.setMarketValue(new CurrencyAmount(1700, "USD"));
        bobsPrius.setOwner(bob);
        bob.setCars(Collections.singleton(bobsPrius));
        return bobsPrius;
    }

    private static Car chucksPrius(Owner chuck, CarModel prius)
    {
        Car chucksPrius = new Car();
        chucksPrius.setModel(prius);
        chucksPrius.setRegistrationDate(new GregorianCalendar(2011, Calendar.DECEMBER, 13));
        chucksPrius.setMarketValue(new CurrencyAmount(5400, "USD"));
        chucksPrius.setOwner(chuck);
        chuck.setCars(Collections.singleton(chucksPrius));
        return chucksPrius;
    }

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
        // FIXME evicting breaks the test, there must be something wrong in the hibernate mapping configuration
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
        st2.setName("Object in the mirror are losing");
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
