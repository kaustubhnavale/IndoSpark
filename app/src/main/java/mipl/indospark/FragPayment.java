package mipl.indospark;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class FragPayment extends Fragment {

    StringRequest stringRequest;
    ProgressDialog myDialog;
    WebView wvPayPage;

    String encVal;
    String vResponse;

    final String mimeType = "text/html";
    final String encoding = "UTF-8";
    String token, Amount, payID;
    SharedPreferences sharedpreferences;
    ProgressDialog myDialogPlaceOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_payment, container, false);

        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(commonVariables.token, "");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            // handle your code here.
            Amount = getArguments().getString("Amount");
        }

        wvPayPage = (WebView) v.findViewById(R.id.wvPayPage);
        placeOrder();

       /* wvPayPage.clearCache(true);
        wvPayPage.clearHistory();
        wvPayPage.getSettings().setJavaScriptEnabled(true);
        wvPayPage.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvPayPage.getSettings().setJavaScriptEnabled(true);
        wvPayPage.getSettings().setDomStorageEnabled(true);
        wvPayPage.getSettings().setDatabaseEnabled(true);
//        wvPages.getSettings().setDatabasePath(dbpath); //check the documentation for info about dbpath
        wvPayPage.getSettings().setMinimumFontSize(1);
        wvPayPage.getSettings().setMinimumLogicalFontSize(1);

//        wvPayPage.loadUrl("http://shop.indospark.com/android_api/paypage.php?order_id=65");

        progressBar = new ProgressDialog(getActivity());
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(false); */

        wvPayPage.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                Log.d("WebView", "your current url when webpage loading.." + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebView", "your current url when webpage loading.. finish" + url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("when you click on any interlink on webview that time you got url :-" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        return v;
    }

    public void placeOrder() {
        myDialogPlaceOrder = commonVariables.showProgressDialog(getActivity(), "Loading ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/place_order.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("response", response);
                        myDialogPlaceOrder.dismiss();

                        try {
                            if (response.contains("message")) {

                            }

                            String[] separated = response.split("\"");
                            payID = separated[1];
                            String status = separated[2];

                            if (status.equals("1")) {
//                                placePayment(payID);
//                                wvPayPage.loadUrl("http://shop.indospark.com/android_api/paypage.php?order_id=" + payID);
                                get_RSA_key();

                            } else {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().popBackStack();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                myDialogPlaceOrder.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
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

    public void placePayment(final String payID) {
        myDialog = commonVariables.showProgressDialog(getActivity(), "Loading ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/paypage.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("response", response);

                        if (response.contains("ccavRequestHandler.php")){

                            String html = "<!DOCTYPE html><head><style>@font-face{font-family: 'arial';src: url('file:///file:///android_asset/fonts/arial.ttf');}body{font-family: 'arial';font-size:14px}</style></head>"
                                    + "<body ><font color='#000000'>"
                                    + response + "</font></body></html>";
                            wvPayPage.loadData(html, mimeType, encoding);

                        } else {
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
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
//                params.put("token", "gqt18hvy6b1xg2sej4iicfl4hdqa33di");
                params.put("order_id", payID);
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

    public void get_RSA_key() {
        myDialog = commonVariables.showProgressDialog(getActivity(), "Loading ...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/PHP/GetRSA.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(WebViewActivity.this,response,Toast.LENGTH_LONG).show();
                        myDialog.dismiss();

                        if (response != null && !response.equals("")) {
                            vResponse = response;     ///save retrived rsa key
                            if (vResponse.contains("!ERROR!")) {
                                show_alert(vResponse);
                            } else {
                                new RenderView().execute();   // Calling async task to get display content
                            }
                        }
                        else
                        {
                            show_alert("No response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        myDialog.dismiss();
                        //Toast.makeText(WebViewActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put(AvenuesParams.ACCESS_CODE, AvenuesParams.access_code_key);
                params.put(AvenuesParams.ORDER_ID, payID);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void show_alert(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        alertDialog.setTitle("Error!!!");
        if (msg.contains("\n"))
            msg = msg.replaceAll("\\\n", "");

        alertDialog.setMessage(msg);

        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    private class RenderView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            myDialog = commonVariables.showProgressDialog(getActivity(), "Loading ...");

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (!ServiceUtility.chkNull(vResponse).equals("")
                    && ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR") == -1) {

                try {
                    StringBuffer vEncVal = new StringBuffer("");
                    vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, Amount));
                    vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, AvenuesParams.currency_key));
                    encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), vResponse);  //encrypt amount and currency
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
//            myDialog.dismiss();

            @SuppressWarnings("unused")
            class MyJavaScriptInterface {
                @JavascriptInterface
                public void processHTML(String html) {
                    // process the html source code to get final status of transaction
                    String status = null;
                    if (html.indexOf("Failure") != -1) {
                        status = "Transaction Declined!";
                    } else if (html.indexOf("Success") != -1) {
                        status = "Transaction Successful!";
                    } else if (html.indexOf("Aborted") != -1) {
                        status = "Transaction Cancelled!";
                    } else {
                        status = "Status Not Known!";
                    }
                    //Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
                    intent.putExtra("transStatus", status);
                    startActivity(intent);*/
                }
            }

            wvPayPage.getSettings().setJavaScriptEnabled(true);
            wvPayPage.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
            wvPayPage.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(wvPayPage, url);
//                    myDialog.dismiss();
                    if (url.indexOf("/ccavResponseHandler.jsp") != -1) {
                        wvPayPage.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                    }
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
//                    myDialog = commonVariables.showProgressDialog(getActivity(), "Loading ...");
                }
            });


            try {
                String postData = AvenuesParams.ACCESS_CODE + "=" + URLEncoder.encode(AvenuesParams.access_code_key, "UTF-8") + "&" +
                        AvenuesParams.MERCHANT_ID + "=" + URLEncoder.encode(AvenuesParams.merchant_id_key, "UTF-8") + "&" +
                        AvenuesParams.ORDER_ID + "=" + URLEncoder.encode(payID, "UTF-8") + "&" +
                        AvenuesParams.REDIRECT_URL + "=" + URLEncoder.encode(AvenuesParams.redirect_url_key, "UTF-8") + "&" +
                        AvenuesParams.CANCEL_URL + "=" + URLEncoder.encode(AvenuesParams.cancel_url_key, "UTF-8") + "&" +
                        AvenuesParams.ENC_VAL + "=" + URLEncoder.encode(encVal, "UTF-8");

                wvPayPage.postUrl("https://secure.ccavenue.com/transaction/initTrans", postData.getBytes());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }
}