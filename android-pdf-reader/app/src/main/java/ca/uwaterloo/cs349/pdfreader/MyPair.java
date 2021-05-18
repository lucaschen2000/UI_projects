package ca.uwaterloo.cs349.pdfreader;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class MyPair {
    private final Path path;
    private final Paint paint;


    public MyPair(Path path, Paint paint)
    {
        this.path = new Path(path);
        this.paint = new Paint(paint);

    }

    public Path path()   { return path; }
    public Paint paint()   { return paint; }
}
