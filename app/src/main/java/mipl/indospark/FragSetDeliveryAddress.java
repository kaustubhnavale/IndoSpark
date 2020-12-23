package mipl.indospark;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragSetDeliveryAddress extends Fragment {

    TextView tvSetOtherAddress, tvGoPayment, tvCardSetDelivery;
    TextView tvSetAddName, tvSetAddCompany, tvSetAddAddress, tvSetAddMobile;
    String Amount;
    LinearLayout llHasAddress, llNoAddress;
    TextView tvAddAddresss;

    public FragSetDeliveryAddress() {
    }

    SharedPreferences sharedpreferences;
    String token, customer_id;

    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;
    private List<AddressPojo> mUsers;

    StringRequest stringRequest;
    ProgressDialog myDialog;

    String billing_city,
            billing_company,
            billing_email,
            billing_firstname,
            billing_lastname,
            billing_postcode,
            billing_region,
            billing_street,
            billing_tel,

    shipping_city,
            shipping_company,
            shipping_email,
            shipping_firstname,
            shipping_lastname,
            shipping_postcode,
            shipping_region,
            shipping_street,
            shipping_tel;

    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_frag_set_delivery_address, container, false);

        tvSetOtherAddress = (TextView) v.findViewById(R.id.tvSetOtherAddress);
        tvGoPayment = (TextView) v.findViewById(R.id.tvGoPayment);

        tvSetAddName = (TextView) v.findViewById(R.id.tvSetAddName);
        tvSetAddCompany = (TextView) v.findViewById(R.id.tvSetAddCompany);
        tvSetAddAddress = (TextView) v.findViewById(R.id.tvSetAddAddress);
        tvSetAddMobile = (TextView) v.findViewById(R.id.tvSetAddMobile);
        tvCardSetDelivery = (TextView) v.findViewById(R.id.tvCardSetDelivery);
        tvAddAddresss = (TextView) v.findViewById(R.id.tvAddAddresss);

        llHasAddress = (LinearLayout) v.findViewById(R.id.llHasAddress);
        llNoAddress = (LinearLayout) v.findViewById(R.id.llNoAddress);

        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(commonVariables.token, "");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            // handle your code here.
            Amount = getArguments().getString("Amount");
        }

        mUsers = new ArrayList<>();
        getAddList();

        tvSetOtherAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.setdeliveryaddress);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.BOTTOM;
                wlp.width = WindowManager.LayoutParams.FILL_PARENT;

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                mRecyclerView = (RecyclerView) dialog.findViewById(R.id.rvAddListSet);
                mRecyclerView.setNestedScrollingEnabled(false);
                mRecyclerView.setLayoutManager(linearLayoutManager);

                if (mUsers.size() > 0) {

                    mUserAdapter = new UserAdapter();
                    mRecyclerView.setAdapter(mUserAdapter);
                }

                LinearLayout llAddAddress = (LinearLayout) dialog.findViewById(R.id.llAddAddress);
                llAddAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                        Bundle bundle = new Bundle();
                        bundle.putString("AddID", "New");
                        bundle.putString("Amount", Amount);
                        //set Fragmentclass Arguments
                        FragAddAddress fragobj = new FragAddAddress();
                        fragobj.setArguments(bundle);

                        android.support.v4.app.FragmentManager fm = ((FragmentActivity) getActivity()).getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                        if (fm.findFragmentById(R.id.fragDrower) != null) {
                            ft.hide(fm.findFragmentById(R.id.fragDrower));
                        }
                        ft.add(R.id.fragDrower, fragobj, FragAddAddress.class.getCanonicalName())
                                .addToBackStack(FragAddAddress.class.getCanonicalName()).commit();
                    }
                });
                dialog.show();
            }
        });

        tvGoPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (AddressPojo pojo : mUsers) {

                    if (pojo.getDefaultShipping().equals("true")) {
                        shipping_firstname = pojo.getFirstname();
                        shipping_lastname = pojo.getLastname();
                        shipping_company = pojo.getCompany();
                        shipping_city = pojo.getCity();
                        shipping_email = pojo.getEmail();
                        shipping_region = pojo.getRegion();
                        shipping_street = pojo.getStreet();
                        shipping_postcode = pojo.getPostcode();
                        shipping_tel = pojo.getTelephone();
                        customer_id = pojo.getCustomerID();

                    }
                    if (pojo.getDefaultBilling().equals("true")) {
                        billing_firstname = pojo.getFirstname();
                        billing_lastname = pojo.getLastname();
                        billing_company = pojo.getCompany();
                        billing_city = pojo.getCity();
                        billing_email = pojo.getEmail();
                        billing_region = pojo.getRegion();
                        billing_street = pojo.getStreet();
                        billing_postcode = pojo.getPostcode();
                        billing_tel = pojo.getTelephone();
                        customer_id = pojo.getCustomerID();
                    }
                }
                setAddress();
            }
        });

        tvCardSetDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (AddressPojo pojo : mUsers) {

                    if (pojo.getDefaultShipping().equals("true")) {
                        shipping_firstname = pojo.getFirstname();
                        shipping_lastname = pojo.getLastname();
                        shipping_company = pojo.getCompany();
                        shipping_city = pojo.getCity();
                        shipping_email = pojo.getEmail();
                        shipping_region = pojo.getRegion();
                        shipping_street = pojo.getStreet();
                        shipping_postcode = pojo.getPostcode();
                        shipping_tel = pojo.getTelephone();
                        customer_id = pojo.getCustomerID();

                    }
                    if (pojo.getDefaultBilling().equals("true")) {
                        billing_firstname = pojo.getFirstname();
                        billing_lastname = pojo.getLastname();
                        billing_company = pojo.getCompany();
                        billing_city = pojo.getCity();
                        billing_email = pojo.getEmail();
                        billing_region = pojo.getRegion();
                        billing_street = pojo.getStreet();
                        billing_postcode = pojo.getPostcode();
                        billing_tel = pojo.getTelephone();
                        customer_id = pojo.getCustomerID();
                    }
                }
                setAddress();
            }
        });

        tvAddAddresss.setOnClickListener(new View.OnClickListener() {
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

        return v;
    }

    public void getAddList() {
        myDialog = commonVariables.showProgressDialog(getActivity(), "Getting Addresses ...");
        mUsers.clear();

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/get_all_address.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            String default_billing = null, default_shipping = null;

                            JSONObject reader = new JSONObject(response);

                            String group_id = reader.getString("group_id");
                            String email = reader.getString("email");

                            JSONArray items = reader.getJSONArray("addresses");

                            if (items.length() > 0) {

                                llHasAddress.setVisibility(View.VISIBLE);
                                llNoAddress.setVisibility(View.GONE);

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

                                    AddressPojo user = new AddressPojo();
                                    user.setDefaultBilling(default_billing);
                                    user.setDefaultShipping(default_shipping);
                                    user.setGroupID(group_id);
                                    user.setCustomerID(customer_id);
                                    user.setCompany(company);
                                    user.setTelephone(telephone);
                                    user.setPostcode(postcode);
                                    user.setCity(city);
                                    user.setFirstname(firstname);
                                    user.setLastname(lastname);
                                    user.setAddID(id);
                                    user.setRegion(regionName);
                                    user.setStreet(streetName);
                                    user.setEmail(email);
                                    user.setCount(String.valueOf(i + 1));

                                    mUsers.add(user);

                                    if (default_shipping.equals("true")) {
                                        tvSetAddName.setText(firstname + " " + lastname);
                                        tvSetAddCompany.setText(company);
                                        tvSetAddAddress.setText(city + ", " + postcode);
                                        tvSetAddMobile.setText("T: " + telephone);

                                    } else {

                                    }
                                }
                            } else {
                                llHasAddress.setVisibility(View.GONE);
                                llNoAddress.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
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

    static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCardFNM, tvCardComp, tvCardAddress, tvCardTele;
        public TextView tvSetDefault;
        public LinearLayout llAddCard;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvCardFNM = (TextView) itemView.findViewById(R.id.tvCardFNM);
            tvCardComp = (TextView) itemView.findViewById(R.id.tvCardComp);
            tvCardAddress = (TextView) itemView.findViewById(R.id.tvCardAddress);
            tvCardTele = (TextView) itemView.findViewById(R.id.tvCardTele);
            tvSetDefault = (TextView) itemView.findViewById(R.id.tvSetDefault);
            llAddCard = (LinearLayout) itemView.findViewById(R.id.llAddCard);
        }
    }

    class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        public UserAdapter() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return mUsers.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.setdeliverycard, parent, false);
                return new UserViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserViewHolder) {
                final AddressPojo user = mUsers.get(position);
                final UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.tvCardFNM.setText(user.getFirstname() + " " + user.getLastname());
                userViewHolder.tvCardComp.setText(user.getCompany());
                userViewHolder.tvCardTele.setText("T: " + user.getTelephone());
                userViewHolder.tvCardAddress.setText(user.getCity() + " " + user.getPostcode());

                userViewHolder.llAddCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        shipping_firstname = user.getFirstname();
                        shipping_lastname = user.getLastname();
                        shipping_company = user.getCompany();
                        shipping_city = user.getCity();
                        shipping_email = user.getEmail();
                        shipping_region = user.getRegion();
                        shipping_street = user.getStreet();
                        shipping_postcode = user.getPostcode();
                        shipping_tel = user.getTelephone();
                        customer_id = user.getCustomerID();

                        billing_firstname = user.getFirstname();
                        billing_lastname = user.getLastname();
                        billing_company = user.getCompany();
                        billing_city = user.getCity();
                        billing_email = user.getEmail();
                        billing_region = user.getRegion();
                        billing_street = user.getStreet();
                        billing_postcode = user.getPostcode();
                        billing_tel = user.getTelephone();
                        customer_id = user.getCustomerID();

                        dialog.dismiss();
                        setAddress();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
        }
    }

    public void setAddress() {
        myDialog = commonVariables.showProgressDialog(getActivity(), "Loading ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/set_shipping_information.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.contains("grand_total")) {
                            myDialog.dismiss();

                            Bundle bundle = new Bundle();
                            bundle.putString("Amount", Amount);
                            //set Fragmentclass Arguments
                            FragPayment fragobj = new FragPayment();
                            fragobj.setArguments(bundle);

                            android.support.v4.app.FragmentManager fm = ((FragmentActivity) getActivity()).getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                            if (fm.findFragmentById(R.id.fragDrower) != null) {
                                ft.hide(fm.findFragmentById(R.id.fragDrower));
                            }
                            ft.add(R.id.fragDrower, fragobj, FragSetDeliveryAddress.class.getCanonicalName())
                                    .addToBackStack(FragSetDeliveryAddress.class.getCanonicalName())
                                    .commit();

                        } else {
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        }

                        myDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Error")
                        .setMessage("Set Shipping and Billing address, First")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                android.support.v4.app.FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                                android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                                if (fm.findFragmentById(R.id.fragDrower) != null) {
                                    ft.hide(fm.findFragmentById(R.id.fragDrower));
                                }
                                ft.add(R.id.fragDrower, new FragDefaultAddress(), FragDefaultAddress.class.getCanonicalName())
                                        .addToBackStack(FragDefaultAddress.class.getCanonicalName()).commit();

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                myDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("shipping_firstname", shipping_firstname);
                params.put("shipping_lastname", shipping_lastname);
                params.put("shipping_company", shipping_company);
                params.put("shipping_city", shipping_city);
                params.put("shipping_email", shipping_email);
                params.put("shipping_region", shipping_region);
                params.put("shipping_street", shipping_street);
                params.put("shipping_postcode", shipping_postcode);
                params.put("shipping_tel", shipping_tel);

                params.put("billing_firstname", billing_firstname);
                params.put("billing_lastname", billing_lastname);
                params.put("billing_company", billing_company);
                params.put("billing_city", billing_city);
                params.put("billing_email", billing_email);
                params.put("billing_region", billing_region);
                params.put("billing_street", billing_street);
                params.put("billing_postcode", billing_postcode);
                params.put("billing_tel", billing_tel);

                params.put("customer_id", customer_id);
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