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

import it.openutils.dao.hibernate.HibernateDAOImpl;
import it.openutils.hibernate.test.model.Car;
import it.openutils.hibernate.test.model.Designer;
import it.openutils.hibernate.test.model.Employee;
import it.openutils.hibernate.test.model.Person;

import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author gcatania
 * @version $Id$
 */
public class HibernateDAOHierarchyTest
{

    /**
     * make the reference class available for testing
     */
    private static interface ReferenceClassAvailable
    {

        Class< ? > getReferenceClass();
    }

    private static class TestDaoImplParameterizedSimple extends HibernateDAOImpl<Car, Long>
        implements
        ReferenceClassAvailable
    {

        @Override
        public Class<Car> getReferenceClass()
        {
            return super.getReferenceClass();
        }
    }

    private static class TestDaoImplParameterizedInheriting extends TestDaoImplParameterizedSimple
    {
    }

    private static class TestDaoImplParameterizedOverwriting extends HibernateDAOImpl<Person, Long>
        implements
        ReferenceClassAvailable
    {

        private TestDaoImplParameterizedOverwriting()
        {
            super();
            // cheat with raw types to make the method invocation compile
            setReferenceClass((Class) Employee.class);
        }

        @Override
        public Class<Person> getReferenceClass()
        {
            return super.getReferenceClass();
        }
    }

    @SuppressWarnings("rawtypes")
    private static class TestDaoImplRawSimple extends HibernateDAOImpl implements ReferenceClassAvailable
    {

        protected TestDaoImplRawSimple()
        {
            super();
        }

        @SuppressWarnings("unchecked")
        protected TestDaoImplRawSimple(Class< ? > referenceClass)
        {
            super(referenceClass);
        }

        @Override
        public Class< ? > getReferenceClass()
        {
            return super.getReferenceClass();
        }
    }

    private static class TestDaoImplRawInheriting extends TestDaoImplRawSimple
    {

        private TestDaoImplRawInheriting()
        {
            super(Designer.class);
        }
    }

    /**
     * tests a dao implementation in which the reference class has been inferred from the parameterized types of the
     * class itself
     */
    @Test
    public void testInstantiateParameterizedSimple()
    {
        assertReferenceClassEquals(new TestDaoImplParameterizedSimple(), Car.class);
    }

    /**
     * tests a dao implementation in which the reference class has been inferred from the parameterized types of a
     * superclass
     */
    @Test
    public void testInstantiateParameterizedInheriting()
    {
        assertReferenceClassEquals(new TestDaoImplParameterizedInheriting(), Car.class);
    }

    /**
     * tests a dao implementation in which the reference class has been inferred from the parameterized types of the
     * class itself but overwritten by the implementor by calling setReferenceClass() afterwards
     */
    @Test
    public void testInstantiateParameterizedOverwriting()
    {
        assertReferenceClassEquals(new TestDaoImplParameterizedOverwriting(), Employee.class);
    }

    /**
     * tests the anomalous situation in which reference class has not been set (in production code this dao won't work)
     */
    @Test
    public void testInstantiateRawSimple()
    {
        assertReferenceClassEquals(new TestDaoImplRawSimple(), null);
    }

    /**
     * tests a non-parameterized dao implementation in which the reference class has been set by hand
     */
    @Test
    public void testInstantiateRawInheriting()
    {
        assertReferenceClassEquals(new TestDaoImplRawInheriting(), Designer.class);
    }

    private static void assertReferenceClassEquals(ReferenceClassAvailable dao, Class< ? > expectedReferenceClass)
    {
        Assert.assertEquals(dao.getReferenceClass(), expectedReferenceClass);
    }

}
