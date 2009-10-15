import mechanize
import cookielib
import time

URLBASE = "http://boink.cs.berkeley.edu:8080/"

def runIteration(app, username):
    br = mechanize.Browser()
    br.set_handle_equiv(True)
    br.set_handle_redirect(True)
    br.set_handle_referer(True)
    br.set_handle_robots(False)
    br.open(URLBASE+app+"/")
    
    for link in br.links():
        if link.text == "Log In":
            res = br.follow_link(link=link)
            if br.viewing_html():
                br.select_form(nr=0)
                br.form['username'] = username
                br.form['password'] = username
                br.submit()

totalstart = time.time()
for k in range(0, 500, 10):
    start = time.time()
    for i in range(k, k+10):
        runIteration("servlet", "p"+str(i))
    stop = time.time()
    print "%d %0.3f" % (k, stop-start)
totalstop = time.time()
print "Total %0.3f" % (totalstop - totalstart)
