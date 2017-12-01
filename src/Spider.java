import bean.Flower;
import bean.FlowerList;
import com.alibaba.fastjson.JSON;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Spider implements Runnable {

    private static final String[] genre = {"WuJue", "QiJue", "WuLv", "QiLv",
            "Ci", "WuPai", "QiPai", "SiYan", "LiuYan",
            "LiuYan", "GuFeng", "YueFu", "Sao", "Jie", "Qu",
            "Fu", "QinCao", "Zan", "Others"};

    private String url1;
    private String filePath;
    //爬完一个字后，应该停止一会，否则可能被封
    private boolean isNew;

    public Spider(String url1, String filePath, boolean isNew) {
        this.url1 = url1;
        this.filePath = filePath;
        this.isNew = isNew;
    }

    @Override
    public void run() {

        String url = url1;
        FlowerList flowerList = new FlowerList();
        String temp_url1 = url;
        try {
            if (isNew) {
                Thread.sleep(3000);
            }
            for (int t = 0; t < genre.length; t++) {
                url = temp_url1 + "&t=" + genre[t];
                String temp_url2 = url;
                //得到页数
                Document document = null;
                Connection con = Jsoup.connect(url + "&page=0");
                con.timeout(30000);
                document = con.get();
                int page = getPages(document);


                System.out.println(page + "..." + url + "&page=0");
                int sleep = 10;

                for (int j = 0; j <= page; j++) {
                    //将上一次的page数量保存下来，为了避免因为page%10的余数和下一次的访问量叠加
                    //造成IP被封
                    if (page > 10) {
                        if (demo.pages > 5) {
                            Thread.sleep(2000);
                        }
                    } else if (demo.pages + page > 15) {
                        Thread.sleep(2000);
                    }
                    //当访问超过10页时，停止当前线程，休息一会—_—!
                    if (j > sleep) {
                        sleep += 10;
                        Thread.sleep(2000);
                    }
                    url = temp_url2 + "&page=" + j;

                    System.out.println(url);
                    Connection connection = Jsoup.connect(url);
                    connection.timeout(30000);
                    Document document1 = connection.get();

                    Elements setence = document1.select(".sentences");
                    List<String> dynasty = getDynasty(document1);
                    int index = 0;//朝代下标
                    for (Element e : setence) {
                        String temp = e.toString();
                        Elements author_element = e.select(".label");
                        String author[] = getAuthor(author_element);//作者
                        String[] list = temp.split("<li class=\"label\">.*</li>");
                        for (int i = 1; i < list.length; i++) {
                            Document e1 = Jsoup.parse(list[i]);
                            List<String> title = getTitle(e1);
                            List<String> str_url = getUrl(e1);
                            int title_index = 0;
                            int url_index = 0;
                            Elements e2 = e1.select(".poemSentence");
                            for (Element e3 : e2) {
                                Flower flower = new Flower();
                                String content = e3.text().trim();
                                flower.setTitle(title.get(title_index++));
                                flower.setUrl(str_url.get(url_index++));
                                flower.setAuthor(author[i - 1]);
                                flower.setContent(content);
                                flower.setDynasty(dynasty.get(index));
                                flowerList.getFlowers().add(flower);
                            }
                        }
                        index++;
                    }
                }
                //将上一次的page数量保存下来，为了避免因为page%10的余数和下一次的访问量叠加
                //造成IP被封
                //虽然不推荐这样写，但是为了简便，此处就直接在demo中设置了一个static变量了
                demo.pages = page;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //如果发生异常就写入文件
            try {
                File file = new File("exception");
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(file, true);
                out.write(e.getMessage().getBytes());
            } catch (Exception e1) {
            }
        } finally {
            try {
                writeToFile(JSON.toJSONString(flowerList), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int getPages(Document document) {
        Elements element = document.select(".list1");
        int page = 0;
        for (Element e : element) {
            if (e.text().contains("页显示")) {
                String str = e.text();
                str = str.split("页显示")[0].split("分")[1];
                page = Integer.parseInt(str);
                break;
            }
        }
        return page;
    }

    private void writeToFile(String json, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream in = new FileOutputStream(file);
        //设置在文件末尾追加
//        FileOutputStream in = new FileOutputStream(file,true);
        in.write(json.getBytes());
        in.close();
    }

    private List<String> getTitle(Document document) {
        List<String> list = new ArrayList<>();
        Elements titleElement = document.select("[target=\"_blank\"]");
        for (Element element : titleElement) {
            list.add(element.text());
            System.out.println(element.text());
        }
        return list;
    }

    private List<String> getUrl(Document document) {
        //a[href .small]
        Elements elements = document.select("[target=\"_blank\"]");
        List<String> url = new ArrayList<>();
        for (Element e : elements) {
            url.add("http://www.sou-yun.com/" + e.attr("href"));
        }
        return url;
    }

    private List<String> getDynasty(Document document) {
        Elements dynasty_element = document.select(".label1");
        List<String> dyansty = new ArrayList<>();
        for (Element e : dynasty_element) {
            String temp = e.text();
            if (temp.contains("（续上）")) {
                String temp_str = temp;
                if (temp_str.split("（续上）").length != 0) {
                    temp = temp.split("（续上）")[0];
                } else {
                    temp = "暂无";
                }
            }
            dyansty.add(temp);
            System.out.println(temp);
        }
        return dyansty;
    }

    private String[] getAuthor(Elements author_element) {
        String author[] = author_element.text().split(" "); //作者
        for (int i = 0; i < author.length; i++) {
            if (author[i].contains("（续上）")) {
                String temp = author[i];
                if (temp.split("（续上）").length != 0) {
                    author[i] = author[i].split("（续上）")[0];
                } else {
                    author[i] = "暂无";
                }
            }
            System.out.println(author[i]);
        }
        return author;
    }
}
