package com.mpp.custom;


import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@Singleton
public class Celestial {
    private static final Logger LOG = LoggerFactory.getLogger(Celestial.class);

    @Produces
    @IfBuildProfile("dev")
    public OpenShiftClient configLessOc() {
        LOG.info("creating config-less oc");
        final ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.withTrustCerts(true);
        return new DefaultOpenShiftClient(configBuilder.build());
    }


    @Produces
    @DefaultBean
    public OpenShiftClient oc() {
        final ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.withTrustCerts(true)
                .withOauthToken(ConfigProvider.getConfig().getValue("app.access.token", String.class))
                .withConnectionTimeout(600000)
                .withRequestTimeout(600000)
                .withWebsocketTimeout(600000)
                .withWebsocketPingInterval(600000)
                .withUploadConnectionTimeout(600000)
                .withUploadRequestTimeout(600000);
        LOG.info("creating oc with custom configuration");
        return new DefaultOpenShiftClient(configBuilder.build());
    }

}
