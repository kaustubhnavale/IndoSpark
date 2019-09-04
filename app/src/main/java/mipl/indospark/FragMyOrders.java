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
import android.webkit.WebView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragMyOrders extends Fragment {

    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;

    StringRequest stringRequest;
    ProgressDialog myDialog;
    private List<ProdPojo> mUsers = new ArrayList<>();

    SharedPreferences sharedpreferences;
    String email;

    TextView tvOrderCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_my_orders, container, false);

        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        email = sharedpreferences.getString(commonVariables.Email, "");

        tvOrderCount = (TextView) v.findViewById(R.id.tvOrderCount);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rvMyOrderList);
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

//        stringRequest = new StringRequest(Request.Method.GET, "https://shop.indospark.com/index.php/rest/V1/products?searchCriteria",
        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/get_all_orders.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("Responce", response);

                        if (response.length() > 0) {
                            if (response.contains("No Previous  records")) {
                                tvOrderCount.setText("Total Orders: 0");

                            } else {

                                try {

                                    String firstname = null, lastname = null;

                                    JSONArray myOrder = new JSONArray(response);
                                    tvOrderCount.setText("Total Orders: " + myOrder.length());

                                    for (int i = 0; i < myOrder.length(); i++) {
                                        JSONObject curr = myOrder.getJSONObject(i);

                                        String orderID = curr.getString("increment_id");
                                        String status = curr.getString("status");
                                        String entity_id = curr.getString("entity_id");
                                        String created_at = curr.getString("created_at");
                                        String base_grand_total = curr.getString("base_grand_total");

                                        /*JSONArray items = curr.getJSONArray("items");
                                        for (int j = 0; j < items.length(); j++) {
                                            JSONObject item = items.getJSONObject(j);

                                            String name = item.getString("name");
                                            String sku = item.getString("sku");
                                            String price = item.getString("price");
                                            String created_at = item.getString("created_at");
                                            String order_id = item.getString("order_id");
                                        }*/

                                        JSONObject extension_attributes = curr.getJSONObject("extension_attributes");
                                        JSONArray shipping_assignments = extension_attributes.getJSONArray("shipping_assignments");

                                        for (int j = 0; j < shipping_assignments.length(); j++) {
                                            JSONObject shipping = shipping_assignments.getJSONObject(j);
                                            JSONObject shipping1 = shipping.getJSONObject("shipping");
                                            JSONObject address = shipping1.getJSONObject("address");

                                            firstname = address.getString("firstname");
                                            lastname = address.getString("lastname");

                                        }

                                        ProdPojo user = new ProdPojo();
                                        user.setIda(entity_id);
                                        user.setName(firstname + " " + lastname);
                                        user.setPrice(base_grand_total);
                                        user.setQuoteID(orderID);
                                        user.setStatus(status);
                                        user.setDate(created_at);

                                        mUsers.add(user);

                                        mUserAdapter = new UserAdapter();
                                        mRecyclerView.setAdapter(mUserAdapter);
                                    }

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
//                params.put("email_id",  "aniket.tambe@mipl.co.in");
                params.put("email_id", email);
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
        public TextView tvOrderID, tvOrderDate, tvOrderStatus, tvOrderPrice;
        public LinearLayout llCard;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvOrderID = (TextView) itemView.findViewById(R.id.tvOrderID);
            tvOrderDate = (TextView) itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = (TextView) itemView.findViewById(R.id.tvOrderStatus);
            tvOrderPrice = (TextView) itemView.findViewById(R.id.tvOrderPrice);
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
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.order_card, parent, false);
                return new UserViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserViewHolder) {
                final ProdPojo user = mUsers.get(position);
                UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.tvOrderDate.setText("Order Date: " + user.getDate());
                userViewHolder.tvOrderID.setText("Order ID: #" + user.getQuoteID());
                userViewHolder.tvOrderStatus.setText("Status: " + user.getStatus());
                userViewHolder.tvOrderPrice.setText("Order Total: ₹" + user.getPrice());

                userViewHolder.llCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle bundle = new Bundle();
                        bundle.putString("orderID", user.getIda());
                        //set Fragmentclass Arguments
                        FragSingleOrderDetail fragobj = new FragSingleOrderDetail();
                        fragobj.setArguments(bundle);

                        android.support.v4.app.FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                        if (fm.findFragmentById(R.id.fragDrower) != null) {
                            ft.hide(fm.findFragmentById(R.id.fragDrower));
                        }
                        ft.add(R.id.fragDrower, fragobj,FragSingleOrderDetail.class.getCanonicalName())
                                .addToBackStack(FragSingleOrderDetail.class.getCanonicalName()).commit();
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