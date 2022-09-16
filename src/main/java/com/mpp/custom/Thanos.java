package com.mpp.custom;

import io.fabric8.openshift.api.model.Project;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.ConfigProvider;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class Thanos {
    private static final Logger LOG = LoggerFactory.getLogger(Thanos.class);


    private static final String LABEL = "tenant.paas.redhat.com/tenant";

    private final String tenantName;
    private final String appCode;


    private final String excludedProjects;
    private final InfinityGauntlet gauntlet;

    private final LadyDeath girlFriend;


    public Thanos(InfinityGauntlet gauntlet) {
        this.gauntlet = gauntlet;
        this.tenantName = ConfigProvider.getConfig().getValue("app.tenant.name", String.class);
        excludedProjects = ConfigProvider.getConfig().getValue("app.excluded.projects", String.class);
        this.appCode = ConfigProvider.getConfig().getValue("app.code", String.class);
        girlFriend = new LadyDeath();
    }

    void infiltrate(@Observes StartupEvent startupEvent) {
        LOG.info("Watcher deployed!");
        gauntlet.power(Map.of(LABEL, tenantName)).watch(new TheOther(this));
    }


    public int totalPopulation() {
        var labels = Map.of(
                LABEL, tenantName
        );
        List<String> excludedNs = Utility.fromCommaSeperatedString(excludedProjects);
        return gauntlet.noOfTestProjects(labels, excludedNs);
    }

    public void snapProMax() {
        var labels = Map.of(
                LABEL, tenantName
        );
        var templateParameters = new HashMap<String, String>();
        templateParameters.put("APP_CODE", appCode);
        templateParameters.put("TENANT_NAME", tenantName);
        List<String> excludedNs = Utility.fromCommaSeperatedString(excludedProjects);
        gauntlet.nukeAll(labels, excludedNs, templateParameters);
    }

    private void snap(List<Pair<String, Date>> eligibleProjects) {
        eligibleProjects.parallelStream().forEach(entry -> {
            var tParam = Map.of("APP_CODE", appCode,
                    "TENANT_NAME", tenantName,
                    "NS_NAME", entry.getValue0()
            );
            gauntlet.nukeSingle(tParam);
        });
    }

    public void inform() {
        var labels = Map.of(
                LABEL, tenantName
        );
        List<String> excludedNs = Utility.fromCommaSeperatedString(excludedProjects);
        List<Project> projects = gauntlet.listOfEligibleProjects(labels, excludedNs);
        var processedRecord = girlFriend.projectsToDelete(projects);
        LOG.info("the following records has been received {}", processedRecord);
        snap(processedRecord);
    }
}
