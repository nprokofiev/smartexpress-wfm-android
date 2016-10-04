package ru.smartexpress.courierapp.activity.finance;

import ru.smartexpress.courierapp.R;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 04.10.16 12:43
 */
public class CreditAccountFragment extends AccountFragment {
    @Override
    public int getImageResource() {
        return R.drawable.ic_place_selector;
    }

    @Override
    public int getTitle() {
        return R.string.credit_account;
    }

    @Override
    public String getAccountType() {
        return "CREDIT";
    }
}
