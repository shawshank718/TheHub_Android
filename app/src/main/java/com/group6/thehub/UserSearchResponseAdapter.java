package com.group6.thehub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.group6.thehub.Rest.models.UserDetails;
import com.group6.thehub.activities.ProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sathwik on 21-Oct-15.
 */
public class UserSearchResponseAdapter extends RecyclerView.Adapter<UserSearchResponseAdapter.UserViewHolder> {

    Context mContext;
    List<UserDetails> users;

    public UserSearchResponseAdapter(Context context, List<UserDetails> users) {
        this.mContext = context;
        this.users = users;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_list_item, viewGroup, false);
        UserViewHolder userViewHolder = new UserViewHolder(v);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder userViewHolder, int i) {
        userViewHolder.setUser(users.get(i));
        userViewHolder.tvUserName.setText(users.get(i).getFullName());
        if (users.get(i).getRating() > 0) {
            userViewHolder.tvRating.setText(Float.toString(users.get(i).getRating()));
        } else {
            userViewHolder.tvRating.setVisibility(View.GONE);
        }
        userViewHolder.tvUserInfo.setText(users.get(i).getQualification());
        Picasso.with(mContext)
                .load(AppHelper.END_POINT+users.get(i).getImage().getImageUrl())
                .placeholder(R.drawable.ic_account_circle_grey_48dp)
                .error(R.drawable.ic_account_circle_grey_48dp)
                .into(userViewHolder.imgUser);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView imgUser;
        private TextView tvUserName;
        private TextView tvRating;
        private TextView tvUserInfo;
        private UserDetails user;


        public UserViewHolder(View itemView) {
            super(itemView);
            imgUser = (CircleImageView) itemView.findViewById(R.id.img_user);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvRating = (TextView) itemView.findViewById(R.id.tv_rating);
            tvUserInfo = (TextView) itemView.findViewById(R.id.tv_user_info);
            itemView.setOnClickListener(this);
        }

        public void setUser(UserDetails user) {
            this.user = user;
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putInt("userId", this.user.getUserId());
            AppHelper.slideInStayStill((AppCompatActivity) v.getContext(), ProfileActivity.class, bundle);
        }

    }

}
