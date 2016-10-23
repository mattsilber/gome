package com.guardanis.gome;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.guardanis.gome.tools.views.ToolbarLayoutBuilder;

public class SettingsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_settings);
        setupToolbar();
        setup();
    }


    @Override
    protected void setup(ToolbarLayoutBuilder builder) {
        builder.addTitle(R.string.settings__title,
                v -> exit(RESULT_CANCELED, null));
    }

    protected void setup(){
        setupMoveSpeedView();
        setupScrollSpeedView();
    }

    private void setupMoveSpeedView(){
        int progress = Settings.getInstance(this)
                .get(Settings.KEY__MOVE_SPEED, 5);

        final TextView moveSpeedText = (TextView) findViewById(R.id.settings__move_speed_value);

        AppCompatSeekBar seek = (AppCompatSeekBar) findViewById(R.id.settings__move_speed_seekbar);
        seek.setMax(9);
        seek.setProgress(progress - 1);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    Settings.getInstance(SettingsActivity.this)
                            .put(Settings.KEY__MOVE_SPEED, progress + 1);

                moveSpeedText.setText(String.valueOf(progress + 1));
            }
            public void onStartTrackingTouch(SeekBar seekBar) { }
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        moveSpeedText.setText(String.valueOf(progress));
    }

    private void setupScrollSpeedView(){
        int progress = Settings.getInstance(this)
                .get(Settings.KEY__SCROLL_SPEED, 5);

        final TextView scrollSpeedText = (TextView) findViewById(R.id.settings__scroll_speed_value);

        AppCompatSeekBar seek = (AppCompatSeekBar) findViewById(R.id.settings__scroll_speed_seekbar);
        seek.setMax(9);
        seek.setProgress(progress - 1);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    Settings.getInstance(SettingsActivity.this)
                            .put(Settings.KEY__SCROLL_SPEED, progress + 1);

                scrollSpeedText.setText(String.valueOf(progress + 1));
            }
            public void onStartTrackingTouch(SeekBar seekBar) { }
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        scrollSpeedText.setText(String.valueOf(progress));
    }

}
