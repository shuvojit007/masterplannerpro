package com.cruxbd.master_planner_pro.view.activities.nav_rec.rec_model;

public class childModel  {
    private String title;
   private int icon,dropdown,add;


    public childModel(String title, int icon, int dropdown, int add) {
        this.title = title;
        this.icon = icon;
        this.dropdown = dropdown;
        this.add = add;
    }


    public childModel(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public childModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getDropdown() {
        return dropdown;
    }

    public void setDropdown(int dropdown) {
        this.dropdown = dropdown;
    }

    public int getAdd() {
        return add;
    }

    public void setAdd(int add) {
        this.add = add;
    }
}
