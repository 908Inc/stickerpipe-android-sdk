package vc908.stickersample.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import vc908.stickerfactory.StickersKeyboardController;
import vc908.stickerfactory.ui.fragment.StickersFragment;
import vc908.stickerfactory.ui.view.StickersKeyboardLayout;
import vc908.stickersample.R;

/**
 * @author Dmitry Nezhydenko (dehimb@gmail.com)
 */
public class FullscreenStickersActivity extends AppCompatActivity {

    private StickersKeyboardController stickersKeyboardController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        StickersKeyboardLayout container = (StickersKeyboardLayout) findViewById(R.id.container);
        StickersFragment stickersFragment = (StickersFragment) getSupportFragmentManager().findFragmentById(R.id.stickers_frame);
        if (stickersFragment == null) {
            stickersFragment = new StickersFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.stickers_frame, stickersFragment).commit();
        }
        stickersFragment.setFulllSizeEmptyImage(R.drawable.il_intro);
        stickersKeyboardController = new StickersKeyboardController.Builder(this)
                .setStickersKeyboardLayout(container)
                .setStickersFragment(stickersFragment)
                .build();
    }

    @Override
    public void onBackPressed() {
        if (!stickersKeyboardController.hideStickersKeyboard()) {
            super.onBackPressed();
        }
    }
}
