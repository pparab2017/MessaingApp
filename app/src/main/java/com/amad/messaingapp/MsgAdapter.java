package com.amad.messaingapp;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amad.messaingapp.Entities.Message;

import java.util.ArrayList;

/**
 * Created by pushparajparab on 9/20/17.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgAdapterHolder> {

    private ArrayList<Message> mMsgs;
    private LayoutInflater inflater;
    private int mRecourseID;
    private Context mContext;
    private int lastPosition = -1;


    private ItemClickCityCallBack itemClickCityCallBack;
    public interface ItemClickCityCallBack
    {
        void OnItemClick(int p);
        boolean itemUnlock(int p);
        //if next method.
    }


    public void clear() {
        int size = this.mMsgs.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {

                this.mMsgs.remove(0);

                this.notifyItemRemoved(i);
            }

            //this.notifyDataSetChanged();
            //this.notifyItemRemoved(0, size);
        }
    }

    public void SetProducts(ArrayList<Message> messages){
        this.mMsgs = messages;
    }

    public MsgAdapter( Context context, int recourseID)
    {
        this.inflater = LayoutInflater.from(context);
        //this.mProducts = products;
        this.mRecourseID = recourseID;
        this.mContext = context;
        this.itemClickCityCallBack = (ItemClickCityCallBack)context;

    }

    @Override
    public MsgAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(this.mRecourseID,parent,false);
        return new MsgAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(MsgAdapterHolder holder, int position) {


        Message msg = mMsgs.get(position);
        holder.name.setText(msg.getFromName());
        holder.time.setText( msg.getTime());
        holder.content.setText(msg.getContent());

        if(msg.getLocked().equals("UNLOCK")){
            holder.iconLock.setImageResource(R.mipmap.lock_open);
        }
        else{
            holder.iconLock.setImageResource(R.mipmap.lock);
        }
        if(msg.getRead().equals("READ")){
            holder.iconRead.setImageResource(R.mipmap.circle_grey);
        }else
        {
            holder.iconRead.setImageResource(R.mipmap.circle_blue);
        }

        //String url= Helper.URL + product.getPhoto();

        // setFadeAnimation(holder.itemView, position);
    }



    private void setFadeAnimation(View view, int position) {
        if (position > lastPosition) {
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500);
            view.startAnimation(anim);
            lastPosition = position;
        }
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void setScaleAnimation(View view, int position) {
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(500);
            view.startAnimation(anim);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mMsgs.size();
    }


    class MsgAdapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name,content,time;
        private ImageView iconRead,iconLock;
        private View container;

        public MsgAdapterHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.txt_name);
            content = (TextView) itemView.findViewById(R.id.txt_content);
            time = (TextView) itemView.findViewById(R.id.txt_time);

            iconRead = (ImageView) itemView.findViewById(R.id.img_read);
            iconLock = (ImageView) itemView.findViewById(R.id.img_lock);
            container = itemView.findViewById(R.id.item_Layout);

            iconLock.setOnClickListener(this);
            container.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int i = getAdapterPosition();
            switch (v.getId())
            {
                case R.id.img_lock:
                    Log.d("click", "clcick");

                    if(mMsgs.get(i).getLocked().equals( "LOCK")) {
                        if(System.currentTimeMillis() - Messages.regionEnterTime >=  10000)
                        { boolean did = itemClickCityCallBack.itemUnlock(i);

                            {
                                iconLock.setImageResource(R.mipmap.lock_open);
                                mMsgs.get(i).setLocked("UNLOCK");
                            }

                        }
                        else
                        {
                            Toast.makeText(mContext,"Please wait for 10 second to unlock",Toast.LENGTH_SHORT).show();

                        }
                        // change the img and make call to the API


                    }else{
                        // do nothing
                    }
                    break;
                case R.id.item_Layout:
                    if(mMsgs.get(i).getLocked().equals( "LOCK")) {

                        Toast.makeText(mContext,"Message is Locked, Please unlock it to read!",Toast.LENGTH_LONG).show();

                    }else
                    {
                        itemClickCityCallBack.OnItemClick(i);
                    }
                    break;
            }


        }
    }
}


