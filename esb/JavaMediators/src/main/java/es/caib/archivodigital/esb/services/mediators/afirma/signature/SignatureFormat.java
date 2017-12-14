package es.caib.archivodigital.esb.services.mediators.afirma.signature;

public enum SignatureFormat {
	UNRECOGNIZED(new Integer(0), "UNRECOGNIZED"),
	CAdES_A(new Integer(1), "CAdES-A"),
	CAdES_XL(new Integer(2), "CAdES-XL"),
	CAdES_XL1(new Integer(3), "CAdES-XL1"),
	CAdES_XL2(new Integer(4), "CAdES-XL2"),
	CAdES_X(new Integer(5), "CAdES-X"),
	CAdES_X1(new Integer(6), "CAdES-X1"),
	CAdES_X2(new Integer(7), "CAdES-X2"),
	CAdES_C(new Integer(8), "CAdES-C"),
	CAdES_T(new Integer(9), "CAdES-T"),
	CAdES_EPES(new Integer(10), "CAdES-EPES"),
	CAdES_BES(new Integer(11), "CAdES-BES"),
	XAdES_A(new Integer(12), "XAdES-A"),
	XAdES_XL(new Integer(13), "XAdES-XL"),
	XAdES_XL1(new Integer(14), "XAdES-XL1"),
	XAdES_XL2(new Integer(15), "XAdES-XL2"),
	XAdES_X(new Integer(16), "XAdES-X"),
	XAdES_X1(new Integer(17), "XAdES-X1"),
	XAdES_X2(new Integer(18), "XAdES-X2"),
	XAdES_C(new Integer(19), "XAdES-C"),
	XAdES_T(new Integer(20), "XAdES-T"),
	XAdES_EPES(new Integer(21), "XAdES-EPES"),
	XAdES_BES(new Integer(22), "XAdES-BES"),
	PDF(new Integer(23), "PDF"),
	PAdES_Basic(new Integer(24), "PAdES-Basic"),
	PAdES_BES(new Integer(25), "PAdES-BES"),
	PAdES_EPES(new Integer(26), "PAdES-EPES"),
	PAdES_LTV(new Integer(27), "PAdES-LTV"),
	CMS(new Integer(28), "CMS"),
	CMS_TST(new Integer(29), "CMS-TST"),
	CAdES(new Integer(30), "CAdES"),
	XAdES(new Integer(31), "XAdES"),
	PAdES(new Integer(32), "PAdES"),
	XML_SIGNATURE(new Integer(33), "XML-Signature");
	
	private final Integer id;
    private final String name;

    private SignatureFormat(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Integer getId() {
        return this.id;
    }

    public static SignatureFormat getById(Integer id) {

        for (SignatureFormat e : SignatureFormat.values()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }

        return null;
    }
}
