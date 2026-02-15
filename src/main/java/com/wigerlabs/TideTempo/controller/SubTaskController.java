package com.wigerlabs.TideTempo.controller;

import com.wigerlabs.TideTempo.dto.SubTaskDTO;
import com.wigerlabs.TideTempo.service.SubTaskService;
import com.wigerlabs.TideTempo.util.AppUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/subtask")
public class SubTaskController {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSubTask(String jsonData, @Context HttpServletRequest request) {
        SubTaskDTO subTaskDTO = AppUtil.GSON.fromJson(jsonData, SubTaskDTO.class);
        String responseJson = new SubTaskService().addSubTask(subTaskDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSubTasks(@Context HttpServletRequest request) {
        String responseJson = new SubTaskService().getAllSubTasks(request);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubTaskById(@PathParam("id") int id, @Context HttpServletRequest request) {
        String responseJson = new SubTaskService().getSubTaskById(id, request);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Path("/task/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubTasksByTaskId(@PathParam("taskId") int taskId, @Context HttpServletRequest request) {
        String responseJson = new SubTaskService().getSubTasksByTaskId(taskId, request);
        return Response.ok().entity(responseJson).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSubTask(String jsonData, @Context HttpServletRequest request) {
        SubTaskDTO subTaskDTO = AppUtil.GSON.fromJson(jsonData, SubTaskDTO.class);
        String responseJson = new SubTaskService().updateSubTask(subTaskDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSubTask(@PathParam("id") int id, @Context HttpServletRequest request) {
        String responseJson = new SubTaskService().deleteSubTask(id, request);
        return Response.ok().entity(responseJson).build();
    }

    @PUT
    @Path("/{id}/complete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markAsComplete(@PathParam("id") int id, @Context HttpServletRequest request) {
        String responseJson = new SubTaskService().markAsComplete(id, request);
        return Response.ok().entity(responseJson).build();
    }

    @PUT
    @Path("/{id}/ongoing")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markAsOngoing(@PathParam("id") int id, @Context HttpServletRequest request) {
        String responseJson = new SubTaskService().markAsOngoing(id, request);
        return Response.ok().entity(responseJson).build();
    }
}
