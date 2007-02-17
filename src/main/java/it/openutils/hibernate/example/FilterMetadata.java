package it.openutils.hibernate.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Fabrizio Giustina
 * @version $Id$
 */
public interface FilterMetadata
{

    void createFilter(Criteria criteria, String propertyName, Object propertyValue);

    FilterMetadata LIKE = new FilterMetadata()
    {

        private Logger log = LoggerFactory.getLogger(FilterMetadata.class);

        public void createFilter(Criteria crit, String propertyName, Object propertyValue)
        {
            crit.add(Restrictions.ilike(propertyName, (String) propertyValue, MatchMode.ANYWHERE));

            if (log.isDebugEnabled())
            {
                log.debug("crit.add(Expression.like(" + propertyName + ", '%" + propertyValue + "%' ))");
            }
        }
    };

    FilterMetadata EQUAL = new FilterMetadata()
    {

        private Log log = LogFactory.getLog(FilterMetadata.class);

        public void createFilter(Criteria crit, String propertyName, Object propertyValue)
        {
            if (log.isDebugEnabled())
            {
                log.debug("crit.add(Expression.eq(" + propertyName + ", " + propertyValue + "))");
            }

            crit.add(Restrictions.eq(propertyName, propertyValue));
        }
    };
}
