package com.projects.melih.helpcity.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.projects.melih.helpcity.R;
import com.projects.melih.helpcity.ui.base.BaseActivity;

import java.util.Arrays;

/**
 * Created by melih on 4.12.2017
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 123;

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        snackView = findViewById(R.id.container);

        findViewById(R.id.signIn).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                startActivity(MapsActivity.newIntent(this));
                finish();
            } else {
                showSnackBar(getString(R.string.sign_in_failed));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signIn: {
                startSignIn();
                break;
            }
        }
    }

    private void startSignIn() {
        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(true)
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
    }

}