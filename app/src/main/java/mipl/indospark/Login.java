package mipl.indospark;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class Login extends AppCompatActivity {

    StringRequest stringRequest;
    ProgressDialog myDialog;

    TextView tvRegister, tvForgotPassword, tvLoginSubmit, tvForgotSubmit;
    EditText etUserNme, etPassowrd, etForgotMail;
    LinearLayout llForgotPassword, llLogin;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        sharedpreferences = getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(commonVariables.token)) {
            startActivity(new Intent(this, Drower.class));
            finish();
        }

        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvRegister.setText(Html.fromHtml("<u>Register With Us</u>"));

        etForgotMail = (EditText) findViewById(R.id.etForgotMail);
        etUserNme = (EditText) findViewById(R.id.etUserNme);
        etPassowrd = (EditText) findViewById(R.id.etPassowrd);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        tvLoginSubmit = (TextView) findViewById(R.id.tvLoginSubmit);
        tvForgotSubmit = (TextView) findViewById(R.id.tvForgotSubmit);
        llForgotPassword = (LinearLayout) findViewById(R.id.llForgotPassword);
        llLogin = (LinearLayout) findViewById(R.id.llLogin);

        llForgotPassword.setVisibility(View.GONE);

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llLogin.setVisibility(View.GONE);
                llForgotPassword.setVisibility(View.VISIBLE);
            }
        });

        tvLoginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etUserNme.getText().toString().equals("")) {
                    if (!etPassowrd.getText().toString().equals("")) {
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(etUserNme.getText().toString()).matches()) {
                            if (CheckNetwork.isInternetAvailable(Login.this)) {
                                sendLogin();
                            } else {
                                Toast.makeText(Login.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            etUserNme.setError("Enter valid user name");
                        }
                    } else {
                        etPassowrd.setError("Enter password");
                    }
                } else {
                    etUserNme.setError("Enter user name");
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Registration.class));
                finish();
            }
        });

        tvForgotSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etForgotMail.getText().toString().equals("")) {

                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(etForgotMail.getText().toString()).matches()) {
                        if (CheckNetwork.isInternetAvailable(Login.this)) {

                            forgetPassword();
                        } else {
                            Toast.makeText(Login.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        etForgotMail.setError("Enter valid user name");
                    }
                } else {
                    etForgotMail.setError("Enter user name");
                }
            }
        });
    }

    public void sendLogin() {

        myDialog = commonVariables.showProgressDialog(this, "Loging in ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/customer_login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject reader = null;
                        try {
                            reader = new JSONObject(response);

                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                                String msg = reader.getString("message");
                            } else if (status.equals("200")) {
                                String token = reader.getString("token");
                                String email = reader.getString("email");
                                String firstname = reader.getString("firstname");
                                String customer_id = reader.getString("customer_id");

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(commonVariables.Name, firstname);
                                editor.putString(commonVariables.Email, email);
                                editor.putString(commonVariables.token, token);
                                editor.putString(commonVariables.customer_id, customer_id);
                                editor.commit();

                                startActivity(new Intent(Login.this, Drower.class));
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        myDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                myDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email_id", etUserNme.getText().toString());
                params.put("password", etPassowrd.getText().toString());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(stringRequest);
    }

    public void forgetPassword() {
        myDialog = commonVariables.showProgressDialog(this, "Wait ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/forgot_password.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject reader = null;
                        try {
                            reader = new JSONObject(response);

                            String status = reader.getString("result");

                            if (status.equals("true")) {

                                new AlertDialog.Builder(Login.this).setIcon(android.R.drawable.ic_dialog_alert)
                                        .setMessage("Mail sent on your registered mail ID")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                llLogin.setVisibility(View.VISIBLE);
                                                llForgotPassword.setVisibility(View.GONE);
                                                dialog.dismiss();
                                            }
                                        }).show();
                            } else {

                                new AlertDialog.Builder(Login.this).setIcon(android.R.drawable.ic_dialog_alert)
                                        .setMessage("Mail ID not registered.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();
                                            }
                                        }).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        myDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                myDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email_id", etForgotMail.getText().toString());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, 3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {

        if (llLogin.getVisibility() == View.VISIBLE) {

            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                    .setMessage("Are you sure?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finish();
                        }
                    }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

        } else {
            llLogin.setVisibility(View.VISIBLE);
            llForgotPassword.setVisibility(View.GONE);
        }
    }
}