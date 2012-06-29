/**
 * Copyright (c) Energeya LLC.  All rights reserved. http://www.energeya.com
 */
package it.openutils.hibernate.example;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;


/**
 * @author gcatania
 * @version $Id$
 */
final class ExampleTreeUtils
{

    private ExampleTreeUtils()
    {
    }

    /**
     * check the property with the input name was already walked in the input path
     * @param path the current path
     * @param propertyName the property name about to be walked
     * @return true if the property with the input name was already walked in the input path
     */
    public static boolean alreadyWalked(String[] walkedProperties, String propertyName)
    {
        if (walkedProperties.length <= 2)
        {
            return false;
        }
        String parent = walkedProperties[walkedProperties.length - 1];
        boolean lastWasChild = false;
        for (int i = walkedProperties.length - 2; i > 0; i--)
        {
            String currPropertyName = walkedProperties[i];
            if (currPropertyName.equals(propertyName))
            {
                lastWasChild = true;
                continue;
            }
            if (lastWasChild)
            {
                if (currPropertyName.equals(parent))
                {
                    return true;
                }
                else
                {
                    lastWasChild = false;
                }
            }
        }
        return false;
    }

    /**
     * retrieves a value from a collection
     * @param collectionValue the collection
     * @return a value
     * @see http://opensource2.atlassian.com/projects/hibernate/browse/HHH-879
     * @throws IllegalArgumentException if the input collection contains more than one value
     */
    public static Object getValueFromCollection(Object collectionValue) throws IllegalArgumentException
    {
        if (collectionValue != null)
        {
            if (collectionValue instanceof Collection< ? >)
            {
                Collection< ? > coll = (Collection< ? >) collectionValue;
                int size = coll.size();
                if (size == 1)
                {
                    return coll.iterator().next();
                }
                if (size > 1)
                {
                    throw new IllegalArgumentException("More than one element in filter collection is unsupported.");
                }
            }
            Class< ? extends Object> clazz = collectionValue.getClass();
            if (clazz.isArray())
            {
                int length = Array.getLength(collectionValue);
                if (length == 1)
                {
                    return Array.get(collectionValue, 0);
                }
                if (length > 1)
                {
                    throw new IllegalArgumentException("More than one element in filter array is unsupported.");
                }
            }
            // TODO other cases?
        }
        return null;
    }

    /**
     * @param strings an array of strings
     * @param s the string to append
     * @return a new array containing the input string array plus the input string at the end
     */
    public static String[] append(String[] strings, String s)
    {
        String[] result = Arrays.copyOf(strings, strings.length + 1);
        result[strings.length] = s;
        return result;
    }

    /**
     * constructs the association path from an array of property names
     * @param propertyNames the walked properties
     * @return the association path
     */
    public static String getPath(String[] propertyNames)
    {
        return propertyNames.length > 0 ? StringUtils.join(propertyNames, '.') : StringUtils.EMPTY;
    }

}
