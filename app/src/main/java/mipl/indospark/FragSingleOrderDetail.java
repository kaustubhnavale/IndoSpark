package mipl.indospark;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragSingleOrderDetail extends Fragment {

    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;

    StringRequest stringRequest;
    ProgressDialog myDialog;
    private List<ProdPojo> mUsers = new ArrayList<>();

    SharedPreferences sharedpreferences;
    String email, orderID;

    TextView tvOrderID, tvOrderDate, tvOrderStatus, tvOrderStatusUpdateOn;
    TextView tvPaymentMethod, tvShippingMethod;
    TextView tvSName, tvSCompany, tvSStreet, tvSCity, tvSRegion, tvSContact;
    TextView tvBName, tvBCompany, tvBStreet, tvBCity, tvBRegion, tvBContact;
    TextView tvCartCount, tvCartSubTotal, tvShippingCharges, tvCoupenCode, tvOrderTotla;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frag_single_order_detail, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            orderID = getArguments().getString("orderID");
        }

        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        email = sharedpreferences.getString(commonVariables.Email, "");

        tvOrderID = (TextView) v.findViewById(R.id.tvOrderID);
        tvOrderDate = (TextView) v.findViewById(R.id.tvOrderDate);
        tvOrderStatus = (TextView) v.findViewById(R.id.tvOrderStatus);
        tvPaymentMethod = (TextView) v.findViewById(R.id.tvPaymentMethod);
        tvShippingMethod = (TextView) v.findViewById(R.id.tvShippingMethod);
        tvOrderStatusUpdateOn = (TextView) v.findViewById(R.id.tvOrderStatusUpdateOn);

        tvSName = (TextView) v.findViewById(R.id.tvSName);
        tvSCompany = (TextView) v.findViewById(R.id.tvSCompany);
        tvSStreet = (TextView) v.findViewById(R.id.tvSStreet);
        tvSCity = (TextView) v.findViewById(R.id.tvSCity);
        tvSRegion = (TextView) v.findViewById(R.id.tvSRegion);
        tvSContact = (TextView) v.findViewById(R.id.tvSContact);

        tvBName = (TextView) v.findViewById(R.id.tvBName);
        tvBCompany = (TextView) v.findViewById(R.id.tvBCompany);
        tvBStreet = (TextView) v.findViewById(R.id.tvBStreet);
        tvBCity = (TextView) v.findViewById(R.id.tvBCity);
        tvBRegion = (TextView) v.findViewById(R.id.tvBRegion);
        tvBContact = (TextView) v.findViewById(R.id.tvBContact);

        tvCartCount = (TextView) v.findViewById(R.id.tvCartCount);
        tvCartSubTotal = (TextView) v.findViewById(R.id.tvCartSubTotal);
        tvShippingCharges = (TextView) v.findViewById(R.id.tvShippingCharges);
        tvCoupenCode = (TextView) v.findViewById(R.id.tvCoupenCode);
        tvOrderTotla = (TextView) v.findViewById(R.id.tvOrderTotla);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rvSingleOrderList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (CheckNetwork.isInternetAvailable(getActivity())) {
            getAllProd();
        } else {
            Toast.makeText(getActivity(), "Internet Connection not available", Toast.LENGTH_SHORT).show();
        }


        return v;
    }

    public void getAllProd() {

        myDialog = commonVariables.showProgressDialog(getActivity(), "Getting Orders ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/get_order_details.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.length() > 0) {
                            if (response.contains("No Previous  records")) {

                            } else {
                                try {
                                    /*for (int i = 0; i < myOrder.length(); i++) {*/
                                    JSONObject curr = new JSONObject(response);

                                    String orderID = curr.getString("increment_id");
                                    String status = curr.getString("status");
                                    String entity_id = curr.getString("entity_id");
                                    String created_at = curr.getString("created_at");
                                    String grand_total = curr.getString("grand_total");
                                    String updated_at = curr.getString("updated_at");
                                    String subtotal = curr.getString("subtotal");
                                    String shipping_amount = curr.getString("shipping_amount");
                                    String shipping_description = curr.getString("shipping_description");

                                    tvOrderID.setText(orderID);
                                    tvOrderDate.setText(created_at);
                                    tvOrderStatus.setText(status);
                                    tvShippingMethod.setText(shipping_description);
                                    tvOrderStatusUpdateOn.setText(updated_at);

                                    tvShippingCharges.setText(shipping_amount);
                                    tvCartSubTotal.setText(subtotal);
                                    tvOrderTotla.setText(grand_total);

                                    JSONObject extension_attributes = curr.getJSONObject("extension_attributes");
                                    JSONArray shipping_assignments = extension_attributes.getJSONArray("shipping_assignments");

                                    for (int j = 0; j < shipping_assignments.length(); j++) {
                                        JSONObject shipping = shipping_assignments.getJSONObject(j);
                                        JSONObject shipping1 = shipping.getJSONObject("shipping");
                                        JSONObject address = shipping1.getJSONObject("address");

                                        try {
                                            String firstname = address.getString("firstname");
                                            String lastname = address.getString("lastname");
                                            tvSName.setText(firstname + " " + lastname);

                                            String company = address.getString("company");
                                            tvSCompany.setText(company);

                                            String email = address.getString("email");

                                            String city = address.getString("city");
                                            String postcode = address.getString("postcode");
                                            tvSCity.setText(city + ", " + postcode);

                                            String region = address.getString("region");
                                            tvSRegion.setText(region);

                                            String telephone = address.getString("telephone");
                                            tvSContact.setText(telephone);

                                            JSONArray street = address.getJSONArray("street");
                                            String streetName = "";

                                            for (int k = 0; k < street.length(); k++) {
                                                streetName = streetName + " " + street.getString(k);
                                            }
                                            tvSStreet.setText(streetName);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        JSONArray items = shipping.getJSONArray("items");

                                        String count = String.valueOf(items.length());
                                        tvCartCount.setText(count);

                                        for (int a = 0; a < items.length(); a++) {
                                            JSONObject item = items.getJSONObject(a);

                                            String name = item.getString("name");
                                            String sku = item.getString("sku").trim();
                                            String price = item.getString("price");
                                            String qty_ordered = item.getString("qty_ordered");

                                            ProdPojo user = new ProdPojo();
                                            user.setIda(entity_id);
                                            user.setName(name);
                                            user.setPrice(price);
                                            user.setSku(sku);
                                            user.setQty(qty_ordered);

                                            mUsers.add(user);

                                            mUserAdapter = new UserAdapter();
                                            mRecyclerView.setAdapter(mUserAdapter);
                                        }
                                    }

                                    try {
                                        JSONObject billing_address = curr.getJSONObject("billing_address");

                                        String company = billing_address.getString("company");
                                        tvBCompany.setText(company);

                                        String firstname = billing_address.getString("firstname");
                                        String lastname = billing_address.getString("lastname");
                                        tvBName.setText(firstname + " " + lastname);

                                        String telephone = billing_address.getString("telephone");
                                        tvBContact.setText(telephone);

                                        String city = billing_address.getString("city");
                                        String postcode = billing_address.getString("postcode");
                                        tvBCity.setText(city + ", " + postcode);

                                        JSONArray street = billing_address.getJSONArray("street");
                                        String streetName = "";

                                        for (int k = 0; k < street.length(); k++) {
                                            streetName = streetName + " " + street.getString(k);
                                        }
                                        tvBStreet.setText(streetName);

                                        String region = billing_address.getString("region");
                                        tvBRegion.setText(region);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    /*JSONArray items = curr.getJSONArray("items");
                                    for (int j = 0; j < items.length(); j++) {
                                        JSONObject item = items.getJSONObject(j);

                                        String name = item.getString("name");
                                        String sku = item.getString("sku");
                                        String price = item.getString("price");
                                        String order_id = item.getString("order_id");

                                        ProdPojo user = new ProdPojo();
                                        user.setIda(entity_id);
                                        user.setName(firstname + " " + lastname);
                                        user.setPrice(price);
                                        user.setQuoteID(orderID);

                                        mUsers.add(user);

                                        mUserAdapter = new UserAdapter();
                                        mRecyclerView.setAdapter(mUserAdapter);
                                    }*/

                                    JSONObject payment = curr.getJSONObject("payment");
                                    String paymentMethod = payment.getString("method");
                                    tvPaymentMethod.setText(paymentMethod);
//                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
                params.put("order id", orderID);
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
        public TextView tvName, tvQty, tvPrice, tvSubTotal;
        public LinearLayout llCard;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvQty = (TextView) itemView.findViewById(R.id.tvQty);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvSubTotal = (TextView) itemView.findViewById(R.id.tvSubTotal);
            llCard = (LinearLayout) itemView.findViewById(R.id.llCard);
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
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.order_detail_card, parent, false);
                return new UserViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserViewHolder) {
                final ProdPojo user = mUsers.get(position);
                UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.tvName.setText(user.getName());
                userViewHolder.tvQty.setText("Qty: " + user.getQty());
                userViewHolder.tvPrice.setText("Price: ₹" + user.getPrice());

                try {
                    int st = Integer.parseInt(user.getQty()) * Integer.parseInt(user.getPrice());
                    userViewHolder.tvSubTotal.setText("Sub Total: ₹" + String.valueOf(st));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                userViewHolder.llCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle bundle = new Bundle();
                        bundle.putString("SKU", user.getSku());
                        //set Fragmentclass Arguments
                        FragProdDesc fragobj = new FragProdDesc();
                        fragobj.setArguments(bundle);

                        android.support.v4.app.FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                        if (fm.findFragmentById(R.id.fragDrower) != null) {
                            ft.hide(fm.findFragmentById(R.id.fragDrower));
                        }
                        ft.add(R.id.fragDrower, fragobj,FragProdDesc.class.getCanonicalName())
                                .addToBackStack(FragProdDesc.class.getCanonicalName()).commit();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
        }
    }
}