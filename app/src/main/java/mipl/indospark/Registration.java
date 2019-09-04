package mipl.indospark;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    EditText etRegUserNme, etRegPassowrd, etRegConfirmPassowrd, etRegFirstNm, etRegLastNm;
    TextView tvRegSubmit;

    StringRequest stringRequest;
    Boolean status = true;
    ProgressDialog myDialog;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(this, Login.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().hide();

        etRegUserNme = (EditText) findViewById(R.id.etRegUserNme);
        etRegPassowrd = (EditText) findViewById(R.id.etRegPassowrd);
        etRegConfirmPassowrd = (EditText) findViewById(R.id.etRegConfirmPassowrd);
        etRegFirstNm = (EditText) findViewById(R.id.etRegFirstNm);
        etRegLastNm = (EditText) findViewById(R.id.etRegLastNm);
        tvRegSubmit = (TextView) findViewById(R.id.tvRegSubmit);

        tvRegSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    if (CheckNetwork.isInternetAvailable(Registration.this)) {
                        sendRegistration();
                    } else {
                        Toast.makeText(Registration.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    status = true;
                }
            }
        });
    }

    public void sendRegistration() {

        myDialog= commonVariables.showProgressDialog(this,"Registering ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/customer_registration.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject reader = null;
                        try {
                            reader = new JSONObject(response);

                            String status = reader.getString("status");
                            if (status.equals("200")) {

                                JSONObject result = reader.getJSONObject("result");

                                try {
                                    String msg = result.getString("message");

                                    new AlertDialog.Builder(Registration.this).setIcon(android.R.drawable.ic_dialog_alert)
                                            .setMessage(msg)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    dialog.dismiss();
                                                }
                                            }).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    String msg = result.getString("confirmation");
                                    if (!msg.equals("")) {

                                        new AlertDialog.Builder(Registration.this).setIcon(android.R.drawable.ic_dialog_alert)
                                                .setMessage("Activation link sent on mail. Activate first")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        startActivity(new Intent(Registration.this, Login.class));
                                                        dialog.dismiss();
                                                        finish();
                                                    }
                                                }).show();

                                    } else {
                                        Toast.makeText(Registration.this, "Error", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else if (status.equals("500")) {
                                Toast.makeText(Registration.this, "Error", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        myDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Registration.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                myDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email_id", etRegUserNme.getText().toString());
                params.put("password", etRegPassowrd.getText().toString());
                params.put("firstname", etRegFirstNm.getText().toString());
                params.put("lastname", etRegLastNm.getText().toString());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(Registration.this);
        requestQueue.add(stringRequest);
    }

    public boolean validation() {

        if (etRegUserNme.getText().toString().equals("")) {
            etRegUserNme.setError("Enter mail ID");
            etRegUserNme.requestFocus();
            status = false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etRegUserNme.getText().toString()).matches()) {
            etRegUserNme.setError("Enter valid mail ID");
            etRegUserNme.requestFocus();
            status = false;
        }
        if (etRegPassowrd.getText().toString().equals("")) {
            etRegPassowrd.setError("Enter password");
            etRegPassowrd.requestFocus();
            status = false;
        }
        if (etRegConfirmPassowrd.getText().toString().equals("")) {
            etRegConfirmPassowrd.setError("Enter password");
            etRegConfirmPassowrd.requestFocus();
            status = false;
        }
        if (etRegFirstNm.getText().toString().equals("")) {
            etRegFirstNm.setError("Enter First Name");
            etRegFirstNm.requestFocus();
            status = false;
        }
        if (etRegLastNm.getText().toString().equals("")) {
            etRegLastNm.setError("Enter Last Name");
            etRegLastNm.requestFocus();
            status = false;
        }
        if (!etRegPassowrd.getText().toString().equals(etRegConfirmPassowrd.getText().toString())) {
            etRegConfirmPassowrd.setError("Password not match");
            etRegConfirmPassowrd.requestFocus();
            status = false;
            etRegConfirmPassowrd.setText("");
        }
        /*if (!(etRegPassowrd.getText().toString().length() == 6) && !isValidPassword(etRegPassowrd.getText().toString())) {
            Toast.makeText(Registration.this, "Enter at least 1 number, 1 lower case character, 1 upper case character, and 1 special symbol", Toast.LENGTH_SHORT).show();
            etRegPassowrd.requestFocus();
            status = false;
            etRegPassowrd.setText("");
            etRegConfirmPassowrd.setText("");
        }*/
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