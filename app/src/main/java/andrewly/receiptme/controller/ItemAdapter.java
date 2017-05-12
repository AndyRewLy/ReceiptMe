package andrewly.receiptme.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import andrewly.receiptme.R;
import andrewly.receiptme.model.PurchasedItem;

/**
 * Created by Andrew Ly on 5/11/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

    private List<PurchasedItem> listData;
    private LayoutInflater inflater;
    private Context c;


    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback {
        void onItemClick(int p);
        void onSecondaryIconClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    public ItemAdapter(List<PurchasedItem> listData, Context c) {
        this.inflater = LayoutInflater.from(c);
        this.listData = listData;
        this.c = c;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_list_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        PurchasedItem item = listData.get(position);
        holder.title.setText(item.getItemName());
        holder.cost.setText("" + item.getCost());
        holder.categoryDropdown.setAdapter(createSpinnerAdapter());

        this.notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    private ArrayAdapter<CharSequence> createSpinnerAdapter() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(c, R.array.category_array,
                R.layout.support_simple_spinner_dropdown_item);

        return adapter;
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private EditText title;
        private EditText cost;
        //Spinner over here
        private Spinner categoryDropdown;

        public ItemHolder(View itemView) {
            super(itemView);

            title = (EditText) itemView.findViewById(R.id.lbl_item_text);
            cost = (EditText) itemView.findViewById(R.id.lbl_item_cost);
            categoryDropdown = (Spinner) itemView.findViewById(R.id.lbl_item_category);
        }

        @Override
        public void onClick(View v) {
            //TO-DO
        }
    }
}
