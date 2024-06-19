class SegmentConfig {
  SegmentConfig(
      {required this.writeKey,
      this.trackApplicationLifecycleEvents = false,
      this.amplitudeIntegrationEnabled = false,
      this.appsflyerIntegrationEnabled = false,
      this.debug = false,
      required this.cdnSettingsProxyHost,
      required this.cdnProxyHost,
      required this.apiProxyHost,
      this.disableFirebase = true
      });

  final String writeKey;
  final bool trackApplicationLifecycleEvents;
  final bool amplitudeIntegrationEnabled;
  final bool appsflyerIntegrationEnabled;
  final bool debug;
  final bool disableFirebase;
  final String cdnSettingsProxyHost;
  final String cdnProxyHost;
  final String apiProxyHost;

  Map<String, dynamic> toMap() {
    return {
      'writeKey': writeKey,
      'trackApplicationLifecycleEvents': trackApplicationLifecycleEvents,
      'amplitudeIntegrationEnabled': amplitudeIntegrationEnabled,
      'appsflyerIntegrationEnabled': appsflyerIntegrationEnabled,
      'debug': debug,
      'cdnSettingsProxyHost': cdnSettingsProxyHost,
      'cdnProxyHost': cdnProxyHost,
      'apiProxyHost': apiProxyHost,
      'disableFirebase':disableFirebase
    };
  }
}
