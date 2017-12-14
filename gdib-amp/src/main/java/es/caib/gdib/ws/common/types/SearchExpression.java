package es.caib.gdib.ws.common.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

enum Operacion{
	AND,OR,NOT
}

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchExpression", propOrder = {
	"id",
	"type",
},namespace = "http://www.caib.es/gdib/repository/ws")
public class SearchExpression {
	String param;
	String value;
	
	SearchExpression operador1;
	Operacion operacion;
	SearchExpression operador2;
}
