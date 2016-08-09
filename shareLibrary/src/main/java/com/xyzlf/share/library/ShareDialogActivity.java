package com.xyzlf.share.library;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyzlf.com.share.library.R;
import com.xyzlf.share.library.bean.ChannelEntity;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.ShareConstant;
import com.xyzlf.share.library.util.ChannelUtil;
import com.xyzlf.share.library.util.ShareUtil;
import com.xyzlf.share.library.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class ShareDialogActivity extends ShareBaseActivity implements AdapterView.OnItemClickListener {

    protected List<ChannelEntity> channelEntities;

    protected ShareEntity data;
    protected SparseArray<ShareEntity> sparseArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity_dialog);

        Object object;
        if (getIntent().hasExtra(ShareConstant.EXTRA_SHARE_DATA)) {
            Bundle bundle = getIntent().getBundleExtra(ShareConstant.EXTRA_SHARE_DATA);
            if (null != bundle) {
                object = bundle.get(ShareConstant.EXTRA_SHARE_DATA);
            } else {
                object = getIntent().getParcelableExtra(ShareConstant.EXTRA_SHARE_DATA);
                if (null == object) {
                    object = getIntent().getSerializableExtra(ShareConstant.EXTRA_SHARE_DATA);
                }
            }
        } else {
            object = getIntent().getData();
        }

        if (null == object) {
            ToastUtil.showToast(this, getString(R.string.share_empty_tip), true);
            finish();
            return;
        }

        if (object instanceof ShareEntity) {
            data = (ShareEntity) object;
        } else if (object instanceof SparseArray) {
            sparseArray = (SparseArray<ShareEntity>) object;
        }
        if (data == null && sparseArray == null) {
            ToastUtil.showToast(this, getString(R.string.share_empty_tip), true);
            finish();
            return;
        }

        initChannelData();
        if (channelEntities.isEmpty()) {
            finish();
            return;
        }
        initView();
    }

    private void initChannelData() {
        channelEntities = new ArrayList<>();
        /** weixin **/
        if (ChannelUtil.isWeixinInstall(this)) {
            if ((channel & ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND) > 0 && isShowChannel(ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND)) {
                channelEntities.add(new ChannelEntity(ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND, R.drawable.share_wechat, getString(R.string.share_channel_weixin_friend)));
            }
            if ((channel & ShareConstant.SHARE_CHANNEL_WEIXIN_CIRCLE) > 0 && isShowChannel(ShareConstant.SHARE_CHANNEL_WEIXIN_CIRCLE)) {
                channelEntities.add(new ChannelEntity(ShareConstant.SHARE_CHANNEL_WEIXIN_CIRCLE, R.drawable.share_wxcircle, getString(R.string.share_channel_weixin_circle)));
            }
        }
        /** QQ **/
        if (ChannelUtil.isQQInstall(this)) {
            if ((channel & ShareConstant.SHARE_CHANNEL_QQ) > 0 && isShowChannel(ShareConstant.SHARE_CHANNEL_QQ)) {
                channelEntities.add(new ChannelEntity(ShareConstant.SHARE_CHANNEL_QQ, R.drawable.share_qq, getString(R.string.share_channel_qq)));
            }
            if ((channel & ShareConstant.SHARE_CHANNEL_QZONE) > 0 && isShowChannel(ShareConstant.SHARE_CHANNEL_QZONE)) {
                channelEntities.add(new ChannelEntity(ShareConstant.SHARE_CHANNEL_QZONE, R.drawable.share_qzone, getString(R.string.share_channel_qzone)));
            }
        }
        /** weibo **/
        if ((channel & ShareConstant.SHARE_CHANNEL_SINA_WEIBO) > 0 && isShowChannel(ShareConstant.SHARE_CHANNEL_SINA_WEIBO)) {
            channelEntities.add(new ChannelEntity(ShareConstant.SHARE_CHANNEL_SINA_WEIBO, R.drawable.share_weibo, getString(R.string.share_channel_weibo)));
        }
        /** more **/
        if ((channel & ShareConstant.SHARE_CHANNEL_SYSTEM) > 0 && isShowChannel(ShareConstant.SHARE_CHANNEL_SYSTEM)) {
            channelEntities.add(new ChannelEntity(ShareConstant.SHARE_CHANNEL_SYSTEM, R.drawable.share_more, getString(R.string.share_channel_more)));
        }
    }

    private boolean isShowChannel(int channel) {
        if (sparseArray != null) {
            if (sparseArray.get(channel) != null) {
                return true;
            }
            return false;
        }
        return true;
    }

    private void initView() {
        AppGridAdapter adapter = new AppGridAdapter();
        GridView shareGridView = (GridView) findViewById(R.id.share_grid);
        shareGridView.setAdapter(adapter);
        shareGridView.setOnItemClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ShareConstant.REQUEST_CODE) {
            if (null != data) {
                int channel = data.getIntExtra(ShareConstant.EXTRA_SHARE_CHANNEL, -1);
                int status = data.getIntExtra(ShareConstant.EXTRA_SHARE_STATUS, -1);
                finishWithResult(channel, status);
                return;
            }
        }
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ChannelEntity data = ((ChannelEntity) parent.getAdapter().getItem(position));
        if (null == data) {
            return;
        }
        handleShare(data.getchannel());
    }
    /**
     * 分享
     * @param channel {@link ShareConstant#SHARE_CHANNEL_ALL}
     */
    protected void handleShare(int channel) {
        switch (channel) {
            case ShareConstant.SHARE_CHANNEL_QQ:
                shareByQQ();
                break;
            case ShareConstant.SHARE_CHANNEL_QZONE:
                shareByQZone();
                break;

            case ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND:
                shareByWeixinFriend();
                break;
            case ShareConstant.SHARE_CHANNEL_WEIXIN_CIRCLE:
                shareByWeixinCircle();
                break;

            case ShareConstant.SHARE_CHANNEL_SINA_WEIBO:
                shareBySinaWeibo();
                break;

            case ShareConstant.SHARE_CHANNEL_SYSTEM:
                shareBySystem();
                finish();
                break;

            case ShareConstant.SHARE_CHANNEL_SMS:
                shareBySms();
                break;

            case ShareConstant.SHARE_CHANNEL_EMAIL:
                shareByEmail();
                break;
        }
    }
    /**
     * 分享QQ好友
     */
    protected void shareByQQ() {
        ShareUtil.startShare(this, ShareConstant.SHARE_CHANNEL_QQ, getShareData(ShareConstant.SHARE_CHANNEL_QQ), ShareConstant.REQUEST_CODE);
    }
    /**
     * 分享到QQ空间
     */
    protected void shareByQZone() {
        ShareUtil.startShare(this, ShareConstant.SHARE_CHANNEL_QZONE, getShareData(ShareConstant.SHARE_CHANNEL_QZONE), ShareConstant.REQUEST_CODE);
    }
    /**
     * 分享微信好友
     */
    protected void shareByWeixinFriend() {
        ShareUtil.startShare(this, ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND, getShareData(ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND), ShareConstant.REQUEST_CODE);
    }
    /**
     * share to weixin circle
     */
    protected void shareByWeixinCircle() {
        ShareUtil.startShare(this, ShareConstant.SHARE_CHANNEL_WEIXIN_CIRCLE, getShareData(ShareConstant.SHARE_CHANNEL_WEIXIN_CIRCLE), ShareConstant.REQUEST_CODE);
    }
    /**
     * share more
     */
    protected void shareBySystem() {
        ShareUtil.startShare(this, ShareConstant.SHARE_CHANNEL_SYSTEM, getShareData(ShareConstant.SHARE_CHANNEL_SYSTEM), ShareConstant.REQUEST_CODE);
    }
    /**
     * share sms
     */
    protected void shareBySms() {
        ShareUtil.startShare(this, ShareConstant.SHARE_CHANNEL_SMS, getShareData(ShareConstant.SHARE_CHANNEL_SMS), ShareConstant.REQUEST_CODE);
    }
    /**
     * share email
     */
    protected void shareByEmail() {
        ShareUtil.startShare(this, ShareConstant.SHARE_CHANNEL_EMAIL, getShareData(ShareConstant.SHARE_CHANNEL_EMAIL), ShareConstant.REQUEST_CODE);
    }
    /**
     * share weibo
     */
    protected void shareBySinaWeibo() {
        ShareUtil.startShare(this, ShareConstant.SHARE_CHANNEL_SINA_WEIBO, getShareData(ShareConstant.SHARE_CHANNEL_SINA_WEIBO), ShareConstant.REQUEST_CODE);
    }

    protected ShareEntity getShareData(int shareChannel) {
        if (data != null) {
            return data;
        }
        if (sparseArray != null) {
            return sparseArray.get(shareChannel);
        }
        return null;
    }

    private class AppGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return channelEntities.size();
        }

        @Override
        public ChannelEntity getItem(int position) {
            return channelEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.share_gridview_item, parent, false);
                holder.textView = (TextView) convertView.findViewById(R.id.text);
                holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ChannelEntity entity = getItem(position);
            holder.imageView.setImageResource(entity.getIcon());
            holder.textView.setText(entity.getName());
            return convertView;
        }
    }

    private class ViewHolder {
        public TextView textView;
        public ImageView imageView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //API小于11 点击外部消失
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && isOutOfBounds(this, event)) {
                finish();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isOutOfBounds(Activity context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = context.getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop)) || (y > (decorView.getHeight() + slop));
    }


}
