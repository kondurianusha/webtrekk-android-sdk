package com.webtrekk.webbtrekksdk;


import android.test.AndroidTestCase;
import com.webtrekk.webbtrekksdk.TrackingParameter.Parameter;
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

        assertEquals(1, tp.getTparams().size());
        assertEquals("saveButtonClick", tp.getTparams().get(Parameter.ACTION_NAME));

        // add action param
        tp.add(Parameter.ACTION, "1", "test");
        assertEquals(1, tp.getActionParams().size());

        // add page param
        tp.add(Parameter.PAGE, "1", "test");
        assertEquals(1, tp.getPageParams().size());

        // add session param
        tp.add(Parameter.SESSION, "1", "test");
        assertEquals(1, tp.getSessionParams().size());

        // add ecom param
        tp.add(Parameter.ECOM, "1", "test");
        assertEquals(1, tp.getEcomParams().size());

        // add ad param
        tp.add(Parameter.AD, "1", "test");
        assertEquals(1, tp.getAdParams().size());

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

}
