package com.miku.bubble.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ListUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * @author gmj23
 */
@Slf4j
public class ImportExcel {

    public static void main(String[] args) {
        // 写法1：JDK8+ ,不用额外写一个XinQiuTableUserInfoListener
        // since: 3.0.0-beta1
        String fileName = "D:\\Code\\JAVA_Project\\mikububble-backend\\src\\main\\resources\\testexcel.xls";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, XinQiuTableUserInfo.class, new TableListener()).sheet().doRead();
    }
    public static void main2() {

////
//        fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
//        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
//        EasyExcel.read(fileName, XinQiuTableUserInfo.class, new ReadListener<XinQiuTableUserInfo>() {
//            /**
//             * 单次缓存的数据量
//             */
//            public static final int BATCH_COUNT = 100;
//            /**
//             *临时存储
//             */
//            private List<XinQiuTableUserInfo> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
//
//            @Override
//            public void invoke(XinQiuTableUserInfo data, AnalysisContext context) {
//                cachedDataList.add(data);
//                if (cachedDataList.size() >= BATCH_COUNT) {
//                    saveData();
//                    // 存储完成清理 list
//                    cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
//                }
//            }
//
//            @Override
//            public void doAfterAllAnalysed(AnalysisContext context) {
//                saveData();
//            }
//
//            /**
//             * 加上存储数据库
//             */
//            private void saveData() {
//                log.info("{}条数据，开始存储数据库！", cachedDataList.size());
//                log.info("存储数据库成功！");
//            }
//        }).sheet().doRead();
//
//        // 有个很重要的点 XinQiuTableUserInfoListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
//        // 写法3：
//        fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
//        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
//        EasyExcel.read(fileName, XinQiuTableUserInfo.class, new XinQiuTableUserInfoListener()).sheet().doRead();
//
//        // 写法4
//        fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
//        // 一个文件一个reader
//        try (ExcelReader excelReader = EasyExcel.read(fileName, XinQiuTableUserInfo.class, new XinQiuTableUserInfoListener()).build()) {
//            // 构建一个sheet 这里可以指定名字或者no
//            ReadSheet readSheet = EasyExcel.readSheet(0).build();
//            // 读取一个sheet
//            excelReader.read(readSheet);
//        }
  }
}