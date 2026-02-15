package com.wigerlabs.TideTempo.controller;

import com.wigerlabs.TideTempo.service.StatusService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/status")
public class StatusController {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllStatuses(@Context HttpServletRequest request) {
        String responseJson = new StatusService().getAllStatuses(request);
        return Response.ok().entity(responseJson).build();
    }
}
