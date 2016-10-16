package nldr.spamoff;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Roee on 16/10/2016.
 */
public class NestingViewPager extends ViewPager {
    public NestingViewPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public NestingViewPager(final Context context) {
        super(context);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v != this && v instanceof ViewPager) {
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }
}
