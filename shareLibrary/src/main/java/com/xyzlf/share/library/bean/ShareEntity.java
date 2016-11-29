package com.xyzlf.share.library.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

/**
 * Created by zhanglifeng
 */
public class ShareEntity implements Parcelable {

    private String title;
    private String content;
    private String url;

    private String imgUrl;
    private int drawableId;
    private boolean isShareBigImg;

    public ShareEntity(String title, String content) {
        this(title, content, null);
    }

    public ShareEntity(String title, String content, String url) {
        this(title, content, url, null);
    }

    public ShareEntity(String title, String content, String url, String imgUrl) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setDrawableId(@DrawableRes int drawableId) {
        this.drawableId = drawableId;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setShareBigImg(boolean shareBigImg) {
        isShareBigImg = shareBigImg;
    }

    public boolean isShareBigImg() {
        return isShareBigImg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShareEntity> CREATOR = new Creator<ShareEntity>() {
        @Override
        public ShareEntity createFromParcel(Parcel in) {
            return new ShareEntity(in);
        }

        @Override
        public ShareEntity[] newArray(int size) {
            return new ShareEntity[size];
        }
    };

    protected ShareEntity(Parcel in) {
        title = in.readString();
        content = in.readString();
        url = in.readString();
        imgUrl = in.readString();
        drawableId = in.readInt();
        isShareBigImg = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(url);
        dest.writeString(imgUrl);
        dest.writeInt(drawableId);
        dest.writeByte((byte) (isShareBigImg ? 1 : 0));
    }
}
