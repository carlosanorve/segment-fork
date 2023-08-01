package com.example.flutter_segment;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.deeplink.DeepLinkListener;
import com.segment.analytics.Analytics;
import com.segment.analytics.Analytics.LogLevel;
import com.segment.analytics.Properties;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.Logger;
import com.segment.analytics.integrations.TrackPayload;
import com.segment.analytics.internal.Utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomAppsFlyer extends Integration<AppsFlyerLib> {

    static final String AF_SEGMENT_SHARED_PREF = "appsflyer-segment-data";
    static final String CONV_KEY = "AF_onConversion_Data";
    static final Map<String, String> MAPPER;
    private static final String APPSFLYER_KEY = "SefkBG76uAYCK6St9Xcw3h";
    private static final String SEGMENT_REVENUE = "revenue";
    private static final String SEGMENT_CURRENCY = "currency";
    public static CustomAppsFlyer.ExternalAppsFlyerConversionListener conversionListener;
    public static CustomAppsFlyer.ExternalDeepLinkListener deepLinkListener;
    final Logger logger;
    final AppsFlyerLib appsflyer;
    final String appsFlyerDevKey;
    final boolean isDebug;
    private Context context;
    private String customerUserId;
    private String currencyCode;
    public static ConversionListenerDisplay cld;
    public static final Factory FACTORY;

    public CustomAppsFlyer(Context context, Logger logger, AppsFlyerLib afLib, String devKey) {
        this.context = context;
        this.logger = logger;
        this.appsflyer = afLib;
        this.appsFlyerDevKey = devKey;
        this.isDebug = logger.logLevel != LogLevel.NONE;
    }

    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        this.updateEndUserAttributes();
    }

    public AppsFlyerLib getUnderlyingInstance() {
        return this.appsflyer;
    }

    public void identify(IdentifyPayload identify) {
        super.identify(identify);
        this.customerUserId = identify.userId();
        this.currencyCode = identify.traits().getString("currencyCode");
        if (this.appsflyer != null) {
            this.updateEndUserAttributes();
        } else {
            this.logger.verbose("couldn't update 'Identify' attributes", new Object[0]);
        }

    }

    private void updateEndUserAttributes() {
        this.appsflyer.setCustomerUserId(this.customerUserId);
        this.logger.verbose("appsflyer.setCustomerUserId(%s)", new Object[] { this.customerUserId });
        this.appsflyer.setCurrencyCode(this.currencyCode);
        this.logger.verbose("appsflyer.setCurrencyCode(%s)", new Object[] { this.currencyCode });
        this.appsflyer.setDebugLog(this.isDebug);
        this.logger.verbose("appsflyer.setDebugLog(%s)", new Object[] { this.isDebug });
    }

    public void track(TrackPayload track) {
        String event = track.event();
        Properties properties = track.properties();
        Map<String, Object> afProperties = Utils.transform(properties, MAPPER);
        this.appsflyer.logEvent(this.context, event, afProperties);
        this.logger.verbose("appsflyer.logEvent(context, %s, %s)", new Object[] { event, properties });
    }

    static {
        Map<String, String> mapper = new LinkedHashMap();
        mapper.put("revenue", "af_revenue");
        mapper.put("currency", "af_currency");
        MAPPER = Collections.unmodifiableMap(mapper);
        FACTORY = new Factory() {
            public Integration<AppsFlyerLib> create(ValueMap settings, Analytics analytics) {
                Logger logger = analytics.logger("AppsFlyer");
                AppsFlyerLib afLib = AppsFlyerLib.getInstance();
                String devKey = "SefkBG76uAYCK6St9Xcw3h";// settings.getString("app_key_appsflyer");
                boolean trackAttributionData = settings.getBoolean("trackAttributionData", false);
                Application application = analytics.getApplication();
                AppsFlyerConversionListener listener = null;
                if (trackAttributionData) {
                    listener = new ConversionListener(analytics);
                } else {
                    listener = new ConversionListener(analytics);

                }
                afLib.addPushNotificationDeepLinkPath("af_push_one_link");
                afLib.setDebugLog(logger.logLevel != LogLevel.NONE);
                afLib.setDebugLog(true);
                afLib.init(devKey, listener, application.getApplicationContext());
                if (CustomAppsFlyer.deepLinkListener != null) {
                    AppsFlyerLib.getInstance().subscribeForDeepLink(CustomAppsFlyer.deepLinkListener);
                }

                logger.verbose("AppsFlyer.getInstance().start(%s, %s)", new Object[] { application,
                        devKey.substring(0, 1) + "*****" + devKey.substring(devKey.length() - 2) });
                boolean isReact = true;

                try {
                    Class.forName(
                            "com.segment.analytics.reactnative.integration.appsflyer.RNAnalyticsIntegration_AppsFlyerModule");
                } catch (ClassNotFoundException var11) {
                    isReact = false;
                }

                if (isReact) {
                    afLib.start(application, devKey);
                    logger.verbose("Segment React Native AppsFlye rintegration is used, sending first launch manually",
                            new Object[0]);
                }

                return new CustomAppsFlyer(application, logger, afLib, devKey);
            }

            public String key() {
                return "AppsFlyer";
            }
        };
    }

    static class ConversionListener implements AppsFlyerConversionListener {
        final Analytics analytics;

        public ConversionListener(Analytics analytics) {
            this.analytics = analytics;
        }

        public void onConversionDataSuccess(Map<String, Object> conversionData) {
            if (!this.getFlag("AF_onConversion_Data")) {
                this.trackInstallAttributed(conversionData);
                this.setFlag("AF_onConversion_Data", true);
            }

            if (CustomAppsFlyer.cld != null) {
                conversionData.put("type", "onInstallConversionData");
                CustomAppsFlyer.cld.display(conversionData);
            }

            if (CustomAppsFlyer.conversionListener != null) {
                CustomAppsFlyer.conversionListener.onConversionDataSuccess(conversionData);
            }

        }

        public void onConversionDataFail(String errorMessage) {
            if (CustomAppsFlyer.conversionListener != null) {
                CustomAppsFlyer.conversionListener.onConversionDataFail(errorMessage);
            }

        }

        public void onAppOpenAttribution(Map<String, String> attributionData) {
            if (CustomAppsFlyer.cld != null) {
                attributionData.put("type", "onAppOpenAttribution");
                CustomAppsFlyer.cld.display(attributionData);
            }

            if (CustomAppsFlyer.conversionListener != null) {
                CustomAppsFlyer.conversionListener.onAppOpenAttribution(attributionData);
            }

        }

        public void onAttributionFailure(String errorMessage) {
            if (CustomAppsFlyer.conversionListener != null) {
                CustomAppsFlyer.conversionListener.onAttributionFailure(errorMessage);
            }

        }

        private Object getFromAttr(Object value) {
            return value != null ? value : "";
        }

        void trackInstallAttributed(Map<String, ?> attributionData) {
            Map<String, Object> campaign = (new ValueMap())
                    .putValue("source", this.getFromAttr(attributionData.get("media_source")))
                    .putValue("name", this.getFromAttr(attributionData.get("campaign")))
                    .putValue("ad_group", this.getFromAttr(attributionData.get("adgroup")));
            Properties properties = (new Properties()).putValue("provider", "AppsFlyer");
            properties.putAll(attributionData);
            properties.remove("media_source");
            properties.remove("adgroup");
            properties.putValue("campaign", campaign);
            this.analytics.track("Install Attributed", properties);
        }

        private boolean getFlag(String key) {
            Context context = this.getContext();
            if (context == null) {
                return false;
            } else {
                SharedPreferences sharedPreferences = context.getSharedPreferences("appsflyer-segment-data", 0);
                return sharedPreferences.getBoolean(key, false);
            }
        }

        private void setFlag(String key, boolean value) {
            Context context = this.getContext();
            if (context != null) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("appsflyer-segment-data", 0);
                Editor editor = sharedPreferences.edit();
                editor.putBoolean(key, value);
                this.editorCommit(editor);
            }
        }

        private void editorCommit(Editor editor) {
            if (VERSION.SDK_INT >= 9) {
                editor.apply();
            } else {
                editor.commit();
            }

        }

        private Context getContext() {
            return this.analytics.getApplication().getApplicationContext();
        }
    }

    public interface ExternalDeepLinkListener extends DeepLinkListener {
    }

    public interface ExternalAppsFlyerConversionListener extends AppsFlyerConversionListener {
    }

    /** @deprecated */
    @Deprecated
    public interface ConversionListenerDisplay {
        void display(Map<String, ?> var1);
    }
}
