package com.cruxbd.master_planner_pro.di;


import com.cruxbd.master_planner_pro.di.module.RealmChannel;
import com.cruxbd.master_planner_pro.di.scope.AppScope;
import com.squareup.picasso.Picasso;

import dagger.Component;
import io.realm.Realm;

@AppScope
@Component(modules = { RealmChannel.class})
public interface AppComponent {
    Realm GetRealm();

}
