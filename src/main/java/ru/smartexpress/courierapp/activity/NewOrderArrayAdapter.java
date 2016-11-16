package ru.smartexpress.courierapp.activity;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.common.status.OrderTaskStatus;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.activity.finance.AccountFragment;
import ru.smartexpress.courierapp.order.OrderHelper;

import java.util.List;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 15.11.16 17:50
 */
public class NewOrderArrayAdapter  extends ArrayAdapter<OrderDTO> {
    public NewOrderArrayAdapter(Context context, @LayoutRes int resource, @NonNull List<OrderDTO> objects) {
        super(context, resource, 0, objects);
    }

    public NewOrderArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.new_order_layout, null);
        }

        OrderDTO p = getItem(position);

        if (p != null) {
            TextView address = (TextView) v.findViewById(R.id.address);
            TextView headline = (TextView) v.findViewById(R.id.orderDetails);
            TextView buyout = (TextView) v.findViewById(R.id.buyout);
            TextView sum = (TextView) v.findViewById(R.id.courierProfit);
            if (address != null) {
                address.setText(p.getDestinationAddress().getFirstLine());
            }

            if (headline != null) {
                headline.setText(OrderHelper.getOrderHeader(p));
            }

            if(sum!=null){
                String courierProfit = AccountFragment.moneyFormat.format(p.getProfit());
                sum.setText(courierProfit);
            }

            if (buyout != null) {
                if (p.getBuyoutSum() > 0.0) {
                    buyout.setVisibility(View.VISIBLE);
                    String sumText = AccountFragment.moneyFormat.format(p.getBuyoutSum());
                    buyout.setText(getContext().getString(R.string.buyout, sumText));
                } else {
                    buyout.setVisibility(View.INVISIBLE);
                }
            }
        }


        return v;
    }

}