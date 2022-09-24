package com.mpp.custom;

import io.fabric8.openshift.api.model.Project;
import org.eclipse.microprofile.config.ConfigProvider;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class LadyDeath {

    private static final Logger LOG = LoggerFactory.getLogger(LadyDeath.class);
    private final int threshold;

    public LadyDeath() {
        this.threshold = ConfigProvider.getConfig().getValue("app.project.threshold", Integer.class);
    }


    public List<Pair<String, Date>> selectProjects(List<Project> eligibleProjects) {

        int noOfEligibleProjects = eligibleProjects.size();

        boolean condition = noOfEligibleProjects > threshold;
        if (!condition) {
            LOG.info("tenants are within limits");
            return Collections.emptyList();
        }

        var projectPairs = eligibleProjects.stream().map(entry -> new Pair<>(
                removeTenantFromProjectName(entry.getMetadata().getName()),
                stringToDate(entry.getMetadata().getCreationTimestamp())))
                .sorted(Comparator.comparing(Pair::getValue1))
                .collect(Collectors.toList());
        var upto= noOfEligibleProjects - threshold;
        return projectPairs.stream().limit(upto).collect(Collectors.toList());
    }

    private String removeTenantFromProjectName(String projectName) {
        return projectName.replace("spaship--", "");
    }

    private Date stringToDate(String creationTime) {
        var dateParse = Instant.parse(creationTime);
        return Date.from(dateParse);
    }


}
