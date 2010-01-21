import xml.parsers.expat

classname = None    # this is the fully qualified name of the class being modified
data = []           # contains information about what fields to make available
                    # and read/write permissions
data.append({})     # first is session members
data.append({})     # 2nd is cookie members

index = None        # used for parsing (are we looking at a session or cookie member?)
name = None         # what's the name of the member we're looking at?
loc = None          # what attribute of the member are we looking at?

"""
TODO: error handling
TODO: what happens if they don't specify read/write default to false, shouldn't break
"""
def start_element(n, attrs):
    """
    start element handler for the xml parser. 
    """
    global loc,index,classname,name
    if n=="class":
        classname = attrs['name']
    elif n=="session-member":
        index = 0
        name = attrs['name']
        data[index][name] = {}
    elif n=="cookie-member":
        index = 1
        name = attrs['name']
        data[index][name] = {}
    elif n=="read" or n=="type" or n=="write":
        loc = n
    else:
        raise Exception("invalid element name " + n)

def end_element(name):
    """
    end element handler for the xml parser
    """
    global loc
    loc = None

def char_data(d):
    """
    char_data handler for the xml parser
    """
    if loc == "type":
        data[index][name]["type"] = d
    elif loc == "read":
        if d == "True":
            data[index][name]["read"] = True
        else:
            data[index][name]["read"] = False
    elif loc == "write":
        if d == "True":
            data[index][name]["write"] = True
        else:
            data[index][name]["write"] = False


# set up the xml parser and parse the specified file. Basically this just
# fills the data array with the information specified in the document

p = xml.parsers.expat.ParserCreate()

p.StartElementHandler = start_element
p.EndElementHandler = end_element
p.CharacterDataHandler = char_data

f = open("IndexServlet.xml")
p.ParseFile(f)


# These are the imports that may need to be added to the java source file. As we
# process the file we'll make sure to not include imports that are already there
imports = ["import javax.servlet.http.Cookie;", 
           "import javax.servlet.http.HttpSession;", 
           "import org.joe_e.servlet.AbstractCookieView;",
           "import org.joe_e.servlet.AbstractSessionView;"]

# This is the string of entirely new source code that needs to be added to the
# file. It contains the SessionView and CookieView definitions. The next several
# lines are exclusively for determining what this string should be.
outputString="""
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

outputString += "\n}"





# The following is actually reading the source file and "modifying it" For now
# we just print out a new source file that can replace the existing one

fname = "src/"+classname.replace(".", "/")+".java"
f = open(fname, "r")

abbrClassName = classname[classname.rfind(".")+1:]

outputFile = ""     # this is the text of the new source file

importsdone=False   # have we seen all the imports?

for line in f:
    if line.strip()[0:7] == "package":
        outputFile += line
        continue

    if line.strip()[0:6] == "import":
        # if this is an import, then check that it's not one of the required
        # ones. If it is remove that requirement
        for imp in imports:
            if line.strip() == imp:
                imports.remove(imp)
                break

    if not importsdone and line.strip()[0:6] != "import" and line.strip() != "":
        # We're done looking at all the imports, so add in the ones that we need
        importsdone=True
        for imp in imports:
            outputFile += imp+"\n"

    if line.strip().find("class") != -1 and line.strip().find(abbrClassName) != -1:
        # we found the beginning of the class definition, inject the new source
        # code
        outputFile += line
        outputFile += outputString

    else:
        outputFile += line

print outputFile
        
