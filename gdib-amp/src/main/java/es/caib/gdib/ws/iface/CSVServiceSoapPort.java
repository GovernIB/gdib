package es.caib.gdib.ws.iface;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://www.caib.es/gdib/csv/ws", name = "GdibCSVServiceSoapPort")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface CSVServiceSoapPort {

	@WebMethod(action = "getCSV")
	public String getCSV();

}
