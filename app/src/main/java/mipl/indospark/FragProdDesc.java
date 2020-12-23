package mipl.indospark;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
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
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragProdDesc extends Fragment {

    StringRequest stringRequest;
    CustomPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;
    List<String> myImageList;
    List<String> myPriseList;
    HashMap<String, String> h1;
    private List<AddressPojo> mUsers;

    TextView prodTitle, prodPrise, prodSpePrise, prodQty, tvPostalCode, tvDeliveryStatus;
    TextView tvAddToCart, tvBuy, tvAddReview;
    WebView wvProdDesc, wvProdShortDesc;
    LinearLayout llDeliverTo;

    ImageView imageView, text;
    ProgressDialog dialog, myDialog;
    Dialog dialogView;

    final String mimeType = "text/html";
    final String encoding = "UTF-8";

    ArrayList<String> v1;
    String itemValue = "1";
    String price, spe_price, id, name, prodstatus;
    String sku1, sku;

    SharedPreferences sharedpreferences;
    String token, customer_id;
    RecyclerView mRecyclerView;
    UserAdapter mUserAdapter;
    TextView tvDeliveryStatusdialog;

    RecyclerView rvShowReview;
    TextView tvNoRevoewText;
    private List<ProdPojo> reviewList = new ArrayList<>();
    UserAdapter1 mReviewAdapter;
    PageIndicatorView pageIndicatorView;

    public FragProdDesc() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_frag_prod_desc, container, false);

        mUsers = new ArrayList<>();

        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(commonVariables.token, "");
        customer_id = sharedpreferences.getString(commonVariables.customer_id, "");

        prodTitle = (TextView) v.findViewById(R.id.prodTitle);
        prodPrise = (TextView) v.findViewById(R.id.prodPrise);
        prodSpePrise = (TextView) v.findViewById(R.id.prodSpePrise);
        prodQty = (TextView) v.findViewById(R.id.prodQty);
        tvPostalCode = (TextView) v.findViewById(R.id.tvPostalCode);
        tvDeliveryStatus = (TextView) v.findViewById(R.id.tvDeliveryStatus);
        tvAddToCart = (TextView) v.findViewById(R.id.tvAddToCart);
        tvBuy = (TextView) v.findViewById(R.id.tvBuy);
        tvAddReview = (TextView) v.findViewById(R.id.tvAddReview);
        wvProdDesc = (WebView) v.findViewById(R.id.wvProdDesc);
        wvProdShortDesc = (WebView) v.findViewById(R.id.wvProdShortDesc);
        llDeliverTo = (LinearLayout) v.findViewById(R.id.llDeliverTo);

        mViewPager = (ViewPager) v.findViewById(R.id.pager);

        rvShowReview = (RecyclerView) v.findViewById(R.id.rvShowReview);
        tvNoRevoewText = (TextView) v.findViewById(R.id.tvNoRevoewText);

        pageIndicatorView = (PageIndicatorView) v.findViewById(R.id.pageIndicatorView);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sku = getArguments().getString("SKU");
        }

        myImageList = new ArrayList<String>();
        myPriseList = new ArrayList<String>();
        h1 = new HashMap<String, String>();

        if (CheckNetwork.isInternetAvailable(getActivity())) {
            getProdDesc(sku);
        } else {
            Toast.makeText(getActivity(), "Internet Connection not available", Toast.LENGTH_SHORT).show();
        }

        mCustomPagerAdapter = new CustomPagerAdapter(getActivity());

        prodQty.setText("  QTY " + itemValue + "  ");

        prodQty.setOnClickListener(new View.OnClickListener() {
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
                        String itemValue = etQtyDialog.getText().toString();

                        try {
                            if (!itemValue.equals("")) {
                                if (!(Integer.parseInt(itemValue) == 0)) {

                                    prodQty.setText(" QTY " + itemValue.trim() + " ");
                                    String key = null, value = null;

                                    if (h1.size() > 0) {
                                        for (Map.Entry<String, String> entry : h1.entrySet()) {
                                            key = entry.getKey();
                                            value = entry.getValue();

                                            if (key.equals(itemValue)) {
                                                prodSpePrise.setText("₹ " + value);
                                                break;
                                            } else {
                                                int totalPrice = Math.round(Float.parseFloat(spe_price)) * Integer.parseInt(itemValue);
                                                prodSpePrise.setText("₹ " + Math.round(totalPrice));
                                            }
                                        }
                                    } else {
                                        int totalPrice =  Math.round(Float.parseFloat(spe_price)) * Integer.parseInt(itemValue);
                                        prodSpePrise.setText("₹ " + Math.round(totalPrice));
                                    }

                                    dialog.dismiss();
                                } else {
                                    etQtyDialog.setError("Minimum quantity is 1");
                                }

                            } else {
                                etQtyDialog.setError("Enter Quantity");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                /*v1 = new ArrayList<String>();

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
                        itemValue = (String) v1.get(position);

                        prodQty.setText(" QTY " + itemValue + " ");
                        dialog.dismiss();

                        String key = null, value = null;

                        if (h1.size() > 0) {
                            for (Map.Entry<String, String> entry : h1.entrySet()) {
                                key = entry.getKey();
                                value = entry.getValue();

                                if (key.equals(itemValue)) {
                                    prodPrise.setText("₹ " + value);
                                    break;
                                } else {
                                    int totalPrice = Integer.parseInt(price) * Integer.parseInt(itemValue);
                                    prodPrise.setText("₹ " + totalPrice);
                                }
                            }
                        } else {
                            int totalPrice = Integer.parseInt(price) * Integer.parseInt(itemValue);
                            prodPrise.setText("₹ " + totalPrice);
                        }
                    }
                });*/

                dialog.show();
            }
        });

        tvAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.add_review_dialog);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
//        wlp.gravity = Gravity.TOP;
                wlp.width = WindowManager.LayoutParams.FILL_PARENT;

                final EditText etReviewName = (EditText) dialog.findViewById(R.id.etReviewName);
                final EditText etReviewTitle = (EditText) dialog.findViewById(R.id.etReviewTitle);
                final EditText etReviewDetail = (EditText) dialog.findViewById(R.id.etReviewDetail);
                final TextView tvProdName = (TextView) dialog.findViewById(R.id.tvProdName);
                tvProdName.setText(name);
                Button btnSubmitDialogReview = (Button) dialog.findViewById(R.id.btnSubmitDialogReview);

                btnSubmitDialogReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!etReviewName.getText().toString().equals("")) {
                            if (!etReviewTitle.getText().toString().equals("")) {
                                if (!etReviewDetail.getText().toString().equals("")) {

                                    if (CheckNetwork.isInternetAvailable(getActivity())) {
                                        sendReview(etReviewName.getText().toString(), etReviewTitle.getText().toString(), etReviewDetail.getText().toString());
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), "Internet Connection not available", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    etReviewDetail.setError("Enter Details");
                                }
                            } else {
                                etReviewTitle.setError("Enter Title");
                            }
                        } else {
                            etReviewName.setError("Enter Name");
                        }
                    }
                });
                dialog.show();
            }
        });

        tvAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!sku1.equals("")) {
                    if (CheckNetwork.isInternetAvailable(getActivity())) {
                        addInCart("Cart");
                    } else {
                        Toast.makeText(getActivity(), "Network not available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!sku1.equals("")) {
                    if (CheckNetwork.isInternetAvailable(getActivity())) {

                        addInCart("Buy");
                    } else {
                        Toast.makeText(getActivity(), "Network not available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tvPostalCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    dialogView = new Dialog(getActivity());
                    dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogView.setContentView(R.layout.set_delivey_pincode);
                    Window window = dialogView.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();
                    wlp.gravity = Gravity.BOTTOM;
                    wlp.width = WindowManager.LayoutParams.FILL_PARENT;

                    LinearLayoutManager linearLayoutManager
                            = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    mRecyclerView = (RecyclerView) dialogView.findViewById(R.id.rvAddListSetforPINCODE);
                    mRecyclerView.setNestedScrollingEnabled(false);
                    mRecyclerView.setLayoutManager(linearLayoutManager);

                    if (mUsers.size() > 0) {

                        mUserAdapter = new UserAdapter();
                        mRecyclerView.setAdapter(mUserAdapter);
                    }

                    final EditText etEnterPinCode = (EditText) dialogView.findViewById(R.id.etEnterPinCode);
                    LinearLayout llApplyPincode = (LinearLayout) dialogView.findViewById(R.id.llApplyPincode);
                    tvDeliveryStatusdialog = (TextView) dialogView.findViewById(R.id.tvDeliveryStatus);

                    llApplyPincode.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (etEnterPinCode.getText().toString().length() == 6) {
                                getDeliveryStatus(etEnterPinCode.getText().toString());
                                tvPostalCode.setText("Deliver To - " + etEnterPinCode.getText().toString());

                            } else {
                                etEnterPinCode.setError("Enter valid Pincode");
                                etEnterPinCode.setText("");
                            }
                        }
                    });

                    dialogView.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
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

                        String shipping_postcode = user.getPostcode();
                        getDeliveryStatus(user.getPostcode());
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
        }
    }


    public void getProdDesc(String sku) {
        dialog = commonVariables.showProgressDialog(getActivity(), "Loading ...");

        stringRequest = new StringRequest(Request.Method.GET, "https://shop.indospark.com/index.php/rest/V1/products/" + sku,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.length() > 0) {
                            try {
                                JSONObject reader = new JSONObject(response);

                                id = reader.getString("id");
                                sku1 = reader.getString("sku").trim();
                                name = reader.getString("name");
                                prodstatus = reader.getString("status");
                                prodTitle.setText(name);

                                if (prodstatus.equals("2")) {
                                    tvAddToCart.setClickable(false);
                                    tvBuy.setClickable(false);

                                    tvBuy.setAlpha((float) 0.1);
                                    tvAddToCart.setAlpha((float) 0.1);

                                    tvDeliveryStatus.setText("Out of stock");
                                    tvDeliveryStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.themecolor));
                                } else {
                                    getDefaultPINCODE();
                                }

                                getReview();

                                String attribute_set_id = reader.getString("attribute_set_id");

                                price = reader.getString("price");
                                prodPrise.setText("₹ " + price);

                                String status = reader.getString("status");
                                String visibility = reader.getString("visibility");
                                String type_id = reader.getString("type_id");
                                String created_at = reader.getString("created_at");
                                String updated_at = reader.getString("updated_at");

                                JSONArray media_gallery_entries = reader.getJSONArray("media_gallery_entries");

                                for (int j = 0; j < media_gallery_entries.length(); j++) {
                                    JSONObject curr = media_gallery_entries.getJSONObject(j);

                                    String ida = curr.getString("id");
                                    String media_type = curr.getString("media_type");
                                    String label = curr.getString("label");
                                    String position = curr.getString("position");
                                    String disabled = curr.getString("disabled");
                                    String file = curr.getString("file");

                                    myImageList.add(commonVariables.imagePath + "" + file);
                                }

                                JSONArray custom_attributes = reader.getJSONArray("custom_attributes");

                                for (int j = 0; j < custom_attributes.length(); j++) {
                                    JSONObject curr = custom_attributes.getJSONObject(j);

                                    String attribute_code = curr.getString("attribute_code");
                                    String value = curr.getString("value");

                                    if (curr.getString("attribute_code").equals("description")) {
                                        String descValue = curr.getString("value");

                                        String html = "<!DOCTYPE html><head><style>@font-face{font-family: 'arial';src: url('file:///file:///android_asset/fonts/arial.ttf');}body{font-family: 'arial';font-size:14px}</style></head>"
                                                + "<body ><font color='#000000'>"
                                                + descValue + "</font></body></html>";
                                        wvProdDesc.loadData(html, mimeType, encoding);
                                    }

                                    if (curr.getString("attribute_code").equals("short_description")) {
                                        String descValue = curr.getString("value");

                                        String html = "<!DOCTYPE html><head><style>@font-face{font-family: 'arial';src: url('file:///file:///android_asset/fonts/arial.ttf');}body{font-family: 'arial';font-size:14px}</style></head>"
                                                + "<body ><font color='#000000'>"
                                                + descValue + "</font></body></html>";
                                        wvProdShortDesc.loadData(html, mimeType, encoding);
                                    }

                                    if (curr.getString("attribute_code").equals("special_price")) {
                                        spe_price = curr.getString("value");

                                        if (price.equals(spe_price)) {
                                            prodSpePrise.setText("₹ " + price);
                                        } else {
                                            prodPrise.setVisibility(View.VISIBLE);
                                            prodPrise.setText("₹ " + price);
                                            prodSpePrise.setText("  ₹ " + spe_price);
                                            prodPrise.setPaintFlags(prodPrise.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                        }
                                    }
                                }

                                JSONArray tier_prices = reader.getJSONArray("tier_prices");

                                for (int j = 0; j < tier_prices.length(); j++) {
                                    JSONObject curr = tier_prices.getJSONObject(j);

                                    String customer_group_id = curr.getString("customer_group_id");
                                    String qty = curr.getString("qty");
                                    String value = curr.getString("value");

                                    h1.put(qty, value);
                                }

                                mViewPager.setAdapter(mCustomPagerAdapter);
                                pageIndicatorView.setViewPager(mViewPager);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), "Product not available", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public class CustomPagerAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return myImageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            Picasso.get().load(myImageList.get(position)).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog((Activity) mContext, myImageList.get(position));
                }
            });

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    public void showDialog(Activity activity, String msg) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.imgpinchdialog);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.FILL_PARENT;

        text = (ImageView) dialog.findViewById(R.id.ivPinch);
        Picasso.get().load(msg).into(text);

        text.setOnTouchListener(new ImageMatrixTouchHandler(activity));

        ImageView inDialogClose = (ImageView) dialog.findViewById(R.id.inDialogClose);
        inDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void getDefaultPINCODE() {

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/get_all_address.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.length() > 0) {
                            try {

                                String default_billing = null, default_shipping = null;

                                JSONObject reader = new JSONObject(response);
                                String group_id = reader.getString("group_id");
                                String email = reader.getString("email");

                                if (!(reader.has("default_shipping"))){
                                    llDeliverTo.setVisibility(View.GONE);
                                }

                                JSONArray items = reader.getJSONArray("addresses");

                                if (items.length() > 0) {

                                    for (int i = 0; i < items.length(); i++) {
                                        JSONObject address = items.getJSONObject(i);

                                        try {
                                            default_shipping = address.getString("default_shipping");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            default_shipping = "false";
                                        }

                                        if (default_shipping.equals("true")) {

                                            String postcode = address.getString("postcode");
                                            tvPostalCode.setText("Deliver To - " + postcode);
                                            getDeliveryStatus(postcode);
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
                                    }
                                } else {
                                    llDeliverTo.setVisibility(View.GONE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                llDeliverTo.setVisibility(View.GONE);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void getDeliveryStatus(final String postcode) {

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/check_delivery.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject reader = null;
                        try {
                            reader = new JSONObject(response);

                            String status = reader.getString("status");
                            if (status.equals("200")) {

                                tvDeliveryStatus.setText("Instock");
                                tvDeliveryStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                                try {
                                    tvDeliveryStatusdialog.setText("We deliver product to this address.");
                                    tvDeliveryStatusdialog.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                                    tvAddToCart.setClickable(true);
                                    tvBuy.setClickable(true);

                                    tvBuy.setAlpha((float) 1);
                                    tvAddToCart.setAlpha((float) 1);

                                    tvDeliveryStatus.setText("Instock");
                                    tvDeliveryStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (status.equals("500")) {
                                tvDeliveryStatus.setText("Out of stock");
                                tvDeliveryStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.themecolor));

                                tvAddToCart.setClickable(false);
                                tvBuy.setClickable(false);

                                tvBuy.setAlpha((float) 0.1);
                                tvAddToCart.setAlpha((float) 0.1);

                                try {
                                    tvDeliveryStatusdialog.setText("Sorry, we don't deliver product to this address.");
                                    tvDeliveryStatusdialog.setTextColor(ContextCompat.getColor(getActivity(), R.color.themecolor));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pincode", postcode);
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

    public void addInCart(final String page) {
        myDialog = commonVariables.showProgressDialog(getActivity(), "Updating ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/add_to_cart.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            if (page.equals("Cart")) {
                                if (response.length() > 0) {

                                    if (response.contains("item_id")) {

                                        Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_SHORT).show();
                                        ((Drower) getActivity()).getCartCount();
                                    } else if (response.contains("message")) {
                                        JSONObject reader = null;
                                        try {
                                            reader = new JSONObject(response);

                                            String message = reader.getString("message");
                                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } else if (page.equals("Buy")) {
                                if (response.length() > 0) {

                                    if (response.contains("item_id")) {
                                        myDialog.dismiss();
                                        ((Drower) getActivity()).getCartCount();
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragDrower, new FragCart(), "SOMETAG").addToBackStack("Indo").commit();

                                    } else if (response.contains("message")) {
                                        JSONObject reader = null;
                                        try {
                                            reader = new JSONObject(response);

                                            String message = reader.getString("message");
                                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
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
                params.put("token", token);
                params.put("quantity", itemValue);
                params.put("sku", sku1);
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

    public void sendReview(final String name, final String title, final String detail) {
        myDialog = commonVariables.showProgressDialog(getActivity(), "Uploading ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/add_product_review.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject reader = null;
                        try {
                            reader = new JSONObject(response);

                            String status = reader.getString("status");
                            String message = reader.getString("message");

                            if (status.equals("500")) {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            } else if (status.equals("200")) {
                                Toast.makeText(getActivity(), "You submitted your review for moderation.", Toast.LENGTH_SHORT).show();
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
                params.put("customer_id", customer_id);
                params.put("nickname", name);
                params.put("title", title);
                params.put("detail", detail);
                params.put("product_id", id);
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


    public void getReview() {
        reviewList.clear();

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/get_product_review.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray reader = new JSONArray(response);
                            for (int j = 0; j < reader.length(); j++) {
                                JSONObject curr = reader.getJSONObject(j);

                                String title = curr.getString("title");
                                String detail = curr.getString("detail");
                                String nickname = curr.getString("nickname");

                                ProdPojo user = new ProdPojo();
                                user.setName(nickname);
                                user.setTitle(title);
                                user.setDetail(detail);

                                reviewList.add(user);
                            }

                            if (reviewList.size() > 0) {
                                mReviewAdapter = new UserAdapter1();
                                rvShowReview.setAdapter(mReviewAdapter);
                                rvShowReview.setLayoutManager(new LinearLayoutManager(getActivity()));
                            } else {
                                tvNoRevoewText.setVisibility(View.VISIBLE);
                                rvShowReview.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("product_id", id);
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

    static class UserViewHolder1 extends RecyclerView.ViewHolder {
        public TextView tvNickName, tvTitle, tvDetail;

        public UserViewHolder1(View itemView) {
            super(itemView);
            tvNickName = (TextView) itemView.findViewById(R.id.tvNickName);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDetail = (TextView) itemView.findViewById(R.id.tvDetail);
        }
    }

    class UserAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        public UserAdapter1() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rvShowReview.getLayoutManager();
            rvShowReview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return reviewList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.shoereview, parent, false);
                return new UserViewHolder1(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserViewHolder1) {
                final ProdPojo user = reviewList.get(position);
                final UserViewHolder1 userViewHolder = (UserViewHolder1) holder;
                userViewHolder.tvNickName.setText(user.getName());
                userViewHolder.tvTitle.setText(user.getTitle());
                userViewHolder.tvDetail.setText(user.getDetail());
            }
        }

        @Override
        public int getItemCount() {
            return reviewList == null ? 0 : reviewList.size();
        }
    }
}