from pylab import *

l = []
j = 20

for i in range(50):
    f = file("oldlogs/perf50flat2/p"+str(i)+".log").readlines()
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
print x
print y
show()
