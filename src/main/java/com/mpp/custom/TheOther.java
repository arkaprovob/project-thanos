package com.mpp.custom;

import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.fabric8.openshift.api.model.Project;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TheOther implements Watcher<Project> {
    private static final Logger LOG = LoggerFactory.getLogger(TheOther.class);

    private final Thanos thanos;
    private static final List<Pair<String, Date>> recentlyDeletedProjects = new ArrayList<>();

    public TheOther(Thanos thanos) {
        this.thanos = thanos;
    }

    @Override
    public void eventReceived(Action action, Project project) {
        LOG.info("entered into event received state event {} for project {}", action,project.getMetadata().getName());
        if (!(Action.ADDED.equals(action)))
            return;
        LOG.info("EVENT is {}", action);
        thanos.inform();
    }

    @Override
    public void onClose(WatcherException e) {
        LOG.info("onClose event invoked due to {} ", e.asClientException().getStatus().getStatus());
    }

    public static List<Pair<String, Date>> getRecentlyDeletedProjects() {
        return recentlyDeletedProjects;
    }

    public static void setRecentlyDeletedProjects(List<Pair<String, Date>> recentlyDeletedProjects) {
        TheOther.recentlyDeletedProjects.clear();
        TheOther.recentlyDeletedProjects.addAll(recentlyDeletedProjects);
    }
}
