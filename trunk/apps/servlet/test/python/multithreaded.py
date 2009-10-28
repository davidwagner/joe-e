import threading
import mechanize
import cookielib
import time
import os

URLBASE = "http://boink.cs.berkeley.edu:8080/"
SLEEPTIME = 800
BASEDIR = "throughput/"
APPNAME = "servlet/"

def login(br, username):
    for link in br.links():
        if link.text == "Log In":
            res = br.follow_link(link=link)
            if br.viewing_html():
                br.select_form(nr=0)
                br.form["username"] = username
                br.form["password"] = username
                br.submit()

def readEmail(br, name):
    for l in br.links():
        if l.text == name:
            br.follow_link(link=l)
#            print br.response().read()
            for link in br.links():
                if link.text == "Back to Inbox":
                    br.follow_link(link=link)
                    return
    return

def composeMail(br, to=None, subject=None, body=None):
    for l in br.links():
        if l.text == "Write an email":
            br.follow_link(link=l)
            br.select_form(nr=0)
            br.form["to"] = to
            br.form["subject"] = subject
            br.form["body"] = body
            br.submit()

def logout(br):
    for l in br.links():
        if l.text == "logout":
            br.follow_link(link=l)

done = False

class SThread(threading.Thread):
    def __init__(self, i, f):
        threading.Thread.__init__(self)
        self.i = i
        self.file = file(f+"/"+str(i)+".log", "w")
        self.done = False

    def run(self):
        br = mechanize.Browser()
        br.set_handle_equiv(True)
        br.set_handle_redirect(True)
        br.set_handle_referer(True)
        br.set_handle_robots(False)

        while not done:
            try:
                br.open(URLBASE+APPNAME)
		br.open(URLBASE+APPNAME+"login")
#                username = "p"+str(self.i)
#                login(br, username)
#                readEmail(br, "Welcome to Joe-E Mail")
#                logout(br)
                stop = time.time()
                self.file.write(str(stop)+"\n")
                self.file.flush()
            except Exception:
                print "error: " + str(time.time())

#for kk in [1,5, 10, 15, 20, 25, 30, 35, 40, 45, 50]:
for kk in [1]:
    for jj in ["servlet", "perf"]:
#    for jj in ["perf"]:
	os.mkdir(BASEDIR+jj+str(kk))
	os.popen("/usr/lib/apache-tomcat/bin/shutdown.sh").read()
	time.sleep(5)
	os.popen("/usr/lib/apache-tomcat/bin/startup.sh").read()
	time.sleep(10)
	print "Starting memory monitor ... "
	os.popen("java -cp /home/akshayk/servlet/test/ org.joe_e.servlet.test.JMXMonitor > memoryusage.out &")
	time.sleep(10)
	print "Starting experiment ... "
	print "*** Running " + jj + " with " + str(kk) + "threads ***"
	
	sthreads = []
	done = False
	APPNAME=jj+"/"
	for i in range(kk):
	    sthreads.append(SThread(i, BASEDIR+jj+str(kk)))

	for i in range(kk):
	    sthreads[i].start()

	time.sleep(SLEEPTIME)
	
	done = True

	for i in range(kk):
	    sthreads[i].join()
