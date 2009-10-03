import mechanize
import cookielib
import time

URLBASE = "http://boink.cs.berkeley.edu:8080/"

def createAccount(username):
    for link in br.links():
        if link.text == "Create an Account":
            br.follow_link(link=link)
            br.select_form(nr=0)
            br.form["username"] = username
            br.form["password1"] = username
            br.form["password2"] = username
            br.submit()


br = mechanize.Browser()
br.set_handle_equiv(True)
br.set_handle_redirect(True)
br.set_handle_referer(True)
br.set_handle_robots(False)



for i in range(1,50):
    br.open(URLBASE+"servlet/")
    createAccount("p"+str(i))
    print "p"+str(i)
    time.sleep(2)
