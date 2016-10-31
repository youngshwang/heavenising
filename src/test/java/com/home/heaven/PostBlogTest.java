package com.home.heaven;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.bson.Document;
import org.junit.Test;

public class PostBlogTest {

    // @Test
    // public void sendBlogDataToEgloosTest() throws XmlRpcException,
    // IOException {
    // ExploratoryRobot er = new ExploratoryRobot();
    //
    // PostBlog pb = new PostBlog();
    //
    // assertNotNull(pb.sendBlogDataToEgloos("���� �׽�Ʈ",
    // er.generateNewsContents()));
    // }

    // @Test
    // public void sendBlogDataToTstoryTest() {
    // ExploratoryRobot er = new ExploratoryRobot();
    //
    // String keyword = "�¿� �ܹ߸Ӹ�";
    // String title = keyword + " ���߳׿�.";
    // String tag = "�¿�,�ܹ߸Ӹ�,�¿��ܹ߸Ӹ�,�ʱ�";
    // String startmsg = "�¿� �ܹ߸Ӹ��ϴϰ� ���ο� �ŷ��� ��������. <br>" + ""
    // + "��������� ������ ����.. .  <br>"
    // + "�׸��� �ξ� ������̱���.    <br>";
    //
    // String description = er.getAddScense()
    // + er.generateNaverNewsContents(keyword, 10, startmsg);
    //
    // PostBlog pb = new PostBlog();
    // assertNotNull(pb.sendBlogDataToTstory(title, description, tag));
    // }

    @Test
    public void sendBlogImgToTstoryTest() {

        // String trans =
        //
//	 "������� - \\u0026#39;���̾��3\\u0026#39; �߱��� �ٸ� ����..�Ǻ�� �⿬�� ���";
        // trans = trans.replace("\\u0026", "\u0026");
        // try {
        // byte[] converttoBytes = trans.getBytes("UTF-8");
        // trans = new String(converttoBytes, "UTF-8");
        // } catch (UnsupportedEncodingException e) {
        // e.printStackTrace();
        // }

        ExploratoryRobot er = new ExploratoryRobot();

        String keyword = "오하늬 화보";
        String exkeyword = "오하늬";
        // exkeyword = null;
        String title = "" + keyword + "" + " 사진모음";
        String startmsg = "<br><br>" + title + "<br>" + "<br>";

        String keyword2 = keyword;
        // keyword2 = "Jessica Gomes";

        String description = er.generateImgContentsHtml(keyword2, 200, startmsg, exkeyword)
                + er.generateNaverNewsContentsByJsoup(keyword, 10, "");

        String tag = keyword.replace(" ", "").trim();
        if (er.naver_tag != null) {
            tag += "," + er.naver_tag;
        }

        PostBlog pb = new PostBlog();
        // assertNotNull(pb.sendBlogDataToNaver(title, description, tag));

        // description = er.getAddScense() + description;

        assertNotNull(pb.sendBlogDataToTstory(title, description, tag));
    }

    // @Test
    // public void daumDataToNaver() {
    //
    // ExploratoryRobot er = new ExploratoryRobot();
    //
    // String keyword = "에이핑크 화보";
    // String title = "" + keyword + " 사진모음";
    // String startmsg = "<br><br>" + title + "<br>" + "<br>";
    //
    // String keyword2 = keyword;
    // // keyword2 = "iron man 3";
    //
    // String description = er.generateImgContents(keyword2, 100, startmsg,
    // null) + er.generateDaumNewsContents(keyword2, 10, "");
    //
    // String tag = keyword.replace(" ", "").trim();
    // if (er.daum_tag != null) {
    // tag += "," + er.daum_tag;
    // }
    //
    // PostBlog pb = new PostBlog();
    // assertNotNull(pb.sendBlogDataToNaver(title, description, tag));
    //
    // }
    //
    // @Test
    // public void bingAndYahooToTistory() {
    // ExploratoryRobot er = new ExploratoryRobot();
    //
    // String keyword = "Chris Hemsworth";
    // String title = "" + keyword + "'s news articles and photo collections";
    // String startmsg = "<br><br>" + title + "<br>" + "<br>";
    //
    // String keyword2 = keyword;
    // // keyword2 = "iron man 3";
    //
    // String description = er.generateImgContentsFromYahooAndBing(keyword2,
    // 100, startmsg, null)
    // + er.generateBingNewsContents(keyword2, 10, "");
    //
    // // er.generateImgContents(keyword2, 100, startmsg,
    // // null) + er.generateDaumNewsContents(keyword2, 10, "");
    //
    // String tag = keyword.replaceAll(" ", "").trim();
    //
    // PostBlog pb = new PostBlog();
    // description = er.getAddScense() + description;
    // assertNotNull(pb.sendBlogDataToTstory(title, description, tag));
    // }

//	@Test
//	public void sendBlogImgToMongodb() {
//		ExploratoryRobot er = new ExploratoryRobot();
//		int pid = er.getPid() + 1;
//
//		String keyword = "조윤희 화보";
//		String exkeyword = "조윤희";
//		String title = "" + keyword + "" + " 사진모음";
//		String keyword2 = keyword;
//
//		Document[] imglist = er.generateImgContents(keyword2, 200, exkeyword);
//		Document[] newslist = er.generateNaverNewsContentsForMongo(keyword, 10);
//
//		Document mongo = new Document();
//		mongo.append("pid", pid);
//		mongo.append("title", title);
//		mongo.append("imglist", asList(imglist));
//		mongo.append("newslist", asList(newslist));
//
//		System.out.println(mongo);
//		assertEquals(er.insertOne(mongo), true);
//
//	}
}
