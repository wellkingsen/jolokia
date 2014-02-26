package org.jolokia.core.service;

import java.util.Set;
import java.util.SortedSet;

import javax.management.*;

import org.jolokia.core.config.ConfigKey;
import org.jolokia.core.service.detector.ServerHandle;

/**
 * The context providing access to all Jolokia internal services. This context
 * will be given through during request handling to the various methods.
 * It is also an restrictor used for access restriction handling and responsible
 * for logging aspects.
 *
 * @author roland
 * @since 09.04.13
 */
public interface JolokiaContext extends LogHandler, Restrictor {

    /**
     * Get Jolokia services of a certain kind. The returned list might be empty,
     * but never null. The set is sorted according to the service order
     * (see {@link JolokiaService#getOrder()}).
     *
     * @param pType requested service type
     * @return sorted set of services or an empty set
     */
    <T extends JolokiaService> SortedSet<T> getServices(Class<T> pType);

    /**
     * Get a single service. If more than one service of the given type has been
     * registered, return the one with the highest order. If no one has been registered
     * return <code>null</code>
     *
     * @param pType requested service type
     * @return the requested service or null if none has been registered
     */
    <T extends JolokiaService> T getService(Class<T> pType);

    /**
     * Access to the server handle holding the server side information
     * of the detected server.
     *
     * @return the server handle
     */
    ServerHandle getServerHandle();

    /**
     * Register an MBean which gets automatically unregistered during shutdown.
     *
     * @param pMBean MBean to register
     * @param pOptionalName optional name under which the bean should be registered. If not provided,
     * it depends on whether the MBean to register implements {@link javax.management.MBeanRegistration} or
     * not.
     *
     * @return the name under which the MBean is registered.
     */
    ObjectName registerMBean(Object pMBean, String... pOptionalName)
            throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException;

    /**
     * Get a configuration value if set as configuration or the default
     * value if not
     *
     * @param pKey the configuration key to lookup
     * @return the configuration value or the default value if no configuration
     *         was given.
     */
    String getConfig(ConfigKey pKey);

    /**
     * Get all keys stored in this configuration
     */
    Set<ConfigKey> getConfigKeys();

    /**
     * Method to set the server handle. This can be called only once after which this
     * method is sealed. The setter is given so that a lazy initialization is easily possible
     * It MUST not be used by any custom code. So, I'm still looking for a cleaner solution here (probably
     * letting the handle return from the request handler service).
     *
     * @param pHandle handle to use.
     */
    void setServerHandle(ServerHandle pHandle);


    /**
     * Get the details which specify the current agent. The returned
     * details should not be kept but instead each time details are needed
     * this interface should be queried again.
     *
     * @return the details for this agent.
     */
    AgentDetails getAgentDetails();
}