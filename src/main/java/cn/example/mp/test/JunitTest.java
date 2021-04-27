package cn.example.mp.test;
import cn.example.mp.test.annotation.JTestAnno;
import cn.example.mp.test.annotation.PropertyName;
import cn.example.mp.test.constant.SystemConstant;
import cn.example.mp.test.fileparam.FileResponse;
import cn.example.mp.test.util.*;
import com.alibaba.fastjson.JSON;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.xuggler.IStreamCoder;
import org.apache.commons.io.FileUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Listeners({ExtentTestNGIReporterListener.class})
public class JunitTest {

    private static int total = 1000000;


    @Test
    public void propertiesTest(){
        String filePath = null;
        try {
            String s = "aaaaaaaaaaa";
            filePath = PropertiesUtil.getProperties("config.properties", "report_temp_dir");
            String fileName = "adsafdsa.txt";
            FileWriter writer = new FileWriter(filePath+fileName);
            writer.write(s);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(filePath);
    }

    @Test
    @JTestAnno("测试文件上传")
    public void fileDownLoadTest() throws IOException, URISyntaxException {
        String downloadUrl = "http://192.168.2.33:8091/api/bsp/fileManager/fileinfo/download";
        //17位时间格式 例:202004201139234
        String replace = DateUtil.formatDate(new Date(), DateUtil.SIMPLE_DATE_PATTERN3)
                .replace("-", "")
                .replace(" ", "")
                .replace(":", "")
                .replace(".","");
        //系统流水号
        String sNo = "S"+"CIM"+replace+UUID.randomUUID().toString().substring(0,5);
        //全局流水号
        String gNo = "G"+"CIM"+replace+UUID.randomUUID().toString().substring(0,5);
        Map<String,String > paraMap = new HashMap<String,String>();

      /*  String fileName = "ff80808171beb8150171bede30290007.txt";
        String fileFid = "wKgChF6npXaAS5t3AAJl5jFQsz8453.txt";*/
        String fileName = "404.png";
        //String fileFid = "wKgChV6EN02Ad-FyAAD5QMTRD3M986.png";

        String fileFid = "wKgB8F6ewIaAV4JXAAD5QMTRD3M711.png";


        paraMap.put("fileName",fileName);
        paraMap.put("fileFdfsName",fileFid);
        paraMap.put("svcRqsSysEnShrtName","CIM");
        paraMap.put("svcRqsSysTxnTimpstamp",DateUtil.formatDate(new Date(),DateUtil.SIMPLE_DATE_PATTERN3));
        paraMap.put("svcRqsSysSerNo",sNo);
        paraMap.put("globalSerialNo",gNo);
        paraMap.put("origSvcRqsSysEnShrtName","BSP");
        paraMap.put("origSvcRqsSysTxnTimpstamp",DateUtil.formatDate(new Date(),DateUtil.SIMPLE_DATE_PATTERN3));
        paraMap.put("flagStatus","");
        paraMap.put("uploadFileDate",DateUtil.formatDate5(new Date()));

        byte []  result = HttpClientUtil.sendGet(downloadUrl, paraMap, "utf-8");
        String bResult = new String(result);
        InputStream inputStream = null;
        if(bResult.contains("retCode")){
            System.out.println("下载文件失败");
        }else{
            inputStream = new ByteArrayInputStream(result);
        }
        OutputStream out = null;
        File file = new File("E:\\"+"mm7.txt");
        if(!file.exists()){
            file.createNewFile();
        }
        out = new FileOutputStream(file);
        byte[] buffer= new byte[1024];
        int len = 0;
        while ((len=inputStream.read(buffer))!=-1){
            out.write(buffer,0,len);
        }
        if(inputStream!=null){
            inputStream.close();
        }
        if(out!=null){
            out.close();
        }
    }

    @Test
    @JTestAnno("测试文件下载")
    public void fileUploadTest() throws IOException {

        String uploadUrl = "http://192.168.2.33:8091/api/bsp/fileManager/fileinfo/upload";
        String replace = DateUtil.formatDate(new Date(), DateUtil.SIMPLE_DATE_PATTERN3)
                .replace("-", "")
                .replace(" ", "")
                .replace(":", "")
                .replace(".","");
        //系统流水号
        String sNo = "S"+"CIM"+replace+ UUID.randomUUID().toString().substring(0,5);
        //全局流水号
        String gNo = "G"+"CIM"+replace+UUID.randomUUID().toString().substring(0,5);

        String fileName = "40280a8171b62aa60171b646c9a50003.txt";
        String path = "E:\\"+fileName;
        MultipartFile mFile = FileUtil.getMulFileByPath(path,fileName);
        String originalFilename = mFile.getOriginalFilename();
        long size = mFile.getSize();
        Map<String,Object> upParams = new HashMap<String,Object>();
        upParams.put("file",mFile);
        upParams.put("fileName",fileName);
        upParams.put("fileMd5", FileMd5Util.getFileMd5(mFile));
        upParams.put("batchNo","CIM"+System.currentTimeMillis());
        upParams.put("flag","0");
        upParams.put("svcRqsSysEnShrtName","CIM");
        upParams.put("svcRqsSysTxnTimpstamp", DateUtil.formatDate(new Date(),DateUtil.SIMPLE_DATE_PATTERN3));
        upParams.put("svcRqsSysSerNo",sNo);
        upParams.put("globalSerialNo",gNo);
        upParams.put("origSvcRqsSysEnShrtName","BSP");
        upParams.put("origSvcRqsSysTxnTimpstamp",DateUtil.formatDate(new Date(),DateUtil.SIMPLE_DATE_PATTERN3));
        byte [] response = HttpClientUtil.sendPost(uploadUrl, upParams, "utf-8");
        String result = new String(response);
        String s = result.replace("null", "\"null\"");
        FileResponse fileResponse1 = JSON.parseObject(s, FileResponse.class);
        String retUrl = fileResponse1.getRetEntity();
    }

    @Test
    @JTestAnno("测试文件写出")
    public void testFileOut(){

        String result = "<reqID>40280a8171b437c80171b437c8cf0000</reqID><resCode>000000</resCode><resMsg>查询成功</resMsg>";
        String fileName = "ppppp"+".txt";
        String filePath = "E:\\";
        try {
            File file = new File(filePath+fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.write(result);
            writer.flush();
            writer.close();
            if(!file.exists()){
                file.createNewFile();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    @JTestAnno("测试截取时间")
    public void testDateSub(){
        String startDate  = "20200227";
        String endDate = "20200429";
        StringBuffer sb = new StringBuffer(startDate);
        sb.insert(4,"-");
        sb.insert(7,"-");

        String DStr= sb.toString()+" 23:59:59";
        System.out.println();
        Date date = DateUtil.stringToDate(DStr, "yyyy-MM-dd HH:mm:ss");
        System.out.println(date.toString());

    }


    @Test
    @JTestAnno("无聊的测试")
    public void testIO(){
        String fileName = "ppppp"+".txt";
        String filePath = "E:\\aa";
        File file = new File(filePath+fileName);
    }

    @Test
    public void testIPUtil(){

      /*  String v4IP = IpUtil.getV4IP();
        System.out.println(v4IP);*/

        String str = "hello word";
        int w = str.indexOf("w");
        System.out.println(w);

        int w1 = str.indexOf('w');
        System.out.println(w1);


    }


    /**
     * 布隆过滤器
     */
    @Test
    @JTestAnno("测试布隆过滤器")
    public void testBloomFilter(){

        BloomFilter<Integer> bf = BloomFilter.create(Funnels.integerFunnel(), total,0.003);

        //往布隆过滤器放入100万数据
        for (int i = 0 ; i <total;i++){
            bf.put(i);
        }
        //检测存在过滤器中的元素误判率，存在过滤器中的元素逃脱率0
        for(int i= 0;i<total;i++){
            if(!bf.mightContain(i)){
                System.out.println("逃脱");
            }
        }

        //1万个不在过滤器中的元素，被误判了320个存在，误判率0.032
        int count = 0;
        for(int i = total ;i<total+10000;i++){
            if(bf.mightContain(i)){
                count++;
            }
        }

        System.out.println("误伤:"+count);
    }

    /**
     * 不能再forEach循环中对集合进行add/remove
     */
    @Test
    @JTestAnno("forEach循环中对集合进行add/remove")
    public void testRemove(){
        List<String> ls = new ArrayList<String>();
        ls.add("11");
        ls.add("22");
        ls.add("33");
        ls.add("44");
        /*for (String l : ls) {
            if ("11".equals(l)){
                ls.remove(l);
            }
        }
*/
        Iterator<String> iterator = ls.iterator();
        while (iterator.hasNext()){
            String next = iterator.next();
            if (next.equals("11")){
                iterator.remove();
            }
        }

        for (String l : ls) {
            System.out.println(l);
        }
    }

    @Test
    public void testTreadPollManager(){
        ThreadPoolManager.newInstance().addExecuteTask(()-> {
            try {
                PropertiesUtil.getProperties("config.properties");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        //任务队列指定大小Integer.MAX_VALUE，会出现OOM问题
        ThreadPoolExecutor singleThreadExecutor = (ThreadPoolExecutor) Executors.newSingleThreadExecutor();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        //最大线程数(maximumPoolSize)指定Integer.MAX_VALUE，会出现OOM
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        ExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(20);

    }

    @Test
    @JTestAnno("测试截取时间")
    public void testDate(){

        //String yearOnYearTime = SasLagerScreenUtil.getYearOnYearTime("2020-05", "yyyy-MM", "yyyy-MM");
        //System.out.println(yearOnYearTime);

        String yearOnYearBeginTime = SasLagerScreenUtil.getYearOnYearBeginTime("2020-05", "yyyy-MM", "yyyy-MM");
        System.out.println(yearOnYearBeginTime);
        String lastDayOfLastYear = SasLagerScreenUtil.getLastDayOfLastYear("2020-05", "yyyy-MM", "yyyy-MM");
        System.out.println(lastDayOfLastYear);

    }


    @Test
    public void testCon(){

        List<String> aList = new ArrayList<>();
        aList.add("1");
        aList.add("2");
        aList.add("3");
        aList.add("4");

        List<String> bList = new ArrayList<>();
        bList.add("a");
        bList.add("b");
        bList.add("c");
        bList.add("d");

        List<String> cList = new ArrayList<>();
        cList.add("1");
        cList.add("a");

        aList.addAll(bList);
        aList.addAll(cList);
        //去重
        List<String> collect = aList.stream().distinct().collect(Collectors.toList());
        collect.forEach(System.out::println);

    }


    /**
     * 斐波那契数列递归实现求n位数：1 1 2 3 5 8 13 21 34
     */
    @Test
    @JTestAnno("斐波那契数列递归实现")
    public void TestRecursion(){

       int result = Recursion(4);
        System.out.println(result);

    }

    /**
     * 递归斐波那契方法实现
     * @param num
     * @return
     */
    public  int Recursion(int num){
        if(num<=2){
            return 1;
        }
        int a = Recursion(num - 1);
        int b = Recursion(num - 2);
        int  result = a+b;
        return result;

    }

    /**
     * 二分查找测试
     *
     */
    @Test
    @JTestAnno("二分查找测试")
    public void testSearchFun(){

        int [] arr = {234,245,77,3,543,67,78,9999,378,678,205,753,457,2903,340};
        int searchWord = 378;
        binarySearch(arr,searchWord);

    }

    /**
     * for循环二分查找实现
     * @param arr
     * @param searchWord
     */
    public void binarySearch(int[] arr,int searchWord){

        int startIndex = 0;
        int endIndex = arr.length-1;
        int currentIndex = 0;
        //对数组排序
        Arrays.sort(arr);
        System.out.println("排序后的数组="+Arrays.toString(arr));
        for(int i = 0 ; i<arr.length/2 ;i++){
            currentIndex = (startIndex+endIndex)/2;

            if(arr[currentIndex]>searchWord){
                endIndex = currentIndex-1;
            }
            if(arr[currentIndex]<searchWord){
                startIndex = currentIndex+1;
            }
            if(arr[currentIndex]==searchWord){
                System.out.println("!警告！第"+(i+1)+"次，找到元素"+searchWord);
                break;
            }else {
                System.out.println();
                //System.out.println("第"+(i+1)+"次，没找到元素。中位元素"+arr[currentIndex]+"。起始元素："+arr[startIndex]+"。末尾元素："+arr[endIndex]);
                System.out.println("第"+(i+1)+"次，没找到元素");
            }

        }

    }

    @Test
    @JTestAnno("测试N的阶乘N!")
    public void testFactorial(){
        int factorial = factorial(4);
        System.out.println(factorial);
    }


    /**
     * N！阶乘的递归方法实现
     * @param n
     * @return
     */
    public int factorial(int n) {
        if(n<=1){
            return n;
        }else {
            return n*factorial(n-1);
        }

    }

    /**
     * 正则表达式截取字符串中的数字
     */
    @Test
    @JTestAnno("测试正则表达式截取字符串中的数字")
    public void testStr(){

        String str = "fsaf{1111}fdsfsgt{9999}fhbcbcjjy{5555}fstshs";
        //方法1:此正则表达式的意思是匹配非0-9的字符串
        String reg = "[^0-9]";
        Pattern p = Pattern.compile(reg);
        String[] split = str.split("}");
        for (int i = 0 ; i <split.length ; i++){
            System.out.println(split[i]);
            Matcher matcher = p.matcher(split[i]);
            //匹配出非0-9的字符串，全部替换成”“，然后去空格
            String trim = matcher.replaceAll("").trim();
            System.out.println(trim);
        }


        //方法二：此正则的匹配数字，+号表示多位。
       String regEx = "\\d+";
        Pattern p2 = Pattern.compile(regEx);
        Matcher m = p2.matcher(str);
        while (m.find()){
            if(!"".equals(m.group())){
                System.out.println(m.group());
            }
        }



    }

    /**
     * 写出byte数组
     * @throws IOException
     */
    @Test
    public void testWrite() throws IOException {

//        byte[] templateContent = template.getTemplateContent();
        byte[] templateContent = new byte[1024];

        File tempFile = new File("D:\\templateContent.xml");
        FileOutputStream fos = null;
        try {
            tempFile.createNewFile();
            fos = new FileOutputStream(tempFile);
            fos.write(templateContent,0,templateContent.length);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            fos.flush();
            fos.close();
        }

    }
    @Test
    public void testLocalDateTime(){
      /*  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateTime = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
        System.out.println(dateTime.substring(0,dateTime.length()-3));*/
/*
        String str = "<![CDATA[3级]]>";
        String rgex = "\\[(.*?)\\]";
        String substring = str.substring(str.lastIndexOf("[")+1, str.indexOf("]"));
        System.out.println(substring);*/
       /*  List<String> subUtil = getSubUtil(str, rgex);
        subUtil.forEach(System.out::println);*/

       String str2 = "高温 27℃";
        String substring1 = str2.substring(str2.indexOf(" ")+1, str2.length());
        System.out.println(substring1);


    }
    @Test
    public static List<String> getSubUtil(String soap,String rgex){
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            int i = 1;
            list.add(m.group(i));
            i++;
        }
        return list;

    }


    /**
     * 使用xuggle-xuggler-5.4.jar将mp3波特率转换测试
     * 下载jar加入进项目，使用以下方法转换mp3波特率
     * @throws FileNotFoundException
     */
    @Test
    public  void testAudio() throws FileNotFoundException {

        String filePath = "E:\\我和我的祖国.mp3";
        String filePath2 = "E:\\今天是你的生日,中国.mp3";
        String outputPath = "E:\\今天是你的生日.mp3";
        File file = new File(filePath2);

        convertToMP3(file,outputPath,128*1000);

    }

    /**
     * @param input 需要改变的Mp3
     * @param outputPath 改变后的目标文件输出路径
     * @param kbps 改成的比特率值  要乘以1000
     * @return
     */
    public static boolean convertToMP3(File input, String outputPath, int kbps) {
        boolean result = false;
        // 创建media reader
        IMediaReader mediaReader = ToolFactory.makeReader(input.getPath());
        // 创建media writer
        IMediaWriter mediaWriter = ToolFactory.makeWriter(outputPath, mediaReader);

        // 添加writer进reader，并创建输出文件
        mediaReader.addListener(mediaWriter);

        // 添加IMediaListner到writer，进行比特率的改变
        mediaWriter.addListener(new MediaListenerAdapter() {
            @Override
            public void onAddStream(IAddStreamEvent event) {
                IStreamCoder streamCoder = event.getSource().getContainer().getStream(event.getStreamIndex())
                        .getStreamCoder();
                streamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, false);
                streamCoder.setBitRate(kbps);
                streamCoder.setBitRateTolerance(0);
            }
        });

        // 读流 并重新打包成新文件
        try {
            while (mediaReader.readPacket() == null) {
                System.out.println("进行转码及重新打包");
            }
            result = true;
        } catch (Exception e) {
            result = false;
            System.out.println(e);
        }
        return result;

    }

    @Test
    public void testTData(){
        String dateStr = "2021-03-02 06-02-29";
        Date date = DateUtil.parseSimpleDate(dateStr);
        System.out.println(date);

    }

}
