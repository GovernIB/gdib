package es.caib.gdib.ws.common.types;

public enum EemgdeSignatureProfile {
	
	BES(new Integer(1), "BES"),
	EPES(new Integer(2), "EPES"),
	T(new Integer(3), "T"),
	C(new Integer(4), "C"),
	X(new Integer(5), "X"),
	XL(new Integer(6), "XL"),
	A(new Integer(7), "A"),
	LTV(new Integer(8), "LTV"),
	BASELINE_B_LEVEL(new Integer(9), "BASELINE B-Level"),
	BASELINE_T_LEVEL(new Integer(10), "BASELINE T- Level"),
	BASELINE_LT_LEVEL(new Integer(11), "BASELINE LT- Level"),
	BASELINE_LTA_LEVEL(new Integer(12), "BASELINE LTA- Level");

    private final Integer id;
    private final String name;

    private EemgdeSignatureProfile(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Integer getId() {
        return this.id;
    }

    public static EemgdeSignatureProfile getById(Integer id) {

        for (EemgdeSignatureProfile e : EemgdeSignatureProfile.values()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }

        return null;
    }
}
