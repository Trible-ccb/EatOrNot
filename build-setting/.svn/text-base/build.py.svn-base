#! bin/python
# test.py

import os
import shutil
import sys

print "os.name:"+os.name

if os.getenv("ANDROID_SDK") == None:
	print "please set your local android sdk dir"
	exit()
else :
	print "current $(ANDROID_SDK)="+os.getenv("ANDROID_SDK")
	
if os.getenv("PROJECT_HOTELFINDER_HOME") == None:
	print "please set your local hotel finder project dir"
	exit()
else :
	print "current $(PROJECT_HOTELFINDER_HOME)="+os.getenv("PROJECT_HOTELFINDER_HOME")

def build(market):
	if market == "LePhone":
			shutil.copyfile("..\\build-resources\\lephone_icon.png", "..\\res\\drawable-hdpi\\icon.png")
	else:
		shutil.copyfile("..\\build-resources\\default_hidp_icon.png", "..\\res\\drawable-hdpi\\icon.png")
	os.system("ant -buildfile auto-build.xml hotel_finder -Dmarket={0}".format(market))
	pass
	
if len(sys.argv) > 1 :
	build(sys.argv[1])
else :
	f = open('./market.txt', 'r')
	try:
		for line in f:
			if line.startswith('#'):
				continue
			line = line.strip('\r\n')
			build(line)

	finally:
		f.close()



