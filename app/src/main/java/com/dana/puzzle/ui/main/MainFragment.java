package com.dana.puzzle.ui.main;

import androidx.lifecycle.ViewModelProviders;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dana.puzzle.R;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    View view;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.main_fragment, container, false);

        return view;
    }

    boolean widthCheck=true;
    int widthFinal,heightFinal;
     LinearLayout frameLayout1;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        frameLayout1= view.findViewById(R.id.frame);
        frameLayout1.setOnDragListener(new MyDragListener(null));
        final ViewTreeObserver obs =frameLayout1.getViewTreeObserver();
        obs.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {//width=lGrid.getWidth();
                if (widthCheck) {
                    widthFinal = frameLayout1.getWidth();
                    heightFinal = frameLayout1.getHeight();
                    getImagePeices();
                    widthCheck = false;
                }
            }
        });


       // getImagePeices();
        // TODO: Use the ViewModel
    }

/*private Path puzzlePath;

    private Bitmap getPuzzleBitmap(Bitmap bitmap)
    {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        calculatePuzzlePath(bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawPath(puzzlePath, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private void calculatePuzzlePath(int width, int height)
    {
        float radius = (height / 2) - 5;
        float smallRadius = radius / 3;
        radius -= smallRadius * 2;
        float centerX = width/2;
        float centerY = height/2;
        puzzlePath = new Path();
        // Bottom right
        puzzlePath.moveTo(centerX + radius, centerY + radius);
        // Top right
        puzzlePath.lineTo(centerX + radius, centerY - radius);
        // Center top
        puzzlePath.lineTo(centerX, centerY - radius);
        // Add outside circle to center top
        puzzlePath.addCircle(centerX, centerY - radius - ((radius / 3) / 2), radius / 3, Path.Direction.CCW);

        // Top left
        puzzlePath.lineTo(centerX - radius, centerY - radius);
        // Bottom left
        puzzlePath.lineTo(centerX - radius, centerY + radius);
        //Bottom right
        puzzlePath.lineTo(centerX + radius, centerY + radius);
    }*/





public void getImagePeices()
{
    int chunkNumbers=16;
    int rows,cols;

    //For height and width of the small image chunks
    int chunkHeight,chunkWidth;

    //To store all the small image chunks in bitmap format in this list
    ArrayList<Bitmap> chunkedImages = new ArrayList<Bitmap>(chunkNumbers);

    //Getting the scaled bitmap of the source image


   Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);



    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,widthFinal,heightFinal, false);
    rows = cols = (int) Math.sqrt(chunkNumbers);
    chunkHeight = scaledBitmap.getHeight() / rows;
    chunkWidth = scaledBitmap.getWidth() / cols;

    //xCoord and yCoord are the pixel positions of the image chunks
    int yCoord = 0;
    for(int x = 0; x < rows; x++) {
        LinearLayout linearLayout=new LinearLayout(getActivity());

        int xCoord = 0;
        for(int y = 0; y < cols; y++) {
            Bitmap bitmap1=Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight);
            chunkedImages.add(bitmap1);
            ImageView imageView1=new ImageView(getActivity());
            imageView1.setImageBitmap(bitmap1);
            int p=1;
            imageView1.setPadding(p,p,p,p);
            linearLayout.addView(imageView1);
            xCoord += chunkWidth;
        }
        yCoord += chunkHeight;
            frameLayout1.addView(linearLayout);

    }

    LinearLayout linearLayout=view.findViewById(R.id.ly_pics);
    linearLayout.setOnDragListener(new MyDragListener(null));

int i=0;
    for (Bitmap bitmap1:chunkedImages)
    {
        ImageView imageView1=new ImageView(getActivity());

        imageView1.setImageBitmap(bitmap1);

        imageView1.setPadding(10,10,10,10);

        imageView1.setTag(i+"in");
        i++;

        imageView1.setOnTouchListener(new MyClickListener());


        linearLayout.addView(imageView1);
    }



}


    static public class MyClickListener implements View.OnTouchListener {

        // called when the item is long-clicked


        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());

            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

            view.startDrag(data, //data to be dragged
                    shadowBuilder, //drag shadow
                    view, //local data about the drag and drop operation
                    0   //no needed flags
            );

            view.setVisibility(View.INVISIBLE);
            return true;
        }
    }



    public class MyDragListener implements View.OnDragListener {

        final ImageView imageView;

        public MyDragListener(final ImageView imageView) {
            this.imageView = imageView;
        }


        @Override
        public boolean onDrag(View dragedView, DragEvent event) {

            // Handles each of the expected events
            switch (event.getAction()) {

                //signal for the start of a drag and drop operation.
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;

                //the drag point has entered the bounding box of the View
                case DragEvent.ACTION_DRAG_ENTERED:
                    //v.setBackgroundResource(R.drawable.target_shape);    //change the shape of the view
                    break;

                //the user has moved the drag shadow outside the bounding box of the View
                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setBackgroundResource(R.drawable.normal_shape);    //change the shape of the view back to normal
                    break;

                //drag shadow has been released,the drag point is within the bounding box of the View
                case DragEvent.ACTION_DROP:
                    //v is the dynamic grid imageView, we accept the drag item
                    //view is listView imageView the dragged item

                        View view1= (View) event.getLocalState();
                        view1.setVisibility(View.VISIBLE);


            }
            return true;
        }
    }

}
