package mipl.indospark;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<ExpandedMenuModel> mListDataHeader; // header titles

    // child data in format of header title, child title
    private HashMap<ExpandedMenuModel, List<String>> mListDataChild;
    ExpandableListView expandList;
    HashMap<String, String> id;
    Layout mDrawerLayout;

    SharedPreferences sharedpreferences;

    public ExpandableListAdapter(Context context, List<ExpandedMenuModel> listDataHeader, HashMap<ExpandedMenuModel, List<String>> listChildData, ExpandableListView mView, HashMap<String, String> id) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
        this.expandList = mView;
        this.id = id;
        sharedpreferences = mContext.getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
    }

    @Override
    public int getGroupCount() {
        int i = mListDataHeader.size();
        Log.d("GROUPCOUNT", String.valueOf(i));
        return this.mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int childCount = 0;
        if (groupPosition == 1) {
            childCount = this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();
        }
        return childCount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Log.d("CHILD", mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .get(childPosition).toString());
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ExpandedMenuModel headerTitle = (ExpandedMenuModel) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listheader, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.submenu);
        final ImageView headerIcon = (ImageView) convertView.findViewById(R.id.iconimage);

        if (getChildrenCount(groupPosition) == 0) {
            headerIcon.setVisibility(View.INVISIBLE);
        } else {
            headerIcon.setVisibility(View.VISIBLE);
            headerIcon.setImageResource(isExpanded ? R.drawable.uparroe : R.drawable.downarrow1);
        }

        lblListHeader.setText(headerTitle.getIconName());

        lblListHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, headerTitle.getIconName(), Toast.LENGTH_SHORT).show();
                String headerName = headerTitle.getIconName();

                if (headerName.equals("Home")) {
                    android.support.v4.app.FragmentManager fm = ((FragmentActivity)mContext).getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                    if (fm.findFragmentById(R.id.fragDrower) != null) {
                        ft.hide(fm.findFragmentById(R.id.fragDrower));
                    }
                    ft.add(R.id.fragDrower, new FragHome(),FragHome.class.getCanonicalName()).commit();

                } else if (headerName.equals("Enquiry")) {
                    Intent i = new Intent(mContext, WebViewPages.class);
                    i.putExtra("WebView", "Enquiry");
                    v.getContext().startActivity(i);

                } else if (headerName.equals("About Us")) {
                    Intent i = new Intent(mContext, WebViewPages.class);
                    i.putExtra("WebView", "About Us");
                    v.getContext().startActivity(i);

                } else if (headerName.equals("Contact Us")) {
                    Intent i = new Intent(mContext, WebViewPages.class);
                    i.putExtra("WebView", "Contact Us");
                    v.getContext().startActivity(i);

                } else if (headerName.equals("Terms and Condition")) {
                    Intent i = new Intent(mContext, WebViewPages.class);
                    i.putExtra("WebView", "Terms and Condition");
                    v.getContext().startActivity(i);

                } else if (headerName.equals("Privacy Policy")) {
                    Intent i = new Intent(mContext, WebViewPages.class);
                    i.putExtra("WebView", "Privacy Policy");
                    v.getContext().startActivity(i);

                } else if (headerName.equals("Address Book")) {
//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragDrower, new FragDefaultAddress(), "SOMETAG").addToBackStack("Indo").commit();

                    android.support.v4.app.FragmentManager fm = ((FragmentActivity)mContext).getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                    if (fm.findFragmentById(R.id.fragDrower) != null) {
                        ft.hide(fm.findFragmentById(R.id.fragDrower));
                    }
                    ft.add(R.id.fragDrower, new FragDefaultAddress(),FragDefaultAddress.class.getCanonicalName())
                            .addToBackStack(FragDefaultAddress.class.getCanonicalName()).commit();


                } else if (headerName.equals("Logout")) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();
                    editor.commit();

                    Intent i = new Intent(mContext, Login.class);
                    v.getContext().startActivity(i);
                    System.exit(0);

                    /*android.support.v4.app.FragmentManager fm = ((FragmentActivity)mContext).getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                    if (fm.findFragmentById(R.id.fragDrower) != null) {
                        ft.hide(fm.findFragmentById(R.id.fragDrower));
                    }
                    ft.add(R.id.fragDrower, new FragPayment(),FragPayment.class.getCanonicalName())
                            .addToBackStack(FragPayment.class.getCanonicalName()).commit();*/


                } else if (headerName.equals("My Orders")) {

                    android.support.v4.app.FragmentManager fm = ((FragmentActivity)mContext).getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                    if (fm.findFragmentById(R.id.fragDrower) != null) {
                        ft.hide(fm.findFragmentById(R.id.fragDrower));
                    }
                    ft.add(R.id.fragDrower, new FragMyOrders(),FragMyOrders.class.getCanonicalName())
                            .addToBackStack(FragMyOrders.class.getCanonicalName()).commit();

//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragDrower, new FragMyOrders(), "SOMETAG").addToBackStack("Indo").commit();


                } else if (headerName.equals("My Account")) {

                    android.support.v4.app.FragmentManager fm = ((FragmentActivity)mContext).getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                    if (fm.findFragmentById(R.id.fragDrower) != null) {
                        ft.hide(fm.findFragmentById(R.id.fragDrower));
                    }
                    ft.add(R.id.fragDrower, new FragMyAccount(),FragMyAccount.class.getCanonicalName())
                            .addToBackStack(FragMyAccount.class.getCanonicalName()).commit();

//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragDrower, new FragMyAccount(), "SOMETAG").addToBackStack("Indo").commit();
                } else if (headerName.equals("Login")) {

                    Intent i = new Intent(mContext, Login.class);
                    v.getContext().startActivity(i);
                }

                Drower.closeDrawer();
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_submenu, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.submenu);

        txtListChild.setText(childText);

        txtListChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (Map.Entry<String, String> entry : id.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    /*if (key.equals(childText)) {
                        Toast.makeText(mContext, value, Toast.LENGTH_SHORT).show();
                    }*/

                    if (key.equals(childText)) {
                        /*Intent intent = new Intent(mContext, AllProducts.class);
                        intent.putExtra("catID", value);
                        mContext.startActivity(intent);*/

                        Bundle bundle=new Bundle();
                        bundle.putString("catID", value);
                        //set Fragmentclass Arguments
                        FragCatProdList fragobj=new FragCatProdList();
                        fragobj.setArguments(bundle);

                        android.support.v4.app.FragmentManager fm = ((FragmentActivity)mContext).getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
                        if (fm.findFragmentById(R.id.fragDrower) != null) {
                            ft.hide(fm.findFragmentById(R.id.fragDrower));
                        }
                        ft.add(R.id.fragDrower, fragobj,FragCatProdList.class.getCanonicalName())
                                .addToBackStack(FragCatProdList.class.getCanonicalName()).commit();

//                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragDrower, fragobj, "SOMETAG").addToBackStack("Indo").commit();

                        Drower.closeDrawer();
                    }
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}