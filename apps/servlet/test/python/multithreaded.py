import threading, re
import time
import os, sys
import urllib, urllib2

URLBASE = "http://localhost:8080/"
SLEEPTIME = 180
BASEDIR = "throughput/"
APPNAME = "servlet/"

cookieProcessor = urllib2.HTTPCookieProcessor()
opener = urllib2.build_opener(cookieProcessor)
urllib2.install_opener(opener)
reg = re.compile("hidden\" value=\"([-0-9a-f]*)\"")

done = False
class SThread(threading.Thread):
    def __init__(self, i, f):
        threading.Thread.__init__(self)
        self.i = i
        self.file = file(f+"/"+str(i)+".log", "w")

    def run(self):
	self.arr = []
        while not done:
	    try:
		trace(self.i)
		stop = time.time()
		self.arr.append(str(stop))
	    except Exception as ex:
		pass
	output = reduce(lambda x,y: str(x) + "\n" + str(y), self.arr, "")
	self.file.write(output)
	self.file.flush()
	print "thread %d is done" % (self.i)

def trace(i):
	urllib2.urlopen(URLBASE+APPNAME, None, 10)
	response = urllib2.urlopen(URLBASE+APPNAME+"login", None, 10)
#	text = reduce(lambda x,y: x+ " " + y, response.readlines())
#	m = reg.search(text)
#	token = m.group(1)
#	data = urllib.urlencode({"username": "p"+str(i), "password": "p"+str(i), "__joe-e__csrftoken" : token})
#	response = urllib2.urlopen(URLBASE+APPNAME+"login", data, 5)
	urllib2.urlopen(URLBASE+APPNAME+"logout", None, 10)


for kk in [20]:
    for iters in range(20): #0):
	for jj in ["servlet", "perf"]:
	    os.mkdir(BASEDIR+jj+str(kk)+"-"+str(iters))
	    os.popen("/usr/lib/apache-tomcat/bin/shutdown.sh").read()
	    time.sleep(5)
	    os.popen("/usr/lib/apache-tomcat/bin/startup.sh").read()
	    time.sleep(10)

	    print "Starting experiment ... "
	    print "*** Running " + jj + " with " + str(kk) + "threads ***"
	
	    sthreads = []
	    done = False
	    APPNAME=jj+"/"
	    for i in range(kk):
		sthreads.append(SThread(i, BASEDIR+jj+str(kk)+"-"+str(iters)))

	    for i in range(kk):
		sthreads[i].start()

	    time.sleep(SLEEPTIME)
	    print "joining threads"
	    done = True
	    
	    for i in range(kk):
		sthreads[i].join()

