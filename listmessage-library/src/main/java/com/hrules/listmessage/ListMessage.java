package com.hrules.listmessage;

import android.animation.TimeInterpolator;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ScrollView;

public class ListMessage {
    private static final String TAG = "ListMessage";

    private RecyclerView recyclerView;
    private ListView listView;
    private ObservableScrollView observableScrollView;

    private ListMessageListener listener;

    private View view;
    private boolean viewVisible;
    private int viewHeight;

    private long duration;
    private TimeInterpolator showInterpolator;
    private TimeInterpolator hideInterpolator;

    public static class Duration {
        public static final long INFINITE = -1;
        public static final long SHORT = 2000;
        public static final long LONG = 3500;
    }

    public ListMessage(RecyclerView recyclerView, View view) {
        init(view);

        this.recyclerView = recyclerView;
        this.recyclerView.setOnScrollListener(recyclerViewScrollListener);
    }

    public ListMessage(RecyclerView recyclerView, View view, ListMessageListener listMessageListener) {
        this(recyclerView, view);
        setListener(listMessageListener);
    }

    public ListMessage(ListView listView, View view) {
        init(view);

        this.listView = listView;
        this.listView.setOnScrollListener(listViewViewScrollListener);
    }

    public ListMessage(ListView listView, View view, ListMessageListener listMessageListener) {
        this(listView, view);
        setListener(listMessageListener);
    }

    public ListMessage(ObservableScrollView observableScrollView, View view) {
        init(view);

        this.observableScrollView = observableScrollView;
        this.observableScrollView.setObservableScrollViewListener(observableScrollViewListener);
    }

    public ListMessage(ObservableScrollView observableScrollView, View view, ListMessageListener listMessageListener) {
        this(observableScrollView, view);
        setListener(listMessageListener);
    }

    private void init(View view) {
        if (view == null) {
            throw new IllegalArgumentException("View must not be null");
        }

        this.recyclerView = null;
        this.listView = null;
        this.observableScrollView = null;

        this.view = view;
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        viewHeight = this.view.getMeasuredHeight() * 2;
        viewVisible = true;
        hide();
        this.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                if (listener != null) {
                    listener.onClick();
                }
            }
        });

        duration = Duration.INFINITE;
        showInterpolator = new DecelerateInterpolator(2);
        hideInterpolator = new AccelerateInterpolator(2);
    }

    private void setListener(ListMessageListener listener) {
        this.listener = listener;
    }

    private final RecyclerView.OnScrollListener recyclerViewScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (viewVisible) {
                hide();
            }
        }
    };

    private final ListView.OnScrollListener listViewViewScrollListener = new ListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (viewVisible) {
                hide();
            }
        }
    };

    private final ObservableScrollViewListener observableScrollViewListener = new ObservableScrollViewListener() {
        @Override
        public void onScrollStateChanged(ScrollView scrollView, int newScrollState) {
        }

        @Override
        public void onScrolled(ScrollView scrollView, int dx, int dy) {
            if (viewVisible) {
                hide();
            }
        }

        @Override
        public void onScrollPositionChanged(float posx, float posy) {
        }

        @Override
        public void onScrollDown() {
        }

        @Override
        public void onScrollUp() {
        }
    };

    public void hide() {
        if (viewVisible) {
            view.animate().translationY(-viewHeight).setInterpolator(hideInterpolator).start();
            notifyOnHide();
        }
        viewVisible = false;
    }

    public void show() {
        if (!viewVisible) {
            view.animate().translationY(0).setInterpolator(showInterpolator).start();
            notifyOnShow();

            if (duration > 0) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        hide();
                    }
                }, duration);
            }
        }
        viewVisible = true;
        duration = Duration.INFINITE;
    }

    public void show(long duration) {
        this.duration = duration;
        show();
    }

    private void notifyOnHide() {
        if (listener != null) {
            listener.onHide();
        }
    }

    private void notifyOnShow() {
        if (listener != null) {
            listener.onShow();
        }
    }

    public boolean isVisible() {
        return viewVisible;
    }

    public TimeInterpolator getShowInterpolator() {
        return showInterpolator;
    }

    public void setShowInterpolator(TimeInterpolator showInterpolator) {
        this.showInterpolator = showInterpolator;
    }

    public TimeInterpolator getHideInterpolator() {
        return hideInterpolator;
    }

    public void setHideInterpolator(TimeInterpolator hideInterpolator) {
        this.hideInterpolator = hideInterpolator;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public ListView getListView() {
        return listView;
    }

    public ObservableScrollView getObservableScrollView() {
        return observableScrollView;
    }
}
