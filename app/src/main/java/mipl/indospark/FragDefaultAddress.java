package mipl.indospark;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FragDefaultAddress extends Fragment {

    StringRequest stringRequest;
    ProgressDialog myDialog;

    LinearLayout llShippingAddress, llNoShippingAddress;
    LinearLayout llBillingAddress, llNoBillingAddress;

    TextView tvDefShipName, tvDefShipComp, tvDefShipAddress, tvDefShipTele;
    TextView tvDefBillName, tvDefBillCompany, tvDefBillAddress, tvDefBillTele;
    TextView tvAddShipping, tvAddBilling;
    TextView tvViewAllBilling, tvViewAllShipping;
    ImageView ivDefAddAddress;

    SharedPreferences sharedpreferences;
    String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_frag_default_address, container, false);

        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(commonVariables.token, "");

        llShippingAddress = (LinearLayout) v.findViewById(R.id.llShippingAddress);
        llNoShippingAddress = (LinearLayout) v.findViewById(R.id.llNoShippingAddress);
        llBillingAddress = (LinearLayout) v.findViewById(R.id.llBillingAddress);
        llNoBillingAddress = (LinearLayout) v.findViewById(R.id.llNoBillingAddress);

        tvDefShipName = (TextView) v.findViewById(R.id.tvDefShipName);
        tvDefShipComp = (TextView) v.findViewById(R.id.tvDefShipComp);
        tvDefShipAddress = (TextView) v.findViewById(R.id.tvDefShipAddress);
        tvDefShipTele = (TextView) v.findViewById(R.id.tvDefShipTele);

        tvDefBillName = (TextView) v.findViewById(R.id.tvDefBillName);
        tvDefBillCompany = (TextView) v.findViewById(R.id.tvDefBillCompany);
        tvDefBillAddress = (TextView) v.findViewById(R.id.tvDefBillAddress);
        tvDefBillTele = (TextView) v.findViewById(R.id.tvDefBillTele);

        tvAddShipping = (TextView) v.findViewById(R.id.tvAddShipping);
        tvAddBilling = (TextView) v.findViewById(R.id.tvAddBilling);

        tvViewAllShipping = (TextView) v.findViewById(R.id.tvViewAllShipping);
        tvViewAllBilling = (TextView) v.findViewById(R.id.tvViewAllBilling);

        ivDefAddAddress = (ImageView) v.findViewById(R.id.ivDefAddAddress);

        getAddList();

        tvAddShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v4.app.FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                if (fm.findFragmentById(R.id.fragDrower) != null) {
                    ft.hide(fm.findFragmentById(R.id.fragDrower));
                }
                ft.add(R.id.fragDrower, new FragAddAddress(), FragAddAddress.class.getCanonicalName())
                        .addToBackStack(FragAddAddress.class.getCanonicalName()).commit();
            }
        });

        tvAddBilling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v4.app.FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                if (fm.findFragmentById(R.id.fragDrower) != null) {
                    ft.hide(fm.findFragmentById(R.id.fragDrower));
                }
                ft.add(R.id.fragDrower, new FragAddAddress(), FragAddAddress.class.getCanonicalName())
                        .addToBackStack(FragAddAddress.class.getCanonicalName()).commit();
            }
        });

        tvViewAllShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle=new Bundle();
                bundle.putString("AddType", "shipping");
                //set Fragmentclass Arguments
                FragAddList fragobj=new FragAddList();
                fragobj.setArguments(bundle);

                android.support.v4.app.FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                if (fm.findFragmentById(R.id.fragDrower) != null) {
                    ft.hide(fm.findFragmentById(R.id.fragDrower));
                }
                ft.add(R.id.fragDrower, fragobj,FragAddList.class.getCanonicalName())
                        .addToBackStack(FragAddList.class.getCanonicalName()).commit();
            }
        });

        tvViewAllBilling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle=new Bundle();
                bundle.putString("AddType", "billing");
                //set Fragmentclass Arguments
                FragAddList fragobj=new FragAddList();
                fragobj.setArguments(bundle);

                android.support.v4.app.FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                if (fm.findFragmentById(R.id.fragDrower) != null) {
                    ft.hide(fm.findFragmentById(R.id.fragDrower));
                }
                ft.add(R.id.fragDrower, fragobj,FragAddList.class.getCanonicalName())
                        .addToBackStack(FragAddList.class.getCanonicalName()).commit();
            }
        });

        ivDefAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                if (fm.findFragmentById(R.id.fragDrower) != null) {
                    ft.hide(fm.findFragmentById(R.id.fragDrower));
                }
                ft.add(R.id.fragDrower, new FragAddAddress(), FragAddAddress.class.getCanonicalName())
                        .addToBackStack(FragAddAddress.class.getCanonicalName()).commit();
            }
        });

        return v;
    }

    public void getAddList() {
        myDialog = commonVariables.showProgressDialog(getActivity(), "Getting Addresses ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/get_all_address.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.length() > 0) {
                            try {

                                String imageValue = null;
                                String short_desc = null;
                                String default_billing = null, default_shipping = null;

                                JSONObject reader = new JSONObject(response);

                                String group_id = reader.getString("group_id");

                                JSONArray items = reader.getJSONArray("addresses");

                                if (items.length() > 0) {

                                    for (int i = 0; i < items.length(); i++) {
                                        JSONObject address = items.getJSONObject(i);

                                        try {
                                            default_billing = address.getString("default_billing");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            default_billing = "false";
                                        }

                                        try {
                                            default_shipping = address.getString("default_shipping");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            default_shipping = "false";
                                        }

                                        if (default_billing.equals("true")) {

                                            llBillingAddress.setVisibility(View.VISIBLE);
                                            llNoBillingAddress.setVisibility(View.GONE);
                                            tvViewAllBilling.setVisibility(View.VISIBLE);

                                            String customer_id = address.getString("customer_id");
                                            String id = address.getString("id");
                                            String company = address.getString("company");
                                            String telephone = address.getString("telephone");
                                            String postcode = address.getString("postcode");
                                            String city = address.getString("city");
                                            String firstname = address.getString("firstname");
                                            String lastname = address.getString("lastname");

                                            JSONObject region = address.getJSONObject("region");
                                            String regionName = region.getString("region");

                                            JSONArray street = address.getJSONArray("street");
                                            String streetName = "";

                                            for (int j = 0; j < street.length(); j++) {
                                                streetName = streetName + " " + street.getString(j);
                                            }

                                            tvDefBillName.setText(firstname + " " + lastname);
                                            tvDefBillCompany.setText(company);
                                            tvDefBillAddress.setText(streetName + ", " + city + " " + postcode);
                                            tvDefBillTele.setText("T: " + telephone);
                                        }

                                        if (default_shipping.equals("true")){

                                            llShippingAddress.setVisibility(View.VISIBLE);
                                            llNoShippingAddress.setVisibility(View.GONE);
                                            tvViewAllShipping.setVisibility(View.VISIBLE);

                                            String customer_id = address.getString("customer_id");
                                            String id = address.getString("id");
                                            String company = address.getString("company");
                                            String telephone = address.getString("telephone");
                                            String postcode = address.getString("postcode");
                                            String city = address.getString("city");
                                            String firstname = address.getString("firstname");
                                            String lastname = address.getString("lastname");

                                            JSONObject region = address.getJSONObject("region");
                                            String regionName = region.getString("region");

                                            JSONArray street = address.getJSONArray("street");
                                            String streetName = "";

                                            for (int j = 0; j < street.length(); j++) {
                                                streetName = streetName + " " + street.getString(j);
                                            }

                                            tvDefShipName.setText(firstname + " " + lastname);
                                            tvDefShipComp.setText(company);
                                            tvDefShipAddress.setText(streetName + ", " + city + ", " + postcode);
                                            tvDefShipTele.setText("T: " + telephone);
                                        }
                                    }
                                } else {

                                    llShippingAddress.setVisibility(View.GONE);
                                    llNoShippingAddress.setVisibility(View.VISIBLE);
                                    tvViewAllShipping.setVisibility(View.GONE);

                                    llBillingAddress.setVisibility(View.GONE);
                                    llNoBillingAddress.setVisibility(View.VISIBLE);
                                    tvViewAllBilling.setVisibility(View.GONE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
}