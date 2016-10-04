package ru.smartexpress.courierapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.activity.finance.AccountFragment;
import ru.smartexpress.courierapp.activity.finance.CheckingAccountFragment;
import ru.smartexpress.courierapp.activity.finance.CollectionAccountFragment;
import ru.smartexpress.courierapp.activity.finance.CreditAccountFragment;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 03.10.16 17:16
 */
public class AccountActivity extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.account_layout);
        AppSectionsPagerAdapter appSectionsPagerAdapter = new AppSectionsPagerAdapter(this);

        AccountFragment checkingAccountFragment = new CheckingAccountFragment();
        appSectionsPagerAdapter.add(checkingAccountFragment);

        AccountFragment collectionAccountFragment = new CollectionAccountFragment();
        appSectionsPagerAdapter.add(collectionAccountFragment);

        AccountFragment creditAccountType = new CreditAccountFragment();
        appSectionsPagerAdapter.add(creditAccountType);

        appSectionsPagerAdapter.init();
    }
}
