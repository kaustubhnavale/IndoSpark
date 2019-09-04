package mipl.indospark;

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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FragAddAddress extends Fragment {

    TextView tvAddAddress;
    Spinner spinState;
    EditText etContact, etEmail, etFNM, etLNM, etCompNM, etStreetAdd, etCity, etPinCode, etCountry;
    Boolean validation = true;

    ProgressDialog myDialog1;
    StringRequest stringRequest;

    SharedPreferences sharedpreferences;
    String custID, AddID;
    String Amount;

    String[] states = {"Please select a region, state or province",
            "Andaman Nicobar", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chandigarh", "Chhattisgarh", "Dadra Nagar Haveli", "Daman Diu", "Delhi", "Goa",
            "Gujarat", "Haryana", "Himachal Pradesh", "Jammu Kashmir", "Jharkhand", "Karnataka", "Kerala", "Lakshadweep", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya",
            "Mizoram", "Nagaland", "Odisha", "Pondicherry", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttaranchal", "West Bengal"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_frag_add_address, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        custID = sharedpreferences.getString(commonVariables.customer_id, "");

        etContact = (EditText) v.findViewById(R.id.etContact);
        etEmail = (EditText) v.findViewById(R.id.etEmail);
        etFNM = (EditText) v.findViewById(R.id.etFNM);
        etLNM = (EditText) v.findViewById(R.id.etLNM);
        etCompNM = (EditText) v.findViewById(R.id.etCompNM);
        etStreetAdd = (EditText) v.findViewById(R.id.etStreetAdd);
        etCity = (EditText) v.findViewById(R.id.etCity);
        etPinCode = (EditText) v.findViewById(R.id.etPinCode);
        etCountry = (EditText) v.findViewById(R.id.etCountry);
        tvAddAddress = (TextView) v.findViewById(R.id.tvAddAddress);
        spinState = (Spinner) v.findViewById(R.id.spinState);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, states);
        spinState.setAdapter(adapter);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            // handle your code here.
            AddID = getArguments().getString("AddID");

            if (AddID != null) {
                if (AddID.equals("New")) {
                    Amount = getArguments().getString("Amount");
                } else {
                    tvAddAddress.setText("Update");
                    etContact.setText(getArguments().getString("Telephone"));
                    etEmail.setText(getArguments().getString("Email"));
                    etFNM.setText(getArguments().getString("Firstname"));
                    etLNM.setText(getArguments().getString("Lastname"));
                    etCompNM.setText(getArguments().getString("Company"));
                    etStreetAdd.setText(getArguments().getString("Street"));
                    etCity.setText(getArguments().getString("City"));
                    etPinCode.setText(getArguments().getString("Postcode"));
                    etCountry.setText(getArguments().getString("Region"));
                }
            }
        }

        tvAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    if (CheckNetwork.isInternetAvailable(getActivity())) {

                        if (AddID == null) {
                            addAddress();
                        } else {
                            if (AddID.equals("New")) {
                                addAddress();
                            } else {
                                editAddress();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Not connected to Internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    validation = true;
                }
            }
        });

        return v;
    }

    public void addAddress() {
        myDialog1 = commonVariables.showProgressDialog(getActivity(), "Adding Addresses ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/add_new_address.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("response", response);

                        JSONObject reader = null;
                        try {
                            reader = new JSONObject(response);

                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                            } else if (status.equals("200")) {
                                if (AddID.equals("New")) {

                                    Bundle bundle = new Bundle();
                                    bundle.putString("Amount", Amount);
                                    //set Fragmentclass Arguments
                                    FragSetDeliveryAddress fragobj = new FragSetDeliveryAddress();
                                    fragobj.setArguments(bundle);

                                    android.support.v4.app.FragmentManager fm = ((FragmentActivity) getActivity()).getSupportFragmentManager();
                                    android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                                    if (fm.findFragmentById(R.id.fragDrower) != null) {
                                        ft.hide(fm.findFragmentById(R.id.fragDrower));
                                    }
                                    ft.add(R.id.fragDrower, fragobj, FragCart.class.getCanonicalName())
//                                            .addToBackStack(FragCart.class.getCanonicalName())
                                            .commit();


                                } else {
                                    android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                                    android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                                    if (fm.findFragmentById(R.id.fragDrower) != null) {
                                        ft.hide(fm.findFragmentById(R.id.fragDrower));
                                    }
                                    ft.add(R.id.fragDrower, new FragAddList(), FragAddList.class.getCanonicalName())
                                            .addToBackStack(FragAddList.class.getCanonicalName()).commit();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        myDialog1.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                myDialog1.dismiss();
            }
        }) {

            /*@Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }*/

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", custID);
                params.put("firstname", etFNM.getText().toString());
                params.put("lastname", etLNM.getText().toString());
                params.put("city", etCity.getText().toString());
                params.put("street", etStreetAdd.getText().toString());
//                params.put("region", etState.getText().toString());
                params.put("region", spinState.getSelectedItem().toString());
                params.put("zipcode", etPinCode.getText().toString());
                params.put("telephone", etContact.getText().toString());
                params.put("company", etCompNM.getText().toString());
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

    public void editAddress() {
        myDialog1 = commonVariables.showProgressDialog(getActivity(), "Updating Addresses ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/edit_address.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("response", response);

                        JSONObject reader = null;
                        try {
                            reader = new JSONObject(response);

                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                            } else if (status.equals("200")) {
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragDrower, new FragAddList(), "SOMETAG").addToBackStack("Indo").commit();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        myDialog1.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                myDialog1.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("address_id", AddID);
                params.put("firstname", etFNM.getText().toString());
                params.put("lastname", etLNM.getText().toString());
                params.put("city", etCity.getText().toString());
                params.put("region", etCountry.getText().toString());
                params.put("zipcode", etPinCode.getText().toString());
                params.put("telephone", etContact.getText().toString());
                params.put("street", etStreetAdd.getText().toString());
                params.put("company", etCompNM.getText().toString());
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

    public boolean validate() {

        if (etContact.getText().toString().length() != 10) {
            etContact.setError("Add correct contact number");
            etContact.requestFocus();
            validation = false;
        }
        if (etEmail.getText().toString().equals("")) {
            etEmail.setError("Add E-mail");
            etEmail.requestFocus();
            validation = false;
        }
        if (etFNM.getText().toString().equals("")) {
            etFNM.setError("Add your first name");
            etFNM.requestFocus();
            validation = false;
        }
        if (etLNM.getText().toString().equals("")) {
            etLNM.setError("Add your last name");
            etLNM.requestFocus();
            validation = false;
        }
        if (etCompNM.getText().toString().equals("")) {
            etCompNM.setError("Add company name");
            etCompNM.requestFocus();
            validation = false;
        }
        if (etStreetAdd.getText().toString().equals("")) {
            etStreetAdd.setError("Add address");
            etStreetAdd.requestFocus();
            validation = false;
        }
        if (etCity.getText().toString().equals("")) {
            etCity.setError("Add your city");
            etCity.requestFocus();
            validation = false;
        }
        /*if (etState.getText().toString().equals("")) {
            etState.setError("Add state");
            etState.requestFocus();
            validation = false;
        }*/
        if (etPinCode.getText().toString().length() != 6) {
            etPinCode.setError("Add pincode");
            etPinCode.requestFocus();
            validation = false;
        }
        if (etCountry.getText().toString().equals("")) {
            etCountry.setError("Add country");
            etCountry.requestFocus();
            validation = false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            etEmail.setError("Enter valid mail ID");
            etEmail.requestFocus();
            validation = false;
        }
        if (spinState.getSelectedItemPosition() == 0){
            ((TextView)spinState.getSelectedView()).setError("Select State");
            validation = false;
        }

        return validation;
    }
}