package es.caib.arxiudigital.apirest.tests;

import java.util.Random;

import es.caib.arxiudigital.apirest.ApiArchivoDigital;
import es.caib.arxiudigital.apirest.Utils.DatosConexion;

public abstract class ValidacionesBase {
	
	public static final String SENSE_VALOR = "Sense valor";
	
	
	//static final CabeceraPeticion cabeceraMock = UtilCabeceras.generarCabeceraMock();
	
	final ApiArchivoDigital apiArxiu;
	
	final boolean trazas;

	/**
   * @param apiArxiu
   */
  public ValidacionesBase(ApiArchivoDigital apiArxiu) {
    super();
    this.apiArxiu = apiArxiu;
    trazas = apiArxiu.isTrazas();
  }

  protected void printInicio(String nomTest){
	  if(this.apiArxiu.isTrazas()){
		  mostrar("\n---------------------------------");
		  mostrar("INICIO TEST "+nomTest);
		  mostrar("---------------------------------\n");
	  }
  }

  protected void printFin(String nomTest){
	  if(this.apiArxiu.isTrazas()){
		  mostrar("\n---------------------------------");
		  mostrar("FIN TEST "+nomTest+"\n");
		  mostrar("---------------------------------\n");
	  }
  }
	
	protected static void mostrar(String texto){
		System.out.println(texto);
	}

	protected static String getURLConexion(){
		return DatosConexion.ENDPOINT_DEV;
	}
	
	protected static String getURLConexion(String servicio){
		return DatosConexion.ENDPOINT_DEV+servicio;
	}
	
	public static String obtenerStringAleatorio(){
		Random  rnd = new Random();
		
		return Long.toString(rnd.nextLong());
	}
	
}
