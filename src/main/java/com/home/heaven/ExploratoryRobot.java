package com.home.heaven;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HttpContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class ExploratoryRobot {
	HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
	String useragent = "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36";

	private String NAVER_URL_MAIN = "http://www.naver.com";
	private String NAVER_KEYWORD_LB = "top_lve&amp;ie=utf8\" title=\"";
	private String NAVER_KEYWORD_RB = "\"";

	private String NAVER_URL_NEWS = "http://news.search.naver.com/search.naver?where=news&sm=tab_jum&ie=utf8&query=";
	private String NAVER_URL_SEARCH = "http://search.naver.com/search.naver?where=nexearch&sm=tab_jum&ie=utf8&query=";
	private String NAVER_TITLE_LB = "title=\"\">";
	private String NAVER_TITLE_RB = "</a>";
	private String NAVER_LINK_LB = "<dt><a href=\"";
	private String NAVER_LINK_RB = "\"";
	private String NAVER_COMPANY_LB = "txt_inline\">";
	private String NAVER_COMPANY_RB = "<span";
	private String NAVER_CONTENTS_LB = "</dd> <dd>";
	private String NAVER_CONTENTS_RB = "</dd>";
	private String NAVER_TAG_LB = "urlencode(urlexpand(this.href)))\">";
	private String NAVER_TAG_RB = "</a>";

	private String GOOGLE_URL_IMAGE = "http://www.google.co.kr/search?tbm=isch&biw=1613&bih=949&q=";
	private String GOOGLE_IMG_LB = "imgurl=";
	private String GOOGLE_IMG_RB = "&amp";
	private String GOOGLE_IMG_W_LB = "&amp;w=";
	private String GOOGLE_IMG_W_RB = "&";
	private String GOOGLE_IMG_H_LB = "&amp;h=";
	private String GOOGLE_IMG_H_RB = "&";
	private String GOOGLE_IMG_TITLE_LB = "\"pt\":\"";
	private String GOOGLE_IMG_TITLE_RB = "\"";

	private String DAUM_URL_NEWS = "http://search.daum.net/search?w=news&nil_search=btn&enc=utf8&cluster=y&cluster_page=1&q=";
	private String DAUM_URL_SEARCH = "http://search.daum.net/search?w=tot&nil_profile=rtupkwd&rtupcate=issue&DA=RSTO&rtupcoll=DQP,NNS&q=";
	private String DAUM_TITLE_LB = "target=\"_blank\">";
	private String DAUM_TITLE_RB = "</a>";
	private String DAUM_LINK_LB = "<a href=\""; // <%=related.url%> <-- 요넘은
												// 스크립트"
	private String DAUM_LINK_RB = "\" class=\"f_link_u f_l mg_tit\"";
	private String DAUM_COMPANY_LB = "\">|</span> "; // 10자로
	private String DAUM_COMPANY_RB = "<span class";
	private String DAUM_CONTENTS_LB = "<p class=\"f_eb desc\">"; // <%=news.content.desc%>
																	// <-- 요넘도
																	// 스크립트
	private String DAUM_CONTENTS_RB = "</p>";

	private String NATE_URL_NEWS = "http://search.nate.com/search/all.html?q=&thr=scaa&ssn=036&dsn=3&asn=003600540";
	private String NATE_TITLE_LB = "";
	private String NATE_TITLE_RB = "";
	private String NATE_LINK_LB = "><dt class=\"text-inline\"><a href=\"";
	private String NATE_LINK_RB = "\" target";
	private String NATE_COMPANY_LB = "span><cite>";
	private String NATE_COMPANY_RB = "</cite>";
	private String NATE_CONTENTS_LB = "<dd class=\"search-content\">";
	private String NATE_CONTENTS_RB = "</dd>";

	public String naver_tag = null;
	public String naver_news = null;
	public String daum_tag = null;
	public String daum_news = null;

	private ArrayList<String> sitelist = new ArrayList<String>();

	private void loadingFile() {
		sitelist.clear();
		String file = "site.txt";
		try {
			InputStream ips = new FileInputStream(file);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String line;
			while ((line = br.readLine()) != null) {
				sitelist.add(line.trim());
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public String generateImgContentsFromYahooAndBing(String keyword, int max,
			String startmsg, String exKeyword) {
		loadingFile();

		String bingurl = "http://www.bing.com/images/search?q="
				+ keyword.replaceAll(" ", "+")
				+ "&FORM=NWRFSH&scope=web&setmkt=en-US&setlang=match&FORM=W5WA&uid=11305340";
		// String yahoourl = "http://images.search.yahoo.com/search/images?p="
		// + keyword.replaceAll(" ", "+")
		// + "&fr=yfp-t-900&ei=utf-8&n=30&x=wrt&y=Search";

		String conti = "<P><div><span>" + startmsg + "</span></div><br>";
		String pagecontents1 = "";
		try {
			pagecontents1 = requestGET(bingurl);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList<String> imgs = new ArrayList<String>();
		ArrayList<String> width = new ArrayList<String>();
		ArrayList<String> height = new ArrayList<String>();
		ArrayList<String> title = new ArrayList<String>();

		Document doc = Jsoup.parse(pagecontents1);

		Elements alinks = doc.select("div.dg_u > a");
		for (int i = 0; i < alinks.size(); i++) {
			// 타이틀
			title.add(alinks.get(i).attr("t1").trim());
			// 이미지 링크
			String m = alinks.get(i).attr("m");
			int begin = ("imgurl:\"").length() + m.indexOf("imgurl:\"");
			int end = m.indexOf("\"", begin);
			imgs.add(m.substring(begin, end).trim());
			// 이미지 사이즈
			String t2 = alinks.get(i).attr("t2");
			width.add(t2.substring(0, t2.indexOf("x")).trim());
			height.add(t2.substring(t2.indexOf("x") + 1, t2.indexOf("·"))
					.trim());
		}

		// t2="995 x 1360 · 88 kB · jpeg"
		// {ns:"images.1_2",k:"5016",mid:"E9A82BE037B62E22AD3AF2B6028D5D61FF68B6E3",
		// surl:"http://youngadulthollywood.com/uncategorized/chris-hemsworth-to-star-in-upcoming-film-from-michael-mann/",
		// imgurl:"http://youngadulthollywood.com/wp-content/uploads/2013/02/Chris-Hemsworth.jpg",oh:"300",tft:"22",
		// oi:"http://youngadulthollywood.com/wp-content/uploads/2013/02/Chris-Hemsworth.jpg"}"

		// sorting
		int lenD = height.size();
		int j = 0;
		String tmp;
		for (int i = 0; i < lenD; i++) {
			j = i;
			for (int k = i; k < lenD; k++) {
				if (Integer.parseInt(height.get(j)) < Integer.parseInt(height
						.get(k))) {
					j = k;
				}
			}
			tmp = height.get(i);
			height.set(i, height.get(j));
			height.set(j, tmp);

			tmp = width.get(i);
			width.set(i, width.get(j));
			width.set(j, tmp);

			tmp = imgs.get(i);
			imgs.set(i, imgs.get(j));
			imgs.set(j, tmp);

			tmp = title.get(i);
			title.set(i, title.get(j));
			title.set(j, tmp);
		}

		if (max > height.size())
			max = height.size();

		for (int i = 0; i < max; i++) {
			int h = Integer.parseInt(height.get(i));
			if (h < 500)
				break;

			int w = Integer.parseInt(width.get(i));
			if (w > 600)
				w = 600;
			if (isContainedSite(imgs.get(i)))
				continue;

			if ((exKeyword != null) && (title.get(i).indexOf(exKeyword) == -1))
				continue;

			if (title.get(i).indexOf("Heavenising") != -1)
				continue;

			String statement = "<br><br><div><span><a href=\"" + imgs.get(i)
					+ "\" target=\"_blank\" style=\"color:blue\">"
					+ title.get(i) + "<br>" + "url: " + imgs.get(i)
					+ "<br>[original]width:" + width.get(i) + ",height:"
					+ height.get(i) + "<br><img src=\"" + imgs.get(i)
					+ "\" width=\"" + w + "\" ></a></span></div>";
			System.out.println(statement);
			conti += statement;
		}

		return conti;
	}

	public String generateBingNewsContents(String keyword, int max,
			String startmsg) {
		String conti = "";
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH")
				.format(Calendar.getInstance().getTime());
		conti += "<P><div><span>" + startmsg + "</span></div><br><div><span>"
				+ timeStamp + " - " + keyword + "'s news articles collections"
				+ "</span></div>";

		// bing 뉴스 검색 페이지
		String bingnews = "http://www.bing.com/news/search?q="
				+ keyword.replaceAll(" ", "+")
				+ "&FORM=NWRFSH&scope=web&setmkt=en-US&setlang=match&FORM=W5WA&uid=11305340";
		String pagecontents = "";
		try {
			pagecontents = requestGET(bingnews);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Document doc = Jsoup.parse(pagecontents);

		Elements links = doc.select("div.newstitle > a");
		Elements companys = doc
				.select("span.sn_snip + span.sn_ST > cite.sn_src");
		Elements contents = doc.select("span.sn_snip");

		if (max > links.size())
			max = links.size();

		for (int i = 0; i < max; i++) {
			conti += "<div><span style=\"color:green\">" + (i + 1) + ". "
					+ companys.get(i).text().trim() + " - <a href=\""
					+ links.get(i).attr("href")
					+ "\" target=\"_blank\" style=\"color:blue\">"
					+ links.get(i).html() + "</a></span></div><div><span>"
					+ contents.get(i).html() + "</span></div><br>";
		}

		return conti;
	}

	public String generateNaverNewsContentsByJsoup(String keyword, int max,
			String startmsg) {
		String conti = "";
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH시")
				.format(Calendar.getInstance().getTime());
		conti += "<P><div><span>" + startmsg + "</span></div><br><div><span>"
				+ timeStamp + " - " + keyword + " 기사모음" + "</span></div>";
		String pagecontents = "";
		String pagecontents2 = "";
		try {
			pagecontents = requestGET(NAVER_URL_NEWS
					+ URLEncoder.encode(keyword, "UTF-8"));

			pagecontents2 = requestGET(NAVER_URL_SEARCH
					+ URLEncoder.encode(keyword, "UTF-8"));

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Document doc = Jsoup.parse(pagecontents);
		Elements links = doc.select("li > dl > dt > a[href]");
		System.out.println("link:" + links.size() + " "
				+ links.get(0).attr("href") + ":" + links.get(0).html());

		Elements companys = doc.select("span._sp_each_source");
		// String[] company = companys.get(0).text().split("\\|");
		System.out.println("company:" + companys.size());
		// + company[1].trim());

		Elements contents = doc.select("li > dl > dd:nth-child(3)");
		System.out.println("contents:" + contents.size() + " "
				+ contents.get(0).html());

		doc = Jsoup.parse(pagecontents2);
		Elements tags = doc.select("dd.lst_relate > ul > li > a");
		System.out.println("tags:" + tags.size());

		if (tags.size() > 0) {
			naver_tag = "";
			for (int i = 0; i < tags.size(); i++) {
				if (i == 9)
					break;
				else if (i != 0)
					naver_tag += ",";
				naver_tag += tags.get(i).text().replace(" ", "").trim();
			}
		}
		System.out.println("tags:" + naver_tag);

		if (max > 10)
			max = 10;
		for (int i = 0; i < max; i++) {
			conti += "<div><span style=\"color:green\">" + (i + 1) + ". "
					+ companys.get(i).text().trim() + " - <a href=\""
					+ links.get(i).attr("href")
					+ "\" target=\"_blank\" style=\"color:blue\">"
					+ links.get(i).html() + "</a></span></div><div><span>"
					+ contents.get(i).html() + "</span></div><br>";
		}

		conti += "<br><div><span>[참조] 포털에서 뉴스기사 참조함. 문제시 포스트 삭제. </span></div>";
		return conti;
	}

	public String generateDaumNewsContents(String keyword, int max,
			String startmsg) {
		String conti = "";
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH시")
				.format(Calendar.getInstance().getTime());
		conti += "<P><div><span>" + startmsg + "</span></div><br><div><span>"
				+ timeStamp + " - " + keyword + " 기사모음" + "</span></div>";
		String pagecontents = "";
		String pagecontents2 = "";
		try {
			pagecontents = requestGET(DAUM_URL_NEWS
					+ URLEncoder.encode(keyword, "UTF-8"));

			pagecontents2 = requestGET(DAUM_URL_SEARCH
					+ URLEncoder.encode(keyword, "UTF-8"));

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Document doc = Jsoup.parse(pagecontents);
		Elements links = doc.select("div[class=wrap_tit mg_tit] > a[href]");
		System.out.println("link:" + links.size() + " "
				+ links.get(0).attr("href") + ":" + links.get(0).html());

		Elements companys = doc
				.select("div[class=cont_inner] > span[class=f_nb date]");
		String[] company = companys.get(0).text().split("\\|");
		System.out.println("company:" + companys.size() + " "
				+ company[1].trim());

		Elements contents = doc.select("p[class=f_eb desc]");
		System.out.println("contents:" + contents.size() + " "
				+ contents.get(0).html());

		doc = Jsoup.parse(pagecontents2);
		Elements tags = doc.select("span[class=wsn]");
		System.out.println("tags:" + tags.size());

		if (tags.size() > 0) {
			daum_tag = "";
			for (int i = 0; i < tags.size(); i++) {
				if (i == 9)
					break;
				else if (i != 0)
					daum_tag += ",";
				daum_tag += tags.get(i).text().replace(" ", "").trim();
			}
		}
		System.out.println("tags:" + daum_tag);

		if (max > 10)
			max = 10;
		for (int i = 0; i < max; i++) {
			conti += "<div><span style=\"color:green\">" + (i + 1) + ". "
					+ (companys.get(i).text().split("\\|"))[1].trim()
					+ " - <a href=\"" + links.get(i).attr("href")
					+ "\" target=\"_blank\" style=\"color:blue\">"
					+ links.get(i).html() + "</a></span></div><div><span>"
					+ contents.get(i).html() + "</span></div><br>";
		}

		conti += "<br><div><span>[참조] 포털에서 뉴스기사 참조함. 문제시 포스트 삭제. </span></div>";
		return conti;
	}

	public String generateNaverNewsContents(String keyword, int max,
			String startmsg) {
		String conti = "";

		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH시")
				.format(Calendar.getInstance().getTime());
		conti += "<P><div><span>" + startmsg + "</span></div><br><div><span>"
				+ timeStamp + " - " + keyword + " 기사모음" + "</span></div>";

		String pagecontents = "";
		String pagecontents2 = "";
		try {
			pagecontents = requestGET(NAVER_URL_NEWS
					+ URLEncoder.encode(keyword, "UTF-8"));

			pagecontents2 = requestGET(NAVER_URL_SEARCH
					+ URLEncoder.encode(keyword, "UTF-8"));

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String[] company = getItems(pagecontents, NAVER_COMPANY_LB,
				NAVER_COMPANY_RB);
		System.out.println("company:" + company.length + "\n"
				+ company.toString());

		String[] link = getItems(pagecontents, NAVER_LINK_LB, NAVER_LINK_RB);
		System.out.println("link:" + link.length + "\n" + link.toString());

		String[] title = getItems(pagecontents, NAVER_TITLE_LB, NAVER_TITLE_RB);
		System.out.println("title:" + title.length + "\n" + title.toString());

		String[] contents = getItems(pagecontents, NAVER_CONTENTS_LB,
				NAVER_CONTENTS_RB);

		String[] tags = getItems(pagecontents2, NAVER_TAG_LB, NAVER_TAG_RB);
		if (tags != null) {
			naver_tag = "";
			for (int i = 0; i < (tags.length / 2); i++) {
				if (i == 10)
					break;
				tags[i] = tags[i].replace(" ", "").trim();
				naver_tag += tags[i] + ",";
			}
		}

		Document doc = Jsoup.parse(pagecontents);
		Elements titles = doc.select("ul[class=type01] > li > dl > dt > a");

		System.out.println("tags:" + naver_tag);
		System.out.println("contents:" + contents.length + "\n" + contents);

		if (max > 10)
			max = 10;
		for (int i = 0; i < max; i++) {
			conti += "<div><span style=\"color:green\">" + (i + 1) + ". "
					+ company[i] + " - <a href=\"" + link[i]
					+ "\" target=\"_blank\" style=\"color:blue\">"
					+ titles.get(i).ownText().trim()
					+ "</a></span></div><div><span>" + contents[i]
					+ "</span></div><br>";
		}

		conti += "<br><div><span>[참조] 포털에서 뉴스기사 참조함. 문제시 포스트 삭제. </span></div>";
		return conti;
	}

	public org.bson.Document[] generateNaverNewsContentsForMongo(
			String keyword, int max) {
		// String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH시")
		// .format(Calendar.getInstance().getTime());
		String pagecontents = "";
		String pagecontents2 = "";
		try {
			pagecontents = requestGET(NAVER_URL_NEWS
					+ URLEncoder.encode(keyword, "UTF-8"));

			pagecontents2 = requestGET(NAVER_URL_SEARCH
					+ URLEncoder.encode(keyword, "UTF-8"));

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Document doc = Jsoup.parse(pagecontents);
		Elements links = doc.select("li > dl > dt > a[href]");
		System.out.println("link:" + links.size() + " "
				+ links.get(0).attr("href") + ":" + links.get(0).html());

		Elements companys = doc.select("span._sp_each_source");
		System.out.println("company:" + companys.size());

		Elements contents = doc.select("li > dl > dd:nth-child(3)");
		System.out.println("contents:" + contents.size() + " "
				+ contents.get(0).html());

		doc = Jsoup.parse(pagecontents2);
		Elements tags = doc.select("dd.lst_relate > ul > li > a");
		System.out.println("tags:" + tags.size());

		if (tags.size() > 0) {
			naver_tag = "";
			for (int i = 0; i < tags.size(); i++) {
				if (i == 9)
					break;
				else if (i != 0)
					naver_tag += ",";
				naver_tag += tags.get(i).text().replace(" ", "").trim();
			}
		}
		System.out.println("tags:" + naver_tag);

		if (max > 10)
			max = 10;

		org.bson.Document[] bson = new org.bson.Document[10];
		for (int i = 0; i < max; i++) {
			org.bson.Document news = new org.bson.Document();

			String title = links.get(i).html();
			String desc = contents.get(i).html();

			String[] rep = new String[] {
				"<strong class=\"hl\">","</strong>"
			};
			for (int j=0; j<rep.length; j++) {
				title = title.replaceAll(rep[j], "");
				desc = desc.replaceAll(rep[j], "");
			}

			news.append("company", companys.get(i).text().trim());
			news.append("title", title);
			news.append("desc", desc);
			news.append("link", links.get(i).attr("href"));
			bson[i] = news;
		}

		return bson;
	}

	public void process(final HttpResponse response, final HttpContext context)
			throws HttpException, IOException {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			Header ceheader = entity.getContentEncoding();
			if (ceheader != null) {
				HeaderElement[] codecs = ceheader.getElements();
				for (int i = 0; i < codecs.length; i++) {
					if (codecs[i].getName().equalsIgnoreCase("gzip")) {
						response.setEntity(new GzipDecompressingEntity(response
								.getEntity()));
						return;
					}
				}
			}
		}
	}

	public org.bson.Document[] generateImgContents(String keyword, int max,
			String exKeyword) {

		loadingFile();
		String pagecontents = "";
		try {
			pagecontents = requestGET2(GOOGLE_URL_IMAGE
					+ URLEncoder.encode(keyword, "UTF-8"));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        JSONParser jp = new JSONParser();
        Document jdoc = Jsoup.parse(pagecontents);
        Elements rg_metas = jdoc.select(".rg_meta");
        int size = rg_metas.size();
        String[] images = new String[size];
        String[] imageswidth = new String[size];
        String[] imagesheight = new String[size];
        String[] imagestitle = new String[size];

        for(int i=0; i<size; i++) {
            try {
                String one = rg_metas.get(i).toString();
                one = one.replaceAll("<div class=\"rg_meta\">", "")
                        .replaceAll("</div>","").replaceAll("&quot;", "\"").trim();
                JSONObject jo = (JSONObject) jp.parse(one);
                images[i] = jo.get("ou").toString();
                imageswidth[i] = jo.get("ow").toString();
                imagesheight[i] = jo.get("oh").toString();
                imagestitle[i] = jo.get("pt").toString();

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }



//		String[] images = getItems(pagecontents, GOOGLE_IMG_LB, GOOGLE_IMG_RB);
//		String[] imageswidth = getItems(pagecontents, GOOGLE_IMG_W_LB,
//				GOOGLE_IMG_W_RB);
//		String[] imagesheight = getItems(pagecontents, GOOGLE_IMG_H_LB,
//				GOOGLE_IMG_H_RB);
//		String[] imagestitle = getItems(pagecontents, GOOGLE_IMG_TITLE_LB,
//				GOOGLE_IMG_TITLE_RB);

		// sorting
		int lenD = imagesheight.length;
		int j = 0;
		String tmp;
		for (int i = 0; i < lenD; i++) {
			j = i;
			for (int k = i; k < lenD; k++) {
				if (Integer.parseInt(imagesheight[j]) < Integer
						.parseInt(imagesheight[k])) {
					j = k;
				}
			}
			tmp = imagesheight[i];
			imagesheight[i] = imagesheight[j];
			imagesheight[j] = tmp;

			tmp = imageswidth[i];
			imageswidth[i] = imageswidth[j];
			imageswidth[j] = tmp;

			tmp = images[i];
			images[i] = images[j];
			images[j] = tmp;

			tmp = imagestitle[i];
			imagestitle[i] = imagestitle[j];
			imagestitle[j] = tmp;
		}

		ArrayList<org.bson.Document> doc = new ArrayList<org.bson.Document>();
		for (int i = 0; i < max; i++) {
			if (images[i] == null || imagestitle[i] == null)
				continue;

			int height = Integer.parseInt(imagesheight[i]);
			if (height < 500)
				break;

			int width = Integer.parseInt(imageswidth[i]);
			if (width > 600)
				width = 600;
			if (isContainedSite(images[i]))
				continue;

			if ((exKeyword != null)
					&& (imagestitle[i].indexOf(exKeyword) == -1))
				continue;

			if (imagestitle[i].indexOf("Heavenising") != -1)
				continue;

			org.bson.Document imgs = new org.bson.Document();
			imgs.append("no", i + 1);
			imgs.append("link", images[i]);
			imgs.append("desc", imagestitle[i]);
			imgs.append("size", "[원본]width:" + imageswidth[i] + ",height:"
					+ imagesheight[i]);

			doc.add(imgs);
		}
		return doc.toArray(new org.bson.Document[doc.size()]);
	}

	public int getPid() {
		int rtnval = 0;
		MongoClient client2 = new MongoClient(new MongoClientURI(
				"mongodb://localhost:27017/posting"));
		try {
			MongoDatabase db = client2.getDatabase("posting");
			org.bson.Document doc = db.getCollection("postinfo").find()
					.sort(new org.bson.Document("pid", -1)).limit(1).first();
			if (doc == null) {
                return 10001;
            }
			System.out.println("max pid: " + doc.getInteger("pid"));
			rtnval = doc.getInteger("pid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		client2.close();
		return rtnval;
	}

	public boolean insertOne(org.bson.Document doc) {
		boolean rtnval = false;
		MongoClient client2 = new MongoClient(new MongoClientURI(
				"mongodb://localhost:27017/posting"));
		try {
			MongoDatabase db = client2.getDatabase("posting");
			db.getCollection("postinfo").insertOne(doc);
			rtnval = true;
			System.out.println("insert one success!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		client2.close();
		return rtnval;
	}

	public String generateImgContentsJson(String keyword, int max,
			String startmsg, String exKeyword) {
		loadingFile();

		String conti = "<P><div><span>" + startmsg + "</span></div><br>";
		String pagecontents = "";
		try {
			pagecontents = requestGET2(GOOGLE_URL_IMAGE
					+ URLEncoder.encode(keyword, "UTF-8"));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// String[] imagesheight = getItems(pagecontents, GOOGLE_IMG_H_LB,
		// GOOGLE_IMG_H_RB);

		String[] images = getItems(pagecontents, GOOGLE_IMG_LB, GOOGLE_IMG_RB);
		String[] imageswidth = getItems(pagecontents, GOOGLE_IMG_W_LB,
				GOOGLE_IMG_W_RB);
		String[] imagesheight = getItems(pagecontents, GOOGLE_IMG_H_LB,
				GOOGLE_IMG_H_RB);
		String[] imagestitle = getItems(pagecontents, GOOGLE_IMG_TITLE_LB,
				GOOGLE_IMG_TITLE_RB);

		// sorting
		int lenD = imagesheight.length;
		int j = 0;
		String tmp;
		for (int i = 0; i < lenD; i++) {
			j = i;
			for (int k = i; k < lenD; k++) {
				if (Integer.parseInt(imagesheight[j]) < Integer
						.parseInt(imagesheight[k])) {
					j = k;
				}
			}
			tmp = imagesheight[i];
			imagesheight[i] = imagesheight[j];
			imagesheight[j] = tmp;

			tmp = imageswidth[i];
			imageswidth[i] = imageswidth[j];
			imageswidth[j] = tmp;

			tmp = images[i];
			images[i] = images[j];
			images[j] = tmp;

			tmp = imagestitle[i];
			imagestitle[i] = imagestitle[j];
			imagestitle[j] = tmp;
		}

		for (int i = 0; i < max; i++) {
			int height = Integer.parseInt(imagesheight[i]);
			if (height < 500)
				break;

			int width = Integer.parseInt(imageswidth[i]);
			if (width > 600)
				width = 600;
			if (isContainedSite(images[i]))
				continue;

			if ((exKeyword != null)
					&& (imagestitle[i].indexOf(exKeyword) == -1))
				continue;

			if (imagestitle[i].indexOf("Heavenising") != -1)
				continue;

			String statement = "<br><br><div><span><a href=\"" + images[i]
					+ "\" target=\"_blank\" style=\"color:blue\">"
					+ imagestitle[i] + "<br>" + "출처: " + images[i]
					+ "<br>[원본]width:" + imageswidth[i] + ",height:"
					+ imagesheight[i] + "<br><img src=\"" + images[i]
					+ "\" width=\"" + width + "\" ></a></span></div>";
			System.out.println(statement);
			conti += statement;

			if (i == 9) {
				conti += "<br><script async src=\"//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\">"
						+ "</script><ins class=\"adsbygoogle\" style=\"display:block\" "
						+ "data-ad-client=\"ca-pub-6812867982623858\" data-ad-slot=\"2562957321\" "
						+ "data-ad-format=\"auto\"></ins>"
						+ "<script>(adsbygoogle = window.adsbygoogle || []).push({});</script><br>";
			} else if (i == 18) {
				conti += "<br><script async src=\"//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\">"
						+ "</script><ins class=\"adsbygoogle\" style=\"display:block\" "
						+ "data-ad-client=\"ca-pub-6812867982623858\" data-ad-slot=\"6993156924\" "
						+ "data-ad-format=\"auto\"></ins>"
						+ "<script>(adsbygoogle = window.adsbygoogle || []).push({});</script><br>";
			} else if (i == 27) {
				conti += "<br><script async src=\"//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\">"
						+ "</script><ins class=\"adsbygoogle\" style=\"display:block\" "
						+ "data-ad-client=\"ca-pub-6812867982623858\" data-ad-slot=\"4958020526\" "
						+ "data-ad-format=\"auto\"></ins>"
						+ "<script>(adsbygoogle = window.adsbygoogle || []).push({});</script><br>";
			}

		}

		return conti;
	}

	private boolean isContainedSite(String url) {
		for (String site : sitelist) {
			if (url.indexOf(site) != -1)
				return true;
		}
		return false;
	}

	private String requestGET(String targeturl) throws IOException,
			ClientProtocolException, UnsupportedEncodingException {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(targeturl);
		// client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		// proxy);
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				useragent);
		request.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		request.addHeader("Accept-Encoding", "gzip,deflate,sdch");
		request.addHeader("Accept-Language",
				"ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");

		HttpResponse response = client.execute(request);

		HttpEntity entity = response.getEntity();
		if (entity != null) {
			Header ceheader = entity.getContentEncoding();
			if (ceheader != null) {
				HeaderElement[] codecs = ceheader.getElements();
				for (int i = 0; i < codecs.length; i++) {
					if (codecs[i].getName().equalsIgnoreCase("gzip")) {
						response.setEntity(new GzipDecompressingEntity(response
								.getEntity()));
					}
				}
			}
		}

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent(), "UTF-8"));

		String line = "";
		String pagecontents = "";
		while ((line = rd.readLine()) != null) {
			pagecontents += line + "\n";
		}
		return pagecontents;
	}

	private String requestGET2(String targeturl) throws IOException,
			ClientProtocolException, UnsupportedEncodingException {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(targeturl);
		// client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		// proxy);
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				useragent);

		HttpResponse response = client.execute(request);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent(), "UTF-8"));

		String line = "";
		String pagecontents = "";
		while ((line = rd.readLine()) != null) {
			pagecontents += line + "\n";
		}
		return pagecontents;
	}

	private String[] getItems(String src, String LB, String RB) {
		ArrayList<String> rtnVal = new ArrayList<String>();

		int idx = src.indexOf(LB, 0);
		while (idx != -1) {
			idx += LB.length();
			int endidx = src.indexOf(RB, idx);
			if (endidx == -1)
				break;
			String item = src.substring(idx, endidx);
			if ((item != null) && (!item.trim().equals(""))) {
				String trans = item.trim();
				trans = trans.replace("\\u0026", "\u0026");
				// try {
				// byte[] converttoBytes = trans.getBytes("UTF-8");
				// trans = new String(converttoBytes, "UTF-8");
				// } catch (UnsupportedEncodingException e) {
				// e.printStackTrace();
				// }
				rtnVal.add(trans);
			}
			idx = src.indexOf(LB, endidx);
		}

		return rtnVal.toArray(new String[rtnVal.size()]);
	}

	private String[] getItems(String src, String LB, String RB, int gab) {
		ArrayList<String> rtnVal = new ArrayList<String>();

		int idx = src.indexOf(LB, 0);
		while (idx != -1) {
			idx += LB.length();
			if (idx >= src.length())
				break;
			String token = src.substring(idx, idx + gab);
			int endidx = token.indexOf(RB, 0);
			if (endidx == -1) {
				idx = src.indexOf(LB, idx);
				continue;
			}
			endidx += idx;
			String item = src.substring(idx, endidx);
			if ((item != null) && (!item.trim().equals(""))) {
				String trans = item.trim();
				if ((trans.indexOf("<%=related.url%>") != -1)
						|| (trans.indexOf("<%=news.content.desc%>") != -1)
						|| (trans.indexOf("미디어다음") != -1)) {
					idx = src.indexOf(LB, endidx);
					continue;
				}
				trans = trans.replace("\\u0026", "\u0026");
				rtnVal.add(trans);
			}
			idx = src.indexOf(LB, endidx);
		}

		return rtnVal.toArray(new String[rtnVal.size()]);
	}

	public String getAddScense() {
		String addtag = "<!--구글 본문 상단 좌측 광고 시작�-->\n"
				+ "<div style=\"text-align:center; MARGIN: 0px 2px 10px 0px;\">\n"
				+ "<script type=\"text/javascript\"><!--\n"
				+ "google_ad_client = \"ca-pub-6812867982623858\";\n"
				+ "/*티스토리_모바일 */\n"
				+ "google_ad_slot = \"5203445727\";\n"
				+ "google_ad_width = 300;\n"
				+ "google_ad_height = 250;\n"
				+ "//-->\n"
				+ "</script>\n"
				+ "<script type=\"text/javascript\" src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\">\n"
				+ "</script>\n" + "</div>\n" + "<!--구글 본문 상단 좌측 광고 종료-->";

		return addtag;
	}
}
