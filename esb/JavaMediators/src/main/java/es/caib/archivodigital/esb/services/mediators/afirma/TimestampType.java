package es.caib.archivodigital.esb.services.mediators.afirma;

public enum TimestampType {
	SIGNER(new Integer(1), "SIGNER"),
	CUSTODY(new Integer(2), "CUSTODY"),
	ARCHIVE(new Integer(3), "ARCHIVE");
	
	private final Integer id;
    private final String name;

    private TimestampType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Integer getId() {
        return this.id;
    }

    public static TimestampType getById(Integer id) {

        for (TimestampType e : TimestampType.values()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }

        return null;
    }
}
