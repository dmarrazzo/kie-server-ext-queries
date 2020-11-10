package org.kie.server.ext.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.jbpm.services.api.AdvanceRuntimeDataService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.server.services.api.KieServerApplicationComponentsService;
import org.kie.server.services.api.KieServerRegistry;
import org.kie.server.services.api.SupportedTransports;
import org.kie.server.services.jbpm.RuntimeDataServiceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ContainersQueriesComponentsService implements KieServerApplicationComponentsService {
    private static final Logger logger = LoggerFactory.getLogger(ContainersQueriesComponentsService.class);

    private static final String OWNER_EXTENSION = "jBPM";
    
    public Collection<Object> getAppComponents(String extension, SupportedTransports type, Object... services) {
    	logger.info("begin - ext: {}", extension);
    	
        // skip calls from other than owning extension
        if ( !OWNER_EXTENSION.equals(extension) ) {
            return Collections.emptyList();
        }
        
        ContainerQueryResource customResource = new ContainerQueryResource();

        RuntimeDataService runtimeDataService = null;
        AdvanceRuntimeDataService advanceRuntimeDataService = null;
        
        for (Object object : services) {
            if( ProcessService.class.isAssignableFrom(object.getClass()) ) {
            	customResource.setProcessService((ProcessService) object);
            } else if (DeploymentService.class.isAssignableFrom(object.getClass())) {
            	customResource.setDeploymentService((DeploymentService) object);
            } else if( KieServerRegistry.class.isAssignableFrom(object.getClass()) ) {
            	customResource.setRegistry((KieServerRegistry) object);
			} else if (RuntimeDataService.class.isAssignableFrom(object.getClass())) {
                runtimeDataService = (RuntimeDataService) object;
			} else if (AdvanceRuntimeDataService.class.isAssignableFrom(object.getClass())) {
                advanceRuntimeDataService = (AdvanceRuntimeDataService) object;
            }
        }

        RuntimeDataServiceBase runtimeDataServiceBase = new RuntimeDataServiceBase(runtimeDataService,
                advanceRuntimeDataService, customResource.getRegistry());
        customResource.setRuntimeDataServiceBase(runtimeDataServiceBase);

        ArrayList<Object> components = new ArrayList<>(1);
		if( SupportedTransports.REST.equals(type) ) {
			components.add(customResource);
        }
        
    	logger.info("end");
        return components;

    }

}
