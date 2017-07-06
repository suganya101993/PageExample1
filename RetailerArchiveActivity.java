package com.wish.registry.fragments;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wish.registry.R;
import com.wish.registry.adapterclasses.RetailerArchiveAdapter;
import com.wish.registry.utilclasses.AppController;
import com.wish.registry.utilclasses.Constants;
import com.wish.registry.utilclasses.NetworkConnectionManager;
import com.wish.registry.utilclasses.PrefManager;
import com.wish.registry.utilclasses.RecyclerViewItemOffsetDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RetailerArchiveActivity extends Fragment {


    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView retialerArchiveRecyclerView;
    ProgressBar retialerArchiveProgressBar;
    TextView retialerArchiveNodataTV;
    ArrayList<HashMap<String, String>> data;
    NetworkConnectionManager connectionManager;
    RequestQueue requestQueue;
    RetailerArchiveAdapter archiveadapter;
    private final int VIEW_TYPE_ITEM = 0;

    public RetailerArchiveActivity() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_reatailer_archive, container, false);
        connectionManager = new NetworkConnectionManager(getActivity());
        requestQueue = AppController.getInstance().getRequestQueue();
        data = new ArrayList<>();

        retialerArchiveRecyclerView = (RecyclerView) view.findViewById(R.id.common_recyclerview);
        retialerArchiveProgressBar = (ProgressBar) view.findViewById(R.id.common_progressbar);
        retialerArchiveNodataTV = (TextView) view.findViewById(R.id.common_no_data_TV);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);

        RecyclerViewItemOffsetDecoration itemOffsetDecoration = new RecyclerViewItemOffsetDecoration(getActivity(), R.dimen.spacing);
        // retialerArchiveRecyclerView.addItemDecoration(itemOffsetDecoration);
        retialerArchiveRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSmoothScrollbarEnabled(true);
        retialerArchiveRecyclerView.setLayoutManager(layoutManager);

        archiveadapter = new RetailerArchiveAdapter(getActivity(), data);
        retialerArchiveRecyclerView.setAdapter(archiveadapter);

        String archiveUrl = Constants.LIVE_URL + Constants.ARCHIVE + Constants.RETAILER_ID_PARAM + new PrefManager(getActivity()).getSessionIntValue(Constants.SESSION_USERID) + "&" + Constants.GIFT_START + "0&" + Constants.GIFT_LIMIT + Constants.LIMIT_VALUE;
        loadArchiveDataFromApi(archiveUrl);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (archiveadapter.getItemViewType(position) == VIEW_TYPE_ITEM) {
                    return 1;
                } else {
                    return 2;
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (data.size() > 0) {
                    data.clear();
                    archiveadapter.notifyDataChanged();
                }

                archiveadapter.setMoreDataAvailable(true);
                String archiveUrl = Constants.LIVE_URL + Constants.ARCHIVE + Constants.RETAILER_ID_PARAM + new PrefManager(getActivity()).getSessionIntValue(Constants.SESSION_USERID) + "&" + Constants.GIFT_START + "0&" + Constants.GIFT_LIMIT + Constants.LIMIT_VALUE;
                loadArchiveDataFromApi(archiveUrl);

            }
        });

        archiveadapter.setLoadMoreListener(new RetailerArchiveAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                retialerArchiveRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        int start = data.size();
                        String archiveUrl = Constants.LIVE_URL + Constants.ARCHIVE + Constants.RETAILER_ID_PARAM + new PrefManager(getActivity()).getSessionIntValue(Constants.SESSION_USERID) + "&" + Constants.GIFT_START + start + "&" + Constants.GIFT_LIMIT + Constants.LIMIT_VALUE;
                        loadMoreArchiveDataFromApi(archiveUrl);
                    }
                });
            }
        });


        return view;
    }

    private void loadArchiveDataFromApi(String url) {
        if (connectionManager.isConnectingToInternet()) {
            clearQueue();
            Log.d("Success", "Url:::: " + url);
            JsonObjectRequest retialarchiveJsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Success", "Response:::: " + response);
                    swipeRefreshLayout.setRefreshing(false);
                    try {
                        archiveadapter.setMoreDataAvailable(true);
                        if (response.getInt(Constants.STATUS_KEY) == 1) {
                            JSONArray jsonArray = response.getJSONArray(Constants.ARCHIEVE_ARRAY_KEY);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                HashMap<String, String> loopData = new HashMap<>();
                                loopData.put(Constants.TITLE_KEY, object.getString("name"));
                                loopData.put(Constants.ID_KEY, String.valueOf(object.getInt("id")));
                                loopData.put(Constants.PRICE_KEY, object.getString("price"));
                                loopData.put(Constants.IMAGE_KEY, object.getString("image"));
                                loopData.put(Constants.TYPE_KEY, Constants.ADAPTER_VIEW_TYPE_DATA_VALUE);
                                data.add(loopData);
                            }
                            archiveadapter.notifyDataChanged();
                            retialerArchiveProgressBar.setVisibility(View.GONE);
                            retialerArchiveRecyclerView.setVisibility(View.VISIBLE);
                            retialerArchiveNodataTV.setVisibility(View.GONE);
                        } else if (retialerArchiveRecyclerView.getChildCount() == 0) {

                            retialerArchiveProgressBar.setVisibility(View.GONE);
                            retialerArchiveRecyclerView.setVisibility(View.GONE);
                            retialerArchiveNodataTV.setVisibility(View.VISIBLE);
                            retialerArchiveNodataTV.setText(response.getString(Constants.MESSAGE_KEY));
                            archiveadapter.setMoreDataAvailable(false);
                        } else {
                            retialerArchiveProgressBar.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        Log.e("Exception", "exec" + e);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error: " + error.getMessage());
                    if (error instanceof TimeoutError) {
                        Log.e("Error "+Constants.ARCHIVE_STR, "" + Constants.TIMEOUT_ERROR);
                    } else if (error instanceof NoConnectionError) {
                        Log.e("Error "+Constants.ARCHIVE_STR, "" + Constants.NO_CONNECTION_ERROR);
                    } else if (error instanceof AuthFailureError) {
                        Log.e("Error "+Constants.ARCHIVE_STR, "" + Constants.AUTHENDICATION_ERROR);
                    } else if (error instanceof ServerError) {
                        Log.e("Error "+Constants.ARCHIVE_STR,"" + Constants.SERVER_ERROR);
                    } else if (error instanceof NetworkError) {
                        Log.e("Error "+Constants.ARCHIVE_STR, "" + Constants.NETWORK_ERROR);
                    } else if (error instanceof ParseError) {
                        Log.e("Error "+Constants.ARCHIVE_STR,"" + Constants.PARSE_ERROR);
                    }
                }
            });
            retialarchiveJsonObjectRequest.setTag(Constants.ARCHIVE_STR);
            AppController.getInstance().getRequestQueue().add(retialarchiveJsonObjectRequest);
        } else {
            retialerArchiveRecyclerView.setVisibility(View.GONE);
            retialerArchiveProgressBar.setVisibility(View.GONE);
            retialerArchiveNodataTV.setVisibility(View.VISIBLE);
            retialerArchiveNodataTV.setText(getString(R.string.netUnavailable));
        }
    }

    private void loadMoreArchiveDataFromApi(String url) {
        if (connectionManager.isConnectingToInternet()) {
            clearQueue();
            Log.d("Success", "Url:::: " + url);
            HashMap<String, String> loadData = new HashMap<>();
            loadData.put(Constants.TYPE_KEY, Constants.ADAPTER_VIEW_TYPE_LOADING_VALUE);
            data.add(loadData);
            archiveadapter.notifyItemInserted(data.size() - 1);
            JsonObjectRequest retialarchiveJsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Success", "Response:::: " + response);
                    archiveadapter.setMoreDataAvailable(true);
                    data.remove(data.size() - 1);
                    try {
                        if (response.getInt(Constants.STATUS_KEY) == 1) {
                            JSONArray jsonArray = response.getJSONArray(Constants.ARCHIEVE_ARRAY_KEY);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                HashMap<String, String> loopData = new HashMap<>();
                                loopData.put(Constants.TITLE_KEY, object.getString("name"));
                                loopData.put(Constants.ID_KEY, String.valueOf(object.getInt("id")));
                                loopData.put(Constants.PRICE_KEY, object.getString("price"));
                                loopData.put(Constants.IMAGE_KEY, object.getString("image"));
                                loopData.put(Constants.TYPE_KEY, Constants.ADAPTER_VIEW_TYPE_DATA_VALUE);
                                data.add(loopData);
                            }
                        } else {
                            HashMap<String, String> noData = new HashMap<>();
                            noData.put(Constants.TYPE_KEY, Constants.ADAPTER_VIEW_TYPE_NO_MORE_DATA_VALUE);
                            noData.put(Constants.TITLE_KEY, response.getString(Constants.MESSAGE_KEY));
                            data.add(noData);
                            archiveadapter.notifyItemInserted(data.size() - 1);
                            archiveadapter.setMoreDataAvailable(false);
                        }
                        archiveadapter.notifyDataChanged();
                    } catch (Exception e) {
                        archiveadapter.notifyDataChanged();
                        Log.e("Exception", "exec" + e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    data.remove(data.size() - 1);
                    archiveadapter.notifyDataChanged();
                    archiveadapter.setMoreDataAvailable(true);
                    VolleyLog.d("Error: " + error.getMessage());
                    if (error instanceof TimeoutError) {
                        Log.e("Error "+Constants.ARCHIVE_STR, "" + Constants.TIMEOUT_ERROR);
                    } else if (error instanceof NoConnectionError) {
                        Log.e("Error "+Constants.ARCHIVE_STR, "" + Constants.NO_CONNECTION_ERROR);
                    } else if (error instanceof AuthFailureError) {
                        Log.e("Error "+Constants.ARCHIVE_STR, "" + Constants.AUTHENDICATION_ERROR);
                    } else if (error instanceof ServerError) {
                        Log.e("Error "+Constants.ARCHIVE_STR,"" + Constants.SERVER_ERROR);
                    } else if (error instanceof NetworkError) {
                        Log.e("Error "+Constants.ARCHIVE_STR, "" + Constants.NETWORK_ERROR);
                    } else if (error instanceof ParseError) {
                        Log.e("Error "+Constants.ARCHIVE_STR,"" + Constants.PARSE_ERROR);
                    }
                }
            });
            retialarchiveJsonObjectRequest.setTag(Constants.ARCHIVE_STR);
            AppController.getInstance().getRequestQueue().add(retialarchiveJsonObjectRequest);
        } else {
            Toast.makeText(getActivity(), "" + getString(R.string.netUnavailable), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearQueue() {
        if (AppController.getInstance().getRequestQueue() != null) {
            AppController.getInstance().cancelPendingRequests(Constants.ARCHIVE_STR);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        clearQueue();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

