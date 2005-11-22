package it.openutils.hibernate.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;


/**
 * @author Fabrizio Giustina
 * @version $Id$
 */
public interface FilterMetadata
{

    void createFilter(Criteria criteria, String propertyName, Object propertyValue);

    FilterMetadata LIKE = new FilterMetadata()
    {

        private Log log = LogFactory.getLog(FilterMetadata.class);

        public void createFilter(Criteria crit, String propertyName, Object propertyValue)
        {
            String valoreDescr = "%" + (String) propertyValue + "%";
            crit.add(Restrictions.like(propertyName, valoreDescr));

            if (log.isDebugEnabled())
            {
                log.debug("crit.add(Expression.like(" + propertyName + ", " + valoreDescr + "))");
            }
        }
    };

    FilterMetadata EQUAL = new FilterMetadata()
    {

        private Log log = LogFactory.getLog(FilterMetadata.class);

        public void createFilter(Criteria crit, String propertyName, Object propertyValue)
        {
            {
                if (log.isDebugEnabled())
                {
                    log.debug("crit.add(Expression.eq(" + propertyName + ", " + propertyValue + "))");
                }

                crit.add(Restrictions.eq(propertyName, propertyValue));
            }
        }
    };
}
