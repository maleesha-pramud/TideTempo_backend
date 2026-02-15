package com.wigerlabs.TideTempo.controller;

import com.wigerlabs.TideTempo.dto.TaskDTO;
import com.wigerlabs.TideTempo.service.TaskService;
import com.wigerlabs.TideTempo.util.AppUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/task")
public class TaskController {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTask(String jsonData, @Context HttpServletRequest request) {
        TaskDTO taskDTO = AppUtil.GSON.fromJson(jsonData, TaskDTO.class);
        String responseJson = new TaskService().addTask(taskDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTasks(@Context HttpServletRequest request) {
        String responseJson = new TaskService().getAllTasks(request);
        return Response.ok().entity(responseJson).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTaskById(@PathParam("id") int id, @Context HttpServletRequest request) {
        String responseJson = new TaskService().getTaskById(id, request);
        return Response.ok().entity(responseJson).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTask(String jsonData, @Context HttpServletRequest request) {
        TaskDTO taskDTO = AppUtil.GSON.fromJson(jsonData, TaskDTO.class);
        String responseJson = new TaskService().updateTask(taskDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTask(@PathParam("id") int id, @Context HttpServletRequest request) {
        String responseJson = new TaskService().deleteTask(id, request);
        return Response.ok().entity(responseJson).build();
    }

    @PUT
    @Path("/{id}/complete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markAsComplete(@PathParam("id") int id, @Context HttpServletRequest request) {
        String responseJson = new TaskService().markAsComplete(id, request);
        return Response.ok().entity(responseJson).build();
    }

    @PUT
    @Path("/{id}/ongoing")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markAsOngoing(@PathParam("id") int id, @Context HttpServletRequest request) {
        String responseJson = new TaskService().markAsOngoing(id, request);
        return Response.ok().entity(responseJson).build();
    }
}
