package ru.smartexpress.courierapp.layout;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 10.10.16 13:41
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {
    public interface ScrollUpHandler{
        boolean canScrollUp();
    }

    private ScrollUpHandler scrollUpHandler;

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollUpHandler(ScrollUpHandler scrollUpHandler) {
        this.scrollUpHandler = scrollUpHandler;
    }

    @Override
    public boolean canChildScrollUp() {
        if(scrollUpHandler!=null)
            return scrollUpHandler.canScrollUp();
        return super.canChildScrollUp();
    }
}
