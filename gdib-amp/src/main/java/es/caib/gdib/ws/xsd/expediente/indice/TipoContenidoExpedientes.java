//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.14 at 05:06:23 PM CEST 
//


package es.caib.gdib.ws.xsd.expediente.indice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Lista de subexpedientes de un expediente o agrupación documental.
 * 
 * <p>Java class for TipoContenidoExpedientes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TipoContenidoExpedientes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subexpediente" type="{urn:es:caib:archivodigital:gestiondocumental:expediente-e:indice-e:1.0}TipoExpediente" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TipoContenidoExpedientes", propOrder = {
    "subexpediente"
})
public class TipoContenidoExpedientes {

    @XmlElement(required = true)
    protected List<TipoExpediente> subexpediente;

    /**
     * Gets the value of the subexpediente property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subexpediente property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubexpediente().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TipoExpediente }
     * 
     * 
     */
    public List<TipoExpediente> getSubexpediente() {
        if (subexpediente == null) {
            subexpediente = new ArrayList<TipoExpediente>();
        }
        return this.subexpediente;
    }

}
