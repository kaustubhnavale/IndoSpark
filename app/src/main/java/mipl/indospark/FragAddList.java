package mipl.indospark;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragAddList extends Fragment {

    TextView tvAddNewAddress;

    StringRequest stringRequest;
    ProgressDialog myDialog, myDialog1;

    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;
    private List<AddressPojo> mUsers;

    SharedPreferences sharedpreferences;
    String token, addType;

    public FragAddList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_add_list, container, false);

        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(commonVariables.token, "");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            // handle your code here.
            addType = getArguments().getString("AddType");
        }

        tvAddNewAddress = (TextView) v.findViewById(R.id.tvAddNewAddress);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rvAddList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mUsers = new ArrayList<>();

        getAddList();

        tvAddNewAddress.setOnClickListener(new View.OnClickListener() {
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
        mUsers.clear();

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/get_all_address.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("response", response);

                        if (response.length() > 0) {
                            try {

                                String imageValue = null;
                                String short_desc = null;
                                String default_billing = null, default_shipping = null;

                                JSONObject reader = new JSONObject(response);

                                String group_id = reader.getString("group_id");
                                String email = reader.getString("email");

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

                                        mUserAdapter = new UserAdapter();
                                        mRecyclerView.setAdapter(mUserAdapter);
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "No Address Added", Toast.LENGTH_SHORT).show();
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

            /*@Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }*/

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("token", "gqt18hvy6b1xg2sej4iicfl4hdqa33di");
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
        public TextView tvAddNo, tvCardFNM, tvCardComp, tvCardAddress, tvCardTele;
        public TextView tvSetDefault;
        public LinearLayout llAddCard;
        public ImageView ivEdit;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvAddNo = (TextView) itemView.findViewById(R.id.tvAddNo);
            tvCardFNM = (TextView) itemView.findViewById(R.id.tvCardFNM);
            tvCardComp = (TextView) itemView.findViewById(R.id.tvCardComp);
            tvCardAddress = (TextView) itemView.findViewById(R.id.tvCardAddress);
            tvCardTele = (TextView) itemView.findViewById(R.id.tvCardTele);
            tvSetDefault = (TextView) itemView.findViewById(R.id.tvSetDefault);
            llAddCard = (LinearLayout) itemView.findViewById(R.id.llAddCard);
            ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
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
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.addresscard, parent, false);
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
                userViewHolder.tvCardAddress.setText(user.getStreet() + " " + user.getCity() + " " + user.getPostcode());
                userViewHolder.tvAddNo.setText("Address " + user.getCount());

                userViewHolder.tvSetDefault.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Set Default " + addType + " Address")
                                .setMessage("Are you sure?")
                                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getUploadDefault(addType, user.getCustomerID(), user.getAddID());
                                    }
                                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                });

                userViewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle bundle = new Bundle();
                        bundle.putString("AddID", user.getAddID());
                        bundle.putString("Firstname", user.getFirstname());
                        bundle.putString("Lastname", user.getLastname());
                        bundle.putString("Company", user.getCompany());
                        bundle.putString("Telephone", user.getTelephone());
                        bundle.putString("Street", user.getStreet());
                        bundle.putString("City", user.getCity());
                        bundle.putString("Postcode", user.getPostcode());
                        bundle.putString("Region", user.getRegion());
                        bundle.putString("Email", user.getEmail());
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
            }
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
        }
    }

    public void getUploadDefault(final String address_type, final String customer_id, final String address_id) {
        myDialog1 = commonVariables.showProgressDialog(getActivity(), "Updating Addresses ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/set_default_address.php",
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
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragDrower, new FragAddList(), "SOMETAG").commit();
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
                params.put("customer_id", customer_id);
                params.put("address_id", address_id);
                params.put("address_type", address_type);
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