package com.termux.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.app.ActivityManager;
import java.util.Set;
import java.util.HashSet;
public class DrawerAppAdapter extends RecyclerView.Adapter<DrawerAppAdapter.ViewHolder> {

    public interface OnAppClickListener {
        void onAppClick(TermuxActivity.AppInfo appInfo);
    }

    private List<TermuxActivity.AppInfo> apps;
    private final OnAppClickListener listener;

    public DrawerAppAdapter(List<TermuxActivity.AppInfo> apps, OnAppClickListener listener) {
        this.apps = apps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Container
        LinearLayout container = new LinearLayout(parent.getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(8, 24, 8, 24);
        container.setBackground(null);
        container.setLayoutParams(new RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));

        // App name
        TextView name = new TextView(parent.getContext());
        name.setTextColor(0xFFFFFFFF);
        name.setTextSize(17);
        name.setTypeface(android.graphics.Typeface.create(
            "sans-serif-light", android.graphics.Typeface.NORMAL));
        name.setTag("name");
        container.addView(name);

        // Subtitle row
        TextView subtitle = new TextView(parent.getContext());
        subtitle.setTextColor(0xFF666666);
        subtitle.setTextSize(11);
        subtitle.setTypeface(android.graphics.Typeface.create(
            "sans-serif", android.graphics.Typeface.NORMAL));
        subtitle.setTag("subtitle");
        subtitle.setPadding(0, 4, 0, 0);
        container.addView(subtitle);

        // Package name
        TextView packageName = new TextView(parent.getContext());
        packageName.setTextColor(0xFF444444);
        packageName.setTextSize(10);
        packageName.setTypeface(android.graphics.Typeface.MONOSPACE);
        packageName.setTag("package");
        packageName.setPadding(0, 2, 0, 0);
        container.addView(packageName);

        return new ViewHolder(container);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Force transparent on every bind
        holder.container.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        holder.container.setBackground(null);

        TermuxActivity.AppInfo app = apps.get(position);

        TextView name = holder.container.findViewWithTag("name");
        TextView subtitle = holder.container.findViewWithTag("subtitle");
        TextView packageName = holder.container.findViewWithTag("package");

        name.setText(app.name);

        // Status
        String status = app.isRunning ? "● Running" : "○ Stopped";

        // Size
        String size;
        if (app.totalSize <= 0) {
            size = "—";
        } else if (app.totalSize >= 1024L * 1024 * 1024) {
            size = String.format("%.1f GB", app.totalSize / (1024f * 1024f * 1024f));
        } else if (app.totalSize >= 1024 * 1024) {
            size = String.format("%.0f MB", app.totalSize / (1024f * 1024f));
        } else {
            size = String.format("%.0f KB", app.totalSize / 1024f);
        }

        // Time this week
        String timeStr;
        if (app.usageTimeWeekMs <= 0) {
            timeStr = "—";
        } else {
            long hours = app.usageTimeWeekMs / (1000 * 60 * 60);
            long minutes = (app.usageTimeWeekMs / (1000 * 60)) % 60;
            if (hours > 0) {
                timeStr = hours + "h " + minutes + "m";
            } else {
                timeStr = minutes + "m";
            }
        }

        String fullText = status + "  ·  " + size + "  ·  " + timeStr + " this week";

        android.text.SpannableString spannable =
            new android.text.SpannableString(fullText);

        int statusColor = app.isRunning
            ? 0xFF00B02D   // green
            : 0xFF666666;  // default gray

        spannable.setSpan(
            new android.text.style.ForegroundColorSpan(statusColor),
            0,
            status.length(),
            android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        subtitle.setText(spannable);
        packageName.setText(app.packageName);

        holder.itemView.setOnClickListener(v -> listener.onAppClick(app));
    }

    @Override
    public int getItemCount() { return apps.size(); }

    public void updateList(List<TermuxActivity.AppInfo> newApps) {
        apps = newApps;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        ViewHolder(LinearLayout container) {
            super(container);
            this.container = container;
        }
    }
}
