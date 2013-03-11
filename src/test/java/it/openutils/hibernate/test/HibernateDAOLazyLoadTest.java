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

import it.openutils.hibernate.test.dao.BarDAO;
import it.openutils.hibernate.test.dao.FooDAO;
import it.openutils.hibernate.test.model.Bar;
import it.openutils.hibernate.test.model.Foo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author gcatania
 */
@ContextConfiguration(locations = "/spring-tests.xml")
public class HibernateDAOLazyLoadTest extends AbstractTransactionalTestNGSpringContextTests
{

    @Autowired
    private FooDAO fooDAO;

    @Autowired
    private BarDAO barDAO;

    @BeforeClass
    protected final void preloadData()
    {
        executeSqlScript("/preload-data.sql", false);
    }

    private List<Foo> findFoo(String s, Bar bar)
    {
        Foo filter = new Foo();
        filter.setBar(bar);
        filter.setS(s);
        List<Foo> found = fooDAO.findFiltered(filter);
        return found;
    }

    private void testFind(long barId, String fooStr, Bar bar)
    {
        List<Foo> found = findFoo(fooStr, bar);
        Assert.assertEquals(found.size(), 1);
        Foo foo = found.get(0);
        Assert.assertEquals(foo.getS(), fooStr);
        Assert.assertEquals(foo.getBar().getId().longValue(), barId);
    }

    private void testDontFind(String fooStr, Bar bar)
    {
        List<Foo> found = findFoo(fooStr, bar);
        Assert.assertEquals(found.size(), 0);
    }

    private void testFindEager(long barId, String fooStr)
    {
        Bar bar = barDAO.get(barId);
        testFind(barId, fooStr, bar);
    }

    private void testFindLazy(long barId, String fooStr)
    {
        Bar bar = barDAO.load(barId);
        testFind(barId, fooStr, bar);
    }

    @Test
    public void testFindWithEagerParent1()
    {
        testFindEager(1L, "foo1_2");
    }

    @Test
    public void testFindWithEagerParent2()
    {
        testFindEager(1L, "fooX_X");
    }

    @Test
    public void testFindWithLazyParent1()
    {
        testFindLazy(1L, "foo1_2");
    }

    @Test
    public void testFindWithLazyParent2()
    {
        testFindLazy(1L, "fooX_X");
    }

    @Test
    public void testDontFindWithEagerParent()
    {
        long barId = 1L;
        String fooStr = "foo2_2";

        Bar bar1 = barDAO.get(barId);
        testDontFind(fooStr, bar1);
    }

    @Test
    public void testDontFindWithLazyParent()
    {
        long barId = 1L;
        String fooStr = "foo2_2";

        Bar bar1 = barDAO.load(barId);
        testDontFind(fooStr, bar1);
    }
}
