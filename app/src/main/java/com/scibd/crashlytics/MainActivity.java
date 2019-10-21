package com.scibd.crashlytics;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Button crash;
    private TextView value,welcome;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                value.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Region crashlytics force crash
        Fabric.with(this, new Crashlytics());
        Crashlytics.log("Your log");
        Crashlytics.logException(new Throwable("This your not-fatal name"));


        crash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Crashlytics.getInstance().crash(); // Force a crash
            }
        });
        //region crashlytics end

        //region remote config starts
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build());
        HashMap<String,Object> defaults = new HashMap<>();
        defaults.put("test_config",7);
        defaults.put("welcome_message","Welcome");

        mFirebaseRemoteConfig.setDefaults(defaults);
        final Task<Void> fetch = mFirebaseRemoteConfig.fetch();
        fetch.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFirebaseRemoteConfig.activateFetched();
                maxTextLength();
                setTextViewMessage();
            }
        });
        //region remote config end

    }

    private void init() {
        crash = findViewById(R.id.Crash);
        editText = findViewById(R.id.EdtValue);
        value = findViewById(R.id.Value);
        welcome = findViewById(R.id.Welcome);
    }

    private void maxTextLength() {
        int max = (int) mFirebaseRemoteConfig.getLong("test_config");
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
    }

    private void setTextViewMessage(){
        String msg = mFirebaseRemoteConfig.getString("welcome_message");
        welcome.setText(msg);
    }
}
