package com.infomaniak.lib.pdfview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.infomaniak.lib.pdfview.util.Util;

final class TextSelectionHandle {
    private final boolean start;
    private final Drawable left;
    private final Drawable right;
    private final int touchTargetSize;
    private final RectF touchBounds = new RectF();
    private final PointF textPosition = new PointF();

    TextSelectionHandle(Context context, boolean start, int color) {
        this.start = start;
        TypedArray attributes = context.obtainStyledAttributes(new int[]{
                android.R.attr.textSelectHandleLeft, android.R.attr.textSelectHandleRight
        });
        left = attributes.getDrawable(0);
        right = attributes.getDrawable(1);
        attributes.recycle();
        if (left != null) left.mutate();
        if (right != null) right.mutate();
        touchTargetSize = Util.getDP(context, 48);
        setColor(color);
    }

    void setColor(int color) {
        if (left != null) left.setTint(color);
        if (right != null) right.setTint(color);
    }

    void draw(Canvas canvas, RectF character, boolean rtl, float offsetX, float offsetY) {
        boolean useRight = rtl == start;
        Drawable drawable = useRight ? right : left;
        if (drawable == null) return;
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        float anchorX = useRight ? character.right : character.left;
        float hotspotX = useRight ? width / 4f : width * 3f / 4f;
        int x = Math.round(anchorX - hotspotX);
        int y = Math.round(character.bottom);
        drawable.setBounds(x, y, x + width, y + height);
        drawable.draw(canvas);
        touchBounds.set(x + offsetX, y + offsetY, x + width + offsetX, y + height + offsetY);
        touchBounds.inset(-Math.max(0, touchTargetSize - width) / 2f,
                -Math.max(0, touchTargetSize - height) / 2f);
        textPosition.set(character.centerX() + offsetX, character.centerY() + offsetY);
    }

    void hide() {
        touchBounds.setEmpty();
    }

    boolean contains(float x, float y) {
        return touchBounds.contains(x, y);
    }

    float distanceSquared(float x, float y) {
        float dx = x - touchBounds.centerX();
        float dy = y - touchBounds.centerY();
        return dx * dx + dy * dy;
    }

    PointF textPosition() {
        return new PointF(textPosition.x, textPosition.y);
    }
}
