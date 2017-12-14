package es.caib.arxiudigital.apirest.CSGD.peticiones;


import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.ServiceHeader;


public class Request<T> {

	private ServiceHeader serviceHeader;
	private T param;

	public ServiceHeader getServiceHeader() {
		return serviceHeader;
	}
	public void setServiceHeader(ServiceHeader serviceHeader) {
		this.serviceHeader = serviceHeader;
	}
	public T getParam() {
		return param;
	}
	public void setParam(T param) {
		this.param = param;
	}


}
