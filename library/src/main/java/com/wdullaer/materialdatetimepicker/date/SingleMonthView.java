/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.wdullaer.materialdatetimepicker.date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.wdullaer.materialdatetimepicker.R;

public class SingleMonthView extends SimpleMonthView {

    protected static final int DEFAULT_NUM_ROWS = 5;
    protected static final int MAX_NUM_ROWS = 5;

    public SingleMonthView(Context context, AttributeSet attr, DatePickerController controller) {
        super(context, attr, controller);

        Resources res = context.getResources();

        MONTH_HEADER_SIZE = 0;

        mRowHeight = (res.getDimensionPixelOffset(R.dimen.mdtp_day_picker_view_animator_height)
                - getMonthHeaderSize()) / MAX_NUM_ROWS;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawMonthNums(canvas);
    }

    @Override
    public void reuse() {
        mNumRows = DEFAULT_NUM_ROWS;
        requestLayout();
    }
}
