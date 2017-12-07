package com.projects.melih.helpcity.ui;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.projects.melih.helpcity.ui.base.BaseActivity;

import org.jetbrains.annotations.Nullable;

/**
 * Created by melih on 4.12.2017
 */

public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(MapsActivity.newIntent(this));
        } else {
            startActivity(LoginActivity.newIntent(this));
        }
        finish();
    }
}
