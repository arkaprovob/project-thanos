package com.mpp.custom;

import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.fabric8.openshift.api.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheOther implements Watcher<Project>{
    private static final Logger LOG = LoggerFactory.getLogger(TheOther.class);

    private final Thanos thanos;
    public TheOther(Thanos thanos) {
        this.thanos = thanos;
    }

    @Override
    public void eventReceived(Action action, Project project) {
        if(!(Action.ADDED.equals(action) || Action.MODIFIED.equals(action)))
            return;
        thanos.inform();
    }

    @Override
    public void onClose(WatcherException e) {

    }
}
