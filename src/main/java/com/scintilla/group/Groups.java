package com.scintilla.group;

import java.util.HashMap;
import java.util.Map;

/**
 * Group collection.
 */
public class Groups {
    /**
     * Group map with string key and Group value.
     */
    private Map<String, Group> groups = new HashMap<>();

    /**
     * GetGroup returns the named group previously created with Group,
     * or null if there's no such group.
     *
     * @param name Group name.
     * @return Group object or null.
     */
    public synchronized Group getGroup(String name) {
        return groups.get(name);
    }

    public Map<String, Group> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, Group> groups) {
        this.groups = groups;
    }
}
