package mipl.indospark;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FragMyAccount extends Fragment {

    TextView tvProfileMail, tvChangePassword, tvUpdateProfile;
    EditText etFirstName, etLastName, etBirthdate, etPhone;
    RadioGroup rgGender;

    Calendar myCalendar;
    SharedPreferences sharedpreferences;
    String mail;
    Boolean status = true;
    ProgressDialog myDialog;
    StringRequest stringRequest;
    String token, gender;
    JSONArray reader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_my_account, container, false);

        myCalendar = Calendar.getInstance();

        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        mail = sharedpreferences.getString(commonVariables.Email, "");
        token = sharedpreferences.getString(commonVariables.token, "");

        etFirstName = (EditText) v.findViewById(R.id.etFirstName);
        etLastName = (EditText) v.findViewById(R.id.etLastName);
        etBirthdate = (EditText) v.findViewById(R.id.etBirthdate);
        etPhone = (EditText) v.findViewById(R.id.etPhone);
        rgGender = (RadioGroup) v.findViewById(R.id.rgGender);

        tvUpdateProfile = (TextView) v.findViewById(R.id.tvUpdateProfile);
        tvProfileMail = (TextView) v.findViewById(R.id.tvProfileMail);
        tvProfileMail.setText(mail);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd MMM yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                etBirthdate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        tvChangePassword = (TextView) v.findViewById(R.id.tvChangePassword);

        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v4.app.FragmentManager fm = ((FragmentActivity) getActivity()).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                if (fm.findFragmentById(R.id.fragDrower) != null) {
                    ft.hide(fm.findFragmentById(R.id.fragDrower));
                }
                ft.add(R.id.fragDrower, new FragChangePassword(), FragChangePassword.class.getCanonicalName())
                        .addToBackStack(FragChangePassword.class.getCanonicalName()).commit();
            }
        });

        tvUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()){
                    // get selected radio button from radioGroup
                    int selectedId = rgGender.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    RadioButton selectedRadioButton = (RadioButton) rgGender.findViewById(selectedId);
                    gender = selectedRadioButton.getText().toString();
                    if (gender.equals("Male"))
                        gender = "1";
                    else
                        gender = "2";

                    updateProfile();

                } else {
                    status = true;
                }
            }
        });

        etBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        return v;
    }

    public void updateProfile() {

        myDialog= commonVariables.showProgressDialog(getActivity(),"Updating ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/update_profile.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            if (response.contains("message")) {
                                JSONObject jsonObject = new JSONObject(response);
                                String result = jsonObject.getString("message");

                                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Profile Update Successfully", Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().popBackStack();
                            }

                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        myDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                myDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("firstname", etFirstName.getText().toString());
                params.put("lastname", etLastName.getText().toString());
                params.put("gender", gender);
                params.put("dob", etBirthdate.getText().toString());
                params.put("email", mail);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public boolean validation() {

        if (etFirstName.getText().toString().equals("")) {
            etFirstName.setError("Enter First Name");
            etFirstName.requestFocus();
            status = false;
        }
        if (etLastName.getText().toString().equals("")) {
            etLastName.setError("Enter Last Name");
            etLastName.requestFocus();
            status = false;
        }
        if (etBirthdate.getText().toString().equals("")) {
            etBirthdate.setError("Enter Birth Date");
            etBirthdate.requestFocus();
            status = false;
        }
        if (etPhone.getText().toString().equals("")) {
            etPhone.setError("Enter Phone Number");
            etPhone.requestFocus();
            status = false;
        }
        if (etPhone.getText().toString().length() != 10) {
            etPhone.setError("Enter Valid Phone Number");
            etPhone.requestFocus();
            status = false;
        }
        if (rgGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getActivity(), "Please select Gender", Toast.LENGTH_SHORT).show();
            status = false;
        }

        return status;
    }
}