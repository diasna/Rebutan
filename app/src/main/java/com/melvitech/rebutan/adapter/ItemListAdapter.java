package com.melvitech.rebutan.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.melvitech.rebutan.R;
import com.melvitech.rebutan.crawler.model.Item;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by miku on 5/24/14.
 */
public class ItemListAdapter extends ArrayAdapter<Item> {

    private final Context context;
    private List<Item> items;

    public ItemListAdapter(Context context, List<Item> items) {
        super(context, R.layout.list_row_item, items);
        this.context = context;
        this.items = items;
    }

    public void setItems(List<Item> items) {
        notifyDataSetChanged();
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.location = (TextView) convertView.findViewById(R.id.textView7);
            viewHolder.price = (TextView) convertView.findViewById(R.id.textView6);
            viewHolder.time = (TextView) convertView.findViewById(R.id.textView2);
            viewHolder.user = (TextView) convertView.findViewById(R.id.textView5);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Item item = getItem(position);
        if (item != null) {
            viewHolder.title.setText(item.getName());
            if (item.isRead()) {
                viewHolder.title.setTypeface(null, Typeface.NORMAL);
            } else {
                viewHolder.title.setTypeface(null, Typeface.BOLD);
            }
            viewHolder.user.setText(item.getUser());
            viewHolder.time.setText(DateUtils.getRelativeDateTimeString(context, item.getCreateDate().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_NUMERIC_DATE));
            viewHolder.price.setText(NumberFormat.getCurrencyInstance(new Locale("in_ID")).format(item.getPrice()));
            viewHolder.location.setText(item.getLocation());
        }
        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView time;
        TextView user;
        TextView location;
        TextView price;
    }
}
