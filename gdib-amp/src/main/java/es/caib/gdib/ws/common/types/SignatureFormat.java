package es.caib.gdib.ws.common.types;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa los formatos de firma electrónica avanzadis admitidos o habilitados.
 * @author RICOH
 *
 */
public enum SignatureFormat {

	/**
	 * Relación de formatos admitidos de firma electrónica.
	 */
	UNRECOGNIZED(new Integer(0), "UNRECOGNIZED","UNRECOGNIZED"),
	CAdES_A(new Integer(1), "CAdES-A","CAdES"),
	CAdES_XL(new Integer(2), "CAdES-XL","CAdES"),
	CAdES_XL1(new Integer(3), "CAdES-XL1","CAdES"),
	CAdES_XL2(new Integer(4), "CAdES-XL2","CAdES"),
	CAdES_X(new Integer(5), "CAdES-X","CAdES"),
	CAdES_X1(new Integer(6), "CAdES-X1","CAdES"),
	CAdES_X2(new Integer(7), "CAdES-X2","CAdES"),
	CAdES_C(new Integer(8), "CAdES-C","CAdES"),
	CAdES_T(new Integer(9), "CAdES-T","CAdES"),
	CAdES_EPES(new Integer(10), "CAdES-EPES","CAdES"),
	CAdES_BES(new Integer(11), "CAdES-BES","CAdES"),
	XAdES_A(new Integer(12), "XAdES-A","XAdES"),
	XAdES_XL(new Integer(13), "XAdES-XL","XAdES"),
	XAdES_XL1(new Integer(14), "XAdES-XL1","XAdES"),
	XAdES_XL2(new Integer(15), "XAdES-XL2","XAdES"),
	XAdES_X(new Integer(16), "XAdES-X","XAdES"),
	XAdES_X1(new Integer(17), "XAdES-X1","XAdES"),
	XAdES_X2(new Integer(18), "XAdES-X2","XAdES"),
	XAdES_C(new Integer(19), "XAdES-C","XAdES"),
	XAdES_T(new Integer(20), "XAdES-T","XAdES"),
	XAdES_EPES(new Integer(21), "XAdES-EPES","XAdES"),
	XAdES_BES(new Integer(22), "XAdES-BES","XAdES"),
	PDF(new Integer(23), "PDF","PDF"),
	PAdES_Basic(new Integer(24), "PAdES-Basic","PAdES"),
	PAdES_BES(new Integer(25), "PAdES-BES","PAdES"),
	PAdES_EPES(new Integer(26), "PAdES-EPES","PAdES"),
	PAdES_LTV(new Integer(27), "PAdES-LTV","PAdES"),
	CMS(new Integer(28), "CMS","CMS"),
	CMS_TST(new Integer(29), "CMS-TST","CMS"),
	CAdES(new Integer(30), "CAdES", "CAdES"),
	XAdES(new Integer(31), "XAdES", "XAdES"),
	PAdES(new Integer(32), "PAdES", "PAdES"),
	XML_SIGNATURE(new Integer(33), "XML-Signature", "XML-Signature");

	private static final String CADES_FAMILY_NAME = "CAdES";
	private static final String XADES_FAMILY_NAME = "XAdES";	
	private static final String PADES_FAMILY_NAME = "PAdES";
	
	private static Map<SignatureFormat,Integer> CADES_ADVANCED_ORDER = new HashMap<SignatureFormat,Integer>();
	private static Map<SignatureFormat,Integer> XADES_ADVANCED_ORDER = new HashMap<SignatureFormat,Integer>();
	private static Map<SignatureFormat,Integer> PADES_ADVANCED_ORDER = new HashMap<SignatureFormat,Integer>();
	
	private final Integer id;
    private final String name;
    private final String type;

    static{
    	initCadesMap();
    	initXadesMap();
    	initPadesMap();
    }
    
    private static void initCadesMap(){
    	CADES_ADVANCED_ORDER.put(CAdES, 1);
    	CADES_ADVANCED_ORDER.put(CAdES_BES, 1);
    	CADES_ADVANCED_ORDER.put(CAdES_EPES, 2);
    	CADES_ADVANCED_ORDER.put(CAdES_T, 3);
    	CADES_ADVANCED_ORDER.put(CAdES_C, 4);
    	CADES_ADVANCED_ORDER.put(CAdES_X, 5);
    	CADES_ADVANCED_ORDER.put(CAdES_X1, 5);
    	CADES_ADVANCED_ORDER.put(CAdES_X2, 5);
    	CADES_ADVANCED_ORDER.put(CAdES_XL, 6);
    	CADES_ADVANCED_ORDER.put(CAdES_XL1, 6);
    	CADES_ADVANCED_ORDER.put(CAdES_XL2, 6);
    	CADES_ADVANCED_ORDER.put(CAdES_A, 7);
    }
    
    private static void initXadesMap(){
    	XADES_ADVANCED_ORDER.put(XAdES, 1);
    	XADES_ADVANCED_ORDER.put(XAdES_BES, 1);
    	XADES_ADVANCED_ORDER.put(XAdES_EPES, 2);
    	XADES_ADVANCED_ORDER.put(XAdES_T, 3);
    	XADES_ADVANCED_ORDER.put(XAdES_C, 4);
    	XADES_ADVANCED_ORDER.put(XAdES_X, 5);
    	XADES_ADVANCED_ORDER.put(XAdES_X1, 5);
    	XADES_ADVANCED_ORDER.put(XAdES_X2, 5);
    	XADES_ADVANCED_ORDER.put(XAdES_XL, 6);
    	XADES_ADVANCED_ORDER.put(XAdES_XL1, 6);
    	XADES_ADVANCED_ORDER.put(XAdES_XL2, 6);
    	XADES_ADVANCED_ORDER.put(XAdES_A, 7);
    }
    
    private static void initPadesMap(){
    	PADES_ADVANCED_ORDER.put(PAdES, 1);
    	PADES_ADVANCED_ORDER.put(PAdES_Basic,1);
    	PADES_ADVANCED_ORDER.put(PAdES_BES, 1);
    	PADES_ADVANCED_ORDER.put(PAdES_EPES, 2);
    	PADES_ADVANCED_ORDER.put(PAdES_LTV, 3);
    }
    
    private SignatureFormat(Integer id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public Integer getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }
    
    public static SignatureFormat getById(Integer id) {

        for (SignatureFormat e : SignatureFormat.values()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }

        return null;
    }

    /**
	 * Metodo que comprueba si el formato de firma electrónica es un formato
	 * de firma más avanzado que el pasado como parámetro, aSigntureFormat.
	 * @param aSigntureFormat Un formato de firma electrónica
	 * @return true, si es más avanzado. En caso contrario, false.
	 */
	public Boolean isMoreAdvancedSignatureFormat(SignatureFormat aSigntureFormat) {
		Boolean res = Boolean.FALSE;
		Integer advOrder,aSignAdvOrder;

		advOrder = new Integer(-1);
		aSignAdvOrder = new Integer(-1);
		
		if(this.getType().equals(aSigntureFormat.getType())){
			switch(this.getType()){
				case CADES_FAMILY_NAME:
					if(CADES_ADVANCED_ORDER.get(this) != null){
						advOrder = CADES_ADVANCED_ORDER.get(this);
					}
					if(CADES_ADVANCED_ORDER.get(aSigntureFormat) != null){
						aSignAdvOrder = CADES_ADVANCED_ORDER.get(aSigntureFormat);
					}
					break;
				case XADES_FAMILY_NAME:
					if(XADES_ADVANCED_ORDER.get(this) != null){
						advOrder = XADES_ADVANCED_ORDER.get(this);
					}
					if(XADES_ADVANCED_ORDER.get(aSigntureFormat) != null){
						aSignAdvOrder = XADES_ADVANCED_ORDER.get(aSigntureFormat);
					}
					break;
				case PADES_FAMILY_NAME:
					if(PADES_ADVANCED_ORDER.get(this) != null){
						advOrder = PADES_ADVANCED_ORDER.get(this);
					}
					if(PADES_ADVANCED_ORDER.get(aSigntureFormat) != null){
						aSignAdvOrder = PADES_ADVANCED_ORDER.get(aSigntureFormat);
					}
					break;
			}
		}

		res = advOrder > aSignAdvOrder;
		
		return res;
	}
}
