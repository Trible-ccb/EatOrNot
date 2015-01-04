package com.android.eatingornot.weibo;

import java.io.IOException;
import java.net.MalformedURLException;

import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;

public interface WeiboHandler {
    /**
     * 微博登陆
     */
    public void login();
    
    /**
     * 微博绑定
     */
    public void bangding(String uid);
    /**
     * 微博分享
     *
     * @param content 微博的内容
     * @param picturePath 微博图片路径(可选参数)
     */
    public boolean share(String content, String picturePath);

    /**
     * 获取最新的公共微博
     * @param weibo weibo实体
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws WeiboException
     */
    public String getPublicTimeline(Weibo weibo) throws MalformedURLException,
            IOException, WeiboException;

    /**
     * 上传图片并发布一条微博
     *
     * @param weibo 微博实体
     * @param source 采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey
     * @param file 要上传的图片，仅支持JPEG、GIF、PNG格式，图片大小小于5M
     * @param status 要发布的微博文本内容，必须做URLencode，内容不超过140个汉字。
     * @param lon 经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
     * @param lat 纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
     * @return
     * @throws WeiboException
     */
    public String upload(Weibo weibo, String source, String file,
            String status, String lon, String lat) throws WeiboException;

    /**
    * 发布一条新微博
    * @param weibo 微博实体
    * @param source 采用OAuth授权方式不需要此参数，其他授权方式为必填参数，数值为应用的AppKey。
    * @param status 要发布的微博文本内容，必须做URLencode，内容不超过140个汉字。
    * @param lon 经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
    * @param lat 纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
    * @return
    * @throws WeiboException
    */
   public String update(Weibo weibo, String source, String status,
           String lon, String lat) throws WeiboException;

}
