package com.wigerlabs.TideTempo.controller;

import com.wigerlabs.TideTempo.service.PriorityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/priority")
public class PriorityController {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPriorities(@Context HttpServletRequest request) {
        String responseJson = new PriorityService().getAllPriorities(request);
        return Response.ok().entity(responseJson).build();
    }
}
