package vn.gotech.audiobook.widget.coverflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class AbstractCoverFlowAdapter<T> extends PagerAdapter {

    protected Context context;

    protected final List<T> items;

    public AbstractCoverFlowAdapter(Context context, List<T> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    public Bitmap getReflectionBitmap(Bitmap original) {
        final int w = original.getWidth();
        final int h = original.getHeight();
        final int rh = (h / 2);
        final int gradientColor = Color.argb(0x66, 0xff, 0xff, 0xff);

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        final Bitmap reflection = Bitmap.createBitmap(original, 0, rh, w, rh, matrix, false);

        Paint paint = new Paint();
        final LinearGradient shader = new LinearGradient(0, 0, 0, reflection.getHeight(), gradientColor, 0x00ffffff, Shader.TileMode.CLAMP);
        paint.reset();
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        Canvas canvas = new Canvas(reflection);
        canvas.setBitmap(reflection);
        canvas.drawRect(0, 0, reflection.getWidth(), reflection.getHeight(), paint);

        return reflection;
    }
}
