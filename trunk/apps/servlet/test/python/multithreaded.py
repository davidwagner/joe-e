import threading
import mechanize
import cookielib
import time

URLBASE = "http://boink.cs.berkeley.edu:8080/"


def login(br, username):
    for link in br.links():
        if link.text == "Log In":
            res = br.follow_link(link=link)
            if br.viewing_html():
                br.select_form(nr=0)
                br.form["username"] = username
                br.form["password"] = username
                br.submit()

def readEmail(br, name):
    for l in br.links():
        if l.text == name:
            br.follow_link(link=l)
#            print br.response().read()
            for link in br.links():
                if link.text == "Back to Inbox":
                    br.follow_link(link=link)
                    return
    return

def composeMail(br, to=None, subject=None, body=None):
    for l in br.links():
        if l.text == "Write an email":
            br.follow_link(link=l)
            br.select_form(nr=0)
            br.form["to"] = to
            br.form["subject"] = subject
            br.form["body"] = body
            br.submit()

def logout(br):
    for l in br.links():
        if l.text == "logout":
            br.follow_link(link=l)

done = False
class SThread(threading.Thread):
    def __init__(self, i):
        threading.Thread.__init__(self)
        self.i = i
        self.file = file("s"+str(i)+".log", "w")
        self.done = False

    def run(self):
        br = mechanize.Browser()
        br.set_handle_equiv(True)
        br.set_handle_redirect(True)
        br.set_handle_referer(True)
        br.set_handle_robots(False)

        while not done:
            try:
                br.open(URLBASE+"servlet/")
                username = "p"+str(self.i)
                login(br, username)
                readEmail(br, "Welcome to Joe-E Mail")
                logout(br)
                stop = time.time()
                self.file.write(str(stop)+"\n")
                self.file.flush()
            except mechanize.URLError:
                print "error: " + str(time.time())


sthreads = []
for i in range(50):
    sthreads.append(SThread(i))

for i in range(50):
    sthreads[i].start()

time.sleep(600)

done = True
    
for i in range(50):
    sthreads[i].join()

