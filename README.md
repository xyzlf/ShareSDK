# ShareSDK
分享SDK，包括微信、朋友圈，微博，QQ，QQ空间等渠道分享。

# Gradle
    
    compile ('com.xyzlf.share:sharesdk:0.0.7') {
    	exclude group: 'com.android.support', module: 'appcompat-v7'
    }


# 效果图
<img src="share.png"/>

# 使用方式

1、调用分享界面：

	public void showShareDialog() {
        ShareEntity testBean = new ShareEntity("我是标题", "我是内容，描述内容。");
        testBean.setUrl("https://www.baidu.com"); //分享链接
        testBean.setImgUrl("https://www.baidu.com/img/bd_logo1.png");
        ShareUtil.showShareDialog(getActivity(), testBean, ShareConstant.REQUEST_CODE);
    }

2、使用分享功能，如下实例 使用QQ分享功能：

	public void startShare() {
        ShareEntity testBean = new ShareEntity("我是标题", "我是内容，描述内容。");
        testBean.setUrl("https://www.baidu.com"); //分享链接
        testBean.setImgUrl("https://www.baidu.com/img/bd_logo1.png");
        ShareUtil.startShare(getActivity(), ShareConstant.SHARE_CHANNEL_QQ, testBean, ShareConstant.REQUEST_CODE);
    }

3、处理回调：

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 分享回调处理
         */
        if (requestCode == ShareConstant.REQUEST_CODE) {
            if (data != null) {
                int channel = data.getIntExtra(ShareConstant.EXTRA_SHARE_CHANNEL, -1);
                int status = data.getIntExtra(ShareConstant.EXTRA_SHARE_STATUS, -1);
                onShareCallback(channel, status);
            }
        }
    }

详细使用说明，可以参照上面的使用说明文档。

# 遇到的坑

分享SDK踩过的坑，请参照：<http://xyzlf.github.io/2016/07/01/android-share-sdk-1.html>

配合使用说明文档，在配置调用方面应该不会有啥问题了。

# 更新日志

**0.0.7 支持分享大图，修复分享本地图片

ShareEntity testBean = new ShareEntity("我是标题", "我是内容，描述内容。");

1、去除了testBean.setBitmap(bitmap)方法，因为ShareEntity是通过Intent传递，如果bitmap稍大，就会出现异常。
2、增加了分享大图属性 testBean.setShareBigImg(true);

实例：

	 /**
     * 分享大图，大图分享支持，微信，微信朋友圈，微博，QQ，其他渠道不支持
     *
     * 分享大图注意点
     * 1、setShareBigImg为ture
     * 2、QQ分享大图，只能是本地图片
     */
    public void shareBigImg() {
        ShareEntity testBean = new ShareEntity("", "");
        testBean.setShareBigImg(true);
		// testBean.setImgUrl("https://www.baidu.com/img/bd_logo1.png"); // 网络地址
        testBean.setImgUrl("/storage/sdcard0/Android/data/com.xyzlf.share/files/com.xyzlf.share_share_pic.png"); //本地地址

        /** 如果你要分享的图片是Bitmap，你可以如下使用 **/
		// Bitmap bitmap = null;
		// String filePath = ShareUtil.saveBitmapToSDCard(this, bitmap);
		// testBean.setImgUrl(filePath);

        int channel = ShareConstant.SHARE_CHANNEL_WEIXIN_FRIEND | ShareConstant.SHARE_CHANNEL_WEIXIN_CIRCLE | ShareConstant.SHARE_CHANNEL_SINA_WEIBO | ShareConstant.SHARE_CHANNEL_QQ;
        ShareUtil.showShareDialog(this, channel, testBean, ShareConstant.REQUEST_CODE);
    }


**0.0.6 支持本地图片分享**

ShareEntity testBean = new ShareEntity("我是标题", "我是内容，描述内容。");

1、本地drawable图片，使用方式：testBean.setDrawableId(R.drawable.icon);

2、本地手机SD卡图片，使用方式：testBean.setImgUrl("/sdpath/filepath/icon.png");

3、本地代码截屏Bitmap，使用方式：testBean.setBitmap(bitmap);

# 关于我
有任何使用问题，可以给我发邮件：

Author：张利峰

E-mail：519578280@qq.com

# License

    Copyright(c)2016 xyzlf Open Source Project
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.