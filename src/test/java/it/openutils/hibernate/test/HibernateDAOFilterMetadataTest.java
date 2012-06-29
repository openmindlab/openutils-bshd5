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

import static it.openutils.hibernate.test.EntityBuilder.bobsPrius;
import static it.openutils.hibernate.test.EntityBuilder.chuck;
import static it.openutils.hibernate.test.EntityBuilder.chucksPrius;
import static it.openutils.hibernate.test.EntityBuilder.prius;
import static it.openutils.hibernate.test.EntityBuilder.toyota;
import it.openutils.hibernate.example.FilterMetadata;
import it.openutils.hibernate.test.dao.CarDAO;
import it.openutils.hibernate.test.dao.CarMakerDAO;
import it.openutils.hibernate.test.dao.PersonDAO;
import it.openutils.hibernate.test.dao.StickerDAO;
import it.openutils.hibernate.test.model.Car;
import it.openutils.hibernate.test.model.Sticker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author gcatania
 */
@SuppressWarnings("deprecation")
@ContextConfiguration(locations = "/spring-tests.xml")
public class HibernateDAOFilterMetadataTest extends AbstractTransactionalTestNGSpringContextTests
{

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CarMakerDAO carMakerDAO;

    @Autowired
    private CarDAO carDAO;

    @Autowired
    private StickerDAO stickerDAO;

    @Test
    public void testFindFilterMetadataBasic()
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

        FilterMetadata fm = new FilterMetadata()
        {

            public void createFilter(Criteria criteria, String propertyName, Object propertyValue)
            {
                criteria.add(Restrictions.gt(propertyName, 18d));
                criteria.add(Restrictions.lt(propertyName, 22d));
            }
        };
        List<Sticker> found = stickerDAO.findFiltered(new Sticker(), Collections.singletonMap("height", fm));
        Assert.assertEquals(found.size(), 1);
        Assert.assertEquals(found.get(0), st1);
    }

    @Test
    public void testFindFilterMetadataOnChildProperty()
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

        Car bobsPrius = bobsPrius();
        bobsPrius.setStickers(Arrays.asList(st2.clone()));

        carDAO.save(chucksPrius);
        carDAO.evict(chucksPrius);
        carDAO.save(bobsPrius);
        carDAO.evict(bobsPrius);

        FilterMetadata fm = new FilterMetadata()
        {

            public void createFilter(Criteria criteria, String propertyName, Object propertyValue)
            {
                criteria.add(Restrictions.gt(propertyName, 18d));
                criteria.add(Restrictions.lt(propertyName, 22d));
            }
        };
        Car filter = new Car();
        filter.setStickers(Collections.singletonList(new Sticker()));
        List<Car> found = carDAO.findFiltered(filter, Collections.singletonMap("stickers.height", fm));
        Assert.assertEquals(found.size(), 1);
        Car actual = found.get(0);
        Assert.assertEquals(actual.getStickers().size(), 3);
        // full fledged equality fails because of Object.equals() on PersistentBag
        Assert.assertEquals(actual.getOwner().getName(), chucksPrius.getOwner().getName());
    }

}
