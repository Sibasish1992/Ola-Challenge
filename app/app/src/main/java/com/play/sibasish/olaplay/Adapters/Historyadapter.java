package com.play.sibasish.olaplay.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.play.sibasish.olaplay.Activities.PlayerActivity;
import com.play.sibasish.olaplay.Database_model.HistoryDb;
import com.play.sibasish.olaplay.R;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by Sibasish Mohanty on 20/12/17.
 */

public class Historyadapter extends RecyclerView.Adapter<Historyadapter.DataObjectHolder> {


    Realm realm;
    private ArrayList<HistoryDb> mDataset;
    private Context mContext;


    public Historyadapter(ArrayList<HistoryDb> myDataset,Context ma) {
        mDataset = myDataset;
        mContext = ma;
        realm = Realm.getDefaultInstance();
    }
    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_layout, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        final HistoryDb historyDb = mDataset.get(position);
        holder.history = historyDb;
        if (historyDb.getAction().equals("played")){
            holder.album_art.setImageResource(R.drawable.ic_play_arrow);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                holder.title.setMovementMethod(LinkMovementMethod.getInstance());
                holder.title.setText(Html.fromHtml("Sibasish played the song: "+historyDb.getSong_name(),Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.title.setMovementMethod(LinkMovementMethod.getInstance());
                holder.title.setText(Html.fromHtml("Sibasish played the song: "+historyDb.getSong_name()));

            }

        }
        else{
            holder.album_art.setImageResource(R.drawable.ic_downloading_action);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                holder.title.setMovementMethod(LinkMovementMethod.getInstance());
                holder.title.setText(Html.fromHtml("Sibasish downloaded the song: "+historyDb.getSong_name(),Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.title.setMovementMethod(LinkMovementMethod.getInstance());
                holder.title.setText(Html.fromHtml("Sibasish downloaded the song: "+historyDb.getSong_name()));

            }
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder{
        ImageView album_art;
        TextView title;
        LinearLayout main;
        HistoryDb history;
        public DataObjectHolder(View itemView) {
            super(itemView);

            album_art =(ImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.text);
            main = (LinearLayout) itemView.findViewById(R.id.main);
            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context c = v.getContext();
                    Intent intent = new Intent(c, PlayerActivity.class);
                    intent.putExtra("songID",history.getSong_id());
                    c.startActivity(intent);
                }
            });

        }


    }

    public void addItem(HistoryDb dataObj, int index) {
        mDataset.add(index, dataObj);

        notifyItemInserted(index);
    }

    public void setItems(ArrayList<HistoryDb> songs){
        mDataset = songs;


        notifyDataSetChanged();
    }
    public void deleteItem(int index) {
        mDataset.remove(index);

        notifyItemRemoved(index);
    }






}
