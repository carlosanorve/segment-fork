package com.example.flutter_segment;

import android.os.Bundle;

import java.util.HashMap;

public class FlutterSegmentOptions {
    private final String writeKey;
    private final Boolean trackApplicationLifecycleEvents;
    private final Boolean amplitudeIntegrationEnabled;
    private final Boolean appsflyerIntegrationEnabled;
    private final Boolean debug;
    private final String cdnSettingsProxyHost;
    private final String cdnProxyHost;
    private final String apiProxyHost;
    private final Boolean disableFirebase;

    public FlutterSegmentOptions(
            String writeKey,
            Boolean trackApplicationLifecycleEvents,
            Boolean amplitudeIntegrationEnabled,
            Boolean appsflyerIntegrationEnabled,
            Boolean debug,
            String cdnSettingsProxyHost,
            String cdnProxyHost,
            String apiProxyHost
    ) {
        this.writeKey = writeKey;
        this.trackApplicationLifecycleEvents = trackApplicationLifecycleEvents;
        this.amplitudeIntegrationEnabled = amplitudeIntegrationEnabled;
        this.appsflyerIntegrationEnabled = appsflyerIntegrationEnabled;
        this.debug = debug;
        this.cdnSettingsProxyHost = cdnSettingsProxyHost;
        this.cdnProxyHost = cdnProxyHost;
        this.apiProxyHost = apiProxyHost;
        this.disableFirebase = Boolean.TRUE;
    }

    public FlutterSegmentOptions(
            String writeKey,
            Boolean trackApplicationLifecycleEvents,
            Boolean amplitudeIntegrationEnabled,
            Boolean appsflyerIntegrationEnabled,
            Boolean debug,
            String cdnSettingsProxyHost,
            String cdnProxyHost,
            String apiProxyHost,
            Boolean disableFirebase
    ) {
        this.writeKey = writeKey;
        this.trackApplicationLifecycleEvents = trackApplicationLifecycleEvents;
        this.amplitudeIntegrationEnabled = amplitudeIntegrationEnabled;
        this.appsflyerIntegrationEnabled = appsflyerIntegrationEnabled;
        this.debug = debug;
        this.cdnSettingsProxyHost = cdnSettingsProxyHost;
        this.cdnProxyHost = cdnProxyHost;
        this.apiProxyHost = apiProxyHost;
        this.disableFirebase = disableFirebase;
    }

    public String getWriteKey() {
        return writeKey;
    }

    public Boolean getTrackApplicationLifecycleEvents() {
        return trackApplicationLifecycleEvents;
    }

    public Boolean isAmplitudeIntegrationEnabled() {
        return amplitudeIntegrationEnabled;
    }

    public Boolean isAppsflyerIntegrationEnabled() {
        return appsflyerIntegrationEnabled;
    }

    public Boolean getDebug() {
        return debug;
    }

    public Boolean getDisableFirebase() {
        return disableFirebase;
    }

    public String getCdnSettingsProxyHost() {
        return cdnSettingsProxyHost;
    }

    public String getCdnProxyHost() {
        return cdnProxyHost;
    }

    public String getApiProxyHost() {
        return apiProxyHost;
    }

    static FlutterSegmentOptions create(Bundle bundle) {
        String writeKey = bundle.getString("com.claimsforce.segment.WRITE_KEY");
        Boolean trackApplicationLifecycleEvents = bundle.getBoolean("com.claimsforce.segment.TRACK_APPLICATION_LIFECYCLE_EVENTS");
        Boolean isAmplitudeIntegrationEnabled = bundle.getBoolean("com.claimsforce.segment.ENABLE_AMPLITUDE_INTEGRATION", false);
        Boolean isAppsflyerIntegrationEnabled = bundle.getBoolean("com.claimsforce.segment.ENABLE_APPSFLYER_INTEGRATION", false);
        Boolean debug = bundle.getBoolean("com.claimsforce.segment.DEBUG", false);
        Boolean disableFirebase = true;
        String cdnSettingsProxyHost = bundle.getString("com.claimsforce.segment.CDN_SETTINGS_PROXY_HOST");
        String cdnProxyHost = bundle.getString("com.claimsforce.segment.CDN_PROXY_HOST");
        String apiProxyHost = bundle.getString("com.claimsforce.segment.API_PROXY_HOST");
        return new FlutterSegmentOptions(writeKey, trackApplicationLifecycleEvents, isAmplitudeIntegrationEnabled, isAppsflyerIntegrationEnabled, debug, cdnSettingsProxyHost, cdnProxyHost, apiProxyHost, disableFirebase);
    }

    static FlutterSegmentOptions create(HashMap<String, Object> options) {
        String writeKey = (String) options.get("writeKey");
        Boolean trackApplicationLifecycleEvents = (Boolean) options.get("trackApplicationLifecycleEvents");
        Boolean isAmplitudeIntegrationEnabled = orFalse((Boolean) options.get("amplitudeIntegrationEnabled"));
        Boolean isAppsflyerIntegrationEnabled = orFalse((Boolean) options.get("appsflyerIntegrationEnabled"));
        Boolean debug = orFalse((Boolean) options.get("debug"));
        Boolean disableFirebase = orFalse((Boolean) options.get("disableFirebase"));
        String cdnSettingsProxyHost = (String) options.get("cdnSettingsProxyHost");
        String cdnProxyHost = (String) options.get("cdnProxyHost");
        String apiProxyHost = (String) options.get("apiProxyHost");
        return new FlutterSegmentOptions(writeKey, trackApplicationLifecycleEvents, isAmplitudeIntegrationEnabled, isAppsflyerIntegrationEnabled, debug, cdnSettingsProxyHost, cdnProxyHost, apiProxyHost, disableFirebase);
    }

    private static Boolean orFalse(Boolean value) {
        return value == null ? Boolean.FALSE : value;
    }
}
