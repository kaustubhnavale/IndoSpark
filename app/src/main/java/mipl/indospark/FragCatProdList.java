package mipl.indospark;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragCatProdList extends Fragment {

    StringRequest stringRequest;
    ProgressDialog myDialog;
    private List<ProdPojo> mUsers = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;
    LinearLayout llEmptyCategory;

    String price;
    String catID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_frag_cat_prod_list, container, false);

        llEmptyCategory = (LinearLayout) v.findViewById(R.id.llEmptyCategory);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Bundle bundle = this.getArguments();

        if(bundle != null){
            // handle your code here.
            catID =getArguments().getString("catID");
        }

        if (CheckNetwork.isInternetAvailable(getActivity())) {
            getAllProd();
        } else {
            Toast.makeText(getActivity(), "Internet Connection not available", Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    public void getAllProd() {

        myDialog = commonVariables.showProgressDialog(getActivity(), "Getting Products ...");

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

                                int total_count = Integer.parseInt(reader.getString("total_count"));
                                if (total_count > 0) {

                                    llEmptyCategory.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);

                                    for (int j = 0; j < items.length(); j++) {
                                        JSONObject curr = items.getJSONObject(j);

                                        String status = curr.getString("status");
                                        if (status.equals("1")) {

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
                                    }

                                    if (mUsers.size() > 0) {
                                        mUserAdapter = new UserAdapter();
                                        mRecyclerView.setAdapter(mUserAdapter);
                                    } else {
                                        llEmptyCategory.setVisibility(View.VISIBLE);
                                        mRecyclerView.setVisibility(View.GONE);
                                    }

                                } else {
                                    llEmptyCategory.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.GONE);
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
                params.put("cat_id", catID);
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
        public TextView tvCardName, tvCardPrice;
        public ImageView ivCardImage;
        public WebView tvCardDesc;
        public LinearLayout llCard;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvCardName = (TextView) itemView.findViewById(R.id.tvCardName);
            tvCardDesc = (WebView) itemView.findViewById(R.id.tvCardDesc);
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
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.carallprod, parent, false);
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
                userViewHolder.tvCardPrice.setText("₹ " + user.getPrice());

                final String mimeType = "text/html";
                final String encoding = "UTF-8";

                String html = "<!DOCTYPE html><head><style>@font-face{font-family: 'arial';src: url('file:///file:///android_asset/fonts/arial.ttf');}body{font-family: 'arial';font-size:14px}</style></head>"
                        + "<body ><font color='#000000'>"
                        + user.getShort_desc() + "</font></body></html>";
                userViewHolder.tvCardDesc.loadData(html, mimeType, encoding);

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

                            android.support.v4.app.FragmentManager fm = ((FragmentActivity)getActivity()).getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                            if (fm.findFragmentById(R.id.fragDrower) != null) {
                                ft.hide(fm.findFragmentById(R.id.fragDrower));
                            }
                            ft.add(R.id.fragDrower, fragobj,FragProdDesc.class.getCanonicalName())
                                    .addToBackStack(FragProdDesc.class.getCanonicalName()).commit();

                        } else {
                            Toast.makeText(getActivity(), "Something wen't wrong", Toast.LENGTH_SHORT).show();
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