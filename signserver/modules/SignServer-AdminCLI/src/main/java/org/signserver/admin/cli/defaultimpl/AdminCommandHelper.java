/*************************************************************************
 *                                                                       *
 *  SignServer: The OpenSource Automated Signing Server                  *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.signserver.admin.cli.defaultimpl;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.logging.Level;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.cesecore.audit.audit.SecurityEventsAuditorSessionRemote;
import org.signserver.cli.spi.IllegalCommandArgumentsException;
import org.signserver.common.CESeCoreModules;
import org.signserver.common.GlobalConfiguration;
import org.signserver.common.InvalidWorkerIdException;
import org.signserver.common.ServiceLocator;
import org.signserver.common.WorkerConfig;
import org.signserver.ejb.interfaces.IGlobalConfigurationSession;
import org.signserver.ejb.interfaces.IWorkerSession;
import org.signserver.statusrepo.IStatusRepositorySession;

/**
 * Helper class with methods useful for many Command implementations.
 *
 * @author Markus Kilås
 * @version $Id$
 */
public class AdminCommandHelper {
    
    /** Logger for this class. */
    private static Logger LOG = Logger.getLogger(AdminCommandHelper.class);
    
    /** The global configuration session. */
    private IGlobalConfigurationSession.IRemote globalConfig;

    /** The SignSession. */
    private IWorkerSession.IRemote signsession;
    
    /** The StatusRepositorySession. */
    private IStatusRepositorySession.IRemote statusRepository;
    
    private SecurityEventsAuditorSessionRemote auditorSession;
    
    /**
     * Gets GlobalConfigurationSession Remote.
     * @return SignServerSession
     * @throws RemoteException in case the lookup failed
     */
    public IGlobalConfigurationSession.IRemote getGlobalConfigurationSession()
            throws RemoteException {
        if (globalConfig == null) {
            try {
                globalConfig = ServiceLocator.getInstance().lookupRemote(
                        IGlobalConfigurationSession.IRemote.class);
            } catch (NamingException e) {
                LOG.error("Error instanciating the GlobalConfigurationSession.", e);
                throw new RemoteException("Error instanciating the GlobalConfigurationSession", e);
            }
        }
        return globalConfig;
    }

    /**
     * Gets StatusRepositorySession Remote.
     * @return SignServerSession
     * @throws RemoteException in case the lookup failed
     */
    public IStatusRepositorySession.IRemote getStatusRepositorySession()
            throws RemoteException {
        if (statusRepository == null) {
            try {
                statusRepository = ServiceLocator.getInstance().lookupRemote(
                        IStatusRepositorySession.IRemote.class);
            } catch (NamingException e) {
                LOG.error("Error instanciating the StatusRepositorySession.", e);
                throw new RemoteException(
                        "Error instanciating the StatusRepositorySession", e);
            }
        }
        return statusRepository;
    }

    /**
     * Gets SignServerSession Remote.
     * @return SignServerSession
     * @throws RemoteException in case the lookup failed
     */
    public IWorkerSession.IRemote getWorkerSession() throws RemoteException {
        if (signsession == null) {
            try {
                signsession = ServiceLocator.getInstance().lookupRemote(
                        IWorkerSession.IRemote.class);
            } catch (NamingException e) {
                LOG.error("Error looking up signserver interface");
                throw new RemoteException("Error looking up signserver interface", e);
            }
        }
        return signsession;
    }
    
    public SecurityEventsAuditorSessionRemote getAuditorSession() throws RemoteException {
        if (auditorSession == null) {
            try {
                auditorSession = ServiceLocator.getInstance().lookupRemote(
                        SecurityEventsAuditorSessionRemote.class, CESeCoreModules.CORE);
            } catch (NamingException e) {
                LOG.error("Error instantiating the SecurityEventsAuditorSession.", e);
                throw new RemoteException("Error instantiating the SecurityEventsAuditorSession", e);
            }
        }
        return auditorSession;
    }
    
    /**
     * Help Method that retrieves the id of a worker given either
     * it's id in string format or the name of a worker
     * @throws Exception 
     * @throws RemoteException 
     */
    public int getWorkerId(String workerIdOrName) throws RemoteException, IllegalCommandArgumentsException {
        final int retval;

        if (workerIdOrName.substring(0, 1).matches("\\d")) {
            retval = Integer.parseInt(workerIdOrName);
        } else {
            try {
                retval = getWorkerSession().getWorkerId(workerIdOrName);
            } catch (InvalidWorkerIdException ex) {
                throw new IllegalCommandArgumentsException("Error: No worker with the given name could be found");
            }
        }

        return retval;
    }

    /**
     * Help method that checks that the current worker is a signer
     * @throws Exception 
     * @throws RemoteException 
     */
    public void checkThatWorkerIsProcessable(int signerid) throws RemoteException, IllegalCommandArgumentsException {
        Collection<Integer> signerIds = getWorkerSession().getWorkers(WorkerConfig.WORKERTYPE_PROCESSABLE);
        if (!signerIds.contains(new Integer(signerid))) {
            throw new IllegalCommandArgumentsException("Error: given workerId doesn't seem to point to any processable worker in the system.");
        }
    }
}
