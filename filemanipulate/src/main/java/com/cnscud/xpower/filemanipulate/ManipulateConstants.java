package com.cnscud.xpower.filemanipulate;

import com.cnscud.xpower.filemanipulate.image.IImageSize;
import com.cnscud.xpower.filemanipulate.image.ImageNameSize;
import org.apache.commons.lang.StringUtils;

import com.cnscud.xpower.filemanipulate.image.DefaultImageSize;
import com.cnscud.xpower.filemanipulate.image.ImageUtils;

/**
 * 图像常量.
 * 
 * @author Felix Zhang Date 2012-10-23 17:14
 * @version 1.0.0
 */
public class ManipulateConstants {

    public static boolean KeepAvatarImageFormat = false; // 是否保持头像文件的原格式
    public static boolean KeepGeneralImageFormat = true; // 是否保持普通图像文件的原格式

    public static String DefaultImageFormat = "jpg";
    public static String[] SupportImageFormats = { "jpg", "gif", "png" }; // 存储支持的文件格式
    public static String[] SupportBaseImageFormats = { "jpg", "png" }; // 支持生成2000*2000文件的格式
    // public static String JPGImageFormat = "jpg";

    // 最大的基准图
    public static int IMAGE_BASE_WIDTH = 2000; // 最大宽度
    public static int IMAGE_BASE_HEIGHT = 2000; // 最大高度
    public static IImageSize BASE_SOURCE_SIZE = new ImageNameSize("base", IMAGE_BASE_WIDTH, IMAGE_BASE_HEIGHT);

    public static int IMAGE_MIN_WIDTH_WATERMARK = 330;
    public static int IMAGE_MIN_HEIGHT_WATERMARK = 240;

    public static String IMAGE_SOURCE_NAME = "o"; // 源文件名
    public static String IMAGE_ORIGINAL_NAME = "oo"; // 如果生成了一个缩减版源文件, 此文件后缀为真正的raw文件

    public static String ShortName_Avatar = "a"; // 头像目录前缀
    public static String ShortName_GeneralImage = "m"; // 普通图像目录前缀
    public static String ShortName_OperationImage = "y"; // 运营图像目录前缀
    public static String ShortName_CaptureImage = "s"; //抓取图像
    public static String ShortName_GeneralFile = "f"; //普通文件
    public static String ShortName_POIImage = "p"; //poi 图像
    public static String ShortName_Visa = "visa-";//签证和护照照片的前缀
    public static String ShortName_GopLayer = "gop-";//签证和护照照片的前缀
    public static String ShortName_APPOFFLINEDATA = "xt";//行程大师离线数据
    public static String ShortName_USERCARE = "lv";//用户关怀一封信图像

    public static String ShortName_InnerFile = "inn-"; //内网安全文件
    public static String ShortName_Airticket = "air-"; //机票平台

    public static double Default_Quality = 0.95;


    /*
    图像系列命名规范: ******************************************************
        1. f开头表示要充满裁切, 会裁剪图像.
        2. b结尾表示大图, 用来标识图像尺寸 (如果同时存在fb和b, 以b为准).
        3. 简称后缀不能重复.
        4. 尺寸设为0表示不限制, 以另外一个维度做约束
        5. 如果不是f开头, 且同时指定了宽高, 则会按比例变化, 不会强制拉伸
        6. 如果需要强制拉伸, 则设置 force = true  (名字规则待定!)
    **********************************************************************/


    //最原始的图像系列
    public static final IImageSize PICTURE_BIGSIZE = new DefaultImageSize("b", 900, 0); // 大
    public static final IImageSize PICTURE_HEADSIZE = new DefaultImageSize("fb", 630, 420); // 头条
    public static final IImageSize PICTURE_MEDIUMSIZE = new DefaultImageSize("m", 591, 0); // 中
    public static final IImageSize PICTURE_COVERSIZE = new DefaultImageSize("fc", 315, 210); // 封面
    public static final IImageSize PICTURE_SMALLSIZE = new DefaultImageSize("fs", 150, 100); // 小

    public static final IImageSize PICTURE_TRIP_COVERSIZE = new DefaultImageSize("fbc", 990, 280); //行程头图
    //
    public static final IImageSize[] PICTURE_SIZES = new IImageSize[] { PICTURE_BIGSIZE, PICTURE_MEDIUMSIZE, PICTURE_SMALLSIZE };
    
    /**通用的图片尺寸 "b:900x0,fb:630x420,m:591x0,fc:315x210,fs:150x100" */
    public static final IImageSize[] GENERAL_PICTURE_SIZES = ImageUtils.parseImageSizes("b:900x0,m:591x0,fb:630x420,fs:150x100,fc:315x210");
           // new IImageSize[] { PICTURE_BIGSIZE, PICTURE_MEDIUMSIZE, PICTURE_HEADSIZE, PICTURE_SMALLSIZE, PICTURE_COVERSIZE };

    // 全尺寸
    public static final IImageSize[] FULL_SIZE = new IImageSize[] { PICTURE_BIGSIZE, PICTURE_HEADSIZE, PICTURE_MEDIUMSIZE, PICTURE_COVERSIZE, PICTURE_SMALLSIZE };

    public static final IImageSize MOBILE_PICTURE_BIGSIZE = new DefaultImageSize("ab", 1024, 0);  //大
    public static final IImageSize MOBILE_PICTURE_MEDIUMSIZE = new DefaultImageSize("am", 512, 0);  //中
    public static final IImageSize MOBILE_PICTURE_MEDIUMICON = new DefaultImageSize("fam", 400, 400);   //列表中图
    public static final IImageSize MOBILE_PICTURE_SMALLSIZE = new DefaultImageSize("as", 256, 0); //小
    public static final IImageSize MOBILE_PICTURE_SMALLICON = new DefaultImageSize("fas", 200, 200);    //列表小图
    // 移动端图片尺寸
    public static final IImageSize[] MOBILE_SIZE = new IImageSize[]{MOBILE_PICTURE_BIGSIZE, MOBILE_PICTURE_MEDIUMSIZE, MOBILE_PICTURE_MEDIUMICON,
            MOBILE_PICTURE_SMALLSIZE, MOBILE_PICTURE_SMALLICON};

    //达人工作室
    public static final IImageSize WORKSHOP_COVER_LIMIT_SIZE = new DefaultImageSize("cb",1920,340);
    public static final IImageSize WORKSHOP_LOGO_LIMIT_SIZE = new DefaultImageSize("db",130,130);

    public static final IImageSize WORKSHOP_COVER_LARGE = new DefaultImageSize("fcl",1920,200);  //达人工作室pc管理页背景头图
    public static final IImageSize WORKSHOP_COVER_BIG = new DefaultImageSize("fcb",960,340);
    public static final IImageSize WORKSHOP_COVER_MEDIUM = new DefaultImageSize("fcm",960,90);
    public static final IImageSize WORKSHOP_COVER_SMALL = new DefaultImageSize("fcs",640,340);   //达人工作室h5头图
    public static final IImageSize WORKSHOP_COVER_TINY = new DefaultImageSize("fct",640,90);     //达人工作室列表页头图

    public static final IImageSize WORKSHOP_LOGO_BIG = new DefaultImageSize("fdb",130,130);    //达人工作室首页的logo
    public static final IImageSize WORKSHOP_LOGO_MEDIUM = new DefaultImageSize("fdm",80,80);   //达人工作室基本信息的工作室logo
    public static final IImageSize WORKSHOP_LOGO_SMALL = new DefaultImageSize("fds",40,40);

    /* ========================A计划图片尺寸========================== */
    public static final IImageSize PACK_COVER_BIG = new DefaultImageSize("feb",750,500);                   //封面图片
    public static final IImageSize PACK_COVER_MEDIUM = new DefaultImageSize("fem",375,250);
    public static final IImageSize PACK_COVER_SMALL = new DefaultImageSize("fes",150,100);

    public static final IImageSize PACK_TITLEPIC = new DefaultImageSize("fgb",1920,390);                   //头图
    public static final IImageSize PACK_H5_TITLEPIC = new DefaultImageSize("fgm",640,370);                 //h5头图
    public static final IImageSize PACK_CARTOONMAP = new DefaultImageSize("hb",990,0);                    //卡通地图
    public static final IImageSize PACK_H5_CARTOONMAP = new DefaultImageSize("hm",640,0);                 //h5卡通地图

    //明星出行图片尺寸
    public static final IImageSize STAR_TRAVEL_BIG = new DefaultImageSize("fib",634,534);
    public static final IImageSize STAR_TRAVEL_MEDIUM = new DefaultImageSize("im",640,0);
    //明星出行微博评论图片
    public static final IImageSize STAR_TRAVEL_WEIBO_BIG = new DefaultImageSize("fjb",285,330);
    //APP推荐的图片尺寸
    public static final IImageSize APP_RECOMMEND_BIG = new DefaultImageSize("fkb",1080,810);
    //APP推荐的广告图片尺寸
    public static final IImageSize APP_RECOMMEND_ADVERTISEMENT_BIG = new DefaultImageSize("flb",1080,960);
    //活动聚合页
    public static final IImageSize ACT_COLLECTION = new DefaultImageSize("fnb",750,250);

    //CPS系统尺寸
    public static final IImageSize CPS_SIGNING_SMALL = new DefaultImageSize("os", 160, 0);
    public static final IImageSize CPS_SIGNING_BIG = new DefaultImageSize("ob", 640, 0);

    //
    //  非系统通用尺寸, 不需要定义在此!!!!!!!!!!!!!!!!
    //  非系统通用尺寸, 不需要定义在此!!!!!!!!!!!!!!!!
    //  非系统通用尺寸, 不需要定义在此!!!!!!!!!!!!!!!!
    //  非系统通用尺寸, 不需要定义在此!!!!!!!!!!!!!!!!
    //  非系统通用尺寸, 不需要定义在此!!!!!!!!!!!!!!!!
    //
    //

    // =================================头像系列=========================
    public static final IImageSize AVATAR_BIGSIZE = new DefaultImageSize("b", 180, 180);
    public static final IImageSize AVATAR_MEDIUMSIZE = new DefaultImageSize("m", 48, 48);
    public static final IImageSize AVATAR_SMALLSIZE = new DefaultImageSize("s", 24, 24);

    // 大,中,小
    public static final IImageSize[] AVATAR_SIZES = new IImageSize[] { AVATAR_BIGSIZE, AVATAR_MEDIUMSIZE, AVATAR_SMALLSIZE };




    public static String WaterMark1 = "wm1.png";
    public static String WaterMark_Postion_Default = "BOTTOM_RIGHT";
    public static float WaterMark_Opacity = 1.0f;
    public static String Default_WaterMark = "wm1.png|BOTTOM_RIGHT";

    public static int IMAGE_ERROR = 1101; // 图像转换错误
    public static int IMAGE_BADFORMAT = 1111; // 格式错误
    public static int IMAGE_TOOBIG = 1112; // 图像太大
    public static int IMAGE_TOOSMALL = 1113; // 图像太小
    public static int IMAGE_BADPARA = 1114; // 参数错误

    public static int FTP_UPLOAD_ERROR = 1201; // ftp upload failed
    public static int FTP_UPLOAD_NOSERVER = 1211; // 找不到可用server

    public static int FTP_DELETE_ERROR = 1220;

    public static int SUCCESS = 0;
    public static int FAIL=999;//失败



    /**
     * 替换一张图片为指定尺寸的图片
     * 
     * @param image
     *            图片地址，例如：http://f2.sjbly.cn/m13/0504/1606/viviwanzru_426x583_fs.jpg
     * @param size
     *            图片尺寸参数,可选值为：b,m,s,fb,fc,fs
     * @return 新尺寸的图片地址，例如：http://f2.sjbly.cn/m13/0504/1606/viviwanzru_426x583_fc.jpg
     */
    public static String replace(String image, String size) {
        if (StringUtils.isBlank(image)) {
            return image;
        }
        int lastSlash = image.lastIndexOf('/');
        if (lastSlash > 0 && lastSlash < image.length()) {
            String uriPrefix = image.substring(0, lastSlash);
            String filename = image.substring(lastSlash);
            if ("o".equals(size)) {
                filename = filename.replaceFirst("_[^\\./]+", "_o");
            } else {
                filename = filename.replaceFirst("_[^_/]+\\.", "_" + size + ".");
            }
            return uriPrefix + filename;
        }
        return image;
    }

    /**
     * 替换一张图片为指定尺寸的图片
     * 
     * @param image
     *            图片地址
     * @param size
     *            图片尺寸参数
     * @return 新尺寸的图片地址
     */
    public static String replace(String image, IImageSize size) {
        return replace(image, size.getSuffix());
    }

    /**
     * 判断图片是否属于某个系列
     * 比如_fas.jpg,属于a系列
     * @param image http://f2.sjbly.cn/m13/0504/1606/viviwanzru_426x583_fas.jpg
     * @param appendix a
     * @return
     */
    public static boolean inCategory(String image, String appendix) {
        return image.matches(".*_[^_/]*" + appendix + "[^_/]+\\..*");
    }
}
