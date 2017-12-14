package es.caib.gdib.ws.common.types;

/**
 * Clase que representa los modos de firma electrónica XML admitidos o habilitados.
 * @author RICOH
 *
 */
public enum XmlSignatureMode {

	/**
	 * Relación de modos de firma electrónica XML admitidos.
	 */
	DETACHED(new Integer(1), "DETACHED"), 
	ENVELOPED(new Integer(2), "ENVELOPED"), 
	ENVELOPING(new Integer(3), "ENVELOPING");
	
	private final Integer id;
    private final String name;

    private XmlSignatureMode(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Integer getId() {
        return this.id;
    }

    public static XmlSignatureMode getById(Integer id) {

        for (XmlSignatureMode e : XmlSignatureMode.values()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }

        return null;
    }
	
}
