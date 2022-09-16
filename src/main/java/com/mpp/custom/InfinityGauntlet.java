package com.mpp.custom;

import io.fabric8.kubernetes.api.model.KubernetesList;
import io.fabric8.kubernetes.client.dsl.FilterWatchListDeletable;
import io.fabric8.openshift.api.model.Project;
import io.fabric8.openshift.api.model.ProjectList;
import io.fabric8.openshift.api.model.Template;
import io.fabric8.openshift.client.OpenShiftClient;
import io.fabric8.openshift.client.dsl.TemplateResource;
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

    FilterWatchListDeletable<Project, ProjectList> power(Map<String,String> label){
        return oc.projects().withLabels(label);
    }

    public int noOfTestProjects(Map<String,String> labels, List<String> excludedProjects){
        var projectList = oc.projects().withLabels(labels).list();
        return (int) projectList.getItems().stream()
                .filter(project -> !excludedProjects.contains(project.getMetadata().getName()))
                .count();
    }

    public List<Project> listOfEligibleProjects(Map<String,String> labels, List<String> excludedProjects){
        var projectList = oc.projects().withLabels(labels).list();
        return projectList.getItems().stream()
                .filter(project -> !excludedProjects.contains(project.getMetadata().getName()))
                .collect(Collectors.toList());
    }

    public void nukeAll(Map<String,String> labels, List<String> excludedProjects,Map<String,String> templateParameters){
        var resource = oc
                .templates()
                .load(InfinityGauntlet.class.getResourceAsStream("/ost/mpp-namespace-template.yaml"));

        listOfEligibleProjects(labels,excludedProjects)
                .parallelStream()
                .forEach(project->
                    deleteTenantNameSpace(templateParameters, resource, project)
                );
    }

    public void nukeSingle(Map<String,String> templateParameters){
        var resource = oc
                .templates()
                .load(InfinityGauntlet.class.getResourceAsStream("/ost/mpp-namespace-template.yaml"));
        deleteTenantNameSpace(templateParameters,resource);
    }

    private void deleteTenantNameSpace(Map<String, String> templateParameters,
                                       TemplateResource<Template, KubernetesList> resource, Project project) {
        var projectName = project.getMetadata().getName().replace("spaship--","");
        templateParameters
                .put("NS_NAME",projectName);
        LOG.info("applying the following values -> {}", templateParameters);
        deleteTenantNameSpace(templateParameters,resource);
    }
    private void deleteTenantNameSpace(Map<String, String> templateParameters,TemplateResource<Template, KubernetesList> resource){
        var template= resource.processLocally(templateParameters);
        oc.resourceList(template).delete();
        LOG.info("nuked the following environment {}",templateParameters);
    }



}
