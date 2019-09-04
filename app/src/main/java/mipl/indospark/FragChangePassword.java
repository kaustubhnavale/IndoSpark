package mipl.indospark;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FragChangePassword extends Fragment {

    TextView tvCurrentPasswor, tvNewPassword, tvConfirmPassword, tvUpdatePassword;
    Boolean status = true;
    ProgressDialog myDialog;
    StringRequest stringRequest;
    SharedPreferences sharedpreferences;
    String token, customer_id;
    JSONArray reader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_change_password, container, false);

        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(commonVariables.token, "");
        customer_id = sharedpreferences.getString(commonVariables.customer_id, "");

        tvCurrentPasswor = (TextView) v.findViewById(R.id.tvCurrentPasswor);
        tvNewPassword = (TextView) v.findViewById(R.id.tvNewPassword);
        tvConfirmPassword = (TextView) v.findViewById(R.id.tvConfirmPassword);
        tvUpdatePassword = (TextView) v.findViewById(R.id.tvUpdatePassword);

        tvUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validations()) {
                    if (CheckNetwork.isInternetAvailable(getActivity())) {
                        updatePassword();
                    } else {
                        Toast.makeText(getActivity(), "Not connected to internet", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    status = true;
                }
            }
        });

        return v;
    }

    public void updatePassword() {

        myDialog= commonVariables.showProgressDialog(getActivity(),"Updating ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/change_password.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            if (response.contains("The password doesn't match this account")) {
                                Toast.makeText(getActivity(), "The password doesn't match this account", Toast.LENGTH_SHORT).show();
                            } else if (response.equals("true1")) {
                                Toast.makeText(getActivity(), "Password successfully updated", Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().popBackStack();
                            } else {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
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
                params.put("currentPassword", tvCurrentPasswor.getText().toString());
                params.put("newPassword", tvNewPassword.getText().toString());
                params.put("customerid", customer_id);
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

    public boolean validations() {

        if (tvCurrentPasswor.getText().toString().equals("")) {
            tvCurrentPasswor.setError("Enter Last Name");
            tvCurrentPasswor.requestFocus();
            status = false;
        }
        if (!tvNewPassword.getText().toString().equals(tvConfirmPassword.getText().toString())) {
            tvConfirmPassword.setError("Password not match");
            tvConfirmPassword.requestFocus();
            status = false;
            tvConfirmPassword.setText("");
        }
        if (!(tvNewPassword.getText().toString().length() == 6) && !isValidPassword(tvNewPassword.getText().toString())) {
            Toast.makeText(getActivity(), "Enter at least 1 number, 1 lower case character, 1 upper case character, and 1 special symbol", Toast.LENGTH_SHORT).show();
            tvNewPassword.requestFocus();
            status = false;
            tvNewPassword.setText("");
            tvConfirmPassword.setText("");
        }
        return status;
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }
}