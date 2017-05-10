package andrewly.receiptme.view.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by Andrew Ly on 5/8/2017.
 */

public class CustomFragmentFactory {

    public CustomFragmentFactory() {

    }

    public Fragment createFragment(String fragmentName, int position) {
        Fragment returnFragment;

        switch(fragmentName) {
            case "Placeholder":
                returnFragment = PlaceholderFragment.newInstance(position);
                break;
            case "OcrCapture":
                returnFragment = OcrCaptureFragment.newInstance(position);
                break;
            default:
                returnFragment = null;
                break;
        }

        return returnFragment;
    }
}
