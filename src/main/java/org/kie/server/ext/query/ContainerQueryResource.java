package org.kie.server.ext.query;

import static org.kie.server.api.rest.RestURI.TASKS_ASSIGN_POT_OWNERS_GET_URI;
import static org.kie.server.remote.rest.common.util.RestUtils.buildConversationIdHeader;
import static org.kie.server.remote.rest.common.util.RestUtils.createCorrectVariant;
import static org.kie.server.remote.rest.common.util.RestUtils.errorMessage;
import static org.kie.server.remote.rest.common.util.RestUtils.getVariant;
import static org.kie.server.remote.rest.common.util.RestUtils.internalServerError;
import static org.kie.server.remote.rest.jbpm.docs.ParameterSamples.GET_TASK_SUMMARY_RESPONSE_JSON;
import static org.kie.server.remote.rest.jbpm.docs.ParameterSamples.JSON;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.api.model.instance.TaskSummaryList;
import org.kie.server.remote.rest.common.Header;
import org.kie.server.services.api.KieServerRegistry;
import org.kie.server.services.jbpm.RuntimeDataServiceBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;

@Path("server/queries/containers/{container-id}")
public class ContainerQueryResource {

	private static final Logger logger = LoggerFactory.getLogger(ContainerQueryResource.class);

	private KieServerRegistry registry;
	private ProcessService processService;
	private DeploymentService deploymentService;
	private RuntimeDataServiceBase runtimeDataServiceBase;

	@ApiOperation(value = "Returns tasks with a user defined as a potential owner.", response = TaskSummaryList.class, code = 200)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Unexpected error"),
	        @ApiResponse(code = 200, message = "Successfull response", examples = @Example(value = {
	                @ExampleProperty(mediaType = JSON, value = GET_TASK_SUMMARY_RESPONSE_JSON) })) })
	@GET
	@Path(TASKS_ASSIGN_POT_OWNERS_GET_URI)
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getTasksAssignedAsPotentialOwner(@Context HttpHeaders headers,
	        @PathParam("container-id") String containerId,
	        @ApiParam(value = "optional task status (Created, Ready, Reserved, InProgress, Suspended, Completed, Failed, Error, Exited, Obsolete)", required = false, allowableValues = "Created, Ready, Reserved,InProgress,Suspended,Completed,Failed,Error,Exited,Obsolete") @QueryParam("status") List<String> status,
	        @ApiParam(value = "optional group names to include in the query", required = false, allowMultiple = true) @QueryParam("groups") List<String> groupIds,
			@ApiParam(value = "optional user id to be used instead of authenticated user - only when bypass authenticated user is enabled", required = false) @QueryParam("user") String userId,			
            @ApiParam(value = "optional pagination - at which page to start, defaults to 0 (meaning first)", required = false) @QueryParam("page") @DefaultValue("0") Integer page,
            @ApiParam(value = "optional pagination - size of the result, defaults to 10", required = false) @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
            @ApiParam(value = "optional sort column, no default", required = false) @QueryParam("sort") String sort,
            @ApiParam(value = "optional sort direction (asc, desc) - defaults to asc", required = false) @QueryParam("sortOrder") @DefaultValue("true") boolean sortOrder,
			@ApiParam(value = "optional custom filter for task data", required = false) @QueryParam("filter") String filter) 
	{
		Variant v = getVariant(headers);
		Header conversationIdHeader = buildConversationIdHeader(containerId, registry, headers);

		try {
			TaskSummaryList result = runtimeDataServiceBase.getTasksAssignedAsPotentialOwner(status, groupIds, userId,
			        page, pageSize, sort, sortOrder, filter);

			ArrayList<TaskSummary> filtered = new ArrayList<>();

			for (TaskSummary ts : result.getTasks()) {
				if (ts.getContainerId().equals(containerId))
					filtered.add(ts);
			}

			result.setTasks(filtered.toArray(new TaskSummary[0]));

			return createCorrectVariant(result, headers, Response.Status.OK, conversationIdHeader);
		} catch (Exception e) {
			logger.error("Unexpected error during processing {}", e.getMessage(), e);
			return internalServerError(errorMessage(e), v, conversationIdHeader);
		}
	}

	public KieServerRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(KieServerRegistry registry) {
		this.registry = registry;
	}

	public ProcessService getProcessService() {
		return processService;
	}

	public void setProcessService(ProcessService processService) {
		this.processService = processService;
	}

	public DeploymentService getDeploymentService() {
		return deploymentService;
	}

	public void setDeploymentService(DeploymentService deploymentService) {
		this.deploymentService = deploymentService;
	}

	/**
	 * @return the runtimeDataServiceBase
	 */
	public RuntimeDataServiceBase getRuntimeDataServiceBase() {
		return runtimeDataServiceBase;
	}

	/**
	 * @param runtimeDataServiceBase the runtimeDataServiceBase to set
	 */
	public void setRuntimeDataServiceBase(RuntimeDataServiceBase runtimeDataServiceBase) {
		this.runtimeDataServiceBase = runtimeDataServiceBase;
	}
}
