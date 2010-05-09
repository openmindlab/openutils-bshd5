/**
 * 
 */
package it.openutils.hibernate.test.dao;

import it.openutils.dao.hibernate.HibernateDAO;
import it.openutils.dao.hibernate.HibernateDAOImpl;
import it.openutils.hibernate.test.model.Person;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * @author gcatania
 */
public interface PersonDAO extends HibernateDAO<Person, Long>
{

    /**
     * @author gcatania
     */
    @Repository("personDAO")
    class PersonDAOImpl extends HibernateDAOImpl<Person, Long> implements PersonDAO
    {

        @Autowired
        public PersonDAOImpl(SessionFactory factory)
        {
            super(Person.class);
            setSessionFactory(factory);
        }
    }

}
