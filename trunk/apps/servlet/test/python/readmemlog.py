from pylab import *
import sys

f = open(sys.argv[1]).readlines()
f2 = open(sys.argv[2]).readlines()
output = map(lambda x: int(float(x)/(2**20)), f)
output2 = map(lambda x: int(float(x)/(2**20)), f2)

fig = figure(figsize=(5,4))
ax = fig.add_subplot(1.03,1,1.01)
ax.plot(output[:600])
ax.plot(output2[:600])
ax.set_ylim (0, 60)
xlabel('Time (s)')
ylabel('Tomcat Memory Usage (MB)')
show()

