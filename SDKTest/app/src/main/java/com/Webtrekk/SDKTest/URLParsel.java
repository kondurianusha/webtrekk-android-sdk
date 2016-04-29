package com.Webtrekk.SDKTest;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vartbaronov on 28.04.16.
 */
public class URLParsel
{
    final private Map<String, String> mMap = new HashMap<String, String>();
    public static String URLKEY = "MAIN_URL_KEY";

    public void parseURL(String url)
    {
        Pattern pattern = Pattern.compile("([^?&]+)");
        Matcher matcher = pattern.matcher(url);

        matcher.find();
        mMap.put(URLKEY, matcher.group());
        while (matcher.find())
        {
            final String parValue[] = matcher.group().split("=");

            mMap.put(parValue[0], parValue[1]);
        }
    }

    public String getValue(String key)
    {
        return  mMap.get(key);
    }
}
