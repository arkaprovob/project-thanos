package com.mpp.custom;

import org.jboss.resteasy.reactive.RestResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

@Path("/")
public class Byfrost {

    private final Thanos thanos;

    public Byfrost(Thanos thanos) {
        this.thanos = thanos;
    }


    @GET
    @Path("health")
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<String> hello() {
        return ResponseBuilder.ok("avive!", MediaType.TEXT_PLAIN_TYPE).status(200).build();
    }

    @GET
    @Path("tenant_count")
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<Integer> totalTenants(){
        return ResponseBuilder.ok(thanos.noOfTestTenants(), MediaType.TEXT_PLAIN_TYPE).status(200).build();
    }

    @GET
    @Path("delete_all_tenant")
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<String> printTenatInfoOnConsole(){
        thanos.snap();
        return ResponseBuilder.ok("eligible tenant names are printed in console", MediaType.TEXT_PLAIN_TYPE).status(200).build();
    }

}