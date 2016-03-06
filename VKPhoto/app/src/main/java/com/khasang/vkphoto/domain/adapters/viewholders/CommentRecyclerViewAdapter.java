package com.khasang.vkphoto.domain.adapters.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.presentation.model.Comment;
import com.khasang.vkphoto.util.Logger;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONException;

import java.util.List;

/**
 * Created by admin on 06.03.2016.
 */
public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {

    private List<Comment> comments;
    private VKApiUser user;

    public CommentRecyclerViewAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        getUser(comment.from_id);
        if (user !=null){
            Picasso.with(holder.userImage.getContext()).load(user.photo_50).into(holder.userImage);
            holder.name.setText(user.first_name+" "+user.last_name);
        }else {
            Logger.d("where is user!?");
        }
    }

    private void getUser(final int userId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                RequestMaker.getUserInfoById(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        try {
                            VKApiUser tempUser = new VKApiUser(response.json.getJSONArray("response").getJSONObject(0));
                            setUser(tempUser);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, userId);
            }
        });
        thread.start();
        try {
            thread.join(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setUser(VKApiUser user) {
        this.user = user;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView text;
        private ImageView userImage;
        private TextView date;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.recyclerViewName);
            text = (TextView) itemView.findViewById(R.id.recyclerViewUserText);
            date = (TextView) itemView.findViewById(R.id.recyclerViewDate);
            userImage = (ImageView) itemView.findViewById(R.id.recyclerViewUserImage);
        }
    }
}
