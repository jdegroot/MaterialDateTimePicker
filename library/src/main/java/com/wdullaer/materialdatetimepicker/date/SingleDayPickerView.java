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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;

/**
 * A DayPickerView customized for {@link SingleMonthAdapter}
 */
public class SingleDayPickerView extends DayPickerView {

    private static final String TAG = "MonthFragment";

    public SingleDayPickerView(Context context, AttributeSet attrs) { super(context, attrs); }

    public SingleDayPickerView(Context context, DatePickerController controller) {
        super(context, controller);
    }

    @Override
    public MonthAdapter createMonthAdapter(Context context, DatePickerController controller) {
        return new SingleMonthAdapter(context, controller);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    // Disable scrolling
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if(ev.getAction()==MotionEvent.ACTION_MOVE)
            return true;
        return super.dispatchTouchEvent(ev);
    }
}
