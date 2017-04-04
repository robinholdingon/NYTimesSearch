package fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DatePickerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DatePickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatePickerFragment extends DialogFragment {
    public static String YEAR = "year";
    public static String MONTH = "month";
    public static String DAY = "day";

    private DatePickerDialog.OnDateSetListener listener;

    public static DatePickerFragment newInstance(int year, int month, int day) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle args = new Bundle();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        args.putInt(YEAR, year);
        args.putInt(MONTH, month);
        args.putInt(DAY, day);

        datePickerFragment.setArguments(args);
        return datePickerFragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        int year = args.getInt(YEAR);
        int month = args.getInt(MONTH) - 1;
        int day = args.getInt(DAY);

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }

    public void setCallBack(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }
}
