package com.pz.vocabulary.app.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
* Created by piotr on 07/06/14.
*/
public class QuizResults implements Parcelable
{
    private int correctAnswers;
    private int wrongAnswers;
    private int skippedAnswers;

    public int getSkippedAnswers() {
        return skippedAnswers;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public QuizResults()
    {

    }

    public int getQuestionsCount()
    {
        return correctAnswers + wrongAnswers + skippedAnswers;
    }

    public void addCorrectAnswer()
    {
        correctAnswers++;
    }

    public void addSkippedAnswer()
    {
        skippedAnswers++;
    }

    public void addWrongAnswer()
    {
        wrongAnswers++;
    }

    public float getScore()
    {
        return (1.0f) * correctAnswers / (correctAnswers + wrongAnswers + skippedAnswers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.correctAnswers);
        dest.writeInt(this.wrongAnswers);
        dest.writeInt(this.skippedAnswers);
    }

    QuizResults(Parcel in) {
        this.correctAnswers = in.readInt();
        this.wrongAnswers = in.readInt();
        this.skippedAnswers = in.readInt();
    }

    public static Creator<QuizResults> CREATOR = new Creator<QuizResults>() {
        public QuizResults createFromParcel(Parcel source) {
            return new QuizResults(source);
        }

        public QuizResults[] newArray(int size) {
            return new QuizResults[size];
        }
    };
}
