package com.home.heaven;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Created by Naver on 2016. 5. 13..
 */
public class PostBlogTestNew {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void sendBlogImgToMongodb() {
        ExploratoryRobot er = new ExploratoryRobot();
        int pid = er.getPid() + 1;

        String keyword = "치어리더 박기량";
        String exkeyword = "박기량";
        String title = "" + keyword + "" + " 사진모음";
        String keyword2 = keyword;

        Document[] imglist = er.generateImgContents(keyword2, 200, exkeyword);
        Document[] newslist = er.generateNaverNewsContentsForMongo(keyword, 10);

        Document mongo = new Document();
        mongo.append("pid", pid);
        mongo.append("title", title);
        mongo.append("imglist", asList(imglist));
        mongo.append("newslist", asList(newslist));

        System.out.println(mongo);
        assertEquals(er.insertOne(mongo), true);
    }

}