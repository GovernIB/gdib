package es.caib.gdib.ws.common.types;

public enum EniSignatureType {
	TF01(new Integer(1), "TF01"),
	TF02(new Integer(2), "TF02"),
	TF03(new Integer(3), "TF03"),
	TF04(new Integer(4), "TF04"),
	TF05(new Integer(5), "TF05"),
	TF06(new Integer(6), "TF06");

    private final Integer id;
    private final String name;

    private EniSignatureType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Integer getId() {
        return this.id;
    }

    public static EniSignatureType getById(Integer id) {

        for (EniSignatureType e : EniSignatureType.values()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }

        return null;
    }
}
