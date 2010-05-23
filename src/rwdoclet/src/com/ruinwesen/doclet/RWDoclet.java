package com.ruinwesen.doclet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.*;

import com.sun.javadoc.*;

public class RWDoclet {
	static String outputDir = "/Users/manuel/javadoc-output/";
	
	File classFile(ClassDoc myClass) {
		return new File(outputDir + myClass.name() + ".html");
	}

	String methodName(ExecutableMemberDoc myMethod, ClassDoc myClass) {
		String result = "";
		result += myClass.name() + "-" + myMethod.name();
		Parameter params[] = myMethod.parameters();
		for (int i = 0; i < params.length; i++) {
			Parameter param = params[i];
			result += "-" + param.typeName();
		}
		result = result.replace("[]", "-array");
		return result;
	}

	File methodFile(ExecutableMemberDoc myMethod, ClassDoc myClass) {
		String name = methodName(myMethod, myClass);
		return new File(outputDir + name + ".html");
	}
	
	String methodFileName(ExecutableMemberDoc myMethod, ClassDoc myClass) {
		return methodName(myMethod, myClass) + ".html";
	}

	String classFileName(ClassDoc myClass) {
		return myClass.name() + ".html";
	}
	

	String methodString(ExecutableMemberDoc myMethod, boolean showNames) {
		String result = myMethod.name() + "(";
		
		Parameter params[] = myMethod.parameters();
		for (int i = 0; i < params.length; i++) {
			Parameter param = params[i];
			result += param.typeName();
			if (showNames)
				result += " " + param.name();
			if (i != (params.length - 1)) 
				result += ", ";
		}
		result += ")";
		
		return result;
	}
	
	ExecutableMemberDoc []classMethods(ClassDoc myClass) {
		ArrayList<ExecutableMemberDoc> result = new ArrayList<ExecutableMemberDoc>();
		
		for (ConstructorDoc myConst : myClass.constructors()) {
			if (myConst.commentText().equals("") && (myConst.tags("return").length == 0))
				continue;
			result.add(myConst);
			
		}
		
		for (ExecutableMemberDoc myMethod : myClass.methods()) {
			if (myMethod.name().equals("toString"))
				continue;
			if (myMethod.name().equals("clone"))
				continue;
			if (myMethod.commentText().equals("") && (myMethod.tags("return").length == 0))
				continue;
			result.add(myMethod);
		}
		return result.toArray(new ExecutableMemberDoc[0]);
	}

	ClassDoc []classDocs(ClassDoc []classes) {
		ArrayList<ClassDoc> result = new ArrayList<ClassDoc>();
		
		for (ClassDoc myClass: classes) {
			if (myClass.name().equals("Plug"))
				continue;
			result.add(myClass);
		}
		
		return result.toArray(new ClassDoc[0]);
	}
	
	String classesNavigation(ClassDoc _myClass, ClassDoc classes[]) {
		String result = "<ul>";
		result += "<li><span class=\"navtitle\">RWMIDI Examples</span></li>\n";
		result += "<li><a href=\"sketch1.html\">Sketch 1</a></li>\n";
		result += "</ul>\n";
		result += "<ul>\n";
		result += "<li><span class=\"navtitle\">RWMIDI Classes</span></li>";
		for (int i = classes.length - 1; i >= 0; i --) {
			ClassDoc myClass = classes[i];
			if (myClass == _myClass) {
				result += "<li class=\"active\"><a href=\"" + classFileName(myClass) + "\">" + myClass.name() + "</a></li>\n";
			} else {
				result += "<li><a href=\"" + classFileName(myClass) + "\">" + myClass.name() + "</a></li>\n";
			}
		}
		result += "</ul>";
		return result;
	}
	
	String classMemberNavigation(ExecutableMemberDoc _myMethod, ClassDoc myClass) {
		String result = "<ul>";
		ExecutableMemberDoc[] methods = classMethods(myClass);
		result += "<li><span class=\"navtitle\">Methods:</span></li>";
		for (ExecutableMemberDoc method : methods) {
			if (method == _myMethod) {
				result += "<li class=\"active\"><a href=\"" + methodFileName(method, myClass) + "\">" + methodString(method, false) + "</a></li>\n";
			} else {
				result += "<li><a href=\"" + methodFileName(method, myClass)+ "\">" + methodString(method, false) + "</a></li>\n";
			}
		}
		result += "</ul>";
		return result;
	}
	
	String HTMLHeader(ClassDoc myClass, ClassDoc[] classes, String title) {
		String result = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
		"<html lang=\"en\" xmlns:ruinwesen=\"http://ruinwesen.com/\" xmlns:bknr=\"http://bknr.net/\" xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
		"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\n" +
		"<link href=\"stylesheet.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
		"<title>";
		result += "RWMidi - " + title;
		result += "</title>\n" +
		"<body><div class=\"container\">\n" +
		  "<div class=\"head\">\n" +
		    "<a href=\"http://ruinwesen.com/\" title=\"Ruin &amp; Wesen\"><img src=\"logo2.png\" id=\"logo\" alt=\"Logo\"/></a>\n" +
		    "<div id=\"classnavigation\">\n";

		result += classesNavigation(myClass, classes);
		result += "</div></div>\n" +
		"<div class=\"rightcontainer\">\n" +
		"<div id=\"navigation\">\n";
		
		result += classMemberNavigation(null, myClass);
		result += "</div></div>\n";
		
		result += "<div class=\"content\">";
		// result += "<div class=\"banner\"><img src=\"banner2.png\" alt=\"Banner\"/></div>";
		return result;
	}
	
	static String replaceLinkTags(String text) {
        String patternStr = "\\{@[lL]ink ([^}]*)\\}";
        String replacementStr = "\\1";
        
        // Compile regular expression
        Pattern pattern = Pattern.compile(patternStr);
        
        // Replace all occurrences of pattern in input
        Matcher matcher = pattern.matcher(text);
        // Replace all occurrences of pattern in input
        StringBuffer buf = new StringBuffer();
        boolean found = false;
        while ((found = matcher.find())) {
            // Get the match result
            String replaceStr = matcher.group(1);

            replaceStr = "<a href=\"" + replaceStr + ".html\">" + replaceStr + "</a>";
        
            // Insert replacement
            matcher.appendReplacement(buf, replaceStr);
        }
        matcher.appendTail(buf);
        
        // Get result
        String result = buf.toString();
        return result;
	}

	String HTMLContent(ClassDoc myClass) {
		String result = "";
		result += "<div class=\"contenttext\"><h3>" + myClass.name() + "</h3>";
		result += "<hr/>";
		result += "<p>" + replaceLinkTags(myClass.commentText()) + "</p>";
		
		result += "</div>";
		return result;
	}

	String HTMLContent(ExecutableMemberDoc myExecMember, ClassDoc myClass) {
		String result = "";
		result += "<div class=\"contenttext\"><h3>" + myClass.name() + "." + methodString(myExecMember, true);
		if (myExecMember instanceof MethodDoc) {
			MethodDoc myMethod = (MethodDoc)myExecMember;
			if (!myMethod.returnType().typeName().equals("void")) {
				result += " - " + myMethod.returnType();
			}
		}
		result += "</h3>";
		result += "<hr/>";
		Parameter params[] = myExecMember.parameters();
		Tag tags[] = myExecMember.tags("param");
		int i = 0;
		for (Parameter param : params) {
			result += "<p><b>" + param + "</b>: ";
			if (i < tags.length) {
				String text = replaceLinkTags(tags[i].text());
				int index = text.indexOf(" ");
				result += text.substring(index + 1);
			}
			result += "</p>";
			i++;
		}
		
		tags = myExecMember.tags("return");
		if (tags.length > 0) {
			result += "<p><b>Returns</b> " + tags[0].text() + "</p>";
		}
		
		result += "<p>" + replaceLinkTags(myExecMember.commentText()) + "</p>";
		
		result += "</div>";
		return result;
	}
	
	
	String HTMLFooter() {
		return "</div></div></body></html>";
	}
	
	void writeClassFile(ClassDoc myClass, ClassDoc[] classes) {
		File file = classFile(myClass);
		System.out.println("writing file " + file);
		try {
			FileOutputStream out = new FileOutputStream(file);
			PrintStream ps = new PrintStream(out);
			ps.print(HTMLHeader(myClass, classes, myClass.name()));
			ps.print(HTMLContent(myClass));
			ps.print(HTMLFooter());
			ps.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void writeMethodFile(ExecutableMemberDoc myMethod, ClassDoc myClass, ClassDoc[] classes) {
		File file = methodFile(myMethod, myClass);
		System.out.println("writing file " + file);
		try {
			FileOutputStream out = new FileOutputStream(file);
			PrintStream ps = new PrintStream(out);
			ps.print(HTMLHeader(myClass, classes, myClass.name() + " - " + myMethod.name()));
			ps.print(HTMLContent(myMethod, myClass));
			ps.print(HTMLFooter());
			ps.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * static start point for the ruin & wesen doclet
	 * 
	 * @param root
	 * @return true
	 */
	public static boolean start(RootDoc root) {
		RWDoclet doclet = new RWDoclet();

		//get the sourcepath from the sourcepath option setup with javadoc call
		String libfolder = ".";
		File libFolder;
		for(String[] options:root.options()){
			if(options[0].equals("-sourcepath")){
				libfolder = options[1];
			}
		}
		
		libFolder = new File(libfolder);
		
		//get the docfolder and copies the ressource files from the templatefolder 
		outputDir = libfolder + "/"+"documentation/";
		

		ClassDoc[] classes = doclet.classDocs(root.classes());
		File dirFile = new File(outputDir);
		dirFile.mkdirs();
		for (int i = classes.length - 1; i >= 0; i--) {
			ClassDoc myClass = classes[i];
		
			doclet.writeClassFile(myClass, classes);
			for (ExecutableMemberDoc myMethod : doclet.classMethods(myClass)) {
				doclet.writeMethodFile(myMethod, myClass, classes);
			}
		}
		return true;
	}
	
	public static void main(String args[]) {
		String test = "hallo peepz {@link Java-Foo} blor blorg";
		System.out.println(replaceLinkTags(test));
	}

}
