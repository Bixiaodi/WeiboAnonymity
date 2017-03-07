package indi.anonymity.elements;

import com.sun.xml.internal.rngom.parse.host.Base;

/**
 * Created by zp on 04/03/2017.
 */
public class BaseVertex {

    protected int id;
    protected int round;
    protected String groupId;

    public BaseVertex() {
        this.id = 0;
        this.round = 0;
        this.groupId = "";

    }

    public BaseVertex(int id) {
        this.id = id;
        this.round = 0;
        this.groupId = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BaseVertex) {
            BaseVertex bv = (BaseVertex)obj;
            return id == bv.id && round == bv.round;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new Integer(id + round).hashCode();
    }
}
