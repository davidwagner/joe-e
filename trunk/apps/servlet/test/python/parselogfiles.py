from pylab import *
import sys, os

DIR = sys.argv[1]

fig = figure(figsize=(5, 4))
ax = fig.add_subplot(1.03,1,1.02)
j = 1

servletpoints = []
perfpoints = []
# reflectpoints = []
#xpoints = [1,5,10,15,20,25,30,35,40,45,50,60,70,80]
#xpoints = [1,5,10,15,20,25,30]
xpoints = [20]

# this is the servlet related metric
for kk in xpoints:
    for iters in range(10):
        l = []
        dir = DIR+"/servlet"+str(kk)+"-"+str(iters)
        for x in os.listdir(dir):
            f = file(dir+"/"+x).readlines()
            for line in f:
                l.append(float(line))

        l.sort()
    
        i = 0
        x = []
        start = int(l[0])
        init = int(l[0])
        y = []
        while start+j <= l[len(l)-1]:
            count = 0
            while i < len(l) and int(l[i]) <= start+j:
                count += 1
                i += 1
            x.append(start-init)
            y.append(float(count)/j)

            start += j

        servletmean = mean(y)
        servletpoints.append(servletmean)
        servletvar = var(y)
        print "servlet mean: %f" % (servletmean)


# this is the perf-related metric
for kk in xpoints:
    for iters in range(10):
        l = []


        dir = DIR+"/perf"+str(kk)+"-"+str(iters)
        for x in os.listdir(dir):
            f = file(dir+"/"+x).readlines()
            for line in f:
                l.append(float(line))

        l.sort()
    
        i = 0
        x = []
        start = int(l[0])
        init = int(l[0])
        y = []
        while start+j <= l[len(l)-1]:
            count = 0
            while i < len(l) and int(l[i]) <= start+j:
                count += 1
                i += 1
            x.append(start-init)
            y.append(float(count)/j)

            start += j

        servletmean = mean(y)
        servletpoints.append(servletmean)
        servletvar = var(y)
        print "servlet mean: %f" % (servletmean)
