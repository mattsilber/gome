package com.guardanis.gome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.guardanis.gome.tools.views.ToolbarLayoutBuilder;
import com.guardanis.imageloader.ImageRequest;

public class SplashActivity extends BaseActivity {

    private Handler launchHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_splash);

        ImageRequest.create(findViewById(R.id.splash__image))
                .setTargetAsset("gome__ic_launcher.svg")
                .setFadeTransition()
                .setShowStubOnExecute(false)
                .setShowStubOnError(false)
                .execute(150);

        launchHandler.postDelayed(() -> {
            if (getIntent().getAction().equals(Intent.ACTION_SEND)) {
                String shareTarget = getIntent().getStringExtra(Intent.EXTRA_TEXT);

                if (shareTarget != null && shareTarget.startsWith("http")) {
                    try {
                        Uri uri = Uri.parse(shareTarget);

                        getIntent().setData(uri);
                    }
                    catch (Exception e) { e.printStackTrace(); }
                }
            }

            Intent intent = new Intent(this, ConnectActivity.class)
                    .setData(getIntent().getData());

            startActivity(intent);
            finish();
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        launchHandler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }

    @Override
    protected void setup(ToolbarLayoutBuilder builder) {
        // Nothin there
    }
}
