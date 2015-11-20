package com.webtrekk.webtrekksdk;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.SortedMap;
import java.util.TreeMap;

import com.webtrekk.webtrekksdk.TrackingParameter.Parameter;

/**
 * parses the xml for the tracking configuration
 */
class TrackingConfigurationXmlParser {
    private static final String ns = null;

    /**
     * parses the TrackingConfiguration from XML String
     *
     * @param in the String containing the xml configuration
     * @return returns a TrackingConfiguration object
     * @throws XmlPullParserException
     * @throws IOException
     */
    public TrackingConfiguration parse(String in) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(in));
        parser.nextTag();
        return readConfig(parser);
    }

    /**
     * reads the tags and sets the related configuration values in the TrackingConfiguration object
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private TrackingConfiguration readConfig(XmlPullParser parser) throws XmlPullParserException, IOException {
        TrackingConfiguration config = new TrackingConfiguration();
        parser.require(XmlPullParser.START_TAG, ns, "webtrekkConfiguration");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("version")) {
                parser.require(XmlPullParser.START_TAG, ns, "version");
                String versionValue = readText(parser);
                try {
                    int version = Integer.parseInt(versionValue);
                    if (version > 0) {
                        config.setVersion(version);
                    }
                } catch (Exception ex) {
                    WebtrekkLogging.log("invalid version value, using default: ", ex);
                }
                parser.require(XmlPullParser.END_TAG, ns, "version");


            } else if (name.equals("trackDomain")) {
                parser.require(XmlPullParser.START_TAG, ns, "trackDomain");
                String trackDomain = readText(parser);
                if (trackDomain.endsWith("/")) {
                    trackDomain = trackDomain.substring(0, trackDomain.length() - 1);
                }
                config.setTrackDomain(trackDomain);
                parser.require(XmlPullParser.END_TAG, ns, "trackDomain");


            } else if (name.equals("trackId")) {
                parser.require(XmlPullParser.START_TAG, ns, "trackId");

                String trackId = readText(parser);
                config.setTrackId(trackId);

                parser.require(XmlPullParser.END_TAG, ns, "trackId");


            } else if (name.equals("sampling")) {
                parser.require(XmlPullParser.START_TAG, ns, "sampling");
                String samplingValue = readText(parser);
                try {
                    int sampling = Integer.parseInt(samplingValue);
                    if (sampling != 1 && sampling >= 0) {
                        config.setSampling(sampling);
                    }
                } catch (Exception ex) {
                    WebtrekkLogging.log("invalid sampling value, using default", ex);
                }

                parser.require(XmlPullParser.END_TAG, ns, "sampling");


            } else if (name.equals("maxRequests")) {
                parser.require(XmlPullParser.START_TAG, ns, "maxRequests");

                String maxRequestsValue = readText(parser);

                try {
                    int maxRequests = Integer.parseInt(maxRequestsValue);
                    if (maxRequests > 99) {
                        config.setMaxRequests(maxRequests);
                    } else {
                        WebtrekkLogging.log("invalid maxRequests value, using default");
                    }

                } catch (Exception ex) {
                    WebtrekkLogging.log("invalid maxRequests value, using default: ", ex);
                }

                parser.require(XmlPullParser.END_TAG, ns, "maxRequests");


            } else if (name.equals("sendDelay")) {
                parser.require(XmlPullParser.START_TAG, ns, "sendDelay");
                String sendDelayValue = readText(parser);
                try {
                    int sendDelay = Integer.parseInt(sendDelayValue);
                    if (sendDelay > 9) {
                        config.setSendDelay(sendDelay);
                    } else {
                        WebtrekkLogging.log("invalid sendDelay value, using default");
                    }

                } catch (Exception ex) {
                    WebtrekkLogging.log("invalid sendDelay value, using default", ex);
                }
                parser.require(XmlPullParser.END_TAG, ns, "sendDelay");


            } else if (name.equals("autoTracked")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTracked");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTracked(true);
                } else if (value.equals("false")) {
                    config.setAutoTracked(false);
                } else {
                    WebtrekkLogging.log("invalid autoTracked value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTracked");


            } else if (name.equals("autoTrackAppUpdate")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackAppUpdate");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackAppUpdate(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackAppUpdate(false);
                } else {
                    WebtrekkLogging.log("invalid autoTrackAppUpdate value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackAppUpdate");


            } else if (name.equals("autoTrackAdvertiserId")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackAdvertiserId");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackAdvertiserId(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackAdvertiserId(false);
                } else {
                    WebtrekkLogging.log("invalid autoTrackAdvertiserId value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackAdvertiserId");


            } else if (name.equals("autoTrackAppVersionName")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackAppVersionName");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackAppVersionName(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackAppVersionName(false);
                } else {
                    WebtrekkLogging.log("invalid autoTrackAppVersionName value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackAppVersionName");

            } else if (name.equals("autoTrackAppVersionCode")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackAppVersionCode");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackAppVersionCode(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackAppVersionCode(false);
                } else {
                    WebtrekkLogging.log("invalid autoTrackAppVersionCode value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackAppVersionCode");

            } else if (name.equals("autoTrackAppPreInstalled")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackAppPreInstalled");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackAppPreInstalled(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackAppPreInstalled(false);
                } else {
                    WebtrekkLogging.log("invalid autoTrackAppPreInstalled value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackAppPreInstalled");

            } else if (name.equals("autoTrackPlaystoreUsername")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackPlaystoreUsername");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackPlaystoreUsername(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackPlaystoreUsername(false);
                } else {
                    WebtrekkLogging.log("invalid autoTrackPlaystoreUsername value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackPlaystoreUsername");

            } else if (name.equals("autoTrackPlaystoreMail")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackPlaystoreMail");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackPlaystoreMail(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackPlaystoreMail(false);
                } else {
                    WebtrekkLogging.log("invalid autoTrackPlaystoreMail value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackPlaystoreMail");

            } else if (name.equals("autoTrackPlaystoreGivenName")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackPlaystoreGivenName");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackPlaystoreGivenName(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackPlaystoreGivenName(false);
                } else {
                    WebtrekkLogging.log("invalid autoTrackPlaystoreGivenName value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackPlaystoreGivenName");

            } else if (name.equals("autoTrackPlaystoreFamilyName")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackPlaystoreFamilyName");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackPlaystoreFamilyName(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackPlaystoreFamilyName(false);
                } else {
                    WebtrekkLogging.log("invalid autoTrackPlaystoreFamilyName value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackPlaystoreFamilyName");

            } else if (name.equals("autoTrackApiLevel")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackApiLevel");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackApiLevel(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackApiLevel(false);
                } else {
                    WebtrekkLogging.log("invalid autoTrackApiLevel value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackApiLevel");

            } else if (name.equals("autoTrackScreenOrientation")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackScreenOrientation");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackScreenorientation(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackScreenorientation(false);
                } else {
                    WebtrekkLogging.log("invalid autoTrackScreenOrientation value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackScreenOrientation");

            } else if (name.equals("autoTrackConnectionType")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackConnectionType");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackConnectionType(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackConnectionType(false);
                } else {
                    WebtrekkLogging.log("invalid autoTrackConnectionType value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackConnectionType");

            } else if (name.equals("autoTrackAdvertisementOptOut")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackAdvertisementOptOut");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackAdvertismentOptOut(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackAdvertismentOptOut(false);
                } else {
                    WebtrekkLogging.log("invalid autoTrackAdvertisementOptOut value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackAdvertisementOptOut");

            } else if (name.equals("trackingConfigurationUrl")) {
                parser.require(XmlPullParser.START_TAG, ns, "trackingConfigurationUrl");

                String value = readText(parser);
                config.setTrackingConfigurationUrl(value);
                parser.require(XmlPullParser.END_TAG, ns, "trackingConfigurationUrl");

            } else if (name.equals("enableRemoteConfiguration")) {
                parser.require(XmlPullParser.START_TAG, ns, "enableRemoteConfiguration");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setEnableRemoteConfiguration(true);
                } else if (value.equals("false")) {
                    config.setEnableRemoteConfiguration(false);
                } else {
                    WebtrekkLogging.log("invalid enableRemoteConfiguration value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "enableRemoteConfiguration");

            } else if (name.equals("autoTrackRequestUrlStoreSize")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTrackRequestUrlStoreSize");

                String value = readText(parser);
                if (value.equals("true")) {
                    config.setAutoTrackRequestUrlStoreSize(true);
                } else if (value.equals("false")) {
                    config.setAutoTrackRequestUrlStoreSize(false);
                }else {
                    WebtrekkLogging.log("invalid autoTrackRequestUrlStoreSize value, using default");
                }
                parser.require(XmlPullParser.END_TAG, ns, "autoTrackRequestUrlStoreSize");

            } else if (name.equals("resendOnStartEventTime")) {
                parser.require(XmlPullParser.START_TAG, ns, "resendOnStartEventTime");

                String sendDelayValue = readText(parser);

                try {
                    int resendOnStartEventTime = Integer.parseInt(sendDelayValue);
                    config.setResendOnStartEventTime(resendOnStartEventTime);
                } catch (Exception ex) {
                    WebtrekkLogging.log("invalid resendOnStartEventTime value, using default: ", ex);
                }

                parser.require(XmlPullParser.END_TAG, ns, "resendOnStartEventTime");


            } else if (name.equals("customParameter")) {
                parser.require(XmlPullParser.START_TAG, ns, "customParameter");
                config.setCustomParameter(readParameterConfiguration(parser));
            } else if (name.equals("globalTrackingParameter")) {
                parser.require(XmlPullParser.START_TAG, ns, "globalTrackingParameter");
                config.setGlobalTrackingParameter(readTrackingParameterConfiguration(parser));
            } else if (name.equals("activity")) {
                ActivityConfiguration act = readActivityConfiguration(parser, config.isAutoTracked());
                config.getActivityConfigurations().put(act.getClassName(), act);
            } else {
                skip(parser);
            }
        }
        return config;
    }

    /**
     * ActivityConfigurations are in their own XML Tag, this function reads and parses the Activity Configuration
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private ActivityConfiguration readActivityConfiguration(XmlPullParser parser, boolean globalAutoTracked) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "activity");
        String className = null;
        String mappingName = null;
        boolean isAutoTrack = globalAutoTracked;
        TrackingParameter trackingParameter = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("classname")) {
                parser.require(XmlPullParser.START_TAG, ns, "classname");
                className = readText(parser);
            } else if (name.equals("mappingname")) {
                parser.require(XmlPullParser.START_TAG, ns, "mappingname");
                mappingName = readText(parser);
            } else if (name.equals("autoTracked")) {
                parser.require(XmlPullParser.START_TAG, ns, "autoTracked");
                String autoTracked = readText(parser);
                // the default value for the activities is based on the global setting
                // when global autotracking is enabled, all activities have true by default
                // when its disabled globally, all activities have false by default
                // each activity can override this value with its own
                if (autoTracked.equals("true")) {
                    isAutoTrack = true;
                }
                if (autoTracked.equals("false")) {
                    isAutoTrack = false;
                }
            } else if (name.equals("activityTrackingParameter")) {
                parser.require(XmlPullParser.START_TAG, ns, "activityTrackingParameter");
                trackingParameter = readTrackingParameterConfiguration(parser);
            }
        }
        return new ActivityConfiguration(className, mappingName, isAutoTrack, trackingParameter);
    }

    /**
     * this method is used to parse the global and the activites tracking parameter objects
     * they override the ones set in the code, and allow to set params for all activities or global ones for all requests
     * directly in the xml file
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private TrackingParameter readTrackingParameterConfiguration(XmlPullParser parser) throws XmlPullParserException, IOException {
        TrackingParameter tp = new TrackingParameter();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("parameter")) {
                parser.require(XmlPullParser.START_TAG, ns, "parameter");
                String key = parser.getAttributeValue(ns, "key");
                String value = parser.getAttributeValue(ns, "value");
                if (key != null && value != null) {
                    tp.add(Parameter.getParameterByName(key), value);
                } else {
                    WebtrekkLogging.log("invalid parameter configuration while reading customParameter, missing key or value");
                }
                parser.nextTag();
            } else if (name.equals("pageParameter")) {
                parser.require(XmlPullParser.START_TAG, ns, "pageParameter");
                tp.setPageParameter(readParameterConfiguration(parser));
            } else if (name.equals("sessionParameter")) {
                parser.require(XmlPullParser.START_TAG, ns, "sessionParameter");
                tp.setSessionParameter(readParameterConfiguration(parser));
            } else if (name.equals("ecomParameter")) {
                parser.require(XmlPullParser.START_TAG, ns, "ecomParameter");
                tp.setEcomParameter(readParameterConfiguration(parser));
            } else if (name.equals("userCategories")) {
                parser.require(XmlPullParser.START_TAG, ns, "userCategories");
                tp.setUserCategories(readParameterConfiguration(parser));
            } else if (name.equals("pageCategories")) {
                parser.require(XmlPullParser.START_TAG, ns, "pageCategories");
                tp.setPageCategories(readParameterConfiguration(parser));
            } else if (name.equals("adParameter")) {
                parser.require(XmlPullParser.START_TAG, ns, "adParameter");
                tp.setAdParameter(readParameterConfiguration(parser));
            } else if (name.equals("actionParameter")) {
                parser.require(XmlPullParser.START_TAG, ns, "actionParameter");
                tp.setActionParameter(readParameterConfiguration(parser));
            } else if (name.equals("productCategories")) {
                parser.require(XmlPullParser.START_TAG, ns, "productCategories");
                tp.setProductCategories(readParameterConfiguration(parser));
            } else if (name.equals("mediaCategories")) {
                parser.require(XmlPullParser.START_TAG, ns, "mediaCategories");
                tp.setMediaCategories(readParameterConfiguration(parser));
            }
        }

        return tp;

    }

    private SortedMap<String, String> readParameterConfiguration(XmlPullParser parser) throws XmlPullParserException, IOException {

        TreeMap<String, String> customParameter = new TreeMap<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("parameter")) {
                parser.require(XmlPullParser.START_TAG, ns, "parameter");
                String key = parser.getAttributeValue(ns, "key");
                String value = parser.getAttributeValue(ns, "value");
                if (key != null && value != null) {
                    customParameter.put(key, value);
                } else {
                    WebtrekkLogging.log("invalid parameter configuration while reading customParameter, missing key or value");
                }
                parser.nextTag();
            }
        }
        return customParameter;
    }

    /**
     * reads a String value from xml tag
     *
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * skips a tag during parsing the xml string
     *
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                default:
                    break;
            }
        }
    }


}

