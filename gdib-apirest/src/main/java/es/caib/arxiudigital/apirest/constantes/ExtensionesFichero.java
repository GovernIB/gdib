package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Extensiones de fichero permitidas dentro del archivo
 * 
 * @author u104848
 *
 */
public enum ExtensionesFichero {

	GML 			(".gml"),
	GZ 				(".gz"),
	ZIP 			(".zip"),
	AVI 			(".avi"),
	CSV 			(".csv"),
	HTML 			(".html"),
	HTM 			(".htm"),
	CSS 			(".css"),
	JPG 			(".jpg"),
	JPEG 			(".jpeg"),
	MHTML 			(".mhtml"),
	MHT 			(".mht"),
	ODT 			(".odt"),
	ODS 			(".ods"),
	ODP 			(".odp"),
	ODG 			(".odg"),
	DOCX 			(".docx"),
	XLSX 			(".xlsx"),
	PPTX 			(".pptx"),
	PDF 			(".pdf"),
	PNG 			(".png"),
	RTF 			(".rtf"),
	SVG 			(".svg"),
	TIFF 			(".tiff"),
	TXT 			(".txt"),
	MP3 			(".mp3"),
	OGG 			(".ogg"),
	OGA 			(".oga"),
	MPEG 			(".mpeg"),
	MP4 			(".mp4"),
	WEBM 			(".webm");

    private String value;

    private ExtensionesFichero(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonValue
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
