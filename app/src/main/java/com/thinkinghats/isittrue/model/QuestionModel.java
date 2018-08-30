package com.thinkinghats.isittrue.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by prakash_pun on 7/31/2017.
 */

public class QuestionModel implements Parcelable{
    private String answer;
    private String category;
    private String fact;
    private String question;

    /**
     * No args constructor for use in serialization
     */
    public QuestionModel() {
    }

    private QuestionModel(Parcel in){
        answer = in.readString();
        category = in.readString();
        fact = in.readString();
        question = in.readString();
    }
    public QuestionModel(String answer, String category, String fact, String question) {
        this.answer = answer;
        this.category = category;
        this.fact = fact;
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFact() {
        return fact;
    }

    public void setFact(String fact) {
        this.fact = fact;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(answer);
        parcel.writeString(category);
        parcel.writeString(fact);
        parcel.writeString(question);
    }

    public static final Parcelable.Creator<QuestionModel> CREATOR = new Parcelable.Creator<QuestionModel>() {
        public QuestionModel createFromParcel(Parcel in) {
            return new QuestionModel(in);
        }

        public QuestionModel[] newArray(int size) {
            return new QuestionModel[size];
        }
    };
}
