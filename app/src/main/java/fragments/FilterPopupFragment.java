package fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.feng.jian.nytimessearch.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jian_feng on 4/3/17.
 */

public class FilterPopupFragment extends DialogFragment{
    private OnFilterData filterDataListener;

    public static String DIALOG_TITLE = "Filters";
    private Date startDate = new Date();
    private ArrayList<String> newDeskValues = new ArrayList<>();

    public static String ORDER_KEY = "order";
    public static String START_DATE_KEY = "start_date";
    public static String NEW_DESK_VALUES_KEY = "new_desk_values";

    private int year;
    private int month;
    private int day;
    private String sortOrderValue;

    private TextView tvStartDate;
    private Spinner sortOrder;
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();

    private ArrayList<String> newsDesk = new ArrayList<>();

    static public FilterPopupFragment newInstance(String order, Date startDate, ArrayList<String> newDeskValues) {
        FilterPopupFragment filterPopupFragment = new FilterPopupFragment();

        Bundle args = new Bundle();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        args.putString(ORDER_KEY, order);
        args.putString(START_DATE_KEY, df.format(startDate));
        args.putStringArrayList(NEW_DESK_VALUES_KEY, newDeskValues);

        filterPopupFragment.setArguments(args);
        return filterPopupFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return setUpView(inflater, container);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            filterDataListener= (OnFilterData) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }


    private View setUpView(LayoutInflater inflater, ViewGroup container) {
        final View rootView = inflater.inflate(R.layout.fragment_filter_popup, container, false);
        getDialog().setTitle(DIALOG_TITLE);

        Bundle args = getArguments();
        Button confirmButton = (Button) rootView.findViewById(R.id.confirm_button);
        Button cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
        sortOrder = (Spinner) rootView.findViewById(R.id.sort_order);
        checkBoxes.add((CheckBox) rootView.findViewById(R.id.checkbox_sports));
        checkBoxes.add((CheckBox) rootView.findViewById(R.id.checkbox_arts));
        checkBoxes.add((CheckBox) rootView.findViewById(R.id.checkbox_fns));

        ArrayList<String> valueArray = args.getStringArrayList(NEW_DESK_VALUES_KEY);
        Set<String> values = new HashSet<String>(valueArray);
        for (CheckBox checkBox : checkBoxes) {

            if (values.contains(checkBox.getText().toString())) {
                checkBox.setChecked(true);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isChecked()) {
                        newDeskValues.add(buttonView.getText().toString());
                    } else {
                        newDeskValues.remove(buttonView.getText().toString());
                    }
                }
            });
        }

        sortOrderValue = args.getString(ORDER_KEY);
        setSpinnerToValue(sortOrder, sortOrderValue);

        tvStartDate = (TextView) rootView.findViewById(R.id.begin_date);
        tvStartDate.setPaintFlags(tvStartDate.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tvStartDate.setText(args.getString(START_DATE_KEY));

        String[] timeInfo = args.getString(START_DATE_KEY).split("-");
        this.year = Integer.parseInt(timeInfo[0]);
        this.month = Integer.parseInt(timeInfo[1]);
        this.day = Integer.parseInt(timeInfo[2]);

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterPopupFragment.this.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDataListener.parseFilterData(tvStartDate.getText().toString(), sortOrder.getSelectedItem().toString(), newDeskValues);
                FilterPopupFragment.this.dismiss();
            }
        });

        return rootView;
    }

    private void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break; // terminate loop
            }
        }
        spinner.setSelection(index);
    }

    public DatePickerDialog.OnDateSetListener onDateSetListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                updateDate(year, month, dayOfMonth);
            }
        };
    }

    private void updateDate(int year, int month, int dayOfMonth) {
        month ++;
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;
        String stringMonth = month < 10 ? "0" + Integer.toString(month) : Integer.toString(month);
        String stringDate = dayOfMonth < 10 ? "0" + Integer.toString(day) : Integer.toString(day);
        tvStartDate.setText(String.format("%d-%s-%s", year, stringMonth, stringDate));
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment dpf = DatePickerFragment.newInstance (year, month, day);
        dpf.setCallBack(onDateSetListener());
        FragmentManager fm = getFragmentManager();
        dpf.show(fm, "datePicker");
    }
}
