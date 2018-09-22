package com.laundry.laundry.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laundry.laundry.R;
import com.laundry.laundry.models.BagModel;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Aditya Kulkarni
 */

public class BagRecyclerAdater extends RecyclerView.Adapter<BagRecyclerAdater.ViewHolder> {

    private Context context;
    private ArrayList<BagModel> bagModels;

    public BagRecyclerAdater(Context context, ArrayList<BagModel> bagModels) {
        this.context = context;
        this.bagModels = bagModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bag_row, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvCount.setText("No of clothes: " + String.valueOf(bagModels.get(position).getCount()));
        holder.tvTagId.setText("Bag Tag ID: " + bagModels.get(position).getUid());
        if(bagModels.get(position).getService_type().equalsIgnoreCase("dry_clean")){
            holder.tvService.setText("Service: Dry clean");
        }else if(bagModels.get(position).getService_type().equalsIgnoreCase("iron")){
            holder.tvService.setText("Service: Iron");
        }else if(bagModels.get(position).getService_type().equalsIgnoreCase("wash")){
            holder.tvService.setText("Service: Wash");
        }
        holder.tvCustomer.setText("Customer ID: " + bagModels.get(position).getCustomerModel());
    }

    @Override
    public int getItemCount() {
        return bagModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCount, tvTagId, tvService, tvCustomer;
        public ViewHolder(View itemView) {
            super(itemView);

            tvCount = itemView.findViewById(R.id.tvCount);
            tvTagId = itemView.findViewById(R.id.tvTagId);
            tvService = itemView.findViewById(R.id.tvService);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
        }
    }
}
