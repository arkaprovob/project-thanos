package com.mpp.custom;

import io.fabric8.openshift.client.OpenShiftClient;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class Thanos {
    private static final Logger LOG = LoggerFactory.getLogger(Thanos.class);


    private static final String LABEL = "tenant.paas.redhat.com/tenant";

    private final String tenantName;
    private final String appCode;


    private final String excludedProjects;
    private final InfinityGauntlet gauntlet;




    public Thanos(InfinityGauntlet gauntlet) {
        this.gauntlet = gauntlet;
        this.tenantName = ConfigProvider.getConfig().getValue("app.tenant.name", String.class);
        excludedProjects = ConfigProvider.getConfig().getValue("app.excluded.projects", String.class);
        this.appCode = ConfigProvider.getConfig().getValue("app.code", String.class);

    }





    public int noOfTestTenants(){
        var labels = Map.of(
                LABEL,tenantName
        );
        List<String> excludedNs = Utility.fromCommaSeperatedString(excludedProjects);
        return gauntlet.noOfTestProjects(labels,excludedNs);
    }

    public void snap(){
        var labels = Map.of(
                LABEL,tenantName
        );
        var templateParameters = new HashMap<String,String>();
        templateParameters.put("APP_CODE",appCode);
        templateParameters.put("TENANT_NAME",tenantName);
        List<String> excludedNs = Utility.fromCommaSeperatedString(excludedProjects);
        gauntlet.deleteProjects(labels,excludedNs,templateParameters);
    }


    public void interceptProjectCreation(){
        var labels = Map.of(
                LABEL,tenantName
        );
        var temPlateValues = Map.of();
        //oc.projects().withLabels(labels).watch(new TheOther(this));
    }


    public void inform() {
    }
}
