package ru.smartexpress.courierapp.activity.finance;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import ru.smartexpress.common.data.SimplePagingLoadConfig;
import ru.smartexpress.common.data.SortInfo;
import ru.smartexpress.common.dto.AccountDTO;
import ru.smartexpress.common.dto.AccountRequest;
import ru.smartexpress.common.dto.TransactionDTO;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.activity.InfiniteScrollListener;
import ru.smartexpress.courierapp.activity.SeActivityFragment;
import ru.smartexpress.courierapp.activity.SeArrayAdapter;
import ru.smartexpress.courierapp.core.Logger;
import ru.smartexpress.courierapp.request.AccountRestRequest;
import ru.smartexpress.courierapp.service.JsonSpiceService;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 03.10.16 18:03
 */
public abstract class AccountFragment extends ListFragment implements SeActivityFragment, RequestListener<AccountDTO>{

    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    public static final DateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy");
    public static final NumberFormat moneyFormat = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
    private TransactionArrayAdapter arrayAdapter;

    private class TransactionArrayAdapter extends ArrayAdapter<TransactionDTO> {
        public TransactionArrayAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.transaction_layout, null);
            }

            TransactionDTO p = getItem(position);

            if (p != null) {
                TextView bookedDate = (TextView) v.findViewById(R.id.transactionBookedDate);
                TextView sum = (TextView) v.findViewById(R.id.transactionSum);
                TextView details = (TextView) v.findViewById(R.id.transactionDetails);

                if(p.getAmount() < 0.0)
                    sum.setTextColor(Color.RED);
                sum.setText(moneyFormat.format(p.getAmount()));
                bookedDate.setText(dateFormat.format(p.getDateBooked()));

                details.setText(p.getDescription());

            }

            return v;
        }
    }

    protected void setRefreshing(boolean refreshing){
        Activity activity = getActivity();
        if(activity==null)
            return;
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefreshMain);
        if(swipeRefreshLayout!=null)
            swipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnScrollListener(new InfiniteScrollListener(50) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                Logger.info("loadMoreCalled"+totalItemsCount);
                AccountRequest request = getLoadConfig();
                request.getLoadConfig().setOffset(totalItemsCount);
                spiceManager.execute(new AccountRestRequest(request), AccountFragment.this);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayAdapter = new TransactionArrayAdapter(getActivity(), R.layout.transaction_layout);
        setListAdapter(arrayAdapter);
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void update() {
        if(arrayAdapter!=null)
            arrayAdapter.clear();
        loadData();
    }

    private void loadData(){

        spiceManager.execute(new AccountRestRequest(getLoadConfig()), this);
    }

    private AccountRequest getLoadConfig(){
        SimplePagingLoadConfig loadConfig = new SimplePagingLoadConfig();
        SortInfo sortInfo = new SortInfo();
        sortInfo.setSortDir(SortInfo.SortDir.DESC);
        sortInfo.setSortField("dateBooked");
        loadConfig.setSortInfo(Arrays.asList(sortInfo));
        loadConfig.setLimit(50);
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountType(getAccountType());
        accountRequest.setLoadConfig(loadConfig);
        return accountRequest;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        setRefreshing(false);
        Logger.error(spiceException, "failed to load Account");

    }

    @Override
    public void onRequestSuccess(AccountDTO accountDTO) {
        setRefreshing(false);
        arrayAdapter.addAll(accountDTO.getTransactions().getData());
        TextView accountBalance = (TextView)getActivity().findViewById(R.id.accountBalance);
        accountBalance.setText(moneyFormat.format(accountDTO.getBalance()));
        if(accountDTO.getBalance() < 0){
            accountBalance.setTextColor(Color.RED);
        }
        else {
            accountBalance.setTextColor(Color.parseColor("#008000"));
        }
    }


    @Override
    public void forceDataRefresh() {
        arrayAdapter.clear();
        loadData();
    }

    public abstract String getAccountType();

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this.getActivity());
    }


    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();

    }
}
