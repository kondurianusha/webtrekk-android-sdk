package com.Webtrekk.SDKTest;

import com.webtrekk.webtrekksdk.TrackingParameter;
import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 26.05.16.
 */
public class ProductTrackingTest extends ActivityInstrumentationTestCase2Base<EmptyActivity> {
    private Webtrekk mWebtrekk;

    public ProductTrackingTest() {
        super(EmptyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_no_auto_track);
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        finishActivitySync(getActivity());
        setActivity(null);
        super.tearDown();
    }

    public void testProductTracking()
    {
        final String productsName = "product1;product2";
        final String productQuantity = "3;1";
        final String productCost = "104.44;34.12";
        final String productCurrency = "EUR";
        final String productStatus = "view";
        final String productVoucher = "voucher";
        final String productCat1 = "ProdCat1";
        final String productCat2 = "ProdCat2";


        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                TrackingParameter par = new TrackingParameter();
                par.add(TrackingParameter.Parameter.PRODUCT, productsName);
                par.add(TrackingParameter.Parameter.PRODUCT_COUNT, productQuantity);
                par.add(TrackingParameter.Parameter.PRODUCT_COST, productCost);
                par.add(TrackingParameter.Parameter.CURRENCY, productCurrency);
                par.add(TrackingParameter.Parameter.PRODUCT_STATUS, productStatus);
                par.add(TrackingParameter.Parameter.VOUCHER_VALUE, productVoucher);
                par.add(TrackingParameter.Parameter.PRODUCT_CAT, "1", productCat1);
                par.add(TrackingParameter.Parameter.PRODUCT_CAT, "2", productCat2);
                mWebtrekk.track(par);
            }
        });

        String URL = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        parcel.parseURL(URL);

        assertEquals(productsName, HelperFunctions.urlDecode(parcel.getValue("ba")));
        assertEquals(productQuantity, HelperFunctions.urlDecode(parcel.getValue("qn")));
        assertEquals(productCost, HelperFunctions.urlDecode(parcel.getValue("co")));
        assertEquals(productCurrency, HelperFunctions.urlDecode(parcel.getValue("cr")));
        assertEquals(productStatus, HelperFunctions.urlDecode(parcel.getValue("st")));
        assertEquals(productVoucher, HelperFunctions.urlDecode(parcel.getValue("cb563")));
        assertEquals(productCat1, HelperFunctions.urlDecode(parcel.getValue("ca1")));
        assertEquals(productCat2, HelperFunctions.urlDecode(parcel.getValue("ca2")));
    }
}