import threading
import time
import os
import urllib2

URLBASE = "http://boink.cs.berkeley.edu:8080/"
SLEEPTIME = 300
BASEDIR = "throughput/"
APPNAME = "servlet/"

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
		urllib2.urlopen(URLBASE+APPNAME)
		urllib2.urlopen(URLBASE+APPNAME+"login")
                stop = time.time()
		self.arr.append(str(stop))
            except Exception:
                print "error: " + str(time.time())
	output = reduce(lambda x,y: str(x) + "\n" + str(y), self.arr)
	self.file.write(output)
	self.file.flush()

for kk in [1,5, 10, 15, 20]:
    for iters in range(10):
	for jj in ["perf", "servlet"]:
	    os.mkdir(BASEDIR+jj+str(kk)+"-"+str(iters))
	    os.popen("/usr/lib/apache-tomcat/bin/shutdown.sh").read()
	    time.sleep(5)
	    os.popen("/usr/lib/apache-tomcat/bin/startup.sh").read()
	    time.sleep(15)
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
	    
	    done = True
	    
	    for i in range(kk):
		sthreads[i].join()

