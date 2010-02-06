from pylab import *
import sys, os

DIR = sys.argv[1]

fig = figure(figsize=(5, 4))
ax = fig.add_subplot(1.03,1,1.02)
j = 1

servletpoints = []
perfpoints = []
# reflectpoints = []
xpoints = [1,5,10,15,20,30,40,50]
#xpoints = [1,5,10,15,20,25,30]
#xpoints = [1,5,10]

# this is the servlet related metric
for kk in xpoints:
    for iters in range(1):
        l = []
        dir = DIR+"/servlet"+str(kk)+"-"+str(iters)
        for x in os.listdir(dir):
            f = file(dir+"/"+x).readlines()
            for line in f[1:]:
                l.append(float(line))
        if len(l) == 0:
            print "no data"
            continue
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

        ax.plot(x, y)
        servletmean = mean(y)
        servletpoints.append(servletmean)
        servletvar = var(y)
        print "servlet mean: %f" % (servletmean)


# this is the perf-related metric
for kk in xpoints:
    for iters in range(1):
        l = []


        dir = DIR+"/perf"+str(kk)+"-"+str(iters)
        for x in os.listdir(dir):
            f = file(dir+"/"+x).readlines()
            for line in f[1:]:
                l.append(float(line))
        if len(l) == 0:
            print "no data"
            continue
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

        ax.plot(x,y)
        servletmean = mean(y)
        servletpoints.append(servletmean)
        servletvar = var(y)
        print "perf mean: %f" % (servletmean)


ax.set_ylim(0,200)
xlabel('time')
ylabel('number of requests handled')
show()
