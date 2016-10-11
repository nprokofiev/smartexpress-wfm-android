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
import ru.smartexpress.courierapp.order.OrderHelper;

import java.util.List;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 21.04.16 9:33
 */
public class SeArrayAdapter extends ArrayAdapter<OrderDTO> {
    public SeArrayAdapter(Context context, @LayoutRes int resource, @NonNull List<OrderDTO> objects) {
        super(context, resource, 0, objects);
    }

    public SeArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.order_fragment_list_item, null);
        }

        OrderDTO p = getItem(position);

        if (p != null) {
            TextView address = (TextView) v.findViewById(R.id.order_address);
            TextView headline = (TextView) v.findViewById(R.id.order_headline);
            ImageView imageView = (ImageView) v.findViewById(R.id.order_icon);

            if(OrderTaskStatus.PICKED_UP.toString().equals(p.getStatus()))
                imageView.setImageResource(R.drawable.ic_local_shipping_black_18dp);
            else
                imageView.setImageResource(R.drawable.ic_place_black_18dp);
            if (address != null) {
                address.setText(p.getDestinationAddress().getFirstLine());
            }

            if (headline != null) {
                headline.setText(OrderHelper.getOrderHeader(p));
            }

        }

        return v;
    }
}
