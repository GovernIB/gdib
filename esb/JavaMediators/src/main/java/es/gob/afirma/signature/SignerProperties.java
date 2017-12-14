// Copyright (C) 2012-13 MINHAP, Gobierno de España
// This program is licensed and may be used, modified and redistributed under the terms
// of the European Public License (EUPL), either version 1.1 or (at your
// option) any later version as soon as they are approved by the European Commission.
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
// or implied. See the License for the specific language governing permissions and
// more details.
// You should have received a copy of the EUPL1.1 license
// along with this program; if not, you may find it at
// http://joinup.ec.europa.eu/software/page/eupl/licence-eupl

/**
 * <b>File:</b><p>es.gob.afirma.signature.SignerProperties.java.</p>
 * <b>Description:</b><p>Class that allows to access to the properties defined inside of the configuration file <code>signer.properties</code> used by
 * {@link es.gob.afirma.signature.Signer}.</p>
 * <b>Project:</b><p>@Firma and TS@ Web Services Integration Platform.</p>
 * <b>Date:</b><p>30/07/2015.</p>
 * @author Gobierno de España.
 * @version 1.0, 30/07/2015.
 */
package es.gob.afirma.signature;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

import es.caib.archivodigital.esb.services.mediators.afirma.i18n.CaibEsbLanguage;
import es.gob.afirma.i18n.ILogConstantKeys;
import es.gob.afirma.utils.UtilsResources;

/**
 * <p>Class that allows to access to the properties defined inside of the configuration file <code>signer.properties</code> used by
 * {@link es.gob.afirma.signature.Signer}.</p>
 * <b>Project:</b><p>@Firma and TS@ Web Services Integration Platform.</p>
 * @version 1.0, 30/07/2015.
 */
public final class SignerProperties {

	/**
	 * Attribute that represents the class logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SignerProperties.class);
	

	/**
	 * Attribute that represents the set of properties defined inside of the configuration file <code>signer.properties</code> used by
	 * {@link es.gob.afirma.signature.Signer}.
	 */
	private static Properties signerProperties = new Properties();

	/**
	 * Constructor method for the class SignerProperties.java.
	 */
	private SignerProperties() {
	}

	/**
	 * Gets the value of the attribute {@link #signerProperties}.
	 * @return the value of the attribute {@link #signerProperties}.
	 */
	public static Properties getSignerProperties() {
		// Accedemos al archivo de propiedades asociados a la interfaz Signer
		URL url = SignerProperties.class.getClassLoader().getResource(SignerConstants.SIGNER_PROPERTIES);
		if (url == null) {
			LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.SP_LOG001, new Object[ ] { SignerConstants.SIGNER_PROPERTIES }));
		} else {
			InputStream in = null;
			try {
				signerProperties = new Properties();
				in = new FileInputStream(new File(new URI(url.toString())));
				signerProperties.load(in);
				
					
			} catch (Exception e) {
				LOGGER.error(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.SP_LOG002, new Object[ ] { SignerConstants.SIGNER_PROPERTIES }));
			} finally {
				UtilsResources.safeCloseInputStream(in);
			}
		}
		return signerProperties;
	}

}
