package test.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.webtrekk.webtrekksdk.Webtrekk;
import com.webtrekk.webtrekksdk.WebtrekkUserParameters;

/**
 * Created by vartbaronov on 29.02.16.
 */
public class CDBActivityTest extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cdb_test);

        findViewById(R.id.cdb_send_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Webtrekk webtrekk = Webtrekk.getInstance();

                webtrekk.track(new WebtrekkUserParameters().
                        setAddress(getTextFromID(R.id.cdb_address)).
                        setEmail(getTextFromID(R.id.cdb_email)).
                        setPhone(getTextFromID(R.id.cdb_phone)).
                        setAndroidId(getTextFromID(R.id.cdb_android_id)).
                        setiOSId(getTextFromID(R.id.cdb_ios_id)).
                        setWindowsId(getTextFromID(R.id.cdb_windows_id)).
                        setFacebookID(getTextFromID(R.id.cdb_facebook_id)).
                        setTwitterID(getTextFromID(R.id.cdb_twitter_id)).
                        setGooglePlusID(getTextFromID(R.id.cdb_google_plus_id)).
                        setLiknedInID(getTextFromID(R.id.cdb_linkin_id)).
                        setCustom(Integer.valueOf(getTextFromID(R.id.cdb_custom_ind1)), getTextFromID(R.id.cdb_custom_value1)).
                        setCustom(Integer.valueOf(getTextFromID(R.id.cdb_custom_ind2)), getTextFromID(R.id.cdb_custom_value2)));
            }
        });
        findViewById(R.id.cdb_clear_fields).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ids[] = {R.id.cdb_address, R.id.cdb_email, R.id.cdb_phone, R.id.cdb_android_id, R.id.cdb_ios_id,
                        R.id.cdb_windows_id, R.id.cdb_facebook_id, R.id.cdb_twitter_id, R.id.cdb_google_plus_id,
                        R.id.cdb_linkin_id, R.id.cdb_custom_ind1, R.id.cdb_custom_ind2, R.id.cdb_custom_value1,
                        R.id.cdb_custom_value2};

                for (int id: ids)
                {
                    setTextByID(id, "");
                }
            }
        });
    }

    private String getTextFromID(int id) {
        String value = ((TextView) findViewById(id)).getText().toString();

        return value.equals("null") ? null : value;
    }

    private void setTextByID(int id, String text)
    {
        ((TextView) findViewById(id)).setText(text);
    }
}
