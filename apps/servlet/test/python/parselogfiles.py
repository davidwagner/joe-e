from pylab import *
import sys, os

DIR = sys.argv[1]

fig = figure(figsize=(5, 4))
ax = fig.add_subplot(1.03,1,1.02)
j = 1

servletpoints = []
perfpoints = []
reflectpoints = []
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

    l3 = []
    dir = DIR+"/reflect"+str(kk)
    for x in os.listdir(dir):
        f = file(dir+"/"+x).readlines()
        for line in f:
            l3.append(float(line))
    l3.sort()
 
    init = int(l[0])
    init2 = int(l2[0])
    init3 = int(l3[0])
    start = int(l[0])
    start2 = int(l2[0])
    start3 = int(l3[0])

    i = 0
    i2 = 0
    i3 = 0
    x = []

    y = []
    y2 = []
    y3 = []
    while start + j <= l[len(l)-1] and start2+j <= l2[len(l2)-1] and start3+j <= l3[len(l3)-1] and start < int(l[0]) + 1400 and start2 < int(l2[0])+1400 and start3 < int(l3[0])+1400:
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
            

        count = 0
        while i3 < len(l3) and int(l3[i3]) <= start3+j:
            count += 1
            i3 += 1
        y3.append(float(count)/j)


        start += j
        start2 += j
        start3 += j

    perfmean = mean(y2)
    servletmean = mean(y)
    reflectmean = mean(y3)
    perfpoints.append(perfmean)
    servletpoints.append(servletmean)
    reflectpoints.append(reflectmean)

    perfvar = var(y2)
    servletvar = var(y)
    reflectvar = var(y3)
    print "perfmean: %f servletmean: %f reflectmean: %f" % (perfmean, servletmean, reflectmean)
    print "perfvar: %f servletvar: %f reflectvar: %f" % (perfvar, servletvar, reflectvar)

    ax.plot(x, y)
    ax.plot(x, y2)
    ax.plot(x, y3)

#plot(xpoints, servletpoints)
#plot(xpoints, perfpoints)
ax.set_ylim(0, 1000)
xlabel('Number of Threads')
ylabel('Average Requests per Second')
show()
