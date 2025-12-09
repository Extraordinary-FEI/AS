package com.example.cn.helloworld.ui.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.AppCompatSpinner;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.Toast;
import android.content.DialogInterface;

import com.example.cn.helloworld.MusicService;
import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.AdminMetrics;
import com.example.cn.helloworld.data.model.Playlist;
import com.example.cn.helloworld.data.model.Order;
import com.example.cn.helloworld.data.session.SessionManager;
import com.example.cn.helloworld.data.repository.AdminMetricsRepository;
import com.example.cn.helloworld.data.repository.AdminOrderRepository;
import com.example.cn.helloworld.data.repository.SupportTaskRepository;
import com.example.cn.helloworld.ui.auth.LoginActivity;
import com.example.cn.helloworld.ui.admin.AdminOrderListActivity;
import com.example.cn.helloworld.ui.admin.PlaylistManagementActivity;
import com.example.cn.helloworld.ui.admin.ProductManagementActivity;
import com.example.cn.helloworld.ui.admin.StatsBarView;
import com.example.cn.helloworld.ui.admin.SupportTaskApprovalActivity;
import com.example.cn.helloworld.ui.admin.SupportTaskManagementActivity;
import com.example.cn.helloworld.ui.main.FakeHomeDataSource;
import com.example.cn.helloworld.ui.main.HomeDataSource;
import com.example.cn.helloworld.ui.main.HomeModels;
import com.example.cn.helloworld.ui.main.PlaylistAdapter;
import com.example.cn.helloworld.ui.playlist.PlaylistDetailActivity;
import com.example.cn.helloworld.ui.playlist.PlaylistOverviewActivity;
import com.example.cn.helloworld.ui.user.AddressManagementActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心 Fragment，复用活动中的展示内容以保证底部导航切换时不丢失。
 */
public class UserCenterFragment extends Fragment {

    private Context appContext;
    private SessionManager sessionManager;
    private HomeDataSource homeDataSource;
    private RecyclerView playlistList;
    private View viewAllPlaylistsButton;
    private View playlistCard;
    private View medalWallCard;
    private RecyclerView quickActionList;
    private View manageQuickActionsButton;
    private View adminCard;
    private View checkinCard;
    private RecyclerView checkinList;
    private TextView checkinSubtitle;
    private TextView checkinProgressLabel;
    private View checkinMapButton;
    private android.widget.ProgressBar checkinProgressBar;
    private StatsBarView adminStatsView;
    private TextView adminSummary;
    private View btnAdminProducts;
    private View btnAdminPlaylists;
    private View btnAdminSupportTasks;
    private View btnAdminTaskApproval;
    private View btnAdminOrders;
    private View btnAdminAddresses;
    private AdminMetricsRepository adminMetricsRepository;
    private SupportTaskRepository supportTaskRepository;
    private AdminOrderRepository adminOrderRepository;
    private QuickActionAdapter quickActionAdapter;
    private QuickActionStorage quickActionStorage;
    private CheckinManager checkinManager;
    private CheckinLocationAdapter checkinAdapter;
    private List<CheckinLocation> checkinLocations;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 记录 applicationContext，避免在 onCreateView 中因临时 Context 为空导致崩溃
        appContext = context.getApplicationContext();
        sessionManager = new SessionManager(appContext);
        homeDataSource = new FakeHomeDataSource(appContext);
        supportTaskRepository = new SupportTaskRepository(appContext);
        adminOrderRepository = new AdminOrderRepository(appContext);
        adminMetricsRepository = new AdminMetricsRepository(supportTaskRepository, adminOrderRepository);
        quickActionStorage = new QuickActionStorage(appContext);
        checkinManager = new CheckinManager(appContext);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_center, container, false);

        ImageView avatar = (ImageView) root.findViewById(R.id.avatarImage);
        TextView username = (TextView) root.findViewById(R.id.tvUsername);
        TextView userId = (TextView) root.findViewById(R.id.tvUserId);
        TextView logout = (TextView) root.findViewById(R.id.btnLogout);
        playlistList = (RecyclerView) root.findViewById(R.id.playlistList);
        viewAllPlaylistsButton = root.findViewById(R.id.button_view_all_playlists);
        playlistCard = root.findViewById(R.id.card_playlist_section);
        medalWallCard = root.findViewById(R.id.card_medal_wall);
        quickActionList = (RecyclerView) root.findViewById(R.id.recyclerQuickActions);
        manageQuickActionsButton = root.findViewById(R.id.button_manage_quick_actions);
        adminCard = root.findViewById(R.id.card_admin_center);
        checkinCard = root.findViewById(R.id.card_checkin);
        checkinList = (RecyclerView) root.findViewById(R.id.recycler_checkin);
        checkinSubtitle = (TextView) root.findViewById(R.id.text_checkin_subtitle);
        checkinProgressLabel = (TextView) root.findViewById(R.id.text_checkin_progress);
        checkinMapButton = root.findViewById(R.id.button_view_checkin_map);
        checkinProgressBar = (android.widget.ProgressBar) root.findViewById(R.id.progress_checkin);
        adminStatsView = (StatsBarView) root.findViewById(R.id.adminStatsView);
        adminSummary = (TextView) root.findViewById(R.id.textAdminSummary);
        btnAdminProducts = root.findViewById(R.id.buttonAdminManageProducts);
        btnAdminPlaylists = root.findViewById(R.id.buttonAdminManagePlaylists);
        btnAdminSupportTasks = root.findViewById(R.id.buttonAdminSupportTasks);
        btnAdminTaskApproval = root.findViewById(R.id.buttonAdminTaskApproval);
        btnAdminOrders = root.findViewById(R.id.buttonAdminOrders);
        btnAdminAddresses = root.findViewById(R.id.buttonAdminAddresses);

        avatar.setImageResource(R.drawable.ic_user_default);
        username.setText(sessionManager.getUsername());
        userId.setText("UID: " + sessionManager.getUserId());

        logout.setText(R.string.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                Context context = getContext();
                if (context != null) {
                    context.sendBroadcast(new Intent("ACTION_STOP"));
                    context.stopService(new Intent(context, MusicService.class));
                    context.sendBroadcast(new Intent(MusicService.ACTION_HIDE_FLOATING_MUSIC));
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });

        setupPlaylists();
        setupQuickActions(inflater);
        setupAdminArea();
        setupCheckins();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupAdminArea();
        if (checkinAdapter != null) {
            checkinAdapter.notifyDataSetChanged();
        }
        updateCheckinProgress();
    }

    private void setupPlaylists() {
        if (playlistList == null) {
            return;
        }

        playlistList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        playlistList.setNestedScrollingEnabled(false);

        playlistList.setAdapter(new PlaylistAdapter(
                homeDataSource.loadPlaylists(),
                new PlaylistAdapter.OnPlaylistClickListener() {
                    @Override
                    public void onPlaylistClick(Playlist playlist) {
                        Context context = getContext();
                        if (context != null) {
                            startActivity(PlaylistDetailActivity.createIntent(
                                    context,
                                    playlist.getId()
                            ));
                        }
                    }

                    @Override
                    public void onPlaylistClick(HomeModels.Playlist playlist) {
                        // legacy callback ignored
                    }
                }
        ));

        if (viewAllPlaylistsButton != null) {
            viewAllPlaylistsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getContext();
                    if (context != null) {
                        startActivity(new Intent(context, PlaylistOverviewActivity.class));
                    }
                }
            });
        }
    }

    private void setupQuickActions(final LayoutInflater inflater) {
        if (quickActionList == null || quickActionStorage == null) {
            return;
        }

        quickActionList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        quickActionList.setNestedScrollingEnabled(false);

        quickActionAdapter = new QuickActionAdapter(
                quickActionStorage.loadActions(),
                new QuickActionAdapter.Listener() {
                    @Override
                    public void onActionClick(QuickAction action) {
                        handleQuickActionClick(action);
                    }

                    @Override
                    public void onActionEdit(QuickAction action) {
                        showQuickActionEditor(action, inflater);
                    }
                }
        );
        quickActionList.setAdapter(quickActionAdapter);

        if (manageQuickActionsButton != null) {
            manageQuickActionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (quickActionAdapter != null) {
                        showQuickActionPicker(inflater);
                    }
                }
            });
        }
    }

    private void setupCheckins() {
        if (checkinList == null || checkinManager == null) {
            return;
        }

        checkinLocations = buildCheckinLocations();
        checkinList.setLayoutManager(new LinearLayoutManager(getContext()));
        checkinList.setNestedScrollingEnabled(false);

        checkinAdapter = new CheckinLocationAdapter(checkinLocations, checkinManager,
                new CheckinLocationAdapter.Listener() {
                    @Override
                    public void onCheckinClick(CheckinLocation location) {
                        checkinManager.toggleCompleted(location.getId());
                        updateCheckinProgress();
                        if (checkinAdapter != null) {
                            checkinAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onNavigateClick(CheckinLocation location) {
                        showAchievementTips(location);
                    }
                });
        checkinList.setAdapter(checkinAdapter);

        if (checkinMapButton != null) {
            checkinMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAchievementRules();
                }
            });
        }

        updateCheckinProgress();
    }

    private List<CheckinLocation> buildCheckinLocations() {
        List<CheckinLocation> locations = new ArrayList<CheckinLocation>();
        locations.add(new CheckinLocation(
                "rafles",
                getString(R.string.checkin_location_stage),
                getString(R.string.checkin_location_stage_desc),
                getString(R.string.checkin_location_stage_tips),
                getString(R.string.checkin_location_stage_query)));
        locations.add(new CheckinLocation(
                "river",
                getString(R.string.checkin_location_river),
                getString(R.string.checkin_location_river_desc),
                getString(R.string.checkin_location_river_tips),
                getString(R.string.checkin_location_river_query)));
        locations.add(new CheckinLocation(
                "plaza",
                getString(R.string.checkin_location_plaza),
                getString(R.string.checkin_location_plaza_desc),
                getString(R.string.checkin_location_plaza_tips),
                getString(R.string.checkin_location_plaza_query)));
        return locations;
    }

    private void updateCheckinProgress() {
        if (checkinLocations == null || checkinProgressBar == null || checkinProgressLabel == null
                || checkinManager == null) {
            return;
        }
        int total = checkinLocations.size();
        int completed = checkinManager.getCompletedCount();
        checkinProgressBar.setMax(total);
        checkinProgressBar.setProgress(completed);
        checkinProgressLabel.setText(getString(R.string.checkin_progress_label, completed, total));

        if (checkinSubtitle != null) {
            checkinSubtitle.setText(completed >= total
                    ? getString(R.string.checkin_completed_all)
                    : getString(R.string.user_checkin_subtitle));
        }
    }

    private void showAchievementTips(CheckinLocation location) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        new AlertDialog.Builder(context)
                .setTitle(location.getName())
                .setMessage(location.getTips())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void showAchievementRules() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.user_checkin_task_route)).append("\n\n")
                .append(getString(R.string.user_checkin_task_reward)).append("\n\n")
                .append(getString(R.string.user_checkin_task_time)).append("\n\n")
                .append(getString(R.string.user_checkin_task_tips));

        new AlertDialog.Builder(context)
                .setTitle(R.string.user_checkin_action)
                .setMessage(message.toString())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void openLocationOnMap(String query) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            intent.setPackage(null);
        }
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, R.string.checkin_map_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleQuickActionClick(QuickAction action) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        Intent intent = action.getType().buildIntent(context);
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(context,
                    getString(R.string.quick_action_empty_action, action.getTitle()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showQuickActionPicker(final LayoutInflater inflater) {
        if (quickActionAdapter == null) {
            return;
        }

        final List<QuickAction> actions = quickActionStorage.loadActions();
        CharSequence[] names = new CharSequence[actions.size()];
        for (int i = 0; i < actions.size(); i++) {
            names[i] = actions.get(i).getTitle();
        }

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.quick_action_manage)
                .setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showQuickActionEditor(actions.get(which), inflater);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showQuickActionEditor(final QuickAction action, LayoutInflater inflater) {
        View dialogView = inflater.inflate(R.layout.dialog_quick_action_editor, null, false);
        final AppCompatSpinner spinner = (AppCompatSpinner) dialogView.findViewById(R.id.spinnerQuickActionType);
        final EditText titleInput = (EditText) dialogView.findViewById(R.id.inputQuickActionTitle);
        final EditText descInput = (EditText) dialogView.findViewById(R.id.inputQuickActionDescription);

        final QuickAction.Type[] types = QuickAction.Type.values();
        String[] labels = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            labels[i] = getString(types[i].getDisplayNameRes());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int selected = 0;
        for (int i = 0; i < types.length; i++) {
            if (types[i] == action.getType()) {
                selected = i;
                break;
            }
        }
        spinner.setSelection(selected);

        titleInput.setText(action.getTitle());
        descInput.setText(action.getDescription());

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.quick_action_manage)
                .setView(dialogView)
                .setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = titleInput.getText().toString();
                        String desc = descInput.getText().toString();
                        QuickAction.Type type = types[spinner.getSelectedItemPosition()];
                        QuickAction updated = action.withContent(title, desc, type);
                        quickActionStorage.saveActions(replaceAction(updated));
                        if (quickActionAdapter != null) {
                            quickActionAdapter.updateAction(updated);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private List<QuickAction> replaceAction(QuickAction updated) {
        List<QuickAction> current = quickActionStorage.loadActions();
        List<QuickAction> replaced = new ArrayList<QuickAction>(current.size());
        boolean found = false;
        for (QuickAction action : current) {
            if (action.getId().equals(updated.getId())) {
                replaced.add(updated);
                found = true;
            } else {
                replaced.add(action);
            }
        }
        if (!found) {
            replaced.add(updated);
        }
        return replaced;
    }

    private void setupAdminArea() {
        if (adminCard == null || adminMetricsRepository == null) {
            return;
        }

        if (!sessionManager.isAdmin()) {
            adminCard.setVisibility(View.GONE);
            if (checkinCard != null) {
                checkinCard.setVisibility(View.VISIBLE);
            }
            if (playlistCard != null) {
                playlistCard.setVisibility(View.VISIBLE);
            }
            if (medalWallCard != null) {
                medalWallCard.setVisibility(View.VISIBLE);
            }
            return;
        }

        adminCard.setVisibility(View.VISIBLE);
        if (checkinCard != null) {
            checkinCard.setVisibility(View.GONE);
        }
        if (playlistCard != null) {
            playlistCard.setVisibility(View.GONE);
        }
        if (medalWallCard != null) {
            medalWallCard.setVisibility(View.GONE);
        }

        AdminMetrics metrics = adminMetricsRepository.loadMetrics();
        if (adminStatsView != null && metrics != null) {
            float[] values = new float[]{
                    metrics.getOrderCount(),
                    metrics.getPendingTasks(),
                    metrics.getNewRegistrations(),
                    metrics.getActiveUsers()
            };
            String[] labels = new String[]{
                    getString(R.string.stat_label_order),
                    getString(R.string.stat_label_support),
                    getString(R.string.stat_label_new_user),
                    getString(R.string.stat_label_active)
            };
            adminStatsView.setData(values, labels);
        }

        if (adminSummary != null && metrics != null) {
            adminSummary.setText(getString(R.string.dashboard_summary,
                    metrics.getOrderCount(),
                    metrics.getPendingTasks(),
                    metrics.getNewRegistrations()));
        }

        if (btnAdminProducts != null) {
            btnAdminProducts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), ProductManagementActivity.class));
                }
            });
        }

        if (btnAdminPlaylists != null) {
            btnAdminPlaylists.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), PlaylistManagementActivity.class));
                }
            });
        }

        if (btnAdminSupportTasks != null) {
            btnAdminSupportTasks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), SupportTaskManagementActivity.class));
                }
            });
        }

        if (btnAdminTaskApproval != null) {
            btnAdminTaskApproval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), SupportTaskApprovalActivity.class));
                }
            });
        }

        if (btnAdminOrders != null) {
            btnAdminOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Order> orders = new ArrayList<Order>(adminMetricsRepository.getOrders());
                    startActivity(AdminOrderListActivity.createIntent(getContext(), orders));
                }
            });
        }

        if (btnAdminAddresses != null) {
            btnAdminAddresses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), AddressManagementActivity.class));
                }
            });
        }
    }
}
