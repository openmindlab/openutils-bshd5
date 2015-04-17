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
package it.openutils.hibernate.example;

import static it.openutils.hibernate.example.ExampleTreeUtils.addIdentifierRestriction;
import static it.openutils.hibernate.example.ExampleTreeUtils.alreadyWalked;
import static it.openutils.hibernate.example.ExampleTreeUtils.append;
import static it.openutils.hibernate.example.ExampleTreeUtils.getClassMetadata;
import static it.openutils.hibernate.example.ExampleTreeUtils.getPath;
import static it.openutils.hibernate.example.ExampleTreeUtils.getValueFromCollection;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.mockito.ArgumentMatcher;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author gcatania
 * @version $Id$
 */
public class ExampleTreeUtilsTest
{

    @Test
    public void testAlreadyWalked()
    {
        Assert.assertFalse(alreadyWalked(new String[0], "prop"));
        Assert.assertFalse(alreadyWalked(new String[]{"prop" }, "prop"));
        Assert.assertFalse(alreadyWalked(new String[]{"prop", "childProp" }, "childProp"));
        Assert.assertFalse(alreadyWalked(new String[]{"prop", "childProp", "grandchildProp" }, "childProp"));
        Assert.assertFalse(alreadyWalked(new String[]{"prop", "childProp", "grandchildProp" }, "grandchildProp"));
        Assert.assertFalse(alreadyWalked(new String[]{"prop", "childProp", "parentProp" }, "childProp"));
        Assert.assertFalse(alreadyWalked(new String[]{"prop", "childProp", "parentProp", "childProp" }, "childProp"));
        Assert.assertFalse(alreadyWalked(new String[]{"a", "b", "c", "b" }, "b"));
        Assert.assertTrue(alreadyWalked(new String[]{"a", "b", "a", "b" }, "a"));
    }

    @Test
    public void testGetValueFromColl()
    {
        Assert.assertNull(getValueFromCollection("prop", null));
        Assert.assertNull(getValueFromCollection("prop", Collections.emptySet()));
        Assert.assertNull(getValueFromCollection("prop", Collections.emptyList()));
        Assert.assertNull(getValueFromCollection("prop", new Object[0]));
        Assert.assertNull(getValueFromCollection("prop", new double[0]));

        Object singleResult = new Object();
        Assert.assertEquals(getValueFromCollection("prop", Collections.singleton(singleResult)), singleResult);
        Assert.assertEquals(getValueFromCollection("prop", Collections.singletonList(singleResult)), singleResult);
        Assert.assertEquals(getValueFromCollection("prop", new Object[]{singleResult }), singleResult);
        Assert.assertEquals(getValueFromCollection("prop", new double[]{3d }), 3d);

        try
        {
            getValueFromCollection("wasabi", Arrays.asList("a", "b"));
            Assert.fail("Should have thrown exception");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(e.getMessage().contains("wasabi"), "exception message did not report property name.\n"
                + e.getMessage());
        }
        try
        {
            getValueFromCollection("wasabi", new String[]{"a", "b" });
            Assert.fail("Should have thrown exception");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(e.getMessage().contains("wasabi"), "exception message did not report property name.\n"
                + e.getMessage());
        }
    }

    @Test
    public void testGetClassMetadata()
    {
        SessionFactory sess = mock(SessionFactory.class);
        ClassMetadata clm = mock(ClassMetadata.class);
        when(sess.getClassMetadata(String.class)).thenReturn(clm);

        ClassMetadata returned = getClassMetadata("pippo", sess);
        Assert.assertEquals(returned, clm);
    }

    @Test
    public void testGetClassMetadataNotFound()
    {
        SessionFactory sess = mock(SessionFactory.class);
        when(sess.getClassMetadata(String.class)).thenReturn(null);

        try
        {
            getClassMetadata("pippo", sess);
            Assert.fail("Should have thrown exception");
        }
        catch (IllegalStateException e)
        {
            Assert.assertTrue(
                e.getMessage().contains(String.class.toString()),
                "exception message did not report entity type.\n" + e.getMessage());
        }
    }

    @Test
    public void testAppend()
    {
        Assert.assertEquals(append(new String[0], "a"), new String[]{"a" });
        Assert.assertEquals(append(new String[]{"a" }, "b"), new String[]{"a", "b" });
        Assert.assertEquals(append(new String[]{null }, null), new String[]{null, null });
    }

    @Test
    public void testGetPath()
    {
        Assert.assertEquals(getPath(new String[0]), StringUtils.EMPTY);
        Assert.assertEquals(getPath(new String[]{"pippo" }), "pippo");
        Assert.assertEquals(getPath(new String[]{"a", "b" }), "a.b");
        Assert.assertEquals(getPath(new String[]{"a", "b", "c" }), "a.b.c");
    }

    /**
     * TODO also test HibernateProxy case
     */
    @Test
    public void testAddIdentifierRestrictionWhenAdded()
    {
        Criteria crit = mock(Criteria.class);
        Object entity = new Object();
        // mock EventSource because it needs to implement both Session and SessionImplementor
        Session sess = mock(Session.class);
        ClassMetadata clm = mock(ClassMetadata.class);
        when(sess.getEntityMode()).thenReturn(EntityMode.POJO);
        when(clm.getIdentifierPropertyName()).thenReturn("id");
        when(clm.getIdentifier(entity, EntityMode.POJO)).thenReturn(1);

        boolean added = addIdentifierRestriction(crit, entity, clm, sess);
        Assert.assertTrue(added, "identifier restriction not added");

        Matcher<Criterion> hasSameToStringAsIdEqualsOneRestriction = new ArgumentMatcher<Criterion>()
        {

            @Override
            public boolean matches(Object argument)
            {
                // all this mess because Restriction does not support equals
                return argument != null && Restrictions.idEq(1).toString().equals(argument.toString());
            }

        };
        verify(crit).add(argThat(hasSameToStringAsIdEqualsOneRestriction));
    }

    @Test
    public void testAddIdentifierRestrictionWhenNotAdded1()
    {
        Criteria crit = mock(Criteria.class);
        Object entity = new Object();
        Session sess = mock(Session.class);
        ClassMetadata clm = mock(ClassMetadata.class);
        when(clm.getIdentifierPropertyName()).thenReturn(null);

        boolean added = addIdentifierRestriction(crit, entity, clm, sess);
        Assert.assertFalse(added, "identifier restriction added");
    }

    @Test
    public void testAddIdentifierRestrictionWhenNotAdded2()
    {
        Criteria crit = mock(Criteria.class);
        Object entity = new Object();
        Session sess = mock(Session.class);
        ClassMetadata clm = mock(ClassMetadata.class);
        when(sess.getEntityMode()).thenReturn(EntityMode.POJO);
        when(clm.getIdentifierPropertyName()).thenReturn("id");
        when(clm.getIdentifier(entity, EntityMode.POJO)).thenReturn(null);

        boolean added = addIdentifierRestriction(crit, entity, clm, sess);
        Assert.assertFalse(added, "identifier restriction added");
    }

}
