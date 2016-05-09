package com.home.heaven;

public class HeavenisingStart2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExploratoryRobot er = new ExploratoryRobot();

		String keyword = args[0];
		// String exkeyword = args[1];
		// exkeyword = null;

		String keyword2 = keyword;
		String title = "" + keyword + "";

		// if (args.length > 2)
		// title += args[2];
		// else
		// title += "사진모음";

		String startmsg = "<br><br>" + title
				+ " - news articles and photo collections<br>" + "<br>";

		// if (args.length > 3)
		// keyword2 = args[3];
		// keyword2 = "Jessica Gomes";

		String description = er.generateImgContentsFromYahooAndBing(keyword2,
				100, startmsg, null)
				+ er.generateBingNewsContents(keyword2, 10, "");

		String tag = keyword.replace(" ", "").trim();
		PostBlog pb = new PostBlog();
		description = er.getAddScense() + description;
		pb.sendBlogDataToTstory(title, description, tag);
	}

}
