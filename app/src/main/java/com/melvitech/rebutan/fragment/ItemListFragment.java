package com.melvitech.rebutan.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.melvitech.rebutan.R;
import com.melvitech.rebutan.adapter.ItemListAdapter;
import com.melvitech.rebutan.crawler.ECrawlSource;
import com.melvitech.rebutan.crawler.model.Item;
import com.melvitech.rebutan.crawler.model.Keyword;
import com.melvitech.rebutan.db.NotifSQLiteDataSource;
import com.melvitech.rebutan.sync.SchedulerService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miku on 7/15/14.
 */
public class ItemListFragment extends SherlockFragment {

    private SwipeRefreshLayout swipeLayout;
    private ListView listView;
    private View loadmore;

    private NotifSQLiteDataSource dataSource;
    private ItemListAdapter listAdapter;

    final CharSequence[] items = {ECrawlSource.KASKUS.getCode(), ECrawlSource.OLX.getCode(), ECrawlSource.BERNIAGA.getCode()};
    boolean[] itemsChecked = new boolean[items.length];

    int page = 0;
    int max = 10;

    Keyword keywords;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_main, null);
        listView = (ListView) root.findViewById(R.id.listView);
        swipeLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_container);
        loadmore = inflater.inflate(R.layout.list_row_load_more, null);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setHasOptionsMenu(true);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(getActivity(), SchedulerService.class);
                intent.putExtra("main", true);
                getActivity().startService(intent);
            }
        });
        swipeLayout.offsetTopAndBottom(0);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        dataSource = new NotifSQLiteDataSource(getActivity());
        listAdapter = new ItemListAdapter(getActivity(), loadItem(0, 10, keywords, new CharSequence[]{}));
        listView.setAdapter(listAdapter);
        if (listAdapter.isEmpty()) {
            ((TextView) loadmore.findViewById(R.id.loadMoreText)).setText("Data Empty");
        }
        listView.addFooterView(loadmore);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < listAdapter.getCount()) {
                    try {
                        if (!listAdapter.getItem(i).isRead()) {
                            dataSource.open();
                            dataSource.setRead(listAdapter.getItem(i).getId());
                            dataSource.close();
                            listAdapter.getItem(i).setRead(true);
                            listAdapter.notifyDataSetChanged();
                            ((LeftMenuFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.menu_frame)).refreshListData();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    String url = ((Item) listAdapter.getItem(i)).getLink();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } else {
                    page++;
                    List<Item> pageLoad = loadItem(page, max, keywords, new CharSequence[]{});
                    if (!pageLoad.isEmpty()) {
                        for (Item item : pageLoad) {
                            listAdapter.add(item);
                            listAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getActivity(), "No More Item..", Toast.LENGTH_SHORT).show();
                        page--;
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SchedulerService.ACTION_DONE_REFRESH);
        getActivity().registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                dataSource.open();
                listAdapter = new ItemListAdapter(getActivity(), dataSource.getAllItems(0, 10));
                listView.setAdapter(listAdapter);
                dataSource.close();
                if (!listAdapter.isEmpty()) {
                    ((TextView) loadmore.findViewById(R.id.loadMoreText)).setText("Load more...");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            swipeLayout.setRefreshing(false);
        }
    };

    private List<Item> loadItem(int page, int max, Keyword keyword, CharSequence[] sources) {
        List<Item> items = new ArrayList<Item>();
        try {
            dataSource.open();
            if (keyword != null && keyword.id > 0) {
                items.addAll(dataSource.getAllItemsByKeywords(page, max, keyword.id, sources));
            } else {
                items.addAll(dataSource.getAllItems(page, max));
            }
            dataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void loadItemByKeywords(Keyword keywords) {
        this.keywords = keywords;
        this.page = 0;
        Log.d(LeftMenuFragment.class.getSimpleName(), "change data for keywords \"" + keywords.keywords + "\"");
        listAdapter = new ItemListAdapter(getActivity(), loadItem(0, max, keywords, new CharSequence[]{}));
        listView.setAdapter(listAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showFilterDialog() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick a Source");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedFilter = "";
                for (int i = 0; i < items.length; i++) {
                    if (itemsChecked[i]) {
                        selectedFilter = selectedFilter + "," + items[i] + " ";
                        itemsChecked[i] = false;
                    }
                }
                prefs.edit().putString("filter", selectedFilter);
                prefs.edit().commit();
                Log.d(ItemListFragment.class.getSimpleName(), selectedFilter);
            }
        });

        builder.setMultiChoiceItems(items, new boolean[]{false, false, false}, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                itemsChecked[which] = isChecked;
            }
        });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.action_filter) {
//            showFilterDialog();
//            return true;
//        } else
        if (id == android.R.id.home) {
            ((SlidingFragmentActivity) (getActivity())).toggle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
