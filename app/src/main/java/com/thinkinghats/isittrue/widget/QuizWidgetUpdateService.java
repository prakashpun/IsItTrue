package com.thinkinghats.isittrue.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.google.firebase.database.DatabaseReference;
import com.thinkinghats.isittrue.R;
import com.thinkinghats.isittrue.activity.MainActivity;
import com.thinkinghats.isittrue.model.QuestionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizWidgetUpdateService extends Service {

    QuestionModel question;
    List<String> mCollection = new ArrayList<>();
//    List<QuestionModel> mCollection = new ArrayList<>();
    //Firebase database intitialization
    private DatabaseReference mDatabaseReference;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
                .getApplicationContext());

        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        for (int widgetId : allWidgetIds) {
            fetchQuestionList();

            // create some random data
            int number = (new Random().nextInt(12));

            RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(),R.layout.quiz_widget);
            remoteViews.setTextViewText(R.id.appwidget_text, String.valueOf(mCollection.get(number)));

            /** PendingIntent to launch the MainActivity when the widget was clicked **/
            Intent launchMain = new Intent(this, MainActivity.class);
            PendingIntent pendingMainIntent = PendingIntent.getActivity(this, 0, launchMain, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget, pendingMainIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        stopSelf();
        return START_STICKY ;
    }

    private void fetchQuestionList() {

        mCollection.add(getString(R.string.widget_fact1));
        mCollection.add(getString(R.string.widget_fact2));
        mCollection.add(getString(R.string.widget_fact3));
        mCollection.add(getString(R.string.widget_fact4));
        mCollection.add(getString(R.string.widget_fact5));
        mCollection.add(getString(R.string.widget_fact6));
        mCollection.add(getString(R.string.widget_fact7));
        mCollection.add(getString(R.string.widget_fact8));
        mCollection.add(getString(R.string.widget_fact9));
        mCollection.add(getString(R.string.widget_fact10));
        mCollection.add(getString(R.string.widget_fact11));
        mCollection.add(getString(R.string.widget_fact12));
        mCollection.add(getString(R.string.widget_fact13));


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}