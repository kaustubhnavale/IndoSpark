package mipl.indospark;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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

public class Drower extends AppCompatActivity {

    public static DrawerLayout mDrawerLayout;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<String>> listDataChild;

    ArrayList<ProdPojo> allSampleData;
    List<String> heading2;
    HashMap<String, String> catID;

    StringRequest stringRequest;
    TextView tvBadge;

    TextView tvLoginName;
    String token;
    int cartQty = 0;
    SharedPreferences sharedpreferences;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_view);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragDrower, new FragHome(), "SOMETAG").commit();

        sharedpreferences = getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        token = sharedpreferences.getString(commonVariables.token, "");

        heading2 = new ArrayList<String>();
        catID = new HashMap<String, String>();
        getDrowerCategory();

        final ActionBar ab = getSupportActionBar();
        /* to set the menu icon image*/
        ab.setHomeAsUpIndicator(android.R.drawable.ic_menu_add);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);

        tvLoginName = (TextView) findViewById(R.id.tvLoginName);

        if (sharedpreferences.contains(commonVariables.token)) {
            tvLoginName.setText("Hello " + sharedpreferences.getString(commonVariables.Name, ""));
        } else {
            tvLoginName.setText("Login");
        }

        tvLoginName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedpreferences.contains(commonVariables.token)) {
                } else {
                    startActivity(new Intent(Drower.this, Login.class));
                    finish();
                }
            }
        });

        prepareListData();
        openDrower();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ba0001")));
        getSupportActionBar().setCustomView(R.layout.actionbarlayout);
        View view = getSupportActionBar().getCustomView();

        ImageView drower = (ImageView) view.findViewById(R.id.drower);
        ImageView cartImage = (ImageView) view.findViewById(R.id.cartImage);
        tvBadge = (TextView) view.findViewById(R.id.tvBadge);

        drower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawers();
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        cartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                if (fm.findFragmentById(R.id.fragDrower) != null) {
                    ft.hide(fm.findFragmentById(R.id.fragDrower));
                }
                ft.add(R.id.fragDrower, new FragCart(),FragCart.class.getCanonicalName())
                        .addToBackStack(FragCart.class.getCanonicalName()).commit();
            }
        });

        allSampleData = new ArrayList<ProdPojo>();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Drower.this, WebViewPages.class);
                i.putExtra("WebView", "Live Chat");
                startActivity(i);
            }
        });
    }

    public void hideFloatingActionButton() {
        fab.hide();
    };
    public void showFloatingActionButton() {
        fab.show();
    };

    private void prepareListData() {
        listDataHeader = new ArrayList<ExpandedMenuModel>();
        listDataChild = new HashMap<ExpandedMenuModel, List<String>>();

        ExpandedMenuModel item1 = new ExpandedMenuModel();
        item1.setIconName("Home");
        listDataHeader.add(item1);

        ExpandedMenuModel item2 = new ExpandedMenuModel();
        item2.setIconName("All Products");
        listDataHeader.add(item2);

        ExpandedMenuModel item3 = new ExpandedMenuModel();
        item3.setIconName("About Us");
        listDataHeader.add(item3);

        ExpandedMenuModel item4 = new ExpandedMenuModel();
        item4.setIconName("Contact Us");
        listDataHeader.add(item4);

        ExpandedMenuModel item11 = new ExpandedMenuModel();
        item11.setIconName("Terms and Conditions");
        listDataHeader.add(item11);

        ExpandedMenuModel item12 = new ExpandedMenuModel();
        item12.setIconName("Privacy Policy");
        listDataHeader.add(item12);

        if (sharedpreferences.contains(commonVariables.token)) {

            ExpandedMenuModel item5 = new ExpandedMenuModel();
            item5.setIconName("Enquiry");
            listDataHeader.add(item5);

            /*ExpandedMenuModel item6 = new ExpandedMenuModel();
            item6.setIconName("Register With Us");
            listDataHeader.add(item6);*/

            ExpandedMenuModel item7 = new ExpandedMenuModel();
            item7.setIconName("Address Book");
            listDataHeader.add(item7);

            ExpandedMenuModel item8 = new ExpandedMenuModel();
            item8.setIconName("My Account");
            listDataHeader.add(item8);

            ExpandedMenuModel item9 = new ExpandedMenuModel();
            item9.setIconName("My Orders");
            listDataHeader.add(item9);

            /*ExpandedMenuModel item10 = new ExpandedMenuModel();
            item10.setIconName("My Wish List");
            listDataHeader.add(item10);*/

            ExpandedMenuModel item15 = new ExpandedMenuModel();
            item15.setIconName("Live Chat");
            listDataHeader.add(item15);

            ExpandedMenuModel item13 = new ExpandedMenuModel();
            item13.setIconName("Logout");
            listDataHeader.add(item13);

        } else {
            ExpandedMenuModel item14 = new ExpandedMenuModel();
            item14.setIconName("Login");
            listDataHeader.add(item14);
        }

        listDataChild.put(listDataHeader.get(1), heading2);

        mMenuAdapter = new ExpandableListAdapter(Drower.this, listDataHeader, listDataChild, expandableList, catID);
        expandableList.setAdapter(mMenuAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openDrower() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                //Log.d("DEBUG", "submenu item clicked");
                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                //Log.d("DEBUG", "heading clicked");
                return false;
            }
        });
    }

    public static void closeDrawer(){
        mDrawerLayout.closeDrawers();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        //revision: this don't works, use setOnChildClickListener() and setOnGroupClickListener() above instead
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    public void getDrowerCategory() {

        stringRequest = new StringRequest(Request.Method.GET, "https://shop.indospark.com/android_api/get_all_categories.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // process your response here

                        Log.i("Desc JSON", response);

                        if (response.length() > 0) {
                            try {

                                JSONArray reader = new JSONArray(response);

                                for (int j = 0; j < reader.length(); j++) {

                                    JSONObject curr = reader.getJSONObject(j);

                                    String id = curr.getString("id");
                                    String name = curr.getString("name");
                                    String count = curr.getString("product_count");
                                    String isActive = curr.getString("is_active");

                                    heading2.add(name);
                                    catID.put(name, id);

                                    prepareListData();
                                    mMenuAdapter = new ExpandableListAdapter(Drower.this, listDataHeader, listDataChild, expandableList, catID);
                                    expandableList.setAdapter(mMenuAdapter);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Drower.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(Drower.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {

            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                    .setMessage("Are you sure?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finish();
                        }
                    }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

    public void getCartCount() {

        cartQty = 0;
        stringRequest = new StringRequest(Request.Method.POST, "https://shop.indospark.com/android_api/get_cart_items.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.length() > 0) {
                            try {
                                JSONArray reader = new JSONArray(response);

                                if (reader.length() > 0) {

                                    for (int i = 0; i < reader.length(); i++) {
                                        JSONObject address = reader.getJSONObject(i);

                                        String item_id = address.getString("item_id");

                                        if (item_id.equals("null")) {

                                        } else {

                                            int qty = address.getInt("qty");
                                            cartQty = cartQty + qty;
                                        }
                                    }
                                }
                                tvBadge.setText(String.valueOf(cartQty));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                tvBadge.setText(String.valueOf(cartQty));
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

        RequestQueue requestQueue = Volley.newRequestQueue(Drower.this);
        requestQueue.add(stringRequest);
    }
}