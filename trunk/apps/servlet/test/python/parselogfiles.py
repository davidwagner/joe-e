from pylab import *
import sys, os

l = []
j = 2

dir = sys.argv[1]
for x in os.listdir(dir):
    f = file(dir+"/"+x).readlines()
    for line in f:
        l.append(float(line))

print len(l)

l.sort()

init = int(l[0])
start = int(l[0])
i = 0
x = []
y = []
while start + j <= l[len(l)-1]:
    count = 0
    while i < len(l) and int(l[i]) <= start+j:
        count += 1
        i += 1
    x.append(start-init)
    y.append(float(count)/j)
    start += j

fig = figure()
ax = fig.add_subplot(111)
ax.plot(x, y)
show()
