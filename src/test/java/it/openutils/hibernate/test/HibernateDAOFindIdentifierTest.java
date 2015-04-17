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
package it.openutils.hibernate.test;

import it.openutils.hibernate.example.ExampleTree;
import it.openutils.hibernate.test.dao.FooDAO;
import it.openutils.hibernate.test.model.Bar;
import it.openutils.hibernate.test.model.Foo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author gcatania
 */
@ContextConfiguration(locations = "/spring-tests.xml")
public class HibernateDAOFindIdentifierTest extends AbstractTransactionalTestNGSpringContextTests
{

    @Autowired
    private FooDAO fooDAO;

    @BeforeClass
    protected final void preloadData()
    {
        executeSqlScript("/preload-data.sql", false);
    }

    @AfterClass
    protected final void cleanupData()
    {
        super.deleteFromTables("foo", "bar");
    }

    private List<Foo> searchFoos(Long barId, String barStr, Long fooId, String fooStr)
    {
        Bar barFilter = new Bar();
        barFilter.setId(barId);
        barFilter.setS(barStr);
        Foo fooFilter = new Foo();
        fooFilter.setId(fooId);
        fooFilter.setBar(barFilter);
        fooFilter.setS(fooStr);
        return fooDAO.findFiltered(fooFilter);
    }

    private Foo findFoo(Long barId, String barStr, Long fooId, String fooStr)
    {
        List<Foo> found = searchFoos(barId, barStr, fooId, fooStr);
        Assert.assertEquals(found.size(), 1);
        return found.get(0);
    }

    private void dontFindFoo(Long barId, String barStr, Long fooId, String fooStr)
    {
        List<Foo> found = searchFoos(barId, barStr, fooId, fooStr);
        Assert.assertEquals(found.size(), 0);
    }

    @Test
    public void testFindWithParentId()
    {
        Foo foundFoo = findFoo(1L, null, null, "fooX_X");
        Assert.assertEquals(foundFoo.getId().longValue(), 3L);
    }

    @Test
    public void testFindWithParentProperty()
    {
        Foo foundFoo = findFoo(null, "bar1", null, "fooX_X");
        Assert.assertEquals(foundFoo.getId().longValue(), 3L);
    }

    @Test
    public void testDontFindWithParentId()
    {
        dontFindFoo(1L, null, null, "foo2_1");
    }

    @Test
    public void testDontFindWithParentProperty()
    {
        dontFindFoo(null, "bar1", null, "foo2_1");
    }

    @Test
    public void testFindWithBothParentIdAndPropertyKeepingInMindTheMagicFlagIsDisabled()
    {
        List<Foo> foundFoos = searchFoos(2L, "bar1", null, null);
        Assert.assertEquals(foundFoos.size(), 3);
        for (Foo foo : foundFoos)
        {
            Assert.assertEquals(foo.getBar().getId().longValue(), 2L);
        }
    }

    @Test
    public void testDontFindWithBothParentIdAndPropertyKeepingInMindTheMagicFlagIsEnabled()
    {
        Bar barFilter = new Bar();
        barFilter.setId(2L);
        barFilter.setS("bar1");
        Foo fooFilter = new Foo();
        fooFilter.setBar(barFilter);
        ExampleTree et = new ExampleTree(fooFilter);
        et.enableJointPropertyAndIdentifierFiltering();
        List<Foo> foundFoos = fooDAO.findFiltered(et);
        Assert.assertEquals(foundFoos.size(), 0);
    }

}
