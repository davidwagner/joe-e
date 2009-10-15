"""
" macrobenchmark for Joe-E webmail server
" We simulate user interactions and are comparing times
" between the actual Joe-E server and the testing server
" These are macrobenchmarks in that the simulate one complete
" interaction: each user logs in, reads an email, composes an email
" and then logs out.
"""
import mechanize
import cookielib
import time

URLBASE = "http://boink.cs.berkeley.edu:8080/"


def login(username):
    for link in br.links():
        if link.text == "Log In":
            res = br.follow_link(link=link)
            if br.viewing_html():
                br.select_form(nr=0)
                br.form["username"] = username
                br.form["password"] = username
                br.submit()

def readEmail(name):
    for l in br.links():
        if l.text == name:
            br.follow_link(link=l)
#            print br.response().read()
            for link in br.links():
                if link.text == "Back to Inbox":
                    br.follow_link(link=link)
                    return
    return

def composeMail(to=None, subject=None, body=None):
    for l in br.links():
        if l.text == "Write an email":
            br.follow_link(link=l)
            br.select_form(nr=0)
            br.form["to"] = to
            br.form["subject"] = subject
            br.form["body"] = body
            br.submit()

def logout():
    for l in br.links():
        if l.text == "logout":
            br.follow_link(link=l)

def runIterations(app):
    global br
    br.set_handle_equiv(True)
    br.set_handle_redirect(True)
    br.set_handle_referer(True)
    br.set_handle_robots(False)
    
    j = 10
    while j <= 500:
        start = time.time()
        for i in range(j):
            username="p"+str(i)
            br.open(URLBASE+app+"/")
            
            login(username)
            readEmail("Welcome to Joe-E Mail")
        #    composeMail(to="akshayk@boink.joe-e.org", subject=username, body="hello")
            logout()
        time.sleep(0.5)
        stop = time.time()
        print "users: %d Time: %.3f" % (j, stop-start)
        if j < 100:
            j += 10
        elif j < 250: 
            j += 25
        else:
            j += 50


if __name__=='__main__':
    br = mechanize.Browser()
#     br.set_handle_equiv(True)
#     br.set_handle_redirect(True)
#     br.set_handle_referer(True)
#     br.set_handle_robots(False)
#     br.add_password("http://boink.cs.berkeley.edu:8080/manager/html/stop", "akshayk", "akshayk")
#     br.add_password("http://boink.cs.berkeley.edu:8080/manager/html/start", "akshayk", "akshayk")

#     br.open("http://boink.cs.berkeley.edu:8080/manager/html/stop?path=perf")
#     br.open("http://boink.cs.berkeley.edu:8080/manager/html/start?path=servlet")
#     time.sleep(2)
    runIterations("servlet")
#     br.open("http://boink.cs.berkeley.edu:8080/manager/html/stop?path=servlet")
#     br.open("http://boink.cs.berkeley.edu:8080/manager/html/start?path=perf")
#     time.sleep(2)
#     runIterations("perf")
