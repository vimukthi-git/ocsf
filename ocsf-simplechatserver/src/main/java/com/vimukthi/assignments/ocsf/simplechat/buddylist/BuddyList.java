/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vimukthi.assignments.ocsf.simplechat.buddylist;

import java.util.ArrayList;

/**
 *
 * @author jayangad
 */
public class BuddyList {

    private ArrayList<String> buddies;
    private String name;

    public BuddyList() {
        name = "";
        buddies = new ArrayList<String>();
    }

    public void addBuddy(String buddy) {
        this.getBuddies().add(buddy);
    }

    public void removeBuddy(String buddy) {
        this.getBuddies().remove(buddy);
    }

    public ArrayList<String> getBuddies() {
        return buddies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
