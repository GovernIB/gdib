package es.caib.invoices.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Clase utilizada para encapsular los parametros de salida del metodo de 'updateMetadata' del web service de CAIB
 *
 * @author <a href="mailto:luis.fernandezprado@ricoh.es>Luis Fernandez Prado (LFP)</a>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GreeterResponseType", propOrder = { "number" })
@XmlRootElement(name = "greeterResponse")
public class UpdateMetadataResponse {

    private int number;

    public UpdateMetadataResponse() {

    }

    public UpdateMetadataResponse(int number) {
	super();
	this.number = number;
    }

    public int getNumber() {
	return number;
    }

    public void setNumber(int number) {
	this.number = number;
    }

}
