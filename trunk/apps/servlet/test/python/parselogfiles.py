from pylab import *
import sys, os

DIR = sys.argv[1]

fig = figure(figsize=(5, 4))
ax = fig.add_subplot(1.03,1,1.02)
j = 5

servletpoints = []
perfpoints = []
#xpoints = [1,5,10,15,20,25,30,35,40,45,50,60,70,80]
#xpoints = [1,5,10,15,20,25,30]
xpoints = [1]
for kk in xpoints:
    l = []


    dir = DIR+"/servlet"+str(kk)
    for x in os.listdir(dir):
        f = file(dir+"/"+x).readlines()
        for line in f:
            l.append(float(line))

    print len(l)

    l.sort()
    
    l2 = []
    dir = DIR+"/perf"+str(kk)
    for x in os.listdir(dir):
        f = file(dir+"/"+x).readlines()
        for line in f:
            l2.append(float(line))

    print len(l2)
    l2.sort()

 
    init = int(l[0])
    init2 = int(l2[0])
    start = int(l[0])
    start2 = int(l2[0])

    i = 0
    i2 = 0
    x = []

    y = []
    y2 = []
    while start + j <= l[len(l)-1] and start2+j <= l2[len(l2)-1] and start < int(l[0]) + 1400 and start2 < int(l2[0])+1400:
        count = 0
        while i < len(l) and int(l[i]) <= start+j:
            count += 1
            i += 1
        x.append(start-init)
        y.append(float(count)/j)
        count = 0
        while i2 < len(l2) and int(l2[i2]) <= start2+j:
            count += 1
            i2 += 1
        y2.append(float(count)/j)
        start += j
        start2 += j

    perfmean = mean(y2)
    servletmean = mean(y)
    perfpoints.append(perfmean)
    servletpoints.append(servletmean)

    perfvar = var(y2)
    servletvar = var(y)
    print "perfmean: %f servletmean: %f" % (perfmean, servletmean)
    print "perfvar: %f servletvar: %f" % (perfvar, servletvar)

    ax.plot(x, y)
    ax.plot(x, y2)

#plot(xpoints, servletpoints)
#plot(xpoints, perfpoints)
ax.set_ylim(0, 100)
xlabel('Number of Threads')
ylabel('Average Requests per Second')
show()