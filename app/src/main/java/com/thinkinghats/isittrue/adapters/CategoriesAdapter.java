package com.thinkinghats.isittrue.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thinkinghats.isittrue.R;
import com.thinkinghats.isittrue.model.ViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by prakash_pun on 7/10/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> implements View.OnClickListener {

    private List<ViewModel> items;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public CategoriesAdapter(Context context,List<ViewModel> items) {
        this.items = items;
        this.mContext = context;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_recycler_item, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewModel item = items.get(position);
        holder.text.setText(item.getText());
        holder.image.setImageResource(item.getImage());
        holder.itemView.setTag(item);
        holder.text.setContentDescription(
                mContext.getResources().getString(R.string.content_description_category_title,item.getText()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(final View v) {
        onItemClickListener.onItemClick(v, (ViewModel) v.getTag());
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.categoryImage)   public AppCompatImageView image;
        @BindView(R.id.title)  public TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, ViewModel viewModel);

    }
}


