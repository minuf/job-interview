package com.example.jorge.job_interview.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jorge.job_interview.R;

/**
 * Created by jorge on 22/04/16.
 */
public class DefaultEmptyAdapter extends RecyclerView.Adapter<DefaultEmptyAdapter.DefaultViewHolder> {
    private String[] mDataset;

    /**
     * CUSTOM Default Adapter, setted to recyclerview in oncreateview method,
     * for solve issue with recycler view
     * EXCEPTION: 'no adapter attached, skipping layout',
     * that breaks app main thread on some devices
     */
    public DefaultEmptyAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DefaultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false);
        DefaultViewHolder vh = new DefaultViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        holder.bindItem(mDataset[position]);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public class DefaultViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_default_adapter);
        }
        public void bindItem(String msg){
            textView.setText(msg+"");
        }
    }

    /**
     public class ViewHolder extends RecyclerView.ViewHolder {
     public ViewHolder(View v) {
     super(v);
     }
     }*/
}
