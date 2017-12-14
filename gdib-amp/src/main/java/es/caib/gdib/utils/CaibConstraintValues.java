package es.caib.gdib.utils;

import java.util.List;

/**
 * Enumerado para evaluar los metadatos de tipo lista generados en el archivo XML de metadatos del documento
 *
 * @author Ricoh
 *
 */
public enum CaibConstraintValues {
	clase(ConstantUtils.PROP_CLASE, ConstantUtils.CLASE_VALUES),
	categoria(ConstantUtils.PROP_CATEGORIA, ConstantUtils.CATEGORIA_VALUES),
	lopd(ConstantUtils.PROP_LOPD, ConstantUtils.LOPD_VALUES),
	confidencialidad(ConstantUtils.PROP_CONFIDENCIALIDAD, ConstantUtils.CONFIDENCIALIDAD_VALUES),
	tipo_acceso(ConstantUtils.PROP_TIPO_ACCESO, ConstantUtils.TIPO_ACCESO_VALUES),
	fase_archivo(ConstantUtils.PROP_FASE_ARCHIVO, ConstantUtils.FASE_ARCHIVO_VALUES),
	tipo_asiento_registral(ConstantUtils.PROP_TIPO_ASIENTO_REGISTRAL, ConstantUtils.TIPO_ASIENTO_REGISTRAL_VALUES),
	perfil_firma(ConstantUtils.PROP_PERFIL_FIRMA, ConstantUtils.PERFIL_FIRMA_VALUES),
	origen(ConstantUtils.PROP_ORIGEN, ConstantUtils.ORIGEN_VALUES),
	estado_elaboracion(ConstantUtils.PROP_ESTADO_ELABORACION, ConstantUtils.ESTADO_ELABORACION_VALUES),
	tipo_doc_eni(ConstantUtils.PROP_TIPO_DOC_ENI, ConstantUtils.TIPO_DOC_ENI_VALUES),
	estado_exp(ConstantUtils.PROP_ESTADO_EXP, ConstantUtils.ESTADO_EXP_VALUES)
	;

	private String name;
	private List<String> values;

	private CaibConstraintValues(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

	public String getName() {
		return name;
	}

	public List<String> getValues() {
		return values;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public static CaibConstraintValues getByName(String name) {

        for (CaibConstraintValues e : CaibConstraintValues.values()) {
            if (name.equals(e.getName())) {
                return e;
            }
        }
        return null;
    }
}
