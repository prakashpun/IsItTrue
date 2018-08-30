package com.thinkinghats.isittrue.activity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.thinkinghats.isittrue.R;
import com.thinkinghats.isittrue.adapters.CategoriesAdapter;
import com.thinkinghats.isittrue.model.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoriesAdapter.OnItemClickListener{

    private RecyclerView recyclerView;
    private List<ViewModel> items = new ArrayList<ViewModel>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRecyclerView();
        setRecyclerAdapter(recyclerView);

        items.add(new ViewModel("Knowledge",R.drawable.ic_knowledge));
        items.add(new ViewModel("Movies",R.drawable.ic_movies));
        items.add(new ViewModel("Sports",R.drawable.ic_sports));
        items.add(new ViewModel("Science",R.drawable.ic_science));

    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void setRecyclerAdapter(RecyclerView recyclerView) {
        CategoriesAdapter adapter = new CategoriesAdapter(MainActivity.this,items);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(View view, ViewModel viewModel) {
        Intent startGameIntent = new Intent(this, GameActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("categoryTitle", viewModel.getText());
        bundle.putInt("categoryImage", viewModel.getImage());
        startGameIntent.putExtras(bundle);

        startActivity(startGameIntent, ActivityOptions.makeSceneTransitionAnimation(this,
                    view.findViewById(R.id.categoryImage),this.getString(R.string.transition_categoryImage)).toBundle());
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.game_exit_dialog_title))
                .setMessage(getString(R.string.game_exit_dialog_message))
                .setPositiveButton(getString(R.string.game_exit_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.game_dialog_cancel), null)
                .setCancelable(false)
                .show();
    }
}
