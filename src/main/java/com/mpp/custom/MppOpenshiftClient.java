package com.mpp.custom;



import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.quarkus.arc.profile.IfBuildProfile;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.enterprise.inject.Produces;

public class MppOpenshiftClient {


    @Produces
    @IfBuildProfile("dev")
    public OpenShiftClient configLessOc() {
        return new DefaultOpenShiftClient();
    }


    @Produces
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
        return new DefaultOpenShiftClient(configBuilder.build());
    }

}
