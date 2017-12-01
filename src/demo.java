import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class demo {
    //代理IP
    private static final String[] IP = {"115.248.28.61",
            "187.153.27.206",
            "202.202.90.20",
            "223.99.214.21",
            "47.74.181.235",
            "119.36.92.46",
            "106.14.51.145",
            "112.13.93.43"};
    //对应的端口号
    private static final String[] PORT = {"8080", "8080", "8080", "53281", "80", "80", "8118", "8088"};

    //体裁-->t
    private static final String[] genre = {"WuJue", "QiJue", "WuLv", "QiLv",
            "Ci", "WuPai", "QiPai", "SiYan", "LiuYan",
            "LiuYan", "GuFeng", "YueFu", "Sao", "Jie", "Qu",
            "Fu", "QinCao", "Zan", "Others"};
    //字的位置-->p
    private static final String[] location = {"0", "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "11", "12", "15"};
    //要爬的字-->c&l
    private static final String[] words = {"月", "夜", "花", "雨", "雪", "风", "日", "酒", "人"};
    //c,l表示待爬的字；p表示在第几个字；t表示体裁；page表示第几页
    //http://www.sou-yun.com/CharInClause.aspx?c=日&l=日&p=0&t=WuJue&page=1
    private static final String baseUrl = "http://www.sou-yun.com/CharInClause.aspx?";

    public static int pages = 0;

    public static void main(String[] args) {
        /*System.getProperties().setProperty("proxySet", "true");
        System.getProperties().setProperty("http.proxyHost", "120.198.224.102");
        System.getProperties().setProperty("http.proxyPort", "80");*/
        String folder[] = {"moon", "night", "flower", "rain", "snow", "wind", "sun", "wine", "person"};
        //使用线程池
        ExecutorService single = Executors.newSingleThreadExecutor();
        boolean isNew = false;
        for (int word = 8; word < words.length; word++) {
            isNew = true;
            for (int i = 1; i < location.length; i++) {
                String url = baseUrl + "c=" + words[word] + "&l=" + words[word] + "&p=" + location[i];
                String filePath = folder[word] + File.separator + folder[word] + location[i] + ".json";
                single.execute(new Spider(url, filePath, isNew));
                isNew = false;
            }
        }
        single.shutdown();
    }

    //测试代理IP是否可用
    public static void testIP() throws IOException {
        System.getProperties().setProperty("proxySet", "true");
        System.getProperties().setProperty("http.proxyHost", IP[0]);
        System.getProperties().setProperty("http.proxyPort", PORT[0]);

        HttpURLConnection connection = (HttpURLConnection) new URL("http://www.baidu.com/").openConnection();
        connection.setConnectTimeout(6000); // 6s
        connection.setReadTimeout(6000);
        connection.setUseCaches(false);

        if (connection.getResponseCode() == 200) {
            System.out.println("使用代理IP连接网络成功");
        } else {
            System.out.println("使用代理IP连接网络失败");
        }
    }
}
