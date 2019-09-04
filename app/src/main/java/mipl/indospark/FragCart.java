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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragCart extends Fragment {

    RecyclerView rvCartList;
    ProgressDialog myDialog;
    StringRequest stringRequest;
    private List<ProdPojo> mUsers;
    UserAdapter mUserAdapter;

    TextView tvCoupenText, tvCartProceed, tvContinueShopping;
    TextView tvCartCount, tvCartSubTotal, tvShippingCharges, tvCoupenCode, tvOrderTotla, tvCouponCodeTitle;
    LinearLayout llEmptyCartList, llFilledCart;
    float orderTotal = 0;
    int subTotal = 0;

    SharedPreferences sharedpreferences;
    String token;
    JSONArray reader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_cart, container, false);

        mUsers = new ArrayList<>();
        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(commonVariables.token, "");

        tvCartCount = (TextView) v.findViewById(R.id.tvCartCount);
        tvCartSubTotal = (TextView) v.findViewById(R.id.tvCartSubTotal);
        tvShippingCharges = (TextView) v.findViewById(R.id.tvShippingCharges);
        tvCoupenCode = (TextView) v.findViewById(R.id.tvCoupenCode);
        tvOrderTotla = (TextView) v.findViewById(R.id.tvOrderTotla);
        tvCoupenText = (TextView) v.findViewById(R.id.tvCoupenText);
        tvCouponCodeTitle = (TextView) v.findViewById(R.id.tvCouponCodeTitle);
        tvCartProceed = (TextView) v.findViewById(R.id.tvCartProceed);
        tvContinueShopping = (TextView) v.findViewById(R.id.tvContinueShopping);
        llEmptyCartList = (LinearLayout) v.findViewById(R.id.llEmptyCartList);
        llFilledCart = (LinearLayout) v.findViewById(R.id.llFilledCart);

        rvCartList = (RecyclerView) v.findViewById(R.id.rvCartList);
        rvCartList.setLayoutManager(new LinearLayoutManager(getActivity()));

        getCartList();

        tvCoupenText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.coupenapplylayout);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
//        wlp.gravity = Gravity.TOP;
                wlp.width = WindowManager.LayoutParams.FILL_PARENT;

                final EditText etCoupenCode = (EditText) dialog.findViewById(R.id.etCoupenCode);
                TextView tvApplyCoupen = (TextView) dialog.findViewById(R.id.tvApplyCoupen);

                tvApplyCoupen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (etCoupenCode.getText().toString().length() > 0) {
                            getGetCoupen(etCoupenCode.getText().toString());
                            dialog.dismiss();

                        } else {
                            etCoupenCode.setError("Enter Coupen Code");
                        }
                    }
                });

                dialog.show();
            }
        });

        tvCartProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (String.valueOf(tvOrderTotla).equals("0")) {
                    Toast.makeText(getActivity(), "Something wen't wrong", Toast.LENGTH_SHORT).show();
                } else {
                    if (reader.length() > 0) {
//                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragDrower, new FragSetDeliveryAddress(), "SOMETAG").addToBackStack("Indo").commit();

                        Bundle bundle = new Bundle();
                        bundle.putString("Amount", tvOrderTotla.getText().toString());
                        //set Fragmentclass Arguments
                        FragSetDeliveryAddress fragobj = new FragSetDeliveryAddress();
                        fragobj.setArguments(bundle);

                        android.support.v4.app.FragmentManager fm = ((FragmentActivity) getActivity()).getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                        if (fm.findFragmentById(R.id.fragDrower) != null) {
                            ft.hide(fm.findFragmentById(R.id.fragDrower));
                        }
                        ft.add(R.id.fragDrower, fragobj, FragCart.class.getCanonicalName())
                                .addToBackStack(FragCart.class.getCanonicalName())
                                .commit();
                    } else {
                        Toast.makeText(getActivity(), "You not have any item in cart", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tvContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return v;
    }

    public void getCartList() {
        myDialog = commonVariables.showProgressDialog(getActivity(), "Downloading Cart ...");
        mUsers.clear();

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/get_cart_items.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("response", response);
                        orderTotal = 0;

                        if (response.length() > 0) {
                            try {
                                reader = new JSONArray(response);

                                if (reader.length() > 0) {

                                    for (int i = 0; i < reader.length(); i++) {
                                        JSONObject address = reader.getJSONObject(i);

                                        String item_id = address.getString("item_id");

                                        if (item_id.equals("null")) {
                                            tvCartCount.setText("0");
//                                            tvShippingCharges.setText("0");
                                            tvCartSubTotal.setText("0");
                                            tvOrderTotla.setText("0");
                                            llEmptyCartList.setVisibility(View.VISIBLE);
                                            llFilledCart.setVisibility(View.GONE);
                                            tvCartProceed.setVisibility(View.GONE);

                                        } else {
                                            tvCartCount.setText(String.valueOf(reader.length()));
//                                            tvShippingCharges.setText(String.valueOf(reader.length() * 15));
                                            llFilledCart.setVisibility(View.VISIBLE);
                                            llEmptyCartList.setVisibility(View.GONE);
                                            tvCartProceed.setVisibility(View.VISIBLE);

                                            String sku = address.getString("sku");
                                            int qty = address.getInt("qty");
                                            String name = address.getString("name");
                                            String price = address.getString("price");
                                            String quote_id = address.getString("quote_id");
                                            String product_image = address.getString("product_image");

                                            String subTotal = String.valueOf(Float.parseFloat(price) * qty);
                                            orderTotal = orderTotal + Float.parseFloat(subTotal);

                                            ProdPojo user = new ProdPojo();
                                            user.setIda(item_id);
                                            user.setSku(sku);
                                            user.setQty(String.valueOf(qty));
                                            user.setName(name);
                                            user.setPrice(price);
                                            user.setQuoteID(quote_id);
                                            user.setSubTotal(subTotal);
                                            user.setImageValue(product_image);

                                            mUsers.add(user);

                                            mUserAdapter = new UserAdapter();
                                            rvCartList.setAdapter(mUserAdapter);

                                            tvCartSubTotal.setText(String.valueOf(orderTotal));
//                                            tvOrderTotla.setText(String.valueOf(orderTotal + (reader.length() * 15)));
                                            tvOrderTotla.setText(String.valueOf(orderTotal));

                                            if (String.valueOf(subTotal).equals("0")) {
                                                tvCartProceed.setClickable(false);
                                                Toast.makeText(getActivity(), "Something wen't wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                } else {
                                    llEmptyCartList.setVisibility(View.VISIBLE);
                                    llFilledCart.setVisibility(View.GONE);
                                    tvCartProceed.setVisibility(View.GONE);
//                                    Toast.makeText(getActivity(), "You not have any item in cart", Toast.LENGTH_SHORT).show();
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
//                params.put("token", "gqt18hvy6b1xg2sej4iicfl4hdqa33di");
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
        public TextView tvCartProdName, tvCartItemQty, tvCartItemDelete, tvCartItemPrice, tvCartItemSubTotal;
        public ImageView ivCardItemImage;
        public LinearLayout llDeleteCartItem;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvCartItemQty = (TextView) itemView.findViewById(R.id.tvCartItemQty);
            tvCartItemDelete = (TextView) itemView.findViewById(R.id.tvCartItemDelete);
            tvCartItemPrice = (TextView) itemView.findViewById(R.id.tvCartItemPrice);
            tvCartItemSubTotal = (TextView) itemView.findViewById(R.id.tvCartItemSubTotal);
            tvCartProdName = (TextView) itemView.findViewById(R.id.tvCartProdName);
            ivCardItemImage = (ImageView) itemView.findViewById(R.id.ivCardItemImage);
            llDeleteCartItem = (LinearLayout) itemView.findViewById(R.id.llDeleteCartItem);
        }
    }

    class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        public UserAdapter() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rvCartList.getLayoutManager();
            rvCartList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.cartlistitem, parent, false);
                return new UserViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserViewHolder) {
                final ProdPojo user = mUsers.get(position);
                final UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.tvCartItemQty.setText("  QTY " + user.getQty() + "  ");
                userViewHolder.tvCartProdName.setText(user.getName());
                userViewHolder.tvCartItemPrice.setText("₹: " + user.getPrice());
                userViewHolder.tvCartItemSubTotal.setText(user.getSubTotal());

                Picasso.get().load(commonVariables.imagePath + user.getImageValue()).into(userViewHolder.ivCardItemImage);

                userViewHolder.tvCartProdName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), user.getQuoteID(), Toast.LENGTH_SHORT).show();
                    }
                });

                userViewHolder.tvCartItemQty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.quantitylayout);
                        Window window = dialog.getWindow();
                        WindowManager.LayoutParams wlp = window.getAttributes();
                        wlp.width = WindowManager.LayoutParams.FILL_PARENT;

                        final EditText etQtyDialog = (EditText) dialog.findViewById(R.id.etQtyDialog);
                        Button btnSubmitDialog = (Button) dialog.findViewById(R.id.btnSubmitDialog);

                        btnSubmitDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!etQtyDialog.getText().toString().equals("")) {

                                    String itemValue = etQtyDialog.getText().toString();
                                    userViewHolder.tvCartItemQty.setText("  QTY " + itemValue + "  ");

                                    updateCart(user.getIda(), itemValue, user.getSku());

                                    dialog.dismiss();

                                } else {
                                    etQtyDialog.setError("Enter Quantity");
                                }
                            }
                        });

                        /*final ArrayList<String> v1 = new ArrayList<String>();

                        for (int i = 1; i <= 10; i++) {
                            v1.add(String.valueOf(i));
                        }
                        ListView ivQtyList = (ListView) dialog.findViewById(R.id.ivQtyList);

                        try {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.qtylistitem, v1);
                            ivQtyList.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ivQtyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {

                                // ListView Clicked item index
                                int itemPosition = position;

                                // ListView Clicked item value
                                String itemValue = (String) v1.get(position);
                                userViewHolder.tvCartItemQty.setText("  QTY " + itemValue + "  ");

                                updateCart(user.getIda(), itemValue, user.getSku());

                                dialog.dismiss();
                            }
                        });*/

                        dialog.show();
                    }
                });

                userViewHolder.llDeleteCartItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Are you sure to remove")
                                .setMessage("Are you sure?")
                                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteCart(user.getIda());

                                    }
                                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
        }
    }

    public void updateCart(final String itemID, final String qty, final String sku) {
        myDialog = commonVariables.showProgressDialog(getActivity(), "Updating ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/modify_cart.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("response", response);
                        try {
                            if (response.length() > 0) {

                                myDialog.dismiss();
                                getCartList();
                                /*Fragment someFragment = new FragCart();
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragDrower, someFragment); // give your fragment container id in first parameter
                                transaction.commit();*/

                            } else {
                                Toast.makeText(getActivity(), "Invalid Coupon", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
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
                params.put("token", token);
                params.put("item_id", itemID);
                params.put("quantity", qty);
                params.put("sku", sku);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new

                DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void deleteCart(final String itemID) {
        myDialog = commonVariables.showProgressDialog(getActivity(), "Updating ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/delete_from_cart.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("response", response);
                        try {
                            if (response.length() > 0) {

                                if (response.equals("true1")) {

                                    Toast.makeText(getActivity(), "Product remove successfully", Toast.LENGTH_SHORT).show();
                                    /*Fragment someFragment = new FragCart();
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragDrower, someFragment); // give your fragment container id in first parameter
                                    transaction.commit();*/
                                    myDialog.dismiss();
                                    getCartList();
                                } else {
                                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getActivity(), "Invalid Coupon", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
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
//                params.put("token", "gqt18hvy6b1xg2sej4iicfl4hdqa33di");
                params.put("token", token);
                params.put("item_id", itemID);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new

                DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void getGetCoupen(final String coupon) {
        myDialog = commonVariables.showProgressDialog(getActivity(), "Checking Coupen ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/apply_coupons.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("response", response);
                        try {
                            if (response.length() > 0) {

                                JSONObject coupon = new JSONObject(response);

                                String message = coupon.getString("message");
                                Log.i("Coupon", message);

                                String amt = tvOrderTotla.getText().toString();
                                int finalAmt = Integer.parseInt(amt);
                                tvOrderTotla.setText(String.valueOf(finalAmt));
                                tvCoupenCode.setText("0");
                                tvCouponCodeTitle.setText("Coupen Code (Applied :");

                            } else {
                                Toast.makeText(getActivity(), "Invalid Coupon", Toast.LENGTH_SHORT).show();
                            }

                        } catch (
                                JSONException e) {
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
                params.put("coupon", coupon);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new

                DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}