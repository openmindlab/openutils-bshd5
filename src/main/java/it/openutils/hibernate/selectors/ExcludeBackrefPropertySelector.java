/**
 * Copyright (c) Energeya LLC.  All rights reserved. http://www.energeya.com
 */
package it.openutils.hibernate.selectors;

import java.io.Serializable;

import org.hibernate.criterion.Example.PropertySelector;
import org.hibernate.property.BackrefPropertyAccessor;
import org.hibernate.type.Type;


/**
 * utility selector to avoid class cast exceptions on {@link BackrefPropertyAccessor.UNKNOWN}
 * @see BSHD-15
 * @author gcatania
 * @version $Id$
 */
public class ExcludeBackrefPropertySelector implements PropertySelector, Serializable
{

    private static final long serialVersionUID = -2803322309158823550L;

    private final PropertySelector selector;

    public ExcludeBackrefPropertySelector(PropertySelector selector)
    {
        if (selector == null)
        {
            throw new NullPointerException("Null selector.");
        }
        this.selector = selector;
    }

    public boolean include(Object propertyValue, String propertyName, Type type)
    {
        if (BackrefPropertyAccessor.UNKNOWN.equals(propertyValue))
        {
            return false;
        }
        return selector.include(propertyValue, propertyName, type);
    }

}
