package being.test.app;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;


public class ColorCircle extends AppCompatImageView {

    private static final int PRESSED_COLOR_LIGHTUP = 255 / 25;
    private static final int PRESSED_RING_ALPHA = 75;
    private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 4;
    private static final int ANIMATION_TIME_ID = android.R.integer.config_shortAnimTime;

    private int centerY;
    private int centerX;
    private int outerRadius;
    private float notifRadius;
    private float notifX;
    private float notifY;


    private Paint fillPaint;
    private Paint focusPaint;
    private Paint strokePaint;
    private Paint notificationPaintText;
    private Paint notificationPaint;
    private Paint drawOverTextPaint;

    private float animationProgress;

    private int pressedRingWidth;
    int fillColor;
    int borderColor;

    private int pressedColor;
    private ObjectAnimator pressedAnimator;
    int notificationCount;
    String drawOverText = "";

    float borderWidth, circleRadius;
    String desc = " ";


    Canvas c;

    public ColorCircle(Context context) {
        super(context);
        init(context, null);
    }

    public ColorCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ColorCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        setMeasuredDimension((int) (circleRadius * 2 + borderWidth * 6) | MeasureSpec.EXACTLY, (int) (circleRadius * 2 + borderWidth * 6) | MeasureSpec.EXACTLY);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);


        if (pressed) {
            showPressedRing();
        } else {
            hidePressedRing();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        this.c = canvas;
        canvas.drawCircle(centerX, centerY, circleRadius - borderWidth + animationProgress, focusPaint);
        canvas.drawCircle(centerX, centerY, outerRadius, fillPaint);
        canvas.drawCircle(centerX, centerY, outerRadius, strokePaint);
        desc = desc.toUpperCase();
        int xPos = (int) ((getWidth() / 2) - drawOverTextPaint.measureText(desc) / 2);
        int yPos = (int) ((getHeight() / 2) + centerY * 0.5f);

        canvas.drawText(desc, xPos, yPos, drawOverTextPaint);

        measure((int) (outerRadius * 2 + 2 * borderWidth + 1), (int) (outerRadius * 2 + 2 * borderWidth + 1));
        notifX = (float) (centerX + (circleRadius * 0.75));
        notifY = (float) (centerY - (circleRadius * 0.75));
        notifRadius = (float) (0.25 * outerRadius);
        if (notificationCount > 0) {
            canvas.drawCircle((float) notifX, (float) notifY,
                    (float) notifRadius, notificationPaint);
            canvas.drawText(notificationCount + "", notifX - (notifRadius / 4), notifY + (notifRadius / 4), notificationPaintText);
        }


        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        outerRadius = (int) circleRadius;

    }

    public float getAnimationProgress() {
        return animationProgress;
    }

    public void setAnimationProgress(float animationProgress) {
        this.animationProgress = animationProgress;
        this.invalidate();
    }


    public void setColor(int fillColor, int borderColor) {


        this.fillColor = fillColor;
        this.borderColor = borderColor;
        this.pressedColor = getHighlightColor(fillColor, PRESSED_COLOR_LIGHTUP);

        fillPaint.setColor(fillColor);
        notificationPaint.setColor(Color.RED);
        notificationPaintText.setColor(Color.WHITE);

        strokePaint.setColor(borderColor);

        focusPaint.setAlpha(PRESSED_RING_ALPHA);

        this.invalidate();
    }

    private void hidePressedRing() {
        fillPaint.setColor(fillColor);
        pressedAnimator.setFloatValues(pressedRingWidth, 0f);
        pressedAnimator.start();
    }

    private void showPressedRing() {
        fillPaint.setColor(fillPaint.getColor());
        pressedAnimator.setFloatValues(animationProgress, pressedRingWidth);
        pressedAnimator.start();
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
        this.invalidate();
    }


    public int getNotificationCount() {
        return notificationCount;
    }

    private void init(Context context, AttributeSet attrs) {
        int color = Color.WHITE;
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorCircle);
            color = a.getColor(R.styleable.ColorCircle_fillColor, color);
            fillColor = a.getColor(R.styleable.ColorCircle_fillColor, color);
            desc = (a.getString(R.styleable.ColorCircle_desc) != null ? (a.getString(R.styleable.ColorCircle_desc)) : "");
            borderColor = a.getColor(R.styleable.ColorCircle_borderColor, getResources().getColor(R.color.colorAccent));
            borderWidth = a.getDimension(R.styleable.ColorCircle_borderWidth, 0);
            circleRadius = a.getDimension(R.styleable.ColorCircle_circleRadius, centerX);
            notificationCount = a.getInteger(R.styleable.ColorCircle_notificationCount, 0);
            pressedRingWidth = (int) borderWidth;
            a.recycle();
        }

        this.setFocusable(true);
        this.setScaleType(ScaleType.CENTER_INSIDE);
        setClickable(true);


        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        notificationPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        notificationPaintText.setStyle(Paint.Style.FILL);
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                10, getResources().getDisplayMetrics());
        notificationPaintText.setTextSize(pixel);


        drawOverTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawOverTextPaint.setStyle(Paint.Style.FILL);
        drawOverTextPaint.setTextSize(pixel);


        drawOverTextPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        drawOverTextPaint.setTypeface(ResourcesCompat.getFont(context, R.font.circular_font));
        drawOverTextPaint.setTypeface(Typeface.DEFAULT_BOLD);


        notificationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        notificationPaint.setStyle(Paint.Style.FILL);

        focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        focusPaint.setStyle(Paint.Style.STROKE);

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(0);

        pressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
                .getDisplayMetrics());


        setColor(fillColor, borderColor);

        focusPaint.setStrokeWidth(0);

        final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
        pressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
        pressedAnimator.setDuration(pressedAnimationTime);
    }

    private int getHighlightColor(int color, int amount) {
        return Color.argb(Math.min(255, Color.alpha(color)), Math.min(255, Color.red(color) + amount),
                Math.min(255, Color.green(color) + amount), Math.min(255, Color.blue(color) + amount));
    }
}
