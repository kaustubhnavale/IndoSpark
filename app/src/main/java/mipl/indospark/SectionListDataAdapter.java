package mipl.indospark;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder> {

    private ArrayList<ProdPojo> itemsList;
    private Context mContext;

    public SectionListDataAdapter(Context context, ArrayList<ProdPojo> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_single_card, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        holder.singleItem = itemsList.get(i);

        holder.tvTitle.setText(holder.singleItem.getName());
        holder.tvPrice.setText("₹ : " + holder.singleItem.getPrice());
        Picasso.get().load(holder.singleItem.getImageValue()).into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle, tvPrice;
        protected ImageView itemImage;
        ProdPojo singleItem;

        public SingleItemRowHolder(View view) {
            super(view);

            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.tvPrice = (TextView) view.findViewById(R.id.tvPrice);
            this.itemImage = (ImageView) view.findViewById(R.id.itemImage);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle=new Bundle();
                    bundle.putString("SKU", singleItem.getSku());
                    //set Fragmentclass Arguments
                    FragProdDesc fragobj=new FragProdDesc();
                    fragobj.setArguments(bundle);

                    android.support.v4.app.FragmentManager fm = ((FragmentActivity)mContext).getSupportFragmentManager();
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
}