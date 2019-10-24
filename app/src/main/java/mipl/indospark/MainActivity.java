package mipl.indospark;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    StringRequest stringRequest;
    CustomPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;
    List<String> myImageList;
    List<String> myPriseList;
    HashMap<String, String> h1;

    TextView prodTitle, prodPrise, prodQty, tvPostalCode, tvDeliveryStatus;
    TextView tvAddToCart;
    WebView wvProdDesc, wvProdShortDesc;

    ImageView imageView, text;
    ProgressDialog dialog, myDialog;

    final String mimeType = "text/html";
    final String encoding = "UTF-8";

    List<String> listItems;
    ArrayList<String> v1;
    String itemValue = "1";
    String price;
    String sku1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listItems = new ArrayList<String>();

        prodTitle = (TextView) findViewById(R.id.prodTitle);
        prodPrise = (TextView) findViewById(R.id.prodPrise);
        prodQty = (TextView) findViewById(R.id.prodQty);
        tvPostalCode = (TextView) findViewById(R.id.tvPostalCode);
        tvDeliveryStatus = (TextView) findViewById(R.id.tvDeliveryStatus);
        tvAddToCart = (TextView) findViewById(R.id.tvAddToCart);
        wvProdDesc = (WebView) findViewById(R.id.wvProdDesc);
        wvProdShortDesc = (WebView) findViewById(R.id.wvProdShortDesc);

        String sku = getIntent().getStringExtra("SKU");

        myImageList = new ArrayList<String>();
        myPriseList = new ArrayList<String>();
        h1 = new HashMap<String, String>();

        if (CheckNetwork.isInternetAvailable(this)) {
            getDefaultPINCODE();
            getProdDesc(sku);
        } else {
            Toast.makeText(this, "Internet Connection not available", Toast.LENGTH_SHORT).show();
        }

        mCustomPagerAdapter = new CustomPagerAdapter(this);

        prodQty.setText("  QTY " + itemValue + "  ");

        prodQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.quantitylayout);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
//        wlp.gravity = Gravity.TOP;
                wlp.width = WindowManager.LayoutParams.FILL_PARENT;

                v1 = new ArrayList<String>();

                for (int i = 1; i <= 10; i++) {
                    v1.add(String.valueOf(i));
                }
                ListView ivQtyList = (ListView) dialog.findViewById(R.id.ivQtyList);

                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.qtylistitem, v1);
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
                });

                dialog.show();
            }
        });

        tvAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!sku1.equals("")) {
                    if (CheckNetwork.isInternetAvailable(MainActivity.this)) {

                        addInCart();
                    } else {
                        Toast.makeText(MainActivity.this, "Network not available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void getProdDesc(String sku) {
        dialog = commonVariables.showProgressDialog(this, "Loading ...");

        stringRequest = new StringRequest(Request.Method.GET, "https://shop.indospark.com/index.php/rest/V1/products/" + sku,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.length() > 0) {
                            try {
                                JSONObject reader = new JSONObject(response);

                                String id = reader.getString("id");
                                sku1 = reader.getString("sku");
                                String name = reader.getString("name");
                                prodTitle.setText(name);

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
                                        String spe_price = curr.getString("value");
                                        prodPrise.setText("₹ " + spe_price);
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

                                mViewPager = (ViewPager) findViewById(R.id.pager);
                                mViewPager.setAdapter(mCustomPagerAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
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
//        wlp.gravity = Gravity.TOP;
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
                                JSONArray items = reader.getJSONArray("addresses");

                                if (items.length() > 0) {

                                    for (int i = 0; i < items.length(); i++) {
                                        JSONObject address = items.getJSONObject(i);

                                        try {
                                            default_shipping = address.getString("default_billing");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            default_shipping = "false";
                                        }

                                        if (default_shipping.equals("true")) {

                                            String postcode = address.getString("postcode");
                                            tvPostalCode.setText("Deliver To - " + postcode);
                                            getDeliveryStatus(postcode);
                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "gqt18hvy6b1xg2sej4iicfl4hdqa33di");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
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
                                tvDeliveryStatus.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));

                            } else if (status.equals("500")) {
                                tvDeliveryStatus.setText("Out of stock");
                                tvDeliveryStatus.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.themecolor));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }


    public void addInCart() {
        myDialog = commonVariables.showProgressDialog(MainActivity.this, "Updating ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/add_to_cart.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            if (response.length() > 0) {

                                myDialog.dismiss();

                            } else {
                                Toast.makeText(MainActivity.this, "Invalid Coupon", Toast.LENGTH_SHORT).show();
                            }

                        } catch (
                                Exception e) {
                            e.printStackTrace();
                        }

                        myDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                myDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "gqt18hvy6b1xg2sej4iicfl4hdqa33di");
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

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }
}