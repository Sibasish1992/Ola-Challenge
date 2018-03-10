package com.play.sibasish.olaplay.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.play.sibasish.olaplay.Database_model.Song_Db;
import com.play.sibasish.olaplay.Listners.SongListner;
import com.play.sibasish.olaplay.Models.Song;
import com.play.sibasish.olaplay.R;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;


/**
 * Created by Sibasish Mohanty on 17/12/17.
 */

public class SongAdapters extends RecyclerView.Adapter<SongAdapters.DataObjectHolder> implements Filterable {

    private static String LOG_TAG = "SongAdapters";
    private ArrayList<Song_Db> mDataset;
    private ArrayList<Song_Db> mFilteredList;

    private ArrayList<String> nameList = new ArrayList<String>();

    private SongListner listner;
    private Context mContext;
    private boolean hide_download = false;
    Realm realm;
    public static class DataObjectHolder extends RecyclerView.ViewHolder{

        ImageView album_art;
        TextView title;
        TextView artists;
        ImageButton play;
        ImageButton download;
        Song_Db song;
        ImageButton favourite;
        TextView status;



        public DataObjectHolder(View itemView) {
            super(itemView);
            album_art =(ImageView) itemView.findViewById(R.id.songart);
            title = (TextView) itemView.findViewById(R.id.title);
            artists = (TextView) itemView.findViewById(R.id.artist);
            play = (ImageButton) itemView.findViewById(R.id.play);
            download = (ImageButton) itemView.findViewById(R.id.download);
            favourite = (ImageButton) itemView.findViewById(R.id.favourite);
            status = (TextView) itemView.findViewById(R.id.status);
        }

    }

    public void setSongListner(SongListner listner){
        this.listner = listner;

    }

    public SongAdapters(ArrayList<Song_Db> myDataset,Context ma,boolean hide) {
        mDataset = myDataset;
        mFilteredList = myDataset;
        mContext = ma;
        realm = Realm.getDefaultInstance();
        hide_download = hide;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_layout, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }



    public void addItem(Song_Db dataObj, int index) {
        mDataset.add(index, dataObj);
        nameList.add(index,dataObj.getTitle());
        notifyItemInserted(index);
    }

    public void setItems(ArrayList<Song_Db> songs){
        mDataset = songs;
        mFilteredList = songs;
        for (Song_Db song:
                songs) {
            nameList.add(song.getTitle());

        }
        notifyDataSetChanged();
    }
    public void deleteItem(int index) {
        mDataset.remove(index);
        nameList.remove(index);
        notifyItemRemoved(index);
    }





    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }




    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {

        final Song_Db song = mFilteredList.get(position);

        holder.song = song;
        holder.title.setText(song.getTitle());
        holder.artists.setText("Artists: "+song.getArtist());

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call Listner and pass the url
                listner.LetsPlay(song.getId());
            }
        });

        if (hide_download){
            holder.download.setVisibility(View.GONE);
            holder.status.setVisibility(View.GONE);
            holder.favourite.setVisibility(View.GONE);
        }
        else{
            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Call Listner and pass the url

                    listner.LetsDownLoad(song.getId());
                }
            });

            if (song.getDownload_status() == 0 ){
                holder.download.setEnabled(true);
                holder.status.setVisibility(View.GONE);
                holder.download.setVisibility(View.VISIBLE);
            }
            else if(song.getDownload_status() == 1 ){
                holder.download.setEnabled(false);
                holder.status.setVisibility(View.VISIBLE);
                holder.download.setVisibility(View.GONE);
                holder.status.setText("Saving");
            }
            else{
                holder.status.setVisibility(View.VISIBLE);
                holder.download.setEnabled(false);
                holder.download.setVisibility(View.GONE);
                holder.status.setText("Local");
            }

            if (song.isIs_fav()) {
                holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.golden_star));
            } else {
                holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.lightText));
            }
            holder.favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            song.setIs_fav(! song.isIs_fav());
                        }
                    });

                    if(v.getContext()!=null) {
                        if (song.isIs_fav()) {
                            holder.favourite.setColorFilter(v.getContext().getResources().getColor(R.color.golden_star));
                        } else {
                            holder.favourite.setColorFilter(v.getContext().getResources().getColor(R.color.lightText));
                        }
                    }

                    //Listner Fav
                    listner.LetsMakeFav(song.getId());

                }
            });
        }







        if(song.getImg_path()!=null && !song.getImg_path().trim().isEmpty()){
            File imageFile=new File(song.getImg_path());
            if(imageFile!=null){
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                holder.album_art.setImageBitmap(bitmap);
                holder.album_art.setBackgroundResource(0);
            }
            else{
                if (song.getAlbum_art() != null && !song.getAlbum_art().trim().isEmpty()){
                    Glide.with(holder.album_art.getContext()).load(song.getAlbum_art()).placeholder(R.drawable.olaplay_logo).into(holder.album_art);
                }
            }
        }
        else{
            if (song.getAlbum_art() != null && !song.getAlbum_art().trim().isEmpty()){
                Glide.with(holder.album_art.getContext()).load(song.getAlbum_art()).placeholder(R.drawable.olaplay_logo).into(holder.album_art);
            }
        }

    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                ArrayList<Song_Db> filteredList = new ArrayList<>();
                if (charString.isEmpty()) {
                    filteredList = mDataset;
                } else {


                    for (String title : nameList) {

                        if (title.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(mDataset.get(nameList.indexOf(title)));
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Song_Db>) filterResults.values;
                for (Song_Db project:
                        mFilteredList) {
                    Log.e(LOG_TAG,project.getTitle());
                }

                notifyDataSetChanged();
            }
        };

    }

}
