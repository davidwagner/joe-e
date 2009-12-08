import threading
import time
import os
import urllib2

URLBASE = "http://boink.cs.berkeley.edu:8080/"
SLEEPTIME = 120
BASEDIR = "throughput/"
APPNAME = "servlet/"

done = False
class SThread(threading.Thread):
    def __init__(self, i, f):
        threading.Thread.__init__(self)
        self.i = i
        self.file = file(f+"/"+str(i)+".log", "w")
        self.done = False

    def run(self):
        while not done:
            try:
		urllib2.urlopen(URLBASE+APPNAME)
		urllib2.urlopen(URLBASE+APPNAME+"login")
                stop = time.time()
                self.file.write(str(stop)+"\n")
                self.file.flush()
            except Exception:
                print "error: " + str(time.time())

# for kk in [1,5, 10, 15, 20, 25, 30, 35, 40, 45, 50]:
for kk in [1]:
    for jj in ["servlet", "perf", "reflect"]:
#    for jj in ["perf"]:
	os.mkdir(BASEDIR+jj+str(kk))
# 	os.popen("/usr/lib/apache-tomcat/bin/shutdown.sh").read()
# 	time.sleep(5)
# 	os.popen("/usr/lib/apache-tomcat/bin/startup.sh").read()
# 	time.sleep(15)
#	print "Starting memory monitor ... "
#	os.popen("java -cp /home/akshayk/servlet/test/ org.joe_e.servlet.test.JMXMonitor > memoryusage.out &")
#	time.sleep(10)
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

