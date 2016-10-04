package ru.smartexpress.courierapp.activity.finance;

import ru.smartexpress.courierapp.R;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 04.10.16 12:41
 */
public class CheckingAccountFragment extends AccountFragment {
    @Override
    public int getImageResource() {
        return R.drawable.ic_place_selector;
    }

    @Override
    public int getTitle() {
        return R.string.checking_account;
    }

    @Override
    public String getAccountType() {
        return "CHECKING";
    }
}
