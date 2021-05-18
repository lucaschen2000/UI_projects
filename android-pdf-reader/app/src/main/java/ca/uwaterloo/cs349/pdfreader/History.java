package ca.uwaterloo.cs349.pdfreader;
import android.graphics.*;

public class History {
    String type;
    Path path;
    Paint paint;
    int erase_position;

    public History(String type, int erase_position, Path path, Paint paint){
        this.type = type;
        this.erase_position = erase_position;
        this.path = new Path(path);
        this.paint = new Paint(paint);
    }
}
