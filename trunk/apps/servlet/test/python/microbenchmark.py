import urllib2
import cookielib
import time
import os

URLBASE = "http://localhost:8080/"

# cookieProcessor = urllib2.HTTPCookieProcessor()
# opener = urllib2.url_builder(cookieProcessor)
# urllib2.install_opener(opener)

def runIteration(app, username):
    urllib2.urlopen(URLBASE+app+"/")


print os.popen("/usr/lib/apache-tomcat/bin/shutdown.sh").read()
time.sleep(5)
print os.popen("/usr/lib/apache-tomcat/bin/startup.sh").read()
time.sleep(30)
print "Starting memory monitor... "
os.popen("java -cp /home/akshayk/servlet/bin/ org.joe_e.servlet.test.JMXMonitor > pmemoryusage.out &")
time.sleep(100)
print "Starting experiment... "
totalstart = time.time()
for k in range(0, 15000, 10):
#k = 0
#while True:
    start = time.time()
    for i in range(k, k+10):
        runIteration("servlet", "p"+str(i))
    stop = time.time()
    print "%d %0.3f" % (k, stop-start)
#    k += 10
totalstop = time.time()
print "Total %0.3f" % (totalstop - totalstart)

time.sleep(100)

