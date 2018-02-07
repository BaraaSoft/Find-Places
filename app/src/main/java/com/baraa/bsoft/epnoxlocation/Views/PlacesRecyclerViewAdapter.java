package com.baraa.bsoft.epnoxlocation.Views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baraa.bsoft.epnoxlocation.Model.LocationModel;
import com.baraa.bsoft.epnoxlocation.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by baraa on 05/01/2018.
 */

public class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<PlacesRecyclerViewAdapter.ViewHolder> {
    private ArrayList<LocationModel> listLocationModels;
    private Context mContext;

    public PlacesRecyclerViewAdapter(ArrayList<LocationModel> listLocations, Context context) {
        this.listLocationModels = listLocations;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocationModel model = listLocationModels.get(position);
        holder.getTvTitle().setText(model.getTitle());
        holder.getTvAddress().setText(model.getAddress());
        Picasso.with(mContext).load(model.getImgUrl()).into(holder.getBtnImg());
        if(model.isChecked()){
            holder.getBtnDone().setVisibility(View.VISIBLE);
        }else {
            holder.getBtnDone().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listLocationModels.size();
    }

    public void loadNewLocations(ArrayList<LocationModel> lst){
        this.listLocationModels = lst;
        notifyDataSetChanged();
    }
    public LocationModel getSelectedModelChecked(int index,View view){
        for (int x= 0;x<listLocationModels.size();x++) {
            if (x==index){
                listLocationModels.get(x).setChecked(true);
            }else {
                listLocationModels.get(x).setChecked(false);
            }
        }
        notifyDataSetChanged();
        return listLocationModels.get(index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        private TextView tvAddress;
        private ImageButton btnImg;
        private ImageButton btnDone;
        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            tvAddress = (TextView)itemView.findViewById(R.id.tvAddress);
            btnImg = (ImageButton) itemView.findViewById(R.id.btnImg);
            btnDone = (ImageButton) itemView.findViewById(R.id.btnDone);
        }

        public TextView getTvTitle() {
            return tvTitle;
        }

        public TextView getTvAddress() {
            return tvAddress;
        }

        public ImageButton getBtnImg() {
            return btnImg;
        }

        public ImageButton getBtnDone() {
            return btnDone;
        }
    }
}
