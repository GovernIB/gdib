package es.caib.gdib.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpgradeSignatureJobEntity extends JobEntity{
    private Integer idMinCustodySignature;
    private Integer idEniSignatureNumber;
    private Boolean implicitSignature;

    public Integer getIdMinCustodySignature() {
        return idMinCustodySignature;
    }

    public void setIdMinCustodySignature(Integer idMinCustodySignature) {
        this.idMinCustodySignature = idMinCustodySignature;
    }

    public Integer getIdEniSignatureNumber() {
        return idEniSignatureNumber;
    }

    public void setIdEniSignatureNumber(Integer idEniSignatureNumber) {
        this.idEniSignatureNumber = idEniSignatureNumber;
    }

    public Boolean getImplicitSignature() {
        return implicitSignature;
    }

    public void setImplicitSignature(Boolean implicitSignature) {
        this.implicitSignature = implicitSignature;
    }

    @Override
    public String toString() {
        return "UpgradeSignatureJobEntity{" +
                "idMinCustodySignature=" + idMinCustodySignature +
                ", idEniSignatureNumber=" + idEniSignatureNumber +
                ", implicitSignature=" + implicitSignature +
                "} " + super.toString();
    }

    /**
     * Se insertaran los parametros en el mismo orden que aparecen en base de datos, por lo que:
     * 1-id
     * 2-idMinCustodySignature
     * 3-idEniSignatureNumber
     * 4-implicitSignature
     *
     * @param ps
     */
    @Override
    protected void prepareToInsert(PreparedStatement ps) throws SQLException {
        ps.setString(1, super.getId());
        ps.setInt(2,this.getIdMinCustodySignature());
        ps.setInt(3,this.getIdEniSignatureNumber());
        ps.setBoolean(4,this.getImplicitSignature());
    }
}
