-- ejecutarlo desde conexión www_gdib

CREATE SYNONYM CLOSE_FILE_JOB FOR GDIB.CLOSE_FILE_JOB;
CREATE SYNONYM UPGRADE_SIGNATURE_JOB FOR GDIB.UPGRADE_SIGNATURE_JOB;

-- (si se quiere comprobar) select * from USER_SYNONYMS order by synonym_name;
