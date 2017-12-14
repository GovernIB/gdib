package es.rsits.ws.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.alfresco.service.transaction.TransactionService;
import org.apache.log4j.Logger;

import es.rsits.ws.exception.WSException;

public class WSTransaction {

	private static final Logger LOGGER = Logger.getLogger(WSTransaction.class);

	private TransactionService transactionService;

	private UserTransaction usrTrx;


	public void createTransaction() throws WSException {
		LOGGER.debug("Begin Alfresco Transaction");
		usrTrx = transactionService.getUserTransaction();
		try {
			usrTrx.begin();
		} catch (NotSupportedException e) {
			LOGGER.error(e.getMessage(), e);
			throw new WSException(e.getMessage(), e);
		} catch (SystemException e) {
			throw new WSException(e.getMessage(), e);
		}
	}

	public void commit() throws WSException {
		LOGGER.debug("Commit Alfresco Transaction");
		try {
			usrTrx.commit();
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
			throw new WSException(e.getMessage(), e);
		} catch (IllegalStateException e) {
			LOGGER.error(e.getMessage(), e);
			throw new WSException(e.getMessage(), e);
		} catch (RollbackException e) {
			LOGGER.error(e.getMessage(), e);
			throw new WSException(e.getMessage(), e);
		} catch (HeuristicMixedException e) {
			LOGGER.error(e.getMessage(), e);
			throw new WSException(e.getMessage(), e);
		} catch (HeuristicRollbackException e) {
			LOGGER.error(e.getMessage(), e);
			throw new WSException(e.getMessage(), e);
		} catch (SystemException e) {
			LOGGER.error(e.getMessage(), e);
			throw new WSException(e.getMessage(), e);
		}
	}

	public void rollback() throws WSException {
		LOGGER.debug("Rollback on Alfresco Transaction");
		try {
			usrTrx.rollback();
		} catch (IllegalStateException e) {
			LOGGER.error(e.getMessage(), e);
			throw new WSException(e.getMessage(), e);
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
			throw new WSException(e.getMessage(), e);
		} catch (SystemException e) {
			LOGGER.error(e.getMessage(), e);
			throw new WSException(e.getMessage(), e);
		}
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
}
