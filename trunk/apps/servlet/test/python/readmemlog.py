from pylab import *
import sys

f = open(sys.argv[1]).readlines()

output = map(lambda x: int(x), f)

fig = figure()
ax = fig.add_subplot(111)
ax.plot(output)
show()

