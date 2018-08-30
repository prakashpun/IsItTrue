package com.thinkinghats.isittrue.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thinkinghats.isittrue.R;
import com.thinkinghats.isittrue.model.QuestionModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by prakash_pun on 7/29/2017.
 */

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder>{

    private List<QuestionModel> questionModelList;
    private Context mContext;

    public  static OnTrueBtnClickListener onTrueBtnClickListener;
    public  static OnFalseBtnClickListener onFalseBtnClickListener;

    public QuestionsAdapter(Context context,List<QuestionModel> items) {
        this.questionModelList = items;
        this.mContext = context;
    }

    //Listener for True Btn click
    public interface OnTrueBtnClickListener {
        void onTrueClick(View view, int position);
    }

    //Listener for False Btn click
    public interface OnFalseBtnClickListener {
        void onFalseClick(View view, int position);
    }


    public void setOnTrueClickListener(OnTrueBtnClickListener onTrueClickListener) {
        this.onTrueBtnClickListener = onTrueClickListener;
    }
    public void setOnFalseClickListener(OnFalseBtnClickListener onFalseClickListener) {
        this.onFalseBtnClickListener = onFalseClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QuestionModel item = questionModelList.get(position);
        holder.question_textView.setText(item.getQuestion());
        holder.itemView.setTag(item);
        holder.question_textView.setContentDescription(
                mContext.getResources().getString(R.string.content_description_question,item.getQuestion()));
    }

    @Override
    public int getItemCount() {
        return questionModelList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_question_textView) public TextView question_textView;
        @BindView(R.id.true_button) public Button true_Button;
        @BindView(R.id.false_button) public Button false_Button;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            true_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onTrueBtnClickListener.onTrueClick(view, position);
                    }
                }
            });

            false_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onFalseBtnClickListener.onFalseClick(view, position);
                    }
                }
            });
        }
    }



}
