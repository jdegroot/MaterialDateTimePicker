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

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wdullaer.materialdatetimepicker.R;
import com.wdullaer.materialdatetimepicker.Utils;

import java.util.Calendar;
import java.util.Locale;

/**
 * Dialog allowing users to select a date.
 */
public class DayPickerDialog extends DatePickerDialog {

    private static final String TAG = "DatePickerDialog";

    // December 2014 (Or any month with 31 days with a Sunday on the first day)
    private static final int FIXED_YEAR = 2013;
    private static final int FIXED_MONTH = 11;

    private OnDaySetListener mCallBack;
    private OnDayTappedListener mTappedCallback;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDaySetListener {

        /**
         * @param view       The view associated with this listener.
         * @param dayOfMonth The day of the month that was set.
         */
        void onDaySet(DayPickerDialog view, int dayOfMonth);
    }

    /**
     * The callback used to indicate the user has tapped a day and
     * get a string to display on the header label text.
     */
    public interface OnDayTappedListener {

        /**
         * @param view       The view associated with this listener.
         * @param dayOfMonth The day of the month that was tapped.
         */
        String onDayTapped(DayPickerDialog view, int dayOfMonth);
    }

    public DayPickerDialog() {
        mCalendar = Calendar.getInstance(Locale.US);
        mWeekStart = mCalendar.getFirstDayOfWeek();
    }

    /**
     * @param callBack   How the parent is notified that the date is set.
     * @param dayOfMonth The initial day of the dialog.
     */
    public static DayPickerDialog newInstance(OnDaySetListener callBack,
                                              OnDayTappedListener tapCallBack,
                                              int dayOfMonth) {
        DayPickerDialog ret = new DayPickerDialog();
        ret.initialize(callBack, tapCallBack, dayOfMonth);
        return ret;
    }

    public void initialize(OnDaySetListener callBack, OnDayTappedListener tapCallBack, int dayOfMonth) {
        mCallBack = callBack;
        mTappedCallback = tapCallBack;

        // Preset with a month with 31 days and sunday as first day of the week
        mCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        setFirstDayOfWeek(Calendar.SUNDAY);
        mCalendar.set(Calendar.YEAR, FIXED_YEAR);
        mCalendar.set(Calendar.MONTH, FIXED_MONTH);

        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        // Override ranges
        super.setYearRange(FIXED_YEAR, FIXED_YEAR);

        Calendar minDate = Calendar.getInstance(Locale.US);
        minDate.set(FIXED_YEAR, FIXED_MONTH, 1);
        super.setMinDate(minDate);

        Calendar maxDate = Calendar.getInstance(Locale.US);
        maxDate.set(FIXED_YEAR, FIXED_MONTH, 31);
        super.setMaxDate(maxDate);

    }

    /*
        Override visuals
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Update visuals
        if (mTitle == null || mTitle.isEmpty()) {
            if (mDayOfWeekView != null) mDayOfWeekView.setVisibility(View.GONE);
        } else {
            if (mDayOfWeekView != null) mDayOfWeekView.setVisibility(View.VISIBLE);
        }
        mSelectedMonthTextView.setVisibility(View.GONE);
        mYearView.setVisibility(View.GONE);

        mMonthAndDayView.setOnClickListener(null);
        mYearView.setOnClickListener(null);

        // Update picker height
        ViewGroup.LayoutParams pickerLayoutParams = mAnimator.getLayoutParams();
        pickerLayoutParams.height = (int) getResources().getDimension(R.dimen.mdtp_day_picker_view_animator_height);
        mAnimator.setLayoutParams(pickerLayoutParams);

        // Update header heights
        View selectedDayView = view.findViewById(R.id.day_picker_selected_date_layout);
        ViewGroup.LayoutParams selectedDayLayoutParams = selectedDayView.getLayoutParams();
        selectedDayLayoutParams.height = (int) getResources().getDimension(R.dimen.mdtp_selected_day_height);
        selectedDayView.setLayoutParams(selectedDayLayoutParams);


        return view;
    }

    @Override
    protected DayPickerView getDayPickerView(Activity activity) {
        return new SingleDayPickerView(activity, this);
    }

    @Override
    protected void updateDisplay(boolean announce) {
        if (mDayOfWeekView != null) {
            if(mTitle != null) mDayOfWeekView.setText(mTitle.toUpperCase(Locale.getDefault()));
            else {
                mDayOfWeekView.setText(mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,
                        Locale.getDefault()).toUpperCase(Locale.getDefault()));
            }
        }

        mSelectedMonthTextView.setText(mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
                Locale.getDefault()).toUpperCase(Locale.getDefault()));
        notifyOnDayTappedListener();
        mYearView.setText(YEAR_FORMAT.format(mCalendar.getTime()));

        // Accessibility.
        long millis = mCalendar.getTimeInMillis();
        mAnimator.setDateMillis(millis);
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR;
        String monthAndDayText = DateUtils.formatDateTime(getActivity(), millis, flags);
        mMonthAndDayView.setContentDescription(monthAndDayText);

        if (announce) {
            flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR;
            String fullDateText = DateUtils.formatDateTime(getActivity(), millis, flags);
            Utils.tryAccessibilityAnnounce(mAnimator, fullDateText);
        }
    }


    /*
     * Callback
     */

    @Override
    public void notifyOnDateListener() {
        if (mCallBack != null) {
            mCallBack.onDaySet(DayPickerDialog.this, mCalendar.get(Calendar.DAY_OF_MONTH));
        }
    }

    public void notifyOnDayTappedListener() {
        if (mTappedCallback != null) {
            String label = mTappedCallback.onDayTapped(DayPickerDialog.this, mCalendar.get(Calendar.DAY_OF_MONTH));
            mSelectedDayTextView.setText(label);
        }
        else {
            mSelectedDayTextView.setText(DAY_FORMAT.format(mCalendar.getTime()));
        }
    }

    /*
     * Override unsupported public methods
     */
    @SuppressWarnings("unused")
    @Override
    public void showYearPickerFirst(boolean yearPicker) {
        Log.e(TAG, "Feature not supported");
    }

    @SuppressWarnings("unused")
    @Override
    public void setFirstDayOfWeek(int startOfWeek) {
        Log.e(TAG, "Feature not supported");
    }

    @SuppressWarnings("unused")
    @Override
    public void setYearRange(int startYear, int endYear) {
        Log.e(TAG, "Feature not supported");
    }

    @SuppressWarnings("unused")
    @Override
    public void setMinDate(Calendar calendar) {
        Log.e(TAG, "Feature not supported");
    }

    @SuppressWarnings("unused")
    @Override
    public void setMaxDate(Calendar calendar) {
        Log.e(TAG, "Feature not supported");
    }

    @SuppressWarnings("unused")
    @Override
    public void setSelectableDays(Calendar[] selectableDays) {
        Log.e(TAG, "Feature not supported");
    }

    @SuppressWarnings("unused")
    @Override
    public void setHighlightedDays(Calendar[] highlightedDays) {
        Log.e(TAG, "Feature not supported");
    }
}
