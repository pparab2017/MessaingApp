package com.amad.messaingapp.Entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pushparajparab on 9/20/17.
 */

public class Message  implements Parcelable {

    private int id, fromId, toID;
    private String content, regionID, time, read, Locked, fromName, toName;

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToname() {
        return toName;
    }

    public void setToname(String toname) {
        toName = toname;
    }

    public Message(){}

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toID=" + toID +
                ", content='" + content + '\'' +
                ", regionID='" + regionID + '\'' +
                ", time='" + time + '\'' +
                ", read='" + read + '\'' +
                ", Locked='" + Locked + '\'' +
                ", fromName='" + fromName + '\'' +
                ", toName='" + toName + '\'' +
                '}';
    }

    public Message(Parcel in) {
        id = in.readInt();
        fromId = in.readInt();
        toID = in.readInt();
        content = in.readString();
        regionID = in.readString();
        time = in.readString();
        read = in.readString();
        Locked = in.readString();
        fromName =in.readString();
        toName= in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToID() {
        return toID;
    }

    public void setToID(int toID) {
        this.toID = toID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRegionID() {
        return regionID;
    }

    public void setRegionID(String regionID) {
        this.regionID = regionID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getLocked() {
        return Locked;
    }

    public void setLocked(String locked) {
        Locked = locked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(fromId);
        dest.writeInt(toID);
        dest.writeString(content);
        dest.writeString(regionID);
        dest.writeString(time);
        dest.writeString(read);
        dest.writeString(Locked);
        dest.writeString(fromName);
        dest.writeString(toName);
    }
}
