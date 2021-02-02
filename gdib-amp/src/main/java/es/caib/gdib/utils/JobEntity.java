package es.caib.gdib.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class JobEntity {
    private String id;
    private Integer tried;
    private String error;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTried() {
        return tried;
    }

    public void setTried(Integer tried) {
        this.tried = tried;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "JobEntity{" +
                "id='" + id + '\'' +
                ", tried=" + tried +
                ", error='" + error + '\'' +
                '}';
    }

    protected abstract void prepareToInsert(PreparedStatement ps) throws SQLException;

    public void prepareToDelete(PreparedStatement ps) throws SQLException {
        ps.setString(1, this.getId());
    }

    public void prepareToUpdate(PreparedStatement ps) throws SQLException {
        ps.setString(1, this.getError());
        ps.setInt(2, this.getTried());
        ps.setString(3, this.getId());
    }

}
