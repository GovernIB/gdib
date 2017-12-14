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
 * <b>File:</b><p>es.gob.afirma.signature.ValidationResult.java.</p>
 * <b>Description:</b><p>Class that contains all the information related to the result of a signature validation process.</p>
 * <b>Project:</b><p>Library for the integration with the services of @Firma, eVisor and TS@.</p>
 * <b>Date:</b><p>12/11/2014.</p>
 * @author Gobierno de España.
 * @version 1.0, 12/11/2014.
 */
package es.gob.afirma.signature;

import java.io.Serializable;
import java.util.List;

/** 
 * <p>Class that contains all the information related to the result of a signature validation process.</p>
 * <b>Project:</b><p>Library for the integration with the services of @Firma, eVisor and TS@.</p>
 * @version 1.0, 12/11/2014.
 */
public class ValidationResult implements Serializable {

	/**
	 * Class serial version.
	 */
	private static final long serialVersionUID = -4677489466636522937L;

	/**
	 * Attribute that represents the result of the validation, <code>true</code> if the validation was correct, or <code>false</code> if the validation fails.
	 */
	private boolean isCorrect = true;

	/**
	 * If the validation fails, this attribute represents the description of the cause. 
	 */
	private String errorDescription;

	/**
	 * Attribute that represents the list with the signers of the signature. 
	 */
	private List<SignerValidationInformation> listSigners;

	/**
	 * Gets the value of the attribute {@link #isCorrect}.
	 * @return the value of the attribute {@link #isCorrect}.
	 */
	public final boolean isCorrect() {
		return isCorrect;
	}

	/**
	 * Sets the value of the attribute {@link #isCorrect}.
	 * @param statusParam The value for the attribute {@link #isCorrect}.
	 */
	public final void setIsCorrect(boolean statusParam) {
		this.isCorrect = statusParam;
	}

	/**
	 * Gets the value of the attribute {@link #errorDescription}.
	 * @return the value of the attribute {@link #errorDescription}.
	 */
	public final String getErrorDescription() {
		return errorDescription;
	}

	/**
	 * Sets the value of the attribute {@link #errorDescription}.
	 * @param errorDescriptionParam The value for the attribute {@link #errorDescription}.
	 */
	public final void setErrorDescription(String errorDescriptionParam) {
		this.errorDescription = errorDescriptionParam;
	}

	/**
	 * Gets the value of the attribute {@link #listSigners}.
	 * @return the value of the attribute {@link #listSigners}.
	 */
	public final List<SignerValidationInformation> getListSigners() {
		return listSigners;
	}

	/**
	 * Sets the value of the attribute {@link #listSigners}.
	 * @param listSignersParam The value for the attribute {@link #listSigners}.
	 */
	public final void setListSigners(List<SignerValidationInformation> listSignersParam) {
		this.listSigners = listSignersParam;
	}

}
