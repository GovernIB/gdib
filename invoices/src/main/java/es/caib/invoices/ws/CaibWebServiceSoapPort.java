package es.caib.invoices.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import es.rsits.ws.exception.WSException;


/**
 * Interfaz del servicio web de Caib
 *
 * @author <a href="mailto:luis.fernandezprado@ricoh.es">Luis Fernandez Prado (LFP)</a>
 *
 */
@WebService(targetNamespace = "http://www.caib.es/invoices/ws", name = "CaibServiceSoapPort")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface CaibWebServiceSoapPort {


    /**
     * Actualiza los metadatos de un fichero identificado por su 'id de factura'
     *
     * @param parameters
     * @return
     * @throws WSException
     */
    @WebResult(name = "updateMetadataResponse", targetNamespace = "http://www.caib.es/invoices/ws", partName = "parameters")
    @WebMethod(action = "updateMetadata")
    UpdateMetadataResponse updateMetadata(@WebParam( partName = "parameters", name = "updateMetadataRequest", targetNamespace = "http://www.caib.es/invoices/ws")
    					final UpdateMetadataRequest parameters) throws WSException;


}
