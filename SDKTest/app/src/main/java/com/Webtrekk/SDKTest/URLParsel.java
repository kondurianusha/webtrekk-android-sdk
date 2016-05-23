package com.Webtrekk.SDKTest;

import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

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

    public boolean parseURL(String url)
    {
        Pattern pattern = Pattern.compile("([^?&]+)");
        Matcher matcher = pattern.matcher(url);

        matcher.find();
        mMap.put(URLKEY, matcher.group());
        while (matcher.find())
        {
            final String parValue[] = matcher.group().split("=");

            if (parValue.length == 2)
               mMap.put(parValue[0], parValue[1]);
            else
            {
                WebtrekkLogging.log("key:"+parValue[0]+" don't have value");
                return false;
            }
        }

        return true;
    }

    public String getValue(String key)
    {
        return  mMap.get(key);
    }
}
