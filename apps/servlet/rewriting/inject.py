
def buildInjectionString(data):
    # This is the string of entirely new source code that needs to be added to the
    # file. It contains the SessionView and CookieView definitions. The next several
    # lines are exclusively for determining what this string should be.
    outputString="""
// BEGIN AUTO-GENERATED CODE

public SessionView session;
public CookieView cookies;

public class SessionView extends AbstractSessionView {
\tprivate HttpSession session;
\tpublic SessionView(HttpSession ses) {
\t\tsuper(ses);
\t\tsession = ses;
\t}
"""

    # Complete the SessionView definition.
    for (key,value) in data[0].items():
        if value["read"]:
            outputString += "\tpublic " + value["type"] + " get"+key+"() {\n"
            outputString += "\t\treturn ("+value["type"]+") session.getAttribute(\"__joe-e__"+key+"\");\n\t}\n"
        if value["write"]:
            outputString += "\tpublic void set"+key+"("+value["type"]+" arg) {\n"
            outputString += "\t\tsession.setAttribute(\"__joe-e__"+key+"\", arg);\n\t}\n"

    outputString += """}

public AbstractSessionView getSessionView(HttpSession ses) {
\treturn new SessionView(ses);
}

public class CookieView extends AbstractCookieView {
\tpublic CookieView(Cookie[] c) {
\t\tsuper(c);
\t}
"""

    # complete the CookieView definition
    for (key, value) in data[1].items():
        if value["read"]:
            outputString += "\tpublic String get"+key+"() {\n"
            outputString += "\t\tfor (Cookie c : cookies) {\n"
            outputString += "\t\t\tif (c.getName().equals(\"__joe-e__"+key+"\")) {\n"
            outputString += "\t\t\t\treturn c.getValue();\n\t\t\t}\n\t\t}\n"
            outputString += "\t\treturn null;\n\t}\n"
            
        if value["write"]:
            outputString += "\tpublic void set"+key+"(String arg) {\n"
            outputString += "\t\tboolean done = false;\n"
            outputString += "\t\tfor (Cookie c : cookies) {\n"
            outputString += "\t\t\tif (c.getName().equals(\"__joe-e__"+key+"\")) {\n"
            outputString += "\t\t\t\tc.setValue(arg);\n"
            outputString += "\t\t\t\tdone = true;\n\t\t\t}\n\t\t}\n"
            outputString += "\t\tif (!done) {\n\t\t\tcookies.add(new Cookie(\"__joe-e__"+key+"\", arg));\n"
            outputString += "\t\t}\n\t}\n"

    outputString += """\n}

public AbstractCookieView getCookieView(Cookie[] c) {
\treturn new CookieView(c);
}

// END AUTO-GENERATED CODE
"""

    return outputString

def codeInject(data, srcfile, classname):
    # These are the imports that may need to be added to the java source file. As we
    # process the file we'll make sure to not include imports that are already there
    imports = ["import javax.servlet.http.Cookie;", 
               "import javax.servlet.http.HttpSession;", 
               "import org.joe_e.servlet.AbstractCookieView;",
               "import org.joe_e.servlet.AbstractSessionView;"]
    injectString = buildInjectionString(data)


    # The following is actually reading the source file and "modifying it" For now
    # we just print out a new source file that can replace the existing one
    f = open(srcfile, "r")
    
    outputString = ""     # this is the new source file
    importsdone = False

    for line in f:
        if line.strip()[0:7] == "package":
            outputString += line
            continue

        if line.strip()[0:6] == "import":
            # if this is an import, then check that it's not one of the required
            # ones. If it is remove that requirement
            for imp in imports:
                if line.strip() == imp:
                    imports.remove(imp)
                    break
        if not importsdone and line.strip()[0:6] != "import" and line.strip() != "":
            # We're done looking at all of the imports, so add in the ones we need
            importsdone = True
            for imp in imports:
                outputString += imp+"\n"

        if line.strip().find("class") != -1 and line.strip().find(classname) != -1:
            # we found the beginning of the class definition, inject the new source
            outputString += line
            outputString += injectString

        else:
            outputString += line

    return outputString

