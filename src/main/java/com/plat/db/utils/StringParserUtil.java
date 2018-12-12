package com.plat.db.utils;

public class StringParserUtil {

    /**
     * 将字符串text中由openToken和closeToken组成的占位符依次替换为args数组中的值
     * @param openToken
     * @param closeToken
     * @param text
     * @param args
     * @return
     */
    public static String parse(String openToken, String closeToken, String text, Object... args) {
        if (args == null || args.length <= 0) {
            return text;
        }
        int argsIndex = 0;

        if (text == null || text.isEmpty()) {
            return "";
        }
        char[] src = text.toCharArray();
        int offset = 0;
        // search open token
        int start = text.indexOf(openToken, offset);
        if (start == -1) {
            return text;
        }
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        while (start > -1) {
            if (start > 0 && src[start - 1] == '\\') {
                // this open token is escaped. remove the backslash and continue.
                builder.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                // found open token. let's search close token.
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    if (end > offset && src[end - 1] == '\\') {
                        // this close token is escaped. remove the backslash and continue.
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        expression.append(src, offset, end - offset);
                        offset = end + closeToken.length();
                        break;
                    }
                }
                if (end == -1) {
                    // close token was not found.
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {
                    ///////////////////////////////////////仅仅修改了该else分支下的个别行代码////////////////////////

                    String value = (argsIndex <= args.length - 1) ?
                            (args[argsIndex] == null ? "" : args[argsIndex].toString()) : expression.toString();
                    builder.append(value);
                    offset = end + closeToken.length();
                    argsIndex++;
                    ////////////////////////////////////////////////////////////////////////////////////////////////
                }
            }
            start = text.indexOf(openToken, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }

    public static String parse0(String text, Object... args) {
        return StringParserUtil.parse("${", "}", text, args);
    }

    /*
     * 作用:自定义String过滤
     * content 输入的内容--例:"dsadsada<yyyy>啦啦啦123123<&*&*&*>"
     * start 要剔除内容的开头字--例:"<"
     * end 要剔除内容的结尾字符--例:">"
     * return 返回剔除后得到的结果--例:"yyyy"
     * */
     public static String getText(String content, String open, String close) {
         int start = content.indexOf(open);
         int end = content.indexOf(close);
         if (start != -1 && end != -1 && start < end) {
        	 return content.substring(start+open.length(), end);
         } 
         return null;
     }
    public static String parse1(String text, Object... args) {
        return StringParserUtil.parse("{", "}", text, args);
    }
    public static String parse2(String text, Object... args) {
        return StringParserUtil.parse0(init(text), args);
    }
    private static String init(String inlineExpression){
    	return inlineExpression.contains("$->{") ? inlineExpression.replaceAll("\\$->\\{", "\\$\\{") : inlineExpression;
    }
    
    public static void main(String[] args) {
		System.out.println(parse2("ds-${yyyy}", 2019));
		String inlineExpression = "user_info_$->{user_id % 2}";
		
		 String result = inlineExpression.contains("$->{") ? inlineExpression.replaceAll("\\$->\\{", "\\$\\{") : inlineExpression;
		 System.out.println("result " + result);
		 
		 String acutal = "user_info_2";
		 System.out.println("user info = " + acutal.split("\\.")[0]);
		 
		 System.out.println(acutal.startsWith("user_info"));
		 
		 System.out.println(getText("ds->${yyyy}", "${", "}"));
		 
	}
}
