package es.caib.gdib.ws.common.types;


public enum ValidationStatus {
	NO_DETERMINADO(new Integer(1), "NO DETERMINADO"),
	CORRECTO(new Integer(2), "CORRECTO"),
	NO_CORRECTO(new Integer(2), "NO CORRECTO");
	
	private final Integer id;
    private final String name;

    private ValidationStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Integer getId() {
        return this.id;
    }

    public static ValidationStatus getById(Integer id) {

        for (ValidationStatus e : ValidationStatus.values()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }

        return null;
    }
}
