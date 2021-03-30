package com.dana.puzzle.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class GetGameBeenAsync extends AsyncTask<Void, Void, List<GameBeen> > {

public interface    IGettAcheivement
 {

     void success(List<GameBeen> gameBeens);
     void fail(String error);

 }


    @SuppressLint("StaticFieldLeak")
    Context context;
    IGettAcheivement iGettAcheivement;

   public GetGameBeenAsync( Context context, IGettAcheivement iGettAcheivement) {

        this.context=context;
        this.iGettAcheivement=iGettAcheivement;
    }




    @Override
    protected List<GameBeen> doInBackground(Void... voids) {

        Log.e("doInBackground","in");

        //adding to database
      return   DatabaseClient.getInstance(context).getAppDatabase()
                .gameDao()
                .getAll();


    }

    @Override
    protected void onPostExecute(List<GameBeen> gameBeens) {
        super.onPostExecute(gameBeens);

        if (iGettAcheivement!=null)
            iGettAcheivement.success(gameBeens);

    }


}


