package com.example.tg_patient_profile.view.patient.patientdata.medicaldiagnostics;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tg_patient_profile.R;
import com.example.tg_patient_profile.model.Medical_diagnostic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PastMedicalDiagnosticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastMedicalDiagnosticsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int[] editTextIds = {
            R.id.pastMedicalDiagnosticsNameTextView,
            R.id.pastMedicalDiagnosticsBloodPressureTextView,
            R.id.pastMedicalDiagnosticsPatientTemperatureTextView,
            R.id.pastMedicalDiagnosticsGlucoseLevelTextView,
            R.id.pastMedicalDiagnosticsO2SaturationTextView,
            R.id.pastMedicalDiagnosticsPulseRateTextView,
            R.id.pastMedicalDiagnosticsRespirationRateTextView,
            R.id.pastMedicalDiagnosticsBloodfatLevelTextView
    };

    private final int[] editButtonIds = {
            R.id.past_name_pencil,
            R.id.past_blood_pressure_pencil,
            R.id.past_temperature_pencil,
            R.id.past_glucose_level_pencil,
            R.id.past_blood_o2_saturation_pencil,
            R.id.past_pulse_rate_pencil,
            R.id.past_respiration_rate_pencil,
            R.id.past_bloodfat_level_pencil
    };
    Medical_diagnostic medical_diagnostic_past;
    private EditText[] editTextArray;
    private Button[] editButtonArray;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String patient_id;


    public PastMedicalDiagnosticsFragment() {
        // Required empty public constructor
    }

    public PastMedicalDiagnosticsFragment(final String patient_id) {
        this.patient_id = patient_id;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PastMedicalDiagnosticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PastMedicalDiagnosticsFragment newInstance(final String param1, final String param2) {
        final PastMedicalDiagnosticsFragment fragment = new PastMedicalDiagnosticsFragment();
        final Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_past_medical_diagnostics, container, false);
        editTextArray = new EditText[editTextIds.length];
        editButtonArray = new Button[editButtonIds.length];

        for (int i = 0; i < editTextIds.length; i++) {
            editTextArray[i] = rootView.findViewById(editTextIds[i]);
            editButtonArray[i] = rootView.findViewById(editButtonIds[i]);

            final EditText editText = editTextArray[i];
            final Button editButton = editButtonArray[i];

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    editText.setEnabled(true);
                    editText.requestFocus();
                    editText.selectAll();
                    final InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);


                }
            });
        }

        return rootView;
    }

    private void setInfo() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("health_details");
        final Query query = reference
                .orderByChild("patient_id")
                .equalTo(patient_id);
        //here to be added for grabbing info from firebase
    }

    public void setEditState(final boolean isEditable) {
        if (isEditable) {
            for (final Button editButton : editButtonArray) {
                editButton.setVisibility(View.VISIBLE);
            }
        } else {
            for (final Button editButton : editButtonArray) {
                editButton.setVisibility(View.INVISIBLE);
            }
            for (final EditText editText : editTextArray) {
                editText.setFocusable(false);
                editText.setFocusableInTouchMode(false);
                editText.setEnabled(false);
            }
            saveInFirebase();
        }
    }

    private void saveInFirebase() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("health_details");
        final Query query = reference
                .orderByChild("patient_id")
                .equalTo(patient_id);
        if (dataChecker()) {
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot snapshot) {
                    for (final DataSnapshot childSnapshot : snapshot.getChildren()) {
                        final Boolean isCurrent = childSnapshot.child("current").getValue(Boolean.class);
                        if (!isCurrent) {
                            childSnapshot.getRef().setValue(medical_diagnostic_past);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull final DatabaseError error) {

                }
            });
        }
    }

    private Boolean dataChecker() {
        for (int i = 0; i < editTextArray.length; i++) {
            if (TextUtils.isEmpty(editTextArray[i].getText())) {
                editTextArray[i].setError("it shouldn't be empty!");
                return false;
            }
        }
        medical_diagnostic_past = new Medical_diagnostic(patient_id,
                editTextArray[0].getText().toString(),
                editTextArray[1].getText().toString(),
                editTextArray[2].getText().toString(),
                editTextArray[3].getText().toString(),
                editTextArray[4].getText().toString(),
                editTextArray[5].getText().toString(),
                editTextArray[6].getText().toString(),
                editTextArray[7].getText().toString(),
                false
        );
        return true;
    }


}