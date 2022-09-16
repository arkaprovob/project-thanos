package com.mpp.custom;

import org.jboss.resteasy.reactive.RestResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

@Path("/thanos")
public class Byfrost {

    private final Thanos thanos;

    public Byfrost(Thanos thanos) {
        this.thanos = thanos;
    }


    @GET
    @Path("health")
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<String> hello() {
        return ResponseBuilder.ok("alive!", MediaType.TEXT_PLAIN_TYPE).status(200).build();
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<Integer> noOfEligibleProjects(){
        return ResponseBuilder.ok(thanos.totalPopulation(), MediaType.TEXT_PLAIN_TYPE).status(200).build();
    }

    @GET
    @Path("snap_pro_max")
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<String> nukeAllProjects(){
        thanos.snapProMax();
        return ResponseBuilder.ok("eligible tenant names are printed in console", MediaType.TEXT_PLAIN_TYPE)
                .status(200).build();
    }

}