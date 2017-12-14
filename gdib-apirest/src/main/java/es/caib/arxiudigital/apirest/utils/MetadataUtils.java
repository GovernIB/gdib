package es.caib.arxiudigital.apirest.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.caib.arxiudigital.apirest.CSGD.entidades.comunes.Metadata;
import es.caib.arxiudigital.apirest.CSGD.entidades.resultados.GetDocumentResult;
import es.caib.arxiudigital.apirest.constantes.EstadosElaboracion;
import es.caib.arxiudigital.apirest.constantes.MetadatosDocumento;
import es.caib.arxiudigital.apirest.constantes.MetadatosExpediente;
import es.caib.arxiudigital.apirest.constantes.OrigenesContenido;
import es.caib.arxiudigital.apirest.constantes.TiposDocumentosENI;
import es.caib.arxiudigital.apirest.facade.pojos.Documento;

public class MetadataUtils {

  


  /**
   * Busca un metadato por su nombre dentro de una lista de Matedatos y
   * devuelve una copia de ese metadato.
   * 
   * Si no lo encuentra, devuelve el valor null
   * 
   * @param nombreMetadato
   * @param metadataCollection
   * @return
   */
  public static Metadata buscarMetaDato(String nombreMetadato, List<Metadata>  metadataCollection){
    Metadata rtdo = null;
    
    for (Metadata mt : metadataCollection){
      if(nombreMetadato.equalsIgnoreCase(mt.getQname())){
        //Encontrado
        rtdo = (Metadata) mt.clone();
      } 
    }
    return rtdo;
  }
  
  
  /**
   * Busca un metadato por su nombre dentro de documento y
   * devuelve su valor
   * 
   * Si no lo encuentra, devuelve el valor null
   * 
   * @param nombreMetadato
   * @param metadataCollection
   * @return
   */
  public static Object buscarValorMetaDato(String nombreMetadato, Documento documento){
    Object rtdo = null;

    if(documento.getMetadataCollection() != null){
      rtdo = documento.getMetadataCollection().get(nombreMetadato);
    }
    return rtdo;
  }
  
  /**
   * Busca un metadato por su nombre dentro de un objeto GetDocumentResult y
   * devuelve una copia de ese metadato.
   * 
   * Si no lo encuentra, devuelve el valor null
   * 
   * @param nombreMetadato
   * @param metadataCollection
   * @return
   */
  public static Metadata buscarMetaDato(String nombreMetadato, GetDocumentResult result){
    
    List<Metadata>  metadataCollection = getListaMetadatos(result);
      
    return buscarMetaDato(nombreMetadato,metadataCollection);
  }
  
  private static List<Metadata>  getListaMetadatos(GetDocumentResult result){
    return result.getGetDocumentResult().getResParam().getMetadataCollection();
  }

  public static  Map<String, Object>  CrearListaMetadatosExpediente (
      String codiAplTramite,
      String serieDocumental,
      String codigoProcedimiento,
      String fechaInicio,
      List<String> listaOrganos,
      OrigenesContenido origen
      ) {

    Map<String, Object> metadataCollection  = new HashMap<String, Object>();

    metadataCollection.put(MetadatosExpediente.CODIGO_APLICACION_TRAMITE,codiAplTramite);
    metadataCollection.put(MetadatosExpediente.ORIGEN,origen);
    metadataCollection.put(MetadatosExpediente.CODIGO_CLASIFICACION,serieDocumental);
    metadataCollection.put(MetadatosExpediente.IDENTIFICADOR_PROCEDIMIENTO,codigoProcedimiento);
    metadataCollection.put(MetadatosExpediente.ORGANO,listaOrganos);

    if (fechaInicio!=null){
      metadataCollection.put(MetadatosExpediente.FECHA_INICIO,fechaInicio);
    }
    
    return metadataCollection;
  }
  
  
  
  public static Map<String, Object> CrearListaMetadatosDocumentoMinima (
      String codiAplTramite,
      String serieDocumental,
      TiposDocumentosENI tipoDocEni,
      List<String> listaOrganos,
      EstadosElaboracion estadoElaboracion,
      OrigenesContenido origen
      ) {

    Map<String, Object>  metadataCollection  = new HashMap<String, Object>();

    metadataCollection.put(MetadatosDocumento.CODIGO_APLICACION_TRAMITE,codiAplTramite);
    metadataCollection.put(MetadatosDocumento.CODIGO_CLASIFICACION,serieDocumental);
    metadataCollection.put(MetadatosDocumento.TIPO_DOC_ENI,tipoDocEni);
    metadataCollection.put(MetadatosDocumento.ORGANO,listaOrganos);
    metadataCollection.put(MetadatosDocumento.ESTADO_ELABORACION,estadoElaboracion);
    metadataCollection.put(MetadatosDocumento.ORIGEN,origen);

    return metadataCollection;
  }
  
  
  public static  List<Metadata> generarListaMetadatos(Map<String, Object> mapaMetadatos){
    List<Metadata> lista = null;

    if(mapaMetadatos!=null){
      lista = new ArrayList<Metadata>();
      
      for (Map.Entry<String, Object> entry : mapaMetadatos.entrySet()) {
        Metadata metadata = new Metadata();

        metadata.setQname(entry.getKey());
        metadata.setValue(entry.getValue());
        lista.add(metadata);

      }

    }
    return lista;
  }
  

  public static Map<String, Object> generarMapaMetadatos(List<Metadata> lista){
    Map<String, Object> mapa  = null;
    
    if(lista!=null){
      mapa  = new HashMap<String, Object>();
      for (Metadata mt : lista){
        mapa.put(mt.getQname(), mt.getValue());

      }
    }
    return mapa;
  }
  
}
