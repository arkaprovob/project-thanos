package com.mpp.custom;

import io.fabric8.openshift.api.model.Project;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class LadyDeath {

    private static final Logger LOG = LoggerFactory.getLogger(LadyDeath.class);
    private static final int threshold = 2;

    private final Thanos thanos;

    public LadyDeath(Thanos thanos) {
        this.thanos = thanos;
    }

    public List<Pair<String,Date>> projectsToDelete(List<Project> eligibleProjects){

        int noOfEligibleProjects = eligibleProjects.size();

        if(!(noOfEligibleProjects>threshold)){
            LOG.info("tenants are within limits");
            return Collections.emptyList();
        }

        var projectPairs = eligibleProjects.stream().map(entry -> new Pair<String, Date>(
                removeTenantFromProjectName(entry.getMetadata().getName()),
                stringToDate(entry.getMetadata().getCreationTimestamp())))
                .sorted(Comparator.comparing(Pair::getValue1))
                .collect(Collectors.toList());

        return projectPairs.stream().limit(noOfEligibleProjects-threshold).collect(Collectors.toList());
    }

    private String removeTenantFromProjectName(String projectName){
        return projectName.replace("spaship--","");
    }
    private Date stringToDate(String creationTime){
        var dateParse = Instant.parse(creationTime);
        return Date.from(dateParse);
    }






}
