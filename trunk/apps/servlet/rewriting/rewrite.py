import parser, inject
import sys

if len(sys.argv) == 2:
    filename = sys.argv[1]
    srcdir = "./"
elif len(sys.argv) == 3:
    filename = sys.argv[1]
    srcdir = sys.argv[2]
else:
    print "Missing argument: session and cookie specification file"
    sys.exit()


print "Parsing " + filename + "..."

classname, data = parser.parseDocument(filename)

print "Using " + srcdir +" as source directory"

print "Performing Code Injection on " + classname

fname = srcdir+classname.replace(".", "/")+".java"
abbrClassName = classname[classname.rfind(".")+1:]

print inject.codeInject(data, fname, abbrClassName)
