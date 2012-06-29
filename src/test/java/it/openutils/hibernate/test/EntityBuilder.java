/**
 * Copyright (c) Energeya LLC.  All rights reserved. http://www.energeya.com
 */
package it.openutils.hibernate.test;

import it.openutils.hibernate.test.model.Address;
import it.openutils.hibernate.test.model.Car;
import it.openutils.hibernate.test.model.CarMaker;
import it.openutils.hibernate.test.model.CarModel;
import it.openutils.hibernate.test.model.CurrencyAmount;
import it.openutils.hibernate.test.model.Designer;
import it.openutils.hibernate.test.model.FullName;
import it.openutils.hibernate.test.model.Owner;
import it.openutils.hibernate.test.model.Person;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * @author gcatania
 * @version $Id$
 */
public final class EntityBuilder
{

    private EntityBuilder()
    {
    }

    public static Person alice()
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

    public static Owner bob()
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

    public static Owner chuck()
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

    public static CarMaker toyota()
    {
        CarMaker toyota = new CarMaker();
        toyota.setCode("TYT");
        toyota.setName("Toyota");
        toyota.setCapitalization(new CurrencyAmount(12000, "YEN"));
        return toyota;
    }

    public static CarMaker fiat()
    {
        CarMaker fiat = new CarMaker();
        fiat.setCode("FIA");
        fiat.setName("Fabbrica Italiana Automobili Torino");
        fiat.setCapitalization(new CurrencyAmount(80000, "EUR"));
        return fiat;
    }

    public static CarModel prius(CarMaker toyota)
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

    public static Designer priusDesigner(CarModel prius)
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

    public static Car bobsPrius(Owner bob, CarModel prius)
    {
        Car bobsPrius = new Car();
        bobsPrius.setModel(prius);
        bobsPrius.setRegistrationDate(new GregorianCalendar(2010, Calendar.OCTOBER, 28));
        bobsPrius.setMarketValue(new CurrencyAmount(1700, "USD"));
        bobsPrius.setOwner(bob);
        bob.setCars(Collections.singleton(bobsPrius));
        return bobsPrius;
    }

    public static Car chucksPrius(Owner chuck, CarModel prius)
    {
        Car chucksPrius = new Car();
        chucksPrius.setModel(prius);
        chucksPrius.setRegistrationDate(new GregorianCalendar(2011, Calendar.DECEMBER, 13));
        chucksPrius.setMarketValue(new CurrencyAmount(5400, "USD"));
        chucksPrius.setOwner(chuck);
        chuck.setCars(Collections.singleton(chucksPrius));
        return chucksPrius;
    }

}
