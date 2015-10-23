package com.group6.thehub;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.group6.thehub.Rest.models.Session;
import com.group6.thehub.Rest.models.UserDetails;
import com.group6.thehub.activities.ProfileActivity;
import com.group6.thehub.activities.RequestSessionActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sathwik on 23-Oct-15.
 */
public class SessionResultsAdapter extends RecyclerView.Adapter<SessionResultsAdapter.SessionViewHolder> {


    Context mContext;
    List<Session> sessions;

    public SessionResultsAdapter(Context context, List<Session> sessions) {
        this.mContext = context;
        this.sessions = sessions;
    }

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        SessionViewHolder sessionViewHolder = new SessionViewHolder(v);
        return sessionViewHolder;
    }

    @Override
    public void onBindViewHolder(SessionViewHolder holder, int position) {
        holder.setSession(sessions.get(position));
        holder.tvUserName.setText(sessions.get(position).getName());
        holder.tvRating.setVisibility(View.GONE);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(new Date(sessions.get(position).getStartTime()*1000));
        holder.tvUserInfo.setText(sessions.get(position).getCourseCode()+" - "+date);
        Picasso.with(mContext)
                .load(AppHelper.END_POINT+sessions.get(position).getImage().getImageUrl())
                .placeholder(R.drawable.ic_account_circle_grey_48dp)
                .error(R.drawable.ic_account_circle_grey_48dp)
                .into(holder.imgUser);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView imgUser;
        private TextView tvUserName;
        private TextView tvRating;
        private TextView tvUserInfo;
        private Session session;


        public SessionViewHolder(View itemView) {
            super(itemView);
            imgUser = (CircleImageView) itemView.findViewById(R.id.img_user);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvRating = (TextView) itemView.findViewById(R.id.tv_rating);
            tvUserInfo = (TextView) itemView.findViewById(R.id.tv_user_info);
            itemView.setOnClickListener(this);
        }

        public void setSession(Session session) {
            this.session = session;
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putInt("sessionId", this.session.getSessionId());
            AppHelper.slideInStayStill((AppCompatActivity) v.getContext(), RequestSessionActivity.class, bundle);
        }

    }

}
