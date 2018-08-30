package com.thinkinghats.isittrue.activity;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thinkinghats.isittrue.R;
import com.thinkinghats.isittrue.adapters.QuestionsAdapter;
import com.thinkinghats.isittrue.model.QuestionModel;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thinkinghats.isittrue.activity.GameUtils.setCurrentLife;
import static com.thinkinghats.isittrue.activity.GameUtils.setCurrentScore;

public class GameActivity extends AppCompatActivity implements
        QuestionsAdapter.OnTrueBtnClickListener, QuestionsAdapter.OnFalseBtnClickListener {

    private Menu mMenu;
    CustomLinearLayoutManager layoutManager;
    private ArrayList<QuestionModel> questionModelList = new ArrayList<>();
    private String categorySelected;
    QuestionModel question;

    boolean setScore = false;
    boolean setAttempts = false;
    boolean allowLifeOnce = false;

    private int mCurrentScore;
    private int mHighScore;
    private int mCurrentLife;
    int currentPosition;


    @BindView(R.id.toolbar) Toolbar mtoolBar;
    @BindView(R.id.categoryImage) AppCompatImageView appCompatImageView;
    @BindView(R.id.card_question_recycleView) RecyclerView mQuestionsRecyclerView;
    @BindView(R.id.title) TextView mTitle ;
    @BindView(R.id.empty_view) View emptyView;

    AlertDialog gameOverDialog;
    ProgressDialog progress;


    // For showing facts layout with reveal animation
    LinearLayout questionLayout;
    LinearLayout factLayout;
    TextView card_fact_textView;
    LinearLayout answerLayout;
    TextView answer_indicator_textView;
    Button continue_button;

    //Firebase database intitialization
    private DatabaseReference mDatabaseReference;
    //Firebase Interstitial Ad
    private InterstitialAd mInterstitialAd;

    private final String KEY_QUESTION_LIST = "question_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ButterKnife.bind(this);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.i("Ad Displayed", "onAdDisplayed");

            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                Log.i("Ads", "onAdClosed");

                gameOverDialog.dismiss();
                allowLifeOnce = true;
                mCurrentLife = 1;
                setCurrentLife(GameActivity.this,mCurrentLife);
                setScore = true;
                setAttempts = true;
                invalidateOptionsMenu();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        //Set the current score
        GameUtils.setCurrentScore(this, 0);
        //Set current total number of lives
        GameUtils.setCurrentLife(this,3);

        // Get current and high scores.
        mCurrentScore = GameUtils.getCurrentScore(this);
        mHighScore = GameUtils.getHighScore(this);
        mCurrentLife = GameUtils.getCurrentLife(this);

        Bundle bundle = getIntent().getExtras();
        setSupportActionBar(mtoolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        mTitle.setText(bundle.getString("categoryTitle"));
        appCompatImageView.setImageResource(bundle.getInt("categoryImage"));
        categorySelected = (bundle.getString("categoryTitle")).toLowerCase();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.keepSynced(true);

        progress = new ProgressDialog(GameActivity.this);
        progress.setMessage(getString(R.string.progress_message));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        if(GameUtils.isNetworkAvailable(GameActivity.this)) {
            emptyView.setVisibility(View.GONE);
            if(savedInstanceState != null && savedInstanceState.containsKey(KEY_QUESTION_LIST)) {
                progress.dismiss();
                questionModelList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
                setRecyclerAdapter();
            }else {
                fetchQuestionList();
            }
        }else{
            emptyView.setVisibility(View.VISIBLE);
            Toast.makeText(this, getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show();
            progress.dismiss();
        }

        initRecyclerView();
    }

    public void showInterstitialAd(View view) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            finish();
        }
    }

    private void fetchQuestionList() {
        mDatabaseReference.child("questionsList").orderByChild("category")
                .equalTo(categorySelected).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot questionsDataSnapShot : dataSnapshot.getChildren()) {
                    question = questionsDataSnapShot.getValue(QuestionModel.class);
                    questionModelList.add(question);
                }
                Collections.shuffle(questionModelList);
                // To dismiss the dialog
                progress.dismiss();
                setRecyclerAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView() {
        layoutManager = new CustomLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        SnapHelper snapHelper = new LinearSnapHelper();
        mQuestionsRecyclerView.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(mQuestionsRecyclerView);
    }

    private void setRecyclerAdapter() {
        QuestionsAdapter adapter = new QuestionsAdapter(GameActivity.this, questionModelList);
        adapter.setOnTrueClickListener(GameActivity.this);
        adapter.setOnFalseClickListener(GameActivity.this);
        mQuestionsRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (setScore) {
            TextView menuScoreView = (TextView) menu.findItem(R.id.menu_score_item).getActionView();
            TextView scoreTextView = (TextView) menuScoreView.findViewById(R.id.menu_score_text);
            scoreTextView.setText(String.valueOf(mCurrentScore));
            setScore = false;
        }

        if(setAttempts){
            TextView menuLivesView = (TextView) menu.findItem(R.id.menu_life_item).getActionView();
            TextView remainingLifeTextView = (TextView) menuLivesView.findViewById(R.id.menu_life_text);
            remainingLifeTextView.setText(String.valueOf(mCurrentLife));
            setAttempts = false;
        }
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(GameActivity.this)
                .setTitle(getString(R.string.game_dialog_title))
                .setMessage(getString(R.string.game_dialog_message))
                .setPositiveButton(getString(R.string.game_dialog_quit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        GameActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(getString(R.string.game_dialog_cancel), null)
                .setCancelable(false)
                .show();
    }


    @Override
    public void onTrueClick(View view, int position) {
        revealFactsLayout(position,"TRUE");
    }

    @Override
    public void onFalseClick(View view, int position) {
        revealFactsLayout(position,"FALSE");
    }

    private void revealFactsLayout(int position, String userAnswer) {
        setScore = true;
        setAttempts = true;

        currentPosition = position;
        RecyclerView.ViewHolder viewHolder = mQuestionsRecyclerView.findViewHolderForAdapterPosition(position);

        questionLayout = (LinearLayout) viewHolder.itemView.findViewById(R.id.card_question_LV);
        factLayout = (LinearLayout) viewHolder.itemView.findViewById(R.id.card_fact_LV);

        continue_button = (Button) viewHolder.itemView.findViewById(R.id.continue_button);
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPosition != RecyclerView.NO_POSITION)
                    mQuestionsRecyclerView.scrollToPosition(currentPosition + 1);
                questionLayout.setVisibility(View.VISIBLE);
                factLayout.setVisibility(View.INVISIBLE);

            }
        });
        answerLayout = (LinearLayout) viewHolder.itemView.findViewById(R.id.answerIndicatorLV);
        answer_indicator_textView = (TextView) viewHolder.itemView.findViewById(R.id.answerIndicator_textView);
        card_fact_textView = (TextView) viewHolder.itemView.findViewById(R.id.card_fact_textView);
        card_fact_textView.setText(questionModelList.get(position).getFact());

        //Reveal animation for showing Facts Layout
        doCircularReveal();

        //Check user answers and update the score
        checkAnswers(userAnswer);

    }

    private void doCircularReveal() {
        int x = factLayout.getWidth() / 2;
        int y = factLayout.getHeight() / 2;

        int startRadius = 0;
        int endRadius = (int) Math.hypot(questionLayout.getWidth(), questionLayout.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(factLayout, x, y, startRadius, endRadius);
        anim.start();
        questionLayout.setVisibility(View.INVISIBLE);
        factLayout.setVisibility(View.VISIBLE);
    }

    private void checkAnswers(String userAnswer){
        if (questionModelList.get(currentPosition).getAnswer().equals(userAnswer)) {
            mCurrentScore += 1;

            setCurrentScore(this, mCurrentScore);
            if (mCurrentScore > mHighScore) {
                mHighScore = mCurrentScore;
                GameUtils.setHighScore(this, mHighScore);
            }

            answer_indicator_textView.setText(getString(R.string.correct_answer_msg));
            factLayout.findViewById(R.id.answerIndicatorLV).setBackgroundColor(getResources().getColor(R.color.correctAnswer));

        } else {
            answer_indicator_textView.setText(getString(R.string.wrong_answer_msg));
            factLayout.findViewById(R.id.answerIndicatorLV).setBackgroundColor(getResources().getColor(R.color.wrongAnswer));
            updateRemainingLives();
        }
        invalidateOptionsMenu();
    }

    private void updateRemainingLives(){
        if (mCurrentLife != 0) {
            mCurrentLife -= 1;
            setCurrentLife(this, mCurrentLife);
        }
        if (mCurrentLife == 0) {
            gameOver();
        }
        invalidateOptionsMenu();
    }

    private void gameOver(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View gameOverView = layoutInflater.inflate(R.layout.game_over, null);

        gameOverDialog = new AlertDialog.Builder(this).create();

        Button exitButton = (Button) gameOverView.findViewById(R.id.exit_button);
        Button reviveLifeButton = (Button) gameOverView.findViewById(R.id.revive_life_button);
        TextView userScore_txtView = (TextView)gameOverView.findViewById(R.id.user_score_tv);
        TextView userHighScore_txtView = (TextView)gameOverView.findViewById(R.id.highScore_header_tv);

        userScore_txtView.setText(String.valueOf(mCurrentScore));
        userHighScore_txtView.setText(getResources().getString(R.string.game_high_score)+" "+ String.valueOf((mHighScore)));

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(allowLifeOnce)
            reviveLifeButton.setVisibility(View.GONE);

        reviveLifeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitialAd(view);
            }
        });

        gameOverDialog.setView(gameOverView);
        gameOverDialog.setCancelable(false);
        gameOverDialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(KEY_QUESTION_LIST,questionModelList);
        super.onSaveInstanceState(outState);
    }

}
