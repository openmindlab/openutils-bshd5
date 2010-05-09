/**
 * 
 */
package it.openutils.hibernate.test.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;


/**
 * @author gcatania
 */
@Entity
public class Wish
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "wish", optional = false, cascade = CascadeType.ALL)
    private Person person;

    @Column(nullable = false)
    private String reason;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<FantasticThing> objectsOfWish;

    @Transient
    private Integer objectCount;

    public Wish()
    {
    }

    public Wish(Person person, String reason)
    {
        this.person = person;
        this.reason = reason;
    }

    /**
     * @return the id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return the person
     */
    public Person getPerson()
    {
        return person;
    }

    /**
     * @param person the person to set
     */
    public void setPerson(Person person)
    {
        this.person = person;
    }

    /**
     * @return the reason
     */
    public String getReason()
    {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason)
    {
        this.reason = reason;
    }

    /**
     * @return the objectsOfWish
     */
    public Set<FantasticThing> getObjectsOfWish()
    {
        return objectsOfWish;
    }

    /**
     * @param objectsOfWish the objectsOfWish to set
     */
    public void setObjectsOfWish(Set<FantasticThing> objectsOfWish)
    {
        this.objectsOfWish = objectsOfWish;
    }

    /**
     * @return the objectCount
     */
    public Integer getObjectCount()
    {
        if (objectCount == null)
        {
            objectCount = objectsOfWish.size();
        }
        return objectCount;
    }

    /**
     * @param objectCount the objectCount to set
     */
    public void setObjectCount(Integer objectCount)
    {
        if (objectCount != null && objectCount != objectsOfWish.size())
        {
            throw new IllegalArgumentException();
        }
        this.objectCount = objectCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((objectCount == null) ? 0 : objectCount.hashCode());
        result = prime * result + ((objectsOfWish == null) ? 0 : objectsOfWish.hashCode());
        result = prime * result + ((reason == null) ? 0 : reason.hashCode());
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
        Wish other = (Wish) obj;
        if (id == null)
        {
            if (other.id != null)
            {
                return false;
            }
        }
        else if (!id.equals(other.id))
        {
            return false;
        }
        if (objectCount == null)
        {
            if (other.objectCount != null)
            {
                return false;
            }
        }
        else if (!objectCount.equals(other.objectCount))
        {
            return false;
        }
        if (objectsOfWish == null)
        {
            if (other.objectsOfWish != null)
            {
                return false;
            }
        }
        else if (!objectsOfWish.equals(other.objectsOfWish))
        {
            return false;
        }
        if (reason == null)
        {
            if (other.reason != null)
            {
                return false;
            }
        }
        else if (!reason.equals(other.reason))
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
        return "Wish [id="
            + id
            + ", objectCount="
            + objectCount
            + ", objectsOfWish="
            + objectsOfWish
            + ", reason="
            + reason
            + "]";
    }

}
