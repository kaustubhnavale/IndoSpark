package mipl.indospark;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
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
import java.util.List;

public class FragHome extends Fragment {

    StringRequest stringRequest;
    RecyclerView my_recycler_view;
    ProgressDialog myDialog;
    ArrayList<ProdPojo> allSampleData;
    RelativeLayout rlSearch;
    UserAdapter mUserAdapter;
    RecyclerView mRecyclerView;
    LinearLayout llNoInternet;
    private List<ProdPojo> mUsers = new ArrayList<>();
    Dialog toolbarSearchDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_home, container, false);

        toolbarSearchDialog = new Dialog(getActivity());
        allSampleData = new ArrayList<ProdPojo>();

        rlSearch = (RelativeLayout) v.findViewById(R.id.rlSearch);
        llNoInternet = (LinearLayout) v.findViewById(R.id.llNoInternet);

        my_recycler_view = (RecyclerView) v.findViewById(R.id.my_recycler_view1);
        my_recycler_view.setHasFixedSize(true);

        if (CheckNetwork.isInternetAvailable(getActivity())) {
            getCategoryandProduct();
        } else {
            Toast.makeText(getActivity(), "Internet connection not available", Toast.LENGTH_SHORT).show();
            llNoInternet.setVisibility(View.VISIBLE);
            my_recycler_view.setVisibility(View.GONE);
        }

        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadToolBarSearch();
            }
        });

        return v;
    }

    public void getCategoryandProduct() {

        myDialog = commonVariables.showProgressDialog(getActivity(), "Loading ...");

        stringRequest = new StringRequest(Request.Method.GET, "https://shop.indospark.com/android_api/get_homepage_products.php",
//        stringRequest = new StringRequest(Request.Method.GET, "http://shop.indospark.com/android_api/test_homeproducts.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // process your response here
                        String id, name, sku, price, prodName, img = null;

                        Log.i("Desc JSON", response);
                        RecyclerViewDataAdapter adapter = null;

                        if (response.length() > 0) {
                            try {

                                JSONArray reader = new JSONArray(response);

                                for (int j = 0; j < reader.length(); j++) {

                                    ArrayList<ProdPojo> singleItem = new ArrayList<ProdPojo>();
                                    ProdPojo dm = new ProdPojo();

                                    JSONObject curr = reader.getJSONObject(j);

                                    id = curr.getString("id");
                                    name = curr.getString("cat_name");

                                    JSONObject subProducts = curr.getJSONObject("products");

//                                    String total_count = subProducts.getString("total_count");
//                                    if (!total_count.equals("0")) {
                                    dm.setName(name);             // Category item name
                                    dm.setIda(id);
//                                        dm.setCount(total_count);

                                    JSONArray subProdArray = subProducts.getJSONArray("items");

                                    for (int i = 0; i < subProdArray.length(); i++) {

                                        JSONObject subProdObj = subProdArray.getJSONObject(i);

                                        String status = subProdObj.getString("status");

                                        if (status.equals("1")) {
                                            sku = subProdObj.getString("sku");
                                            price = subProdObj.getString("price");
                                            prodName = subProdObj.getString("name");
                                            prodName = prodName.substring(0, 30);
                                            prodName = prodName + "...";

                                            JSONArray custom_attributes = subProdObj.getJSONArray("custom_attributes");

                                            for (int k = 0; k < custom_attributes.length(); k++) {
                                                JSONObject attribObj = custom_attributes.getJSONObject(k);

                                                if (attribObj.getString("attribute_code").equals("image")) {
                                                    img = attribObj.getString("value");
                                                    Log.i("img", img);
                                                }
                                            }
                                            singleItem.add(new ProdPojo(prodName, sku, price, commonVariables.imagePath + img));
                                        }
                                    }

                                    dm.setAllItemsInSection(singleItem);
                                    allSampleData.add(dm);
//                                    }
                                }

                                adapter = new RecyclerViewDataAdapter(getActivity(), allSampleData);
                                my_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                my_recycler_view.setAdapter(adapter);

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
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void loadToolBarSearch() {

        View view = getActivity().getLayoutInflater().inflate(R.layout.view_toolbar_search, null);
        ImageView imgToolBack = (ImageView) view.findViewById(R.id.img_tool_back);
        final EditText edtToolSearch = (EditText) view.findViewById(R.id.edt_tool_search);
        ImageView imgToolMic = (ImageView) view.findViewById(R.id.img_tool_mic);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view_Search);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        edtToolSearch.setHint("Search");

        toolbarSearchDialog.setContentView(view);
        toolbarSearchDialog.setCancelable(true);
        toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
        toolbarSearchDialog.show();

        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        edtToolSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (edtToolSearch.length() >= 2) {

                    if (CheckNetwork.isInternetAvailable(getActivity())) {
                        searchProduct(edtToolSearch.getText().toString());
                    } else {
                        Toast.makeText(getActivity(), "Internet connection not available", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imgToolBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarSearchDialog.dismiss();
            }
        });

        imgToolMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtToolSearch.setText("");
            }
        });

        toolbarSearchDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                toolbarSearchDialog.dismiss();
            }
        });
    }

    public void searchProduct(String name) {

        stringRequest = new StringRequest(Request.Method.GET, "https://shop.indospark.com/index.php/rest/V1/products/?" +
                "searchCriteria[filter_groups][0][filters][0][field]=name&searchCriteria[filter_groups][0][filters][0]" +
                "[value]=%" + name + "%&searchCriteria[filter_groups][0][filters][0][condition_type]=like",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        mUsers.clear();
                        Log.i("Desc JSON", response);
                        try {
                            if (response.length() > 0) {
                                String imageValue = null, short_desc = null;

                                JSONObject reader = new JSONObject(response);
                                JSONArray items = reader.getJSONArray("items");

                                for (int j = 0; j < items.length(); j++) {
                                    JSONObject curr = items.getJSONObject(j);

                                    String ida = curr.getString("id");
                                    String sku = curr.getString("sku");
                                    String name = curr.getString("name");
                                    String price = curr.getString("price");

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
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
                userViewHolder.tvCardPrice.setText("₹: " + user.getPrice());

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

                            android.support.v4.app.FragmentManager fm = ((FragmentActivity) getActivity()).getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                            if (fm.findFragmentById(R.id.fragDrower) != null) {
                                ft.hide(fm.findFragmentById(R.id.fragDrower));
                            }
                            ft.add(R.id.fragDrower, fragobj, FragProdDesc.class.getCanonicalName())
                                    .addToBackStack(FragProdDesc.class.getCanonicalName()).commit();

                            toolbarSearchDialog.dismiss();

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