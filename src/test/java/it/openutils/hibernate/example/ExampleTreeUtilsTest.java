/**
 * Copyright (c) Energeya LLC.  All rights reserved. http://www.energeya.com
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
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.spi.EventSource;
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

    @Test
    public void testAddIdentifierRestrictionWhenAdded()
    {
        Criteria crit = mock(Criteria.class);
        Object entity = new Object();
        // mock EventSource because it needs to implement both Session and SessionImplementor
        EventSource sess = mock(EventSource.class);
        ClassMetadata clm = mock(ClassMetadata.class);
        when(clm.getIdentifierPropertyName()).thenReturn("id");
        when(clm.getIdentifier(entity, sess)).thenReturn(1);

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
        // mock EventSource because it needs to implement both Session and SessionImplementor
        EventSource sess = mock(EventSource.class);
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
        // mock EventSource because it needs to implement both Session and SessionImplementor
        EventSource sess = mock(EventSource.class);
        ClassMetadata clm = mock(ClassMetadata.class);
        when(clm.getIdentifierPropertyName()).thenReturn("id");
        when(clm.getIdentifier(entity, sess)).thenReturn(null);

        boolean added = addIdentifierRestriction(crit, entity, clm, sess);
        Assert.assertFalse(added, "identifier restriction added");
    }

}
