package es.caib.gdib.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class CloseFileJobEntity extends JobEntity{
    private Date closeDate;


    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    @Override
    public String toString() {
        return "CloseFileJobEntity{" +
                "closeDate=" + closeDate +
                "} " + super.toString();
    }

    /**
     * Se insertaran los parametros en el mismo orden que aparecen en base de datos, por lo que:
     * 1-id
     * 2-signature format
     * 3-signature type
     *
     * @param ps
     */
    @Override
    protected void prepareToInsert(PreparedStatement ps) throws SQLException {
        ps.setString(1, super.getId());
        ps.setDate(2, new java.sql.Date(this.getCloseDate().getTime()));
    }
}
