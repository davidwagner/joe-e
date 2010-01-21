import xml.parsers.expat

index = None    # used for parsing (are we looking at a session or cookie member?)
name = None     # what's the name of the member we're looking at?
loc = None      # what attribute of the member are we looking at?

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


def parseDocument(filename):
    """
    given a file name, treat is an xml document and set up a structure
    containing the information of the document. 

    This returns 2 items: the fully-qualified classname of the source file to be
    modified and a structure of information taken from the xml file. The
    returned structure is a 2-element list of dictionaries. The first dictionary
    is a dictionary of session members and the second is a dictionary of cookie
    members. Each member specifies a key-value mapping where legal keys are
    'read', 'write', and 'type'.
    """
    global data, classname
    classname = None
    data = []
    data.append({})
    data.append({})

    p = xml.parsers.expat.ParserCreate()
    p.StartElementHandler = start_element
    p.EndElementHandler = end_element
    p.CharacterDataHandler = char_data

    f = open(filename)
    p.ParseFile(f)
    
    for x in data:
        for (key, value) in x.items():
            if "read" not in value.keys():
                value["read"] = False
            if "write" not in value.keys():
                value["write"] = False
    for (key,value) in data[0].items():
        if "type" not in value.keys():
            raise Exception("unspecified type for session member: " + key)
    return (classname, data)


if __name__=='__main__':
    (c, d) = parseDocument("IndexServlet.xml")
    print c
    print d
