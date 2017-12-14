package es.caib.archivodigital.esb.services.mediators.wss;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.synapse.Mediator;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;

public class UsernameTokenRestMediator implements Mediator {

	/**
	 *  Attribute that represents the object that manages the log of the class.
	 */
	private static final Logger LOGGER = Logger.getLogger(UsernameTokenRestMediator.class);
	
	private String username;
	
	private String password;
	
	@Override
	public boolean mediate(MessageContext messageContext) {
		org.apache.axis2.context.MessageContext axis2MsgContext = ((Axis2MessageContext) messageContext)
				.getAxis2MessageContext();
		
		LOGGER.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Modificando rampartConfigCallbackProperties");
		String usernamePropValue = messageContext.getProperty(username).toString();
		String passwordPropValue = messageContext.getProperty(password).toString();
		
		Map<String, String> rampConfigCBProperties = new HashMap<String, String>();
			
		rampConfigCBProperties.put("user_name", usernamePropValue);
		messageContext.setProperty("rampartConfigCallbackProperties",rampConfigCBProperties);
    
        messageContext.setProperty("username", usernamePropValue);
        axis2MsgContext.setProperty("username", usernamePropValue);
		axis2MsgContext.getOptions().setUserName(usernamePropValue);
		axis2MsgContext.getOptions().setPassword(passwordPropValue);
		
		LOGGER.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! rampartConfigCallbackProperties Modificadas");
		
		return true;
	}

	public String getUsername() {
		if (username == null) {
			username = "";
		}
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescription(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getMediatorPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShortDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTraceState() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isContentAware() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setMediatorPosition(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setShortDescription(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTraceState(int arg0) {
		// TODO Auto-generated method stub

	}

}
