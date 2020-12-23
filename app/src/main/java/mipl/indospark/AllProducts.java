package mipl.indospark;

import android.app.ProgressDialog;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public class AllProducts extends AppCompatActivity {

    StringRequest stringRequest;
    ProgressDialog myDialog;
    private List<ProdPojo> mUsers = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;

    String price;
    String catID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        catID = getIntent().getStringExtra("catID");

        if (CheckNetwork.isInternetAvailable(this)) {
            getAllProd();
        } else {
            Toast.makeText(this, "Internet Connection not available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getAllProd() {

        myDialog = commonVariables.showProgressDialog(AllProducts.this, "Getting Products ...");

        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/get_categories_products.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.length() > 0) {
                            try {

                                String imageValue = null;
                                String short_desc = null;

                                JSONObject reader = new JSONObject(response);
                                JSONArray items = reader.getJSONArray("items");

                                for (int j = 0; j < items.length(); j++) {
                                    JSONObject curr = items.getJSONObject(j);

                                    String ida = curr.getString("id");
                                    String sku = curr.getString("sku");
                                    String name = curr.getString("name");
                                    price = curr.getString("price");

                                    JSONArray custom_attributes = curr.getJSONArray("custom_attributes");

                                    for (int i = 0; i < custom_attributes.length(); i++) {
                                        JSONObject attribObj = custom_attributes.getJSONObject(i);

                                        if (attribObj.getString("attribute_code").equals("image")) {
                                            imageValue = attribObj.getString("value");
                                        }

                                        if (attribObj.getString("attribute_code").equals("short_description")) {
                                            short_desc = attribObj.getString("value");
                                        }
                                        if (attribObj.getString("attribute_code").equals("special_price")) {
                                            price = attribObj.getString("value");
                                        }
                                    }

                                    ProdPojo user = new ProdPojo();
                                    user.setName(name);
                                    user.setIda(ida);
                                    user.setSku(sku);
                                    user.setPrice(price);
                                    user.setShort_desc(short_desc);
                                    user.setImageValue(commonVariables.imagePath + "" + imageValue);

                                    mUsers.add(user);
                                }

                                mUserAdapter = new UserAdapter();
                                mRecyclerView.setAdapter(mUserAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        myDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(AllProducts.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                myDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cat_id", catID);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(AllProducts.this);
        requestQueue.add(stringRequest);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCardName, tvCardDesc, tvCardPrice;
        public ImageView ivCardImage;
        public LinearLayout llCard;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvCardName = (TextView) itemView.findViewById(R.id.tvCardName);
//            tvCardDesc = (TextView) itemView.findViewById(R.id.tvCardDesc);
            tvCardPrice = (TextView) itemView.findViewById(R.id.tvCardPrice);
            ivCardImage = (ImageView) itemView.findViewById(R.id.ivCardImage);
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
                View view = LayoutInflater.from(AllProducts.this).inflate(R.layout.carallprod, parent, false);
                return new UserViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserViewHolder) {
                final ProdPojo user = mUsers.get(position);
                UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.tvCardName.setText(user.getName());
//                userViewHolder.tvCardDesc.setText(user.getShort_desc());
                userViewHolder.tvCardPrice.setText("₹ " + user.getPrice());

                Picasso.get().load(user.getImageValue()).into(userViewHolder.ivCardImage);

                userViewHolder.llCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!(user.getSku() == null || user.getSku().equals(""))) {

                            Bundle bundle = new Bundle();
                            bundle.putString("SKU", user.getSku());
                            //set Fragmentclass Arguments
                            FragProdDesc fragobj = new FragProdDesc();
                            fragobj.setArguments(bundle);

                            getSupportFragmentManager().beginTransaction().replace(R.id.fragDrower, fragobj, "SOMETAG").addToBackStack("Indo").commit();

                        } else {
                            Toast.makeText(AllProducts.this, "Something wen't wrong.", Toast.LENGTH_SHORT).show();
                        }
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