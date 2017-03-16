package rkr.binatestation.dreammanager.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

import rkr.binatestation.dreammanager.R;
import rkr.binatestation.dreammanager.adapters.DreamListAdapter;
import rkr.binatestation.dreammanager.models.DreamModel;


public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "HomeActivity";

    private static final int LOADER_DREAMS = 1;

    private ContentLoadingProgressBar mContentLoadingProgressBar;
    private View mEmptyMessageView;
    private DreamListAdapter mDreamListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContentLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.AH_progress_bar);
        mEmptyMessageView = findViewById(R.id.AH_empty_dream_view);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.AH_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mDreamListAdapter = new DreamListAdapter());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.AH_action_add_dream);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDreams();
    }

    private void loadDreams() {
        Log.d(TAG, "loadDreams() called");
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(LOADER_DREAMS, null, this);
    }

    private void navigateTo() {
        Log.d(TAG, "navigateTo() called");
        startActivity(new Intent(this, DreamActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void hideProgress() {
        Log.d(TAG, "hideProgress() called");
        mContentLoadingProgressBar.hide();
    }

    private void hideEmptyView() {
        Log.d(TAG, "hideEmptyView() called");
        mEmptyMessageView.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        Log.d(TAG, "showEmptyView() called");
        mEmptyMessageView.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return DreamModel.getAll(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        notifyDataSetChanged(DreamModel.getAll(data));
    }

    private void notifyDataSetChanged(List<DreamModel> dreamModelList) {
        hideProgress();
        if (mDreamListAdapter != null && dreamModelList.size() > 0) {
            hideEmptyView();
            mDreamListAdapter.setDreamModelList(dreamModelList);
        } else {
            showEmptyView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
