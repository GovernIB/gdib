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
 * <b>File:</b><p>es.gob.afirma.signature.CryptoUtil.java.</p>
 * <b>Description:</b><p> Utility class contains encryption and hash functions for digital signature.</p>
 * <b>Project:</b><p>Library for the integration with the services of @Firma, eVisor and TS@.</p>
 * <b>Date:</b><p>29/06/2011.</p>
 * @author Gobierno de España.
 * @version 1.0, 29/06/2011.
 */
package es.gob.afirma.utils;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

import javax.xml.crypto.dsig.DigestMethod;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import es.caib.archivodigital.esb.services.mediators.afirma.i18n.CaibEsbLanguage;
import es.gob.afirma.i18n.ILogConstantKeys;
import es.gob.afirma.signature.SigningException;

/**
 * <p>Utility class contains encryption and hash functions for digital signature.</p>
 * <b>Project:</b><p>Library for the integration with the services of @Firma, eVisor and TS@.</p>
 * @version 1.0, 29/06/2011.
 */
public final class CryptoUtil {

	/**
	 * Attribute that represents the name of hash algorithm for SHA1.
	 */
	public static final String HASH_ALGORITHM_SHA1 = "SHA-1";

	/**
	 * Attribute that represents the name of hash algorithm for SHA256.
	 */
	public static final String HASH_ALGORITHM_SHA256 = "SHA-256";

	/**
	 * Attribute that represents the name of hash algorithm for SHA384.
	 */
	public static final String HASH_ALGORITHM_SHA384 = "SHA-384";

	/**
	 * Attribute that represents the name of hash algorithm for SHA512.
	 */
	public static final String HASH_ALGORITHM_SHA512 = "SHA-512";

	/**
	 * Attribute that represents the name of hash algorithm for RIPEMD-160.
	 */
	public static final String HASH_ALGORITHM_RIPEMD160 = "RIPEMD-160";

	/**
	 * Attribute that represents RSA encryption algorithm.
	 */
	public static final String ENCRYPTION_ALGORITHM_RSA = "RSA";

	/**
	 * Constructor method for the class CryptoUtil.java.
	 */
	private CryptoUtil() {
	}

	/**
	 * Method that obtains the hash computation from a array bytes.
	 * @param algorithm Parameter that represents the algorithm used in the hash computation.
	 * @param data Parameter that represents the source data.
	 * @return the hash value.
	 * @throws SigningException If a MessageDigestSpi implementation for the specified algorithm is not available
	 * from the specified Provider object.
	 */
	public static byte[ ] digest(String algorithm, byte[ ] data) throws SigningException {
		if (GenericUtils.assertStringValue(algorithm) && GenericUtils.assertArrayValid(data)) {
			Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
			try {
				MessageDigest messageDigest = MessageDigest.getInstance(algorithm, provider);
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
				byte[ ] tmp = new byte[NumberConstants.INT_1024];
				int length = 0;
				while ((length = bais.read(tmp, 0, tmp.length)) >= 0) {
					messageDigest.update(tmp, 0, length);
				}
				return messageDigest.digest();
			} catch (NoSuchAlgorithmException e) {
				throw new SigningException(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.CU_LOG001, new Object[ ] { algorithm }), e);
			}
		}
		return null;
	}

	/**
	 * Method that obtains the OID of a hash algorithm from the name.
	 * @param hashAlgorithm Parameter that represents the name of the hash algorithm.
	 * @return the OID value.
	 */
	public static AlgorithmIdentifier getAlgorithmIdentifierByName(String hashAlgorithm) {
		if (hashAlgorithm == null) {
			return null;
		}
		if (hashAlgorithm.equals(HASH_ALGORITHM_SHA1)) {
			return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
		} else if (hashAlgorithm.equals(HASH_ALGORITHM_SHA256)) {
			return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
		} else if (hashAlgorithm.equals(HASH_ALGORITHM_SHA384)) {
			return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384);
		} else if (hashAlgorithm.equals(HASH_ALGORITHM_SHA512)) {
			return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512);
		} else if (hashAlgorithm.equals(HASH_ALGORITHM_RIPEMD160)) {
			return new AlgorithmIdentifier(X509ObjectIdentifiers.ripemd160);
		} else {
			return null;
		}
	}

	/**
	 * Method that translates a {@link AlgorithmIdentifier} object to a digest algorithm string.
	 * @param digestAlg Parameter that represents the digest method algorithm URI.
	 * @return the digest algorithm name.
	 */
	public static String translateXmlDigestAlgorithm(String digestAlg) {
		if (digestAlg == null) {
			return null;
		}
		if (DigestMethod.SHA1.equals(digestAlg)) {
			return HASH_ALGORITHM_SHA1;
		} else if (DigestMethod.SHA256.equals(digestAlg)) {
			return HASH_ALGORITHM_SHA256;
		} else if ("http://www.w3.org/2001/04/xmldsig-more#sha384".equals(digestAlg)) {
			return HASH_ALGORITHM_SHA384;
		} else if (DigestMethod.SHA512.equals(digestAlg)) {
			return HASH_ALGORITHM_SHA512;
		} else {
			return null;
		}
	}

	/**
	 * Method that gets the name of a digest algorithm by name or alias.
	 * @param pseudoName Parameter that represents the name or alias of the digest algorithm.
	 * @return the name of the digest algorithm.
	 */
	public static String getDigestAlgorithmName(String pseudoName) {
		String upperPseudoName = pseudoName.toUpperCase();
		if (upperPseudoName.equals("SHA") || upperPseudoName.startsWith("SHA1") || upperPseudoName.startsWith("SHA-1")) {
			return HASH_ALGORITHM_SHA1;
		} else if (upperPseudoName.startsWith("SHA256") || upperPseudoName.startsWith("SHA-256")) {
			return HASH_ALGORITHM_SHA256;
		} else if (upperPseudoName.startsWith("SHA384") || upperPseudoName.startsWith("SHA-384")) {
			return HASH_ALGORITHM_SHA384;
		}
		return getDigestAlgorithmNameAux(pseudoName);
	}

	/**
	 * Method that gets the name of a digest algorithm by name or alias.
	 * @param pseudoName Parameter that represents the name or alias of the digest algorithm.
	 * @return the name of the digest algorithm.
	 */
	private static String getDigestAlgorithmNameAux(String pseudoName) {
		String upperPseudoName = pseudoName.toUpperCase();
		if (upperPseudoName.startsWith("SHA512") || upperPseudoName.startsWith("SHA-512")) {
			return HASH_ALGORITHM_SHA512;
		} else if (upperPseudoName.startsWith("RIPEMD160") || upperPseudoName.startsWith("RIPEMD-160")) {
			return HASH_ALGORITHM_SHA512;
		} else {
			throw new IllegalArgumentException(CaibEsbLanguage.getFormatResIntegra(ILogConstantKeys.CU_LOG002, new Object[ ] { pseudoName }));
		}
	}

	/**
	 * Method that obtains the MessageImprint from the digest method algorithm URI.
	 * @param hashAlgXML Parameter that represents the digest method algorithm URI.
	 * @param data Parameter that represents the hashed message.
	 * @return the generated MessageImprint.
	 */
	public static MessageImprint generateMessageImprintFromXMLAlgorithm(String hashAlgXML, byte[ ] data) {
		AlgorithmIdentifier algoritmID = null;
		if (hashAlgXML.equalsIgnoreCase(DigestMethod.SHA1)) {
			algoritmID = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
		} else if (hashAlgXML.equalsIgnoreCase(DigestMethod.SHA256)) {
			algoritmID = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
		} else if (hashAlgXML.equalsIgnoreCase(DigestMethod.SHA512)) {
			algoritmID = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512);
		} else if (hashAlgXML.equalsIgnoreCase(DigestMethod.RIPEMD160)) {
			algoritmID = new AlgorithmIdentifier(X509ObjectIdentifiers.ripemd160);
		} else {
			return null;
		}

		return new MessageImprint(algoritmID, data);
	}
}
