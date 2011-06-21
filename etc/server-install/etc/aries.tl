# start ARIES explorer, merge into current thread until shutdown
pload explorer
# start ARIES server app 
http start aries
# start REST services and have server join main thread so no more CL input
rest --server start
