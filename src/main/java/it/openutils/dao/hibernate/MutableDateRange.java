package it.openutils.dao.hibernate;

import java.util.Date;


/**
 * @author fgiust
 * @version $Revision$ ($Author$)
 */
public class MutableDateRange extends Date
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * range start.
     */
    private Date from;

    /**
     * range end.
     */
    private Date to;

    /**
     * Getter for <code>from</code>.
     * @return Returns the from.
     */
    public Date getFrom()
    {
        return this.from;
    }

    /**
     * Setter for <code>from</code>.
     * @param from The from to set.
     */
    public void setFrom(Date from)
    {
        this.from = from;
    }

    /**
     * Getter for <code>to</code>.
     * @return Returns the to.
     */
    public Date getTo()
    {
        return this.to;
    }

    /**
     * Setter for <code>to</code>.
     * @param to The to to set.
     */
    public void setTo(Date to)
    {
        this.to = to;
    }

    /**
     * Returns <code>true</code> if at least one date is set.
     * @return <code>true</code> if at least one date is set
     */
    public boolean isSet()
    {
        return this.to != null || this.from != null;
    }

}
