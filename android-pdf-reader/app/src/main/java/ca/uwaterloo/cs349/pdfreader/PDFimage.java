package ca.uwaterloo.cs349.pdfreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.*;
import android.widget.ImageView;
import android.view.ScaleGestureDetector;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.ArrayList;

@SuppressLint("AppCompatCustomView")
public class PDFimage extends ImageView {

    final String LOGNAME = "pdf_image";

    int cur_page;
    int num_pages;
    String type;
    float posX,posY,lastX,lastY;
    boolean currently_drawing = false;
    // drawing path
    Path cur_path = null;
    ArrayList<ArrayList<MyPair>> pages = new ArrayList();
    int pages_size;
    ArrayList<ArrayList<History>> undo_stacks = new ArrayList();
    ArrayList<ArrayList<History>> redo_stacks = new ArrayList();
    ArrayList<MyPair> pairs;
    ArrayList<History> undo_stack;
    ArrayList<History> redo_stack;

//    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    // image to display
    Bitmap bitmap;
    Paint paint = new Paint();

    // constructor
    public PDFimage(Context context, int num_pages) {
        super(context);

        this.num_pages = num_pages;
        this.pages_size = 1;
        pairs = new ArrayList();
        pages.add(pairs);
        undo_stack = new ArrayList();
        redo_stack = new ArrayList();
        undo_stacks.add(undo_stack);
        redo_stacks.add(redo_stack);

//        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        setDrawType("draw");
        cur_page = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        mScaleDetector.onTouchEvent(event);
        int pointer_count = event.getPointerCount();
        if(pointer_count == 2){
//            Log.d(LOGNAME, "x1: " + event.getX());
//            Log.d(LOGNAME, "y1: " + event.getY());
//            Log.d(LOGNAME, "x2: " + event.getX(1));
//            Log.d(LOGNAME, "y2: " + event.getY(1));
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(pointer_count == 1){
                    currently_drawing = true;
//                    Log.d(LOGNAME, "Action down");
                    cur_path = new Path();
                    cur_path.moveTo(event.getX()/mScaleFactor, event.getY()/mScaleFactor);
                } else {
//                    lastX = event.getX();
//                    lastY = event.getY();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(pointer_count >= 2){
//                    float dx = event.getX() - lastX;
//                    float dy = event.getY() - lastY;
//                    posX = posX + dx;
//                    posY = posY + dy;
//                    Log.d(LOGNAME, "posx: " + posX);
//                    Log.d(LOGNAME, "posy: " + posY);
//                    invalidate();
//                    lastX = event.getX();
//                    lastY = event.getY();
                } else {
//                    Log.d(LOGNAME, "Action move");
                    cur_path.lineTo(event.getX()/mScaleFactor, event.getY()/mScaleFactor);


                    if(type.equals("erase")){
                        for (int i = pairs.size() - 1; i >= 0; i--) {
                            float x = event.getX()/mScaleFactor;
                            float y = event.getY()/mScaleFactor;
                            RectF rectangle = new RectF(x - 1, y-1,
                                    x+1, y+1);
                            (pairs.get(i).path()).computeBounds(rectangle, true);
                            Region r = new Region();
                            r.setPath(pairs.get(i).path(), new Region((int) rectangle.left, (int) rectangle.top, (int) rectangle.right, (int) rectangle.bottom));
                            if(r.contains((int)x,(int) y)){
                                undo_stack.add(new History("erase", i, pairs.get(i).path(), pairs.get(i).paint()));
                                redo_stack.clear();
                                pairs.remove(i);
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(pointer_count == 1) {
                    currently_drawing = false;
//                    Log.d(LOGNAME, "Action up");
                    if (!type.equals("erase")) {
                        pairs.add(new MyPair(cur_path, new Paint(paint)));
                        undo_stack.add(new History("write", pairs.size() - 1, cur_path, new Paint(paint)));
                        redo_stack.clear();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

//        canvas.scale(mScaleFactor, mScaleFactor);
        //        canvas.translate(posX, posY);
        // draw background
        if (bitmap != null) {
            this.setImageBitmap(bitmap);
        }
        for (MyPair pair : pairs) {
            canvas.drawPath(pair.path(), pair.paint());
        }

        if(currently_drawing && !type.equals("erase")) canvas.drawPath(cur_path, paint);

        canvas.restore();
    }

//    // scalelistener taken from this guide:
//    //  https://android-developers.googleblog.com/2010/06/making-sense-of-multitouch.html
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            mScaleFactor *= detector.getScaleFactor();
//
//            // Don't let the object get too small or too large.
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
//
//            invalidate();
//            return true;
//        }
//    }

    public void undo(){
        if(!undo_stack.isEmpty()){
            History history = undo_stack.get(undo_stack.size() - 1);
            undo_stack.remove(undo_stack.size() - 1);
            if(history.type.equals("write")){
                //erase
                pairs.remove(history.erase_position);
                redo_stack.add(new History("erase", -1, history.path, history.paint));
            } else {
                pairs.add(new MyPair(history.path, history.paint));
                redo_stack.add(new History("write", pairs.size() - 1, history.path, history.paint));
            }
        }
    }

    public void redo(){
        if(!redo_stack.isEmpty()){
            History history = redo_stack.get(redo_stack.size() - 1);
            redo_stack.remove(redo_stack.size() - 1);
            if(history.type.equals("write")){
                //erase
                pairs.remove(history.erase_position);
                undo_stack.add(new History("erase", -1, history.path, history.paint));
            } else {
                pairs.add(new MyPair(history.path, history.paint));
                undo_stack.add(new History("write", pairs.size() - 1, history.path, history.paint));
            }
        }
    }


    public void changePage(boolean is_next){
        if(cur_page == 0 && !is_next || cur_page >= num_pages - 1 && is_next){
            return;
        }
//        if(is_next) Log.d("Lucas Debug is_next", "");
//        Log.d("Lucas Debug pages_size", String.valueOf(pages_size));
//        Log.d("Lucas Debug cur_page", String.valueOf(cur_page));

        if(is_next && (pages_size == cur_page + 1)){
//            Log.d("Lucas created new page", String.valueOf(cur_page));
            pairs = new ArrayList();
            pages.add(pairs);
            undo_stack = new ArrayList();
            redo_stack = new ArrayList();
            undo_stacks.add(undo_stack);
            redo_stacks.add(redo_stack);
            pages_size++;
            cur_page++;
        } else if(is_next) {
            pairs = pages.get(cur_page + 1);
            undo_stack = undo_stacks.get(cur_page + 1);
            redo_stack = redo_stacks.get(cur_page + 1);
            cur_page++;
        }
        else {
            pairs = pages.get(cur_page - 1);
            undo_stack = undo_stacks.get(cur_page - 1);
            redo_stack = redo_stacks.get(cur_page - 1);
            cur_page--;
        }
    }

    public void setDrawType(String type){
        this.type = type;
        if(type.equals("draw")){
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setColor(Color.parseColor("#ff0000FF"));
            paint.setAlpha(255);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(18);
        } else if(type.equals("highlight")) {
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setColor(Color.parseColor("#4dccff00"));
            paint.setAlpha(77);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(18);
        }
    }

    private int getIndex(MotionEvent event) {
        int id = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        return id;
    }

    // set image as background
    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


}
