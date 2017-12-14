package es.caib.arxiudigital.apirest.CSGD.entidades.comunes;

public class Metadata implements Cloneable{
	private String qname;
	private Object value;
	
	public String getQname() {
		return qname;
	}
	public void setQname(String qname) {
		this.qname = qname;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}	
	
	 public Object clone(){
	        Object obj=null;
	        try{
	            obj=super.clone();
	        }catch(CloneNotSupportedException ex){
	            System.out.println(" no se puede duplicar");
	        }
	        return obj;
	    }
}
