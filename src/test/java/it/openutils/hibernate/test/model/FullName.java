/**
 * 
 */
package it.openutils.hibernate.test.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * @author gcatania
 */
@Embeddable
public class FullName
{

    private Title title;

    @Column(nullable = false)
    private String givenName;

    @Column(nullable = false)
    private String familyName;

    public FullName()
    {
    }

    public FullName(String givenName, String familyName)
    {
        this.givenName = givenName;
        this.familyName = familyName;
    }

    /**
     * @return the title
     */
    public Title getTitle()
    {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(Title title)
    {
        this.title = title;
    }

    /**
     * @return the givenName
     */
    public String getGivenName()
    {
        return givenName;
    }

    /**
     * @param givenName the givenName to set
     */
    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    /**
     * @return the familyName
     */
    public String getFamilyName()
    {
        return familyName;
    }

    /**
     * @param familyName the familyName to set
     */
    public void setFamilyName(String familyName)
    {
        this.familyName = familyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((familyName == null) ? 0 : familyName.hashCode());
        result = prime * result + ((givenName == null) ? 0 : givenName.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        FullName other = (FullName) obj;
        if (familyName == null)
        {
            if (other.familyName != null)
            {
                return false;
            }
        }
        else if (!familyName.equals(other.familyName))
        {
            return false;
        }
        if (givenName == null)
        {
            if (other.givenName != null)
            {
                return false;
            }
        }
        else if (!givenName.equals(other.givenName))
        {
            return false;
        }
        if (title == null)
        {
            if (other.title != null)
            {
                return false;
            }
        }
        else if (!title.equals(other.title))
        {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "FullName [familyName=" + familyName + ", givenName=" + givenName + ", title=" + title + "]";
    }

}
