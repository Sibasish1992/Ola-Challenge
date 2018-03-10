package com.play.sibasish.olaplay.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.play.sibasish.olaplay.Adapters.Historyadapter;
import com.play.sibasish.olaplay.Database_model.HistoryDb;
import com.play.sibasish.olaplay.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class HistoryActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    Realm realm;
    public Historyadapter madapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private  ArrayList<HistoryDb> hisToDispaly = new ArrayList<HistoryDb>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.messageRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        madapter = new Historyadapter(new ArrayList<HistoryDb>(),this);
        recyclerView.setAdapter(madapter);
        recyclerView.setLayoutManager(mLayoutManager);

        getHistory();

    }

    public void getHistory(){
        realm=Realm.getDefaultInstance();
        RealmResults<HistoryDb> histories=realm.where(HistoryDb.class).findAll();
        histories=histories.sort("push_date", Sort.DESCENDING);

        for (HistoryDb his:
                histories) {
            hisToDispaly.add(his);
        }
        madapter.setItems(hisToDispaly);
        realm.close();
    }



    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }

}
