package cst8319.group11.project3.grocerylist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import android.widget.TextView;

import java.util.List;

import cst8319.group11.project3.grocerylist.R;
import cst8319.group11.project3.grocerylist.models.Item;

/*
 * Author: Rongrong Liu
 * File Name: ItemAdapter.java
 * Group: 11
 * Project: Grocery List
 * Due Date: 04/10/2025
 * Created Date: 04/22/2025
 *
 * */

// Added New ItemAdapter.java class to handle the checkbox logic
public class ItemAdapter extends BaseAdapter {
    public interface OnPurchaseToggleListener {
        void onPurchaseToggled(Item item, boolean isChecked);
    }

    private Context context;
    private List<Item> itemList;
    private LayoutInflater inflater;
    private OnPurchaseToggleListener toggleListener;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnPurchaseToggleListener(OnPurchaseToggleListener listener) {
        this.toggleListener = listener;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).getItemID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_grocery, parent, false);

            holder = new ViewHolder();
            holder.checkBox = convertView.findViewById(R.id.checkBoxItem);
            holder.tvItemName = convertView.findViewById(R.id.tvItemName);
            holder.tvPrice = convertView.findViewById(R.id.tvItemPrice);
            holder.tvQuantity = convertView.findViewById(R.id.tvItemQuantity);
            holder.tvBrand = convertView.findViewById(R.id.tvItemBrand);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Item item = itemList.get(position);

        // First, remove any existing listeners
        holder.checkBox.setOnCheckedChangeListener(null);

        // Set the initial checkbox state
        holder.checkBox.setChecked(item.isPurchased());

        // Add a new listener
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Apply the change to the item object
            item.setPurchased(isChecked);

            // Call the listener if it exists
            if (toggleListener != null) {
                toggleListener.onPurchaseToggled(item, isChecked);
            }
        });

        // Make the whole item clickable to toggle checkbox
        convertView.setOnClickListener(v -> {
            boolean newState = !holder.checkBox.isChecked();
            holder.checkBox.setChecked(newState);
            // The checkbox listener will handle the rest
        });

        holder.tvItemName.setText(item.getItemName());
        holder.tvPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.tvQuantity.setText("Qty: " + item.getQuantity());

        if (item.getBrand() != null && !item.getBrand().isEmpty()) {
            holder.tvBrand.setVisibility(View.VISIBLE);
            holder.tvBrand.setText(item.getBrand());
        } else {
            holder.tvBrand.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        CheckBox checkBox;
        TextView tvItemName;
        TextView tvPrice;
        TextView tvQuantity;
        TextView tvBrand;
    }
}
