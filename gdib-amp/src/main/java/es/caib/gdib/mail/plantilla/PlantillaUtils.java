package es.caib.gdib.mail.plantilla;

import es.caib.gdib.utils.CloseFileJobEntity;
import es.caib.gdib.utils.JobEntity;

import java.util.List;

/**
 * Clase utilidad para generar plantillas para envio de mails
 *
 */
public class PlantillaUtils {


    /**
     * Plantilla para resumen de ejecucion
     *
     * @param success
     * @param errors
     * @param maxTries
     * @return
     */
    public static <T extends JobEntity> String generatePlantilla(List<T> success, List<T> errors,
                                    List<T> maxTries, Operacion operacion){
        if(operacion==null){
            return null;
        }
        String plantilla = "Proceso de "+operacion.getValue()+" finalizado.\n";
        if(success!=null && !success.isEmpty()){
            if(operacion == Operacion.CLOSE_FILE) {
                plantilla += "Ids de expedientes cerrados correctamente: [";
            }else{
                plantilla += "Ids de documentos cuyas firmas han sido upgradeadas correctamente: [";
            }

            for(T x : success){
                plantilla+=x.getId()+", ";
            }

            // Quitamos la ultima ', ' que sobra y cerramos corchete
            plantilla = plantilla.substring(0,plantilla.length()-3) +"]\n";
        }

        if(errors!=null && !errors.isEmpty()){
            plantilla+="Errores en la ultima ejecucion [Id, N Intento, Error]:\n ";
            for(T x : errors){
                plantilla+="["+x.getId()+", "+x.getTried()+", "+x.getError()+"]\n";
            }
        }

        if(maxTries!=null && !maxTries.isEmpty()){
            plantilla+="Llegaron al maximo de reintentos [Id,N Intentos, Ultimo error]:\n ";
            for(T x : maxTries){
                plantilla+="["+x.getId()+", "+x.getTried()+", "+x.getError()+"]\n";
            }
        }
        return plantilla;
    }

    public enum Operacion{
        CLOSE_FILE("cierre de expedientes"),UPGRADE_DOCUMENT("upgradeo de la firma del documento");

        String value;

        Operacion(String value){
            this.value=value;
        }

        public String getValue() {
            return value;
        }
    }
}
