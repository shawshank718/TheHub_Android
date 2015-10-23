package com.group6.thehub.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateTimePickerDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateTimePickerDialogFragment extends DialogFragment {

    // the fragment initialization parameters, DIALOG_ID
    private static final String DIALOG_ID = "dialog_id";
    private static final int DATEPICKER_DIALOG_ID = 0;
    private static final int TIMEPICKER_DIALOG_ID = 1;

    private int dialog_id;

    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param dialog_id "0" for datePicker, "1" for TimePicker.
     * @return A new instance of fragment DateTimePickerDialogFragment.
     */
    public static DateTimePickerDialogFragment newInstance(int dialog_id) {
        DateTimePickerDialogFragment fragment = new DateTimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ID, dialog_id);
        fragment.setArguments(args);
        return fragment;
    }

    public DateTimePickerDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dialog_id = getArguments().getInt(DIALOG_ID);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (dialog_id == DATEPICKER_DIALOG_ID) {
            return createDatePickerDialog();
        } else if (dialog_id == TIMEPICKER_DIALOG_ID) {
            return createTimePickerDialog();
        }

        return super.onCreateDialog(savedInstanceState);
    }

    private Dialog createDatePickerDialog() {
        if (getActivity() instanceof DatePickerDialog.OnDateSetListener) {
            onDateSetListener = (DatePickerDialog.OnDateSetListener) getActivity();

            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog d =  new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
            d.getDatePicker().setMinDate(calendar.getTimeInMillis());
            return d;
        } else {
            throw new ClassCastException(getActivity().toString()+ " must implement DatePickerDialog.OnDateSetListener");
        }

    }

    private Dialog createTimePickerDialog() {
        if (getActivity() instanceof TimePickerDialog.OnTimeSetListener) {
            onTimeSetListener = (TimePickerDialog.OnTimeSetListener) getActivity();

            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            TimePickerDialog t = new TimePickerDialog(getActivity(), onTimeSetListener, hour, min, false);
            return t;
        } else {
            throw new ClassCastException(getActivity().toString()+ " must implement TimePickerDialog.OnTimeSetListener");
        }
    }
}
