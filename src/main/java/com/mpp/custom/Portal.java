package com.mpp.custom;

import org.jboss.resteasy.reactive.RestResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

@Path("/")
public class Portal {
    @GET
    @Path("health")
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<String> hello() {
        return ResponseBuilder.ok("avive!", MediaType.TEXT_PLAIN_TYPE).status(200).build();
    }
}