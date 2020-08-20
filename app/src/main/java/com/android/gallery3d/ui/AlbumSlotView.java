/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d.ui;

import android.graphics.Rect;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

import com.android.gallery3d.anim.Animation;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.app.Config;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.glrenderer.GLCanvas;

public class AlbumSlotView extends SlotView {

    private static final String TAG = "AlbumSlotView";

    public AlbumSlotView(AbstractGalleryActivity activity, SlotView.Spec spec) {
        super(activity, spec);
    }

    boolean mChangeSlotSize = false;
    int slotSizeType = 0;// 0:default, 4 rows in a column; 1: 6 rows in a column; 2：8 rows in a column.
    double originalPointersDistance = 0;
    double lastPointersDistance = 0;
    double currentPointersDistance = 0;

    @Override
    protected boolean onTouch(MotionEvent event) {
        Log.e(TAG, "wude onTouch, event.getAction()->" + event.getAction());
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.e(TAG, "wude onTouch, ACTION_POINTER_DOWN");
                mChangeSlotSize = true;
                lastPointersDistance = Math.sqrt(
                        (event.getX(0) - event.getX(1)) * (event.getX(0) - event.getX(1))
                        + (event.getY(0) - event.getY(1)) * (event.getY(0) - event.getY(1))
                );
                originalPointersDistance = lastPointersDistance;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "wude onTouch, ACTION_MOVE. mChangeSlotSize: " + mChangeSlotSize);
                if(mChangeSlotSize){
                    currentPointersDistance = Math.sqrt(
                            (event.getX(0) - event.getX(1)) * (event.getX(0) - event.getX(1))
                                    + (event.getY(0) - event.getY(1)) * (event.getY(0) - event.getY(1)));
                     if(currentPointersDistance - originalPointersDistance > 50){
                        if(slotSizeType == 1 || slotSizeType == 2){
                            slotSizeType--;
                            mChangeSlotSize = false;
                            changeViewByType(slotSizeType);
                        }
                     }  else if(currentPointersDistance - originalPointersDistance < -50){
                         if(slotSizeType == 0 || slotSizeType == 1){
                             slotSizeType++;
                             mChangeSlotSize = false;
                             changeViewByType(slotSizeType);
                         }
                     }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.e(TAG, "wude onTouch, ACTION_POINTER_UP");
                mChangeSlotSize = false;
                break;

        }
        super.onTouch(event);
        return true;
    }

    public void changeViewByType(int slotSizeType){
        Log.e(TAG, "wude changeViewByType, slotSizeType: " + slotSizeType);
        updateLayoutParameters(slotSizeType);
        requestLayout();
        invalidate();
    }

    private void updateLayoutParameters(int slotSizeType) {
        updateSlotSpec(slotSizeType);
    }

    private void updateSlotSpec(int slotSizeType) {
        Config.AlbumPage.get(mActivity).updateAlbumPageSpec(slotSizeType);
        mLayout.initLayoutParameters();
    }


    public void resume() {
        Config.AlbumPage.get(mActivity).updateAlbumPageSpec(0);
        mLayout.initLayoutParameters();
    }
}
