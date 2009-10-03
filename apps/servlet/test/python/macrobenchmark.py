from mechanize import Browser

br = Browser()
br.open("http://boink.cs.berkeley.edu:8080/servlet/login")
print br.title()
