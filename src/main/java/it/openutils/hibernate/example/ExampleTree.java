/**
 *
 * openutils base Spring-Hibernate DAO for java 5.0 (http://www.openmindlab.com/lab/products/bshd5.html)
 *
 * Copyright(C) null-2010, Openmind S.r.l. http://www.openmindonline.it
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

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.AssociationType;
import org.hibernate.type.Type;


/**
 * facility to create subcriteria and examples for associations of an input entity
 * @author gcatania
 */
public class ExampleTree
{

    // TODO: allow specification of parameters to pass along to created examples, internally save association paths,
    // fail fast on null entity; check for parent entities (currently the parameter is unused) to avoid infinite loops
    // on parent/child references

    private final Object entity;

    /**
     * builds an instance of {@code ExampleTree} associated with the input entity
     * @param entity the example entity
     */
    public ExampleTree(Object entity)
    {
        this.entity = entity;
    }

    /**
     * creates a criteria with the input session, adds to it an example for the input entity, and creates subcriteria
     * and related examples for non-null property values of type {@link AssociationType} on the entity this example tree
     * was initialized with
     * @param ses the session
     * @return a criteria for this example tree
     */
    public Criteria create(Session ses)
    {
        return appendTo(ses.createCriteria(Hibernate.getClass(entity)), ses);
    }

    /**
     * appends examples and subcriteria, created for the entity this example tree was initialized with, to the input
     * criteria, and then returns it
     * @param crit the criteria to append to
     * @param ses the session
     * @return the input criteria with the added subcriteria and examples
     */
    public Criteria appendTo(Criteria crit, Session ses)
    {
        new ExampleTreeWalker(ses).createSubExamples(crit, entity, null);
        return crit;
    }

    private static class ExampleTreeWalker
    {

        private final SessionFactory sessionFactory;

        private EntityMode entityMode;

        public ExampleTreeWalker(Session session)
        {
            sessionFactory = session.getSessionFactory();
            entityMode = session.getEntityMode();
        }

        public void createSubExamples(Criteria crit, Object entity, Object parentEntity)
        {
            crit.add(Example.create(entity));
            ClassMetadata classMetadata = sessionFactory.getClassMetadata(Hibernate.getClass(entity));
            Type[] types = classMetadata.getPropertyTypes();
            String[] names = classMetadata.getPropertyNames();
            for (int i = 0; i < types.length; i++)
            {
                Type propertyType = types[i];
                if (!propertyType.isAssociationType())
                {
                    // handled by Example.create()
                    continue;
                }
                String propertyName = names[i];
                Object propertyValue = classMetadata.getPropertyValue(entity, propertyName, entityMode);
                if (propertyValue == null)
                {
                    // skip null values (TODO: allow specifications of which values to exclude, also see
                    // EnhancedExample.containsSomething()
                    continue;
                }

                Criteria subCriteria = crit.createCriteria(propertyName);
                createSubExamples(subCriteria, propertyValue, entity);
            }
        }
    }

}
