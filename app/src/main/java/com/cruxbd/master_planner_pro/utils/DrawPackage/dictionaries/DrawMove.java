package com.cruxbd.master_planner_pro.utils.DrawPackage.dictionaries;

import android.graphics.Matrix;

import com.cruxbd.master_planner_pro.utils.DrawPackage.enums.DrawingMode;
import com.cruxbd.master_planner_pro.utils.DrawPackage.enums.DrawingTool;
import com.cruxbd.master_planner_pro.utils.DrawPackage.utils.SerializablePaint;
import com.cruxbd.master_planner_pro.utils.DrawPackage.utils.SerializablePath;


import java.io.Serializable;


public class DrawMove implements Serializable {

    private static DrawMove mSingleton;

    private SerializablePaint mPaint;
    private DrawingMode mDrawingMode = null;
    private DrawingTool mDrawingTool = null;
    //private List<SerializablePath> mDrawingPathList;
    private SerializablePath mDrawingPath;
    private float mStartX, mStartY, mEndX, mEndY;
    private String mText;
    private Matrix mBackgroundMatrix;
    private byte[] mBackgroundImage;

    // METHODS
    private DrawMove() {
    }

    public static DrawMove newInstance() {
        mSingleton = new DrawMove();
        return mSingleton;
    }

    // GETTERS

    public SerializablePaint getPaint() {
        return mPaint;
    }

    public DrawingMode getDrawingMode() {
        return mDrawingMode;
    }

    public DrawingTool getDrawingTool() {
        return mDrawingTool;
    }

    public SerializablePath getDrawingPath() {
        return mDrawingPath;
    }

    public float getStartX() {
        return mStartX;
    }

    public float getStartY() {
        return mStartY;
    }

    public float getEndX() {
        return mEndX;
    }

    public float getEndY() {
        return mEndY;
    }

    public String getText() {
        return mText;
    }

    public Matrix getBackgroundMatrix(){
        return mBackgroundMatrix;
    }

    public byte[] getBackgroundImage() {
        return mBackgroundImage;
    }

    // SETTERS

    public DrawMove setPaint(SerializablePaint paint) {
        if (mSingleton != null) {
            mSingleton.mPaint = paint;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setDrawingMode(DrawingMode drawingMode) {
        if (mSingleton != null) {
            mSingleton.mDrawingMode = drawingMode;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setDrawingTool(DrawingTool drawingTool) {
        if (mSingleton != null) {
            mSingleton.mDrawingTool = drawingTool;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setDrawingPathList(SerializablePath drawingPath) {
        if (mSingleton != null) {
            mSingleton.mDrawingPath = drawingPath;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setStartX(float startX) {
        if (mSingleton != null) {
            mSingleton.mStartX = startX;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setStartY(float startY) {
        if (mSingleton != null) {
            mSingleton.mStartY = startY;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setEndX(float endX) {
        if (mSingleton != null) {
            mSingleton.mEndX = endX;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setEndY(float endY) {
        if (mSingleton != null) {
            mSingleton.mEndY = endY;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setText(String text) {
        if (mSingleton != null) {
            mSingleton.mText = text;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setBackgroundImage(byte[] backgroundImage, Matrix backgroundMatrix) {
        if (mSingleton != null) {
            mSingleton.mBackgroundImage = backgroundImage;
            mSingleton.mBackgroundMatrix = backgroundMatrix;
            return mSingleton;
        } else throw new RuntimeException("Create new instance of DrawMove first!");
    }
}
