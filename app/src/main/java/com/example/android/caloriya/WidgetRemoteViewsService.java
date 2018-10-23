package com.example.android.caloriya;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetRemoteViewsService  extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
