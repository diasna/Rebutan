package com.melvitech.rebutan.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.melvitech.rebutan.AboutUs;
import com.melvitech.rebutan.R;
import com.melvitech.rebutan.crawler.model.Keyword;
import com.melvitech.rebutan.db.NotifSQLiteDataSource;
import com.melvitech.rebutan.preferences.QuickPrefsActivity;
import com.melvitech.rebutan.sync.Utils;

import java.sql.SQLException;

public class LeftMenuFragment extends Fragment {

    private static final int RESULT_SETTINGS = 1;

    private NotifSQLiteDataSource dataSource;
    private ListView listView;
    private EditText keywordEt;
    private KeywordsAdapter adapter;
    private LinearLayout setting;
    private LinearLayout about;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.navigation_left, null);
        listView = (ListView) root.findViewById(android.R.id.list);
        keywordEt = (EditText) root.findViewById(R.id.editText);
        return root;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new KeywordsAdapter(getActivity());
        dataSource = new NotifSQLiteDataSource(getActivity());
        refreshListData();
        listView.setAdapter(adapter);
        ((ImageButton) getView().findViewById(R.id.imageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = keywordEt.getText().toString();
                if (!"".equals(keyword.trim())) {
                    try {
                        dataSource.open();
                        dataSource.addKeywords(keyword);
                        dataSource.close();
                        keywordEt.setText("");
                        refreshListData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ItemListFragment listFragment = (ItemListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                listFragment.loadItemByKeywords((Keyword) adapterView.getItemAtPosition(i));
                ((SlidingFragmentActivity) (getActivity())).toggle();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                if (i > 0) {
                    final Keyword selected = adapter.getItem(i);
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Delete")
                            .setMessage("Are you sure to remove " + selected.keywords + "? current data will be removed too")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        dataSource.open();
                                        dataSource.deleteKeywords(selected.id);
                                        dataSource.close();
                                        keywordEt.setText("");
                                        refreshListData();
                                        ItemListFragment listFragment = (ItemListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                                        listFragment.loadItemByKeywords(new Keyword(0));
                                        ((SlidingFragmentActivity) (getActivity())).toggle();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                    return true;
                }
                return false;
            }
        });

        setting = (LinearLayout) getView().findViewById(R.id.linearLayout2);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), QuickPrefsActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
            }
        });
        about = (LinearLayout) getView().findViewById(R.id.linearLayout4);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AboutUs.class);
                startActivity(i);
            }
        });
    }

    public void refreshListData() {
        try {
            adapter.clear();
            Keyword sa = new Keyword(-1, "Show All");
            adapter.add(sa);
            dataSource.open();
            for (Keyword keyword : dataSource.getAllKeywords(true)) {
                adapter.add(keyword);
                sa.count = sa.count + keyword.count;
            }
            dataSource.close();
            adapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public class KeywordsAdapter extends ArrayAdapter<Keyword> {

        public KeywordsAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_keywords, null);
            }
            TextView count = (TextView) convertView.findViewById(R.id.row_icon);
            count.setText(getItem(position).count + "");
            TextView title = (TextView) convertView.findViewById(R.id.row_title);
            title.setText(getItem(position).keywords);

            return convertView;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SETTINGS:
                Utils.setRecurringAlarm(getActivity());
                Toast.makeText(getActivity(), "Settings Updated", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
