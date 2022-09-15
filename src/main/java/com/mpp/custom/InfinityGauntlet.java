package com.mpp.custom;

import io.fabric8.openshift.client.OpenShiftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class InfinityGauntlet {

    private static final Logger LOG = LoggerFactory.getLogger(InfinityGauntlet.class);

    private final OpenShiftClient oc;

    public InfinityGauntlet(OpenShiftClient oc) {
        this.oc = oc;
    }

    public int noOfTestProjects(Map<String,String> labels, List<String> excludedProjects){
        var projectList = oc.projects().withLabels(labels).list();
        return (int) projectList.getItems().stream()
                .filter(project -> !excludedProjects.contains(project.getMetadata().getName()))
                .count();
    }

    public void deleteProjects(Map<String,String> labels, List<String> excludedProjects,Map<String,String> templateParameters){
        var projectList = oc.projects().withLabels(labels).list();

        var resource = oc
                .templates()
                .load(InfinityGauntlet.class.getResourceAsStream("/ost/mpp-namespace-template.yaml"));

        projectList.getItems().stream()
                .filter(project -> !excludedProjects.contains(project.getMetadata().getName()))
                .collect(Collectors.toList())
                .forEach(project->{
                    var projectName = project.getMetadata().getName().replace("spaship--","");
                    var creationTS = project.getMetadata().getCreationTimestamp();
                    templateParameters
                            .put("NS_NAME",projectName);
                    LOG.info("applying the following values -> {}",templateParameters);
                    var template=resource.processLocally(templateParameters);
                    oc.resourceList(template).delete();
                });
    }




}
