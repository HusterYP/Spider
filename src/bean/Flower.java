package bean;

import java.util.ArrayList;
import java.util.List;

//花的数据库
public class Flower {
    private String author; //作者姓名
    private String content; //诗句内容
    private String dynasty; //朝代
    private String url;//作品链接
    private String title;//标题

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Flower() {
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public String getAuthor() {

        return author;
    }

    public String getContent() {
        return content;
    }

    public String getDynasty() {
        return dynasty;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
