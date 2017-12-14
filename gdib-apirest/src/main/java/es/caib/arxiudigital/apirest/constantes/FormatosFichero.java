package es.caib.arxiudigital.apirest.constantes;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Extensiones de fichero permitidas dentro del archivo
 * 
 * @author u104848
 *
 */
public enum FormatosFichero {

	GML 			("GML"),
	WFS				("WFS"),
	WMS 			("WMS"),
	GZIP 			("GZIP"),
	ZIP 			("ZIP"),
	AVI 			("AVI"),
	MP4A			("MPEG-4 MP4 media"),
	CSV 			("Comma Separated Values"),
	HTML 			("HTML"),
	CSS 			("CSS"),
	JPEG 			("JPEG"),
	MHTML 			("MHTML"),
	OASIS12			("ISO/IEC 26300:2006 OASIS 1.2"),
	SOXML 			("Strict Open XML"),
	PDF 			("PDF"),
	PDFA 			("PDF/A"),
	PNG 			("PNG"),
	RTF 			("RTF"),
	SVG 			("SVG"),
	TIFF 			("TIFF"),
	TXT 			("TXT"),
	XHTML 			("XHTML"),
	MP3 			("MP3. MPEG-1 Audio Layer 3"),
	OGG 			("OGG-Vorbis"),
	MP4V 			("MPEG-4 MP4 vídeo"),
	WEBM 			("WebM");

    private String value;

    private FormatosFichero(String value) {
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
