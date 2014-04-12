/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vimukthi.assignments.ocsf.simplechat.buddylist;

import java.util.HashMap;

/**
 *
 * @author jayangad
 */
public class BuddyListManager {

    HashMap<String, BuddyList> buddy_list_map;
    private static BuddyListManager instance;

    public static BuddyListManager getInstance() {
        if (instance == null) {
            instance = new BuddyListManager();
        }
        return instance;
    }

    private BuddyListManager() {
        buddy_list_map = new HashMap<String, BuddyList>();
    }

    public BuddyList create(String buddy_list_name) {
        BuddyList buddy_list = new BuddyList();
        buddy_list.setName(buddy_list_name);
        this.buddy_list_map.put(buddy_list_name, buddy_list);
        return buddy_list;
    }

    public void delete(String buddy_list_name) {
        this.buddy_list_map.remove(buddy_list_name);
    }

    public void addClientToBuddyList(String buddy_list_name, String client_name) {
        BuddyList buddyList = this.buddy_list_map.get(buddy_list_name);
        buddyList.addBuddy(client_name);
    }

    public void removeClientFromBuddyList(String buddy_list_name, String client_name) {
        BuddyList buddyList = this.buddy_list_map.get(buddy_list_name);
        buddyList.removeBuddy(client_name);
    }

    public BuddyList getBuddyListOfClient(String client) {
        for (BuddyList list : this.buddy_list_map.values()) {
            if (list.getBuddies().contains(client)) {
                return list;
            }
        }
        return null;
    }
}
