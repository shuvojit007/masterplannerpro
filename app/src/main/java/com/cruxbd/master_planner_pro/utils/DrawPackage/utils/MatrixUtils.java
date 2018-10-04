package com.cruxbd.master_planner_pro.utils.DrawPackage.utils;

import android.graphics.Matrix;
import android.graphics.RectF;

public class MatrixUtils {
    public static Matrix GetCenterCropMatrix(RectF srcSize, RectF destSize){
        Matrix matrix = new Matrix();
        float scale = Math.max(destSize.width() / srcSize.width(),
                destSize.height() / srcSize.height());
        matrix.setTranslate(-(destSize.width() / 2), - (destSize.height() / 2));
        matrix.setScale(scale, scale);

        return matrix;
    }
}
