package com.example.task;


/**
 * 处理字符串的工具类
 * @author lei
 *
 */
public class HandleStringAndImage {
	
	/**
	 * 将json数据中的链接转化成真实链接
	 * @param ori_url json中的链接
	 * @return 普通的链接url
	 */
	public static String getHandledURL(String ori_url){
		String step_one = ori_url.replace("\\", "");
		String step_two = step_one.replace("[", "");
		String step_three = step_two.replace("]", "");
		String step_four = step_three.replace("\"", "");
		return step_four;
	}
	
}
