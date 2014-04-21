package edu.eurac.kkbook.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.widget.Button;
import edu.eurac.kkbook.R;

/**
 * Created by ebaranov on 17/03/14.
 */
public class RoundCornerBorderButton extends Button {

    private int mBorderColor;
    private int mBorderColorFocused;
    private int mBorderColorPressed;
    private int mBorderColorDisabled;

    private int mBackgroundColor;
    private int mBackgroundColorFocused;
    private int mBackgroundColorPressed;
    private int mBackgroundColorDisabled;

    private int mBorderWidth;
    private int mBorderWidthFocused;
    private int mBorderWidthPressed;

    private int mBorderRadiusPx;
    private int mBorderRadiusLeftTop;
    private int mBorderRadiusRightTop;
    private int mBorderRadiusRightBottom;
    private int mBorderRadiusLeftBottom;

    private int mTextColor;
    private int mTextColorFocused;
    private int mTextColorPressed;
    private int mTextColorDisabled;

    private BorderType mBorderType;

    private enum BorderType {
        btNone(0), btAll(1), btTopBottom(2), btLeftRight(3);

        private int mId;

        private BorderType(int id) {
            mId = id;
        }

        public static BorderType fromId(int id) {
            for (BorderType t: values()) {
                if (t.mId == id) {
                    return t;
                }
            }
            return btNone;
        }
    }


    public RoundCornerBorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerBorderButton);

        mBorderColor = a.getColor(R.styleable.RoundCornerBorderButton_borderColor, 0);
        mBorderColorFocused = a.getColor(R.styleable.RoundCornerBorderButton_borderColorFocused, mBorderColor);
        mBorderColorPressed = a.getColor(R.styleable.RoundCornerBorderButton_borderColorPressed, mBorderColor);
        mBorderColorDisabled = a.getColor(R.styleable.RoundCornerBorderButton_borderColorDisabled, mBorderColor);

        mBackgroundColor = a.getColor(R.styleable.RoundCornerBorderButton_backgroundColor, 0);
        mBackgroundColorFocused = a.getColor(R.styleable.RoundCornerBorderButton_backgroundColorFocused, 0);
        mBackgroundColorPressed = a.getColor(R.styleable.RoundCornerBorderButton_backgroundColorPressed, 0);
        mBackgroundColorDisabled = a.getColor(R.styleable.RoundCornerBorderButton_backgroundColorDisabled, 0);

        mTextColor = getTextColors().getDefaultColor();
        mTextColorFocused = a.getColor(R.styleable.RoundCornerBorderButton_textColorFocused, mTextColor);
        mTextColorPressed = a.getColor(R.styleable.RoundCornerBorderButton_textColorPressed, mTextColor);
        mTextColorDisabled = a.getColor(R.styleable.RoundCornerBorderButton_textColorDisabled, mTextColor);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.RoundCornerBorderButton_borderWidth, 0);
        mBorderWidthFocused = a.getDimensionPixelSize(R.styleable.RoundCornerBorderButton_borderWidthFocused, mBorderWidth);
        mBorderWidthPressed = a.getDimensionPixelSize(R.styleable.RoundCornerBorderButton_borderWidthPressed, mBorderWidth);

        mBorderRadiusPx = a.getDimensionPixelSize(R.styleable.RoundCornerBorderButton_borderRadius, 0);
        mBorderRadiusLeftTop = a.getDimensionPixelSize(R.styleable.RoundCornerBorderButton_borderRadiusLeftTop, mBorderRadiusPx);
        mBorderRadiusRightTop = a.getDimensionPixelSize(R.styleable.RoundCornerBorderButton_borderRadiusRightTop, mBorderRadiusPx);
        mBorderRadiusRightBottom = a.getDimensionPixelSize(R.styleable.RoundCornerBorderButton_borderRadiusRightBottom, mBorderRadiusPx);
        mBorderRadiusLeftBottom = a.getDimensionPixelSize(R.styleable.RoundCornerBorderButton_borderRadiusLeftBottom, mBorderRadiusPx);

        mBorderType = BorderType.fromId(a.getInt(R.styleable.RoundCornerBorderButton_borderType, 0));

        a.recycle();
    }

    public RoundCornerBorderButton(Context context) {
        super(context);
    }

    @SuppressLint("NewApi")
	@Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setTextColor(createTextColor());
        if (android.os.Build.VERSION.SDK_INT>=16) {
            setBackground(createBackground());
        } else {
            //noinspection deprecation
            setBackgroundDrawable(createBackground());
        }
    }

    private Drawable createBackground() {
        float[] bordersArray = new float[]{
                mBorderRadiusLeftTop, mBorderRadiusLeftTop,
                mBorderRadiusRightTop, mBorderRadiusRightTop,
                mBorderRadiusRightBottom, mBorderRadiusRightBottom,
                mBorderRadiusLeftBottom, mBorderRadiusLeftBottom
        };
        /**
         * start: border shape
         */
        StateListDrawable borderDrawable = new StateListDrawable();
        switch (mBorderType) {
            case btNone:
                break;
            case btAll:
                addBorderDrawableAll(bordersArray, borderDrawable);
                break;
            case btLeftRight:
                addBorderDrawableLeftRight(bordersArray, borderDrawable);
                break;
            case btTopBottom:
                addBorderDrawableTopBottom(bordersArray, borderDrawable);
                break;
            default:
        }
        return borderDrawable;
    }

    private ColorStateList createTextColor() {
        int[][] states = new int[][] {
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused},
                new int[]{-android.R.attr.state_enabled},
                new int[]{}
        };
        int[] colors = new int[] {mTextColorPressed, mTextColorFocused, mTextColorDisabled, mTextColor};
        return new ColorStateList(states, colors);
    }

    private void addBorderDrawableAll(float[] bordersArray, StateListDrawable borderDrawable) {
        BorderedRoundRectDrawable borderPressed = new BorderedRoundRectDrawable(bordersArray, mBackgroundColorPressed, mBorderColorPressed, mBorderWidthPressed, new Rect(0, 0, 0, 0));
        borderDrawable.addState(new int[] {android.R.attr.state_pressed}, borderPressed);

        BorderedRoundRectDrawable borderFocused = new BorderedRoundRectDrawable(bordersArray, mBackgroundColorFocused, mBorderColorFocused, mBorderWidthFocused, new Rect(0, 0, 0, 0));
        borderDrawable.addState(new int[] {android.R.attr.state_focused}, borderFocused);

        BorderedRoundRectDrawable borderDisabled = new BorderedRoundRectDrawable(bordersArray, mBackgroundColorDisabled, mBorderColorDisabled, mBorderWidth, new Rect(0, 0, 0, 0));
        borderDrawable.addState(new int[] {-android.R.attr.state_enabled}, borderDisabled);

        BorderedRoundRectDrawable border = new BorderedRoundRectDrawable(bordersArray, mBackgroundColor, mBorderColor, mBorderWidth, new Rect(0, 0, 0, 0));
        borderDrawable.addState(new int[] {}, border);
    }

    public class BorderedRoundRectDrawable extends ShapeDrawable {
        private final Paint mFillPaint;
        private final Rect mPadding;
        private final Paint mStrokePaint;
        private final int mBorderWidth;

        public BorderedRoundRectDrawable(float[] borders, int fillColor, int borderColor, int borderWidth, Rect padding) {
            super(new RoundRectShape(borders, null, null));
            mFillPaint = new Paint(this.getPaint());
            mFillPaint.setColor(fillColor);
            mFillPaint.setStyle(Paint.Style.FILL);

            mStrokePaint = new Paint(mFillPaint);
            mStrokePaint.setStyle(Paint.Style.STROKE);
            mStrokePaint.setStrokeWidth(borderWidth);
            mStrokePaint.setColor(borderColor);

            mPadding = padding;
            mBorderWidth = borderWidth;
        }

        @Override
        protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
            shape.resize(canvas.getClipBounds().right, canvas.getClipBounds().bottom);

            Matrix matrix = new Matrix();
            if (mPadding != null) {
                matrix.setRectToRect(
                        new RectF(0, 0, canvas.getClipBounds().right, canvas.getClipBounds().bottom),
                        new RectF(mPadding.left, mPadding.top, canvas.getClipBounds().right - mPadding.right, canvas.getClipBounds().bottom - mPadding.bottom),
                        Matrix.ScaleToFit.FILL);
                canvas.concat(matrix);
            }

            if (mBorderWidth > 0) {
                matrix.setRectToRect(
                        new RectF(0, 0, canvas.getClipBounds().right, canvas.getClipBounds().bottom),
                        new RectF(mBorderWidth-1, mBorderWidth-1, canvas.getClipBounds().right - mBorderWidth+1, canvas.getClipBounds().bottom - mBorderWidth+1),
                        Matrix.ScaleToFit.FILL);
                canvas.concat(matrix);
            }

            shape.draw(canvas, mFillPaint);

            matrix.setRectToRect(
                    new RectF(0, 0, canvas.getClipBounds().right, canvas.getClipBounds().bottom),
                    new RectF(
                            mBorderWidth/2 - mBorderWidth + 1,
                            mBorderWidth/2 - mBorderWidth + 1,
                            canvas.getClipBounds().right - mBorderWidth/2 + mBorderWidth - 1,
                            canvas.getClipBounds().bottom - mBorderWidth/2 + mBorderWidth - 1),
                    Matrix.ScaleToFit.FILL);
            canvas.concat(matrix);

            shape.draw(canvas, mStrokePaint);
        }
    }

    private void addBorderDrawableTopBottom(float[] bordersArray, StateListDrawable borderDrawable) {
        BorderedRoundRectDrawable borderPressed = new BorderedRoundRectDrawable(bordersArray, mBackgroundColorPressed, mBorderColorPressed, mBorderWidth, new Rect(-mBorderWidth, 0, -mBorderWidth, 0));
        borderDrawable.addState(new int[] {android.R.attr.state_pressed}, borderPressed);
        BorderedRoundRectDrawable borderFocused = new BorderedRoundRectDrawable(bordersArray, mBackgroundColor, mBorderColor, mBorderWidth, new Rect(-mBorderWidth, 0, -mBorderWidth, 0));
        borderDrawable.addState(new int[] {android.R.attr.state_focused}, borderFocused);
        BorderedRoundRectDrawable border = new BorderedRoundRectDrawable(bordersArray, mBackgroundColor, mBorderColor, mBorderWidth, new Rect(-mBorderWidth, 0, -mBorderWidth, 0));
        borderDrawable.addState(new int[] {}, border);
    }

    private void addBorderDrawableLeftRight(float[] bordersArray, StateListDrawable borderDrawable) {
        BorderedRoundRectDrawable borderPressed = new BorderedRoundRectDrawable(bordersArray, mBackgroundColorPressed, mBorderColorPressed, mBorderWidth, new Rect(0, -mBorderWidth, 0, -mBorderWidth));
        borderDrawable.addState(new int[]{android.R.attr.state_pressed}, borderPressed);
        BorderedRoundRectDrawable borderFocused = new BorderedRoundRectDrawable(bordersArray, mBackgroundColor, mBorderColor, mBorderWidth, new Rect(0, -mBorderWidth, 0, -mBorderWidth));
        borderDrawable.addState(new int[] {android.R.attr.state_focused}, borderFocused);
        BorderedRoundRectDrawable border = new BorderedRoundRectDrawable(bordersArray, mBackgroundColor, mBorderColor, mBorderWidth, new Rect(0, -mBorderWidth, 0, -mBorderWidth));
        borderDrawable.addState(new int[] {}, border);
    }
}
