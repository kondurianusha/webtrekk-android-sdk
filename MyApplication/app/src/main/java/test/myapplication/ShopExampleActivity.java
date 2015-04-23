package test.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.webtrekk.android.tracking.Tracker;
import com.webtrekk.android.tracking.WTrackApplication;
import com.webtrekk.android.tracking.TrackingParams;
import com.webtrekk.android.tracking.TrackingParams.Params;



public class ShopExampleActivity extends ActionBarActivity {
    private Tracker t;
    TrackingParams tp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_example);
        t = ((WTrackApplication) getApplication()).getTracker("test");
        t.track();
        tp = new TrackingParams();
        tp.add(Params.PRODUCT, "Brauner Herrenschuh Leder: Mike")
                .add(Params.PRODUCT_CAT, 1, "Herren")
                .add(Params.PRODUCT_CAT, 2, "Schuhe")
                .add(Params.PRODUCT_CAT, 3, "Leder")
                .add(Params.PRODUCT_COUNT, "1")
                .add(Params.PRODUCT_COST, "99,95")
                .add(Params.PRODUCT_STATUS, "view")
                .add(Params.CURRENCY, "EUR");
        t.track(tp);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop_example, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onButtonOrderClicked(View view) {
        tp.add(Params.PRODUCT_STATUS, "add")
        .add(Params.ACTION_NAME, "orderButton");
        t.track(tp);
    }
}
