from pylab import *
import sys

def duplicate (l):
    output = []
    for i in range(len(l)):
        output.append(l[i])
        output.append(l[i])

    return output

f = open(sys.argv[1]).readlines()
f2 = open(sys.argv[2]).readlines()
output = map(lambda x: int(float(x)/(2**20)), f)[90:]
output2 = map(lambda x: int(float(x)/(2**20)), f2)[95:]
output2 = duplicate(output2)
x = map(lambda x: 208*x, range(240))
print len(output)
print len(output2)

fig = figure(figsize=(5,4))
ax = fig.add_subplot(1.03,1,1.01)
ax.plot(x, output[:240])
ax.plot(x, output2[:240])
ax.set_ylim (0, 200)
xlabel('Time (s)')
ylabel('Number of Users')
show()

        
