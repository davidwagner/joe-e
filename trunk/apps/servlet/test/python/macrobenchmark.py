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

br = mechanize.Browser()
br.set_handle_equiv(True)
br.set_handle_redirect(True)
br.set_handle_referer(True)
br.set_handle_robots(False)

for i in range(50):
    username="p"+str(i)
    br.open(URLBASE+"perf/")

    login(username)
    readEmail("Welcome to Joe-E Mail")
#    composeMail(to="akshayk@boink.joe-e.org", subject=username, body="hello")
    logout()
    print username
