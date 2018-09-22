package com.laundry.laundry.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laundry.laundry.R;
import com.laundry.laundry.models.ClothModel;

import java.util.ArrayList;

/**
 * @author Aditya Kulkarni
 */

public class ClothRecyclerAdapter extends RecyclerView.Adapter<ClothRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ClothModel> clothModels;
    private String name;

    public ClothRecyclerAdapter(Context context, ArrayList<ClothModel> bagModels, String name) {
        this.context = context;
        this.clothModels = bagModels;
        this.name = name;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cloth_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvCustName.setText(name);
        holder.tvClothTag.setText("Cloth tag: " + clothModels.get(position).getUid());
        holder.tvClothType.setText("Cloth type: " + clothModels.get(position).getCloth_type());
        holder.tvColor.setText("Color: " + clothModels.get(position).getColor());
        holder.tvBag.setText("Bag tag: " + clothModels.get(position).getBagTagId());
    }

    @Override
    public int getItemCount() {
        return clothModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCustName, tvClothTag, tvClothType, tvColor, tvBag;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCustName = itemView.findViewById(R.id.tvCustName);
            tvClothTag = itemView.findViewById(R.id.tvClothUid);
            tvClothType = itemView.findViewById(R.id.tvClothType);
            tvColor = itemView.findViewById(R.id.tvClothColor);
            tvBag = itemView.findViewById(R.id.tvClothBag);
        }
    }
}