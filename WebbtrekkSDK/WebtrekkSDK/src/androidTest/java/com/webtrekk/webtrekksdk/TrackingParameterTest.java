package com.webtrekk.webtrekksdk;


import android.test.AndroidTestCase;
import com.webtrekk.webtrekksdk.TrackingParameter.Parameter;
import java.util.HashMap;

public class TrackingParameterTest extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // android bug workaround: 308
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().toString());

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAdd() {
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ACTION_NAME, "saveButtonClick");

        assertEquals(1, tp.getDefaultParameter().size());
        assertEquals("saveButtonClick", tp.getDefaultParameter().get(Parameter.ACTION_NAME));

        // add action param
        tp.add(Parameter.ACTION, "1", "test");
        assertEquals(1, tp.getActionParameter().size());

        // add page param
        tp.add(Parameter.PAGE, "1", "test");
        assertEquals(1, tp.getPageParameter().size());

        // add session param
        tp.add(Parameter.SESSION, "1", "test");
        assertEquals(1, tp.getSessionParameter().size());

        // add ecom param
        tp.add(Parameter.ECOM, "1", "test");
        assertEquals(1, tp.getEcomParameter().size());

        // add ad param
        tp.add(Parameter.AD, "1", "test");
        assertEquals(1, tp.getAdParameter().size());

        // add user category
        tp.add(Parameter.USER_CAT, "1", "test");
        assertEquals(1, tp.getUserCategories().size());

        // add page category
        tp.add(Parameter.PAGE_CAT, "1", "test");
        assertEquals(1, tp.getPageCategories().size());

        // add product category
        tp.add(Parameter.PRODUCT_CAT, "1", "test");
        assertEquals(1, tp.getProductCategories().size());

        // add media category
        tp.add(Parameter.MEDIA_CAT, "1", "test");
        assertEquals(1, tp.getMediaCategories().size());
    }

    public void testApplyMapping() {
        TrackingParameter tp = new TrackingParameter();

        HashMap<String, String> customParameter = new HashMap<>();
        // make sure all occurences of screen_resolution as value will be replaced with 123x456
        customParameter.put("screen_resolution", "123x456");

        // add the screen_resolution key to all custom maps for tracking params
        tp.add(Parameter.ACTION, "1", "screen_resolution");
        tp.add(Parameter.PAGE, "1", "screen_resolution");
        tp.add(Parameter.SESSION, "1", "screen_resolution");
        tp.add(Parameter.ECOM, "1", "screen_resolution");
        tp.add(Parameter.AD, "1", "screen_resolution");
        tp.add(Parameter.USER_CAT, "1", "screen_resolution");
        tp.add(Parameter.PAGE_CAT, "1", "screen_resolution");
        tp.add(Parameter.PRODUCT_CAT, "1", "screen_resolution");
        tp.add(Parameter.MEDIA_CAT, "1", "screen_resolution");

        // apply the mapping
        tp.applyMapping(customParameter);

        // make sure all values where replaced
        assertEquals(tp.getActionParameter().get("1"), "123x456");
        assertEquals(tp.getAdParameter().get("1"), "123x456");
        assertEquals(tp.getEcomParameter().get("1"), "123x456");
        assertEquals(tp.getPageCategories().get("1"), "123x456");
        assertEquals(tp.getProductCategories().get("1"), "123x456");
        assertEquals(tp.getSessionParameter().get("1"), "123x456");
        assertEquals(tp.getUserCategories().get("1"), "123x456");
        assertEquals(tp.getPageParameter().get("1"), "123x456");
    }

}
