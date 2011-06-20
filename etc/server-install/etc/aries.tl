# start ARIES explorer, merge into current thread until shutdown
pload explorer
# start REST services on control port 8812
rest -p 8812 start
# start ARIES server app and have server join main thread so no more CL input
http --block start aries
