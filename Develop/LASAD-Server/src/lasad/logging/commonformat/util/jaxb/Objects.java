//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.08.27 at 04:08:09 PM CEST 
//


package lasad.logging.commonformat.util.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "objectDef"
})
@XmlRootElement(name = "objects")
public class Objects {

    @XmlElement(name = "object_def")
    protected List<ObjectDef> objectDef;

    /**
     * Gets the value of the objectDef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the objectDef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObjectDef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ObjectDef }
     * 
     * 
     */
    public List<ObjectDef> getObjectDef() {
        if (objectDef == null) {
            objectDef = new ArrayList<ObjectDef>();
        }
        return this.objectDef;
    }

}