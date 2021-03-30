package com.dana.puzzle.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class InsertGameBeenAsync extends AsyncTask<Void, Void, Void> {

public interface    IInsertAcheivement
 {

     void success(GameBeen gameBeen);
     void fail(GameBeen gameBeen);

 }

    GameBeen been;
    @SuppressLint("StaticFieldLeak")
    Context context;
    IInsertAcheivement iInsertAcheivement;

   public InsertGameBeenAsync(GameBeen gameBeen, Context context, IInsertAcheivement iInsertAcheivement) {
        this.been= gameBeen;
        this.context=context;
        this.iInsertAcheivement=iInsertAcheivement;
    }




    @Override
    protected Void doInBackground(Void... voids) {

        Log.e("doInBackground","in");

        //adding to database
        DatabaseClient.getInstance(context).getAppDatabase()
                .gameDao()
                .insert(been);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e("onPostExecute","in");
        if (iInsertAcheivement!=null)
            iInsertAcheivement.success(been);

    }


}


