package com.nebula.NebulaApp;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
public class LeaveAdapter extends FirebaseRecyclerAdapter<Leave, LeaveAdapter.LeavesViewholder>{
    private Context context;
    public LeaveAdapter(@NonNull FirebaseRecyclerOptions<Leave> options, Context context){
        super(options);
        this.context = context;
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void
    onBindViewHolder(@NonNull LeavesViewholder holder,
                     int position, @NonNull Leave model)
    {

        // Add firstname from model class (here
        // "person.class")to appropriate view in Card
        // view (here "person.xml")
        holder.title.setText(model.getTitle());
        holder.dates.setText(model.getDates());
        holder.totalDays.setText(String.valueOf(model.getTotaldays()));
        holder.status.setText(model.getStatus());
        holder.Requested_By.setText(model.getRequestedBy());
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0.9f, 1f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300); // Set the duration of the animation
        scaleAnimation.setFillAfter(false); // Keep the final state of the animation

// Define a touch listener for the card view
        View.OnTouchListener cardTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // When the user taps the card view
                        v.startAnimation(scaleAnimation); // Start the scale animation
                        break;
                    case MotionEvent.ACTION_UP: // When the user releases the card view
                        v.clearAnimation(); // Clear the animation
                        showConfirmationDialog(holder.getAdapterPosition());
                        break;
                }
                return true;
            }
        };

// Set the touch listener for the card view
        holder.leaveCardView.setOnTouchListener(cardTouchListener);
    }

    @NonNull
    @Override
    public LeavesViewholder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.applied_leave_card, parent, false);
        return new LeavesViewholder(view);
    }
    static class LeavesViewholder extends RecyclerView.ViewHolder {
        TextView title,totalDays,dates,status,Requested_By;
        CardView leaveCardView;
        public LeavesViewholder(@NonNull View itemView)
        {
            super(itemView);
            leaveCardView = itemView.findViewById(R.id.leaveCardView);
            title = itemView.findViewById(R.id.apply_leave_card_leave_title);
            totalDays = itemView.findViewById(R.id.apply_leave_card_leave_count);
            dates = itemView.findViewById(R.id.apply_leave_card_leave_duration);
            status = itemView.findViewById(R.id.apply_leave_card_status);
            Requested_By= itemView.findViewById(R.id.apply_leave_card_requestedBy);
            leaveCardView.setStateListAnimator(AnimatorInflater.loadStateListAnimator(itemView.getContext(), R.animator.leave_card_click_animation));

        }
    }
    private static void scaleCardView(CardView cardView, float scale) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(cardView, "scaleX", scale);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(cardView, "scaleY", scale);
        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);

        scaleDownX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        scaleDownX.start();
        scaleDownY.start();
    }
    private void showConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_delete_leave_card, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the item from the database
                getSnapshots().getSnapshot(position).getRef().removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
