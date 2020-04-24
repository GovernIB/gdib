package es.rsits.ws.auth;

import java.util.Map;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.apache.commons.lang3.StringUtils;

import es.rsits.ws.exception.WSException;

public class WSAuthentication {

	private AuthenticationService authenticationService;

	@SuppressWarnings("rawtypes")
	public void doAuthentication(WebServiceContext wsctx) throws WSException {
		MessageContext mctx = wsctx.getMessageContext();
		Map http_headers2 = (Map) mctx.get("org.apache.chemistry.opencmis.callcontext");
		String username = (String) http_headers2.get("username");
		String password = (String) http_headers2.get("password");

		doAuthentication(username, password);
	}

	public void doAuthentication(String username, String password) throws WSException {
		try {
			// si el usuario viene vacio, se tiene que validar el ticket de autenticacion de alfresco
			if(StringUtils.isEmpty(username)){
				if(password==null)
					throw new WSException("You need authentication to perfom this operation");
				authenticationService.validate(password);
			}else{
				// y sino realizar la autenticacion normal de usuario y password
				if(StringUtils.isEmpty(password)){
					throw new WSException("Username and Password are mandatory");
				}
				// login con usuario y password
				authenticationService.authenticate(username, password.toCharArray());
			}
		}catch (AuthenticationException ae){
			throw new WSException(ae.getMessage());
		}
	}

	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

}
