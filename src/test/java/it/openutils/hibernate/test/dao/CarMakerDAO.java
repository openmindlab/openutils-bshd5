/**
 *
 * openutils base Spring-Hibernate DAO (http://www.openmindlab.com/lab/products/bshd5.html)
 *
 * Copyright(C) 2005-2011, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.hibernate.test.dao;

import it.openutils.dao.hibernate.HibernateDAO;
import it.openutils.dao.hibernate.HibernateDAOImpl;
import it.openutils.hibernate.test.model.CarMaker;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * @author gcatania
 */
public interface CarMakerDAO extends HibernateDAO<CarMaker, Long>
{

    @Repository("carMakerDAO")
    class CarMakerDAOImpl extends HibernateDAOImpl<CarMaker, Long> implements CarMakerDAO
    {

        @Autowired
        public CarMakerDAOImpl(SessionFactory factory)
        {
            super(CarMaker.class);
            setSessionFactory(factory);
        }
    }

}
