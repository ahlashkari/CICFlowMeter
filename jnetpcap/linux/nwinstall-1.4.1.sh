
#！/bin/bash
cp ./jnetpcap-1.4.r1425/libjnetpcap.so /usr/lib/
if [ $? -eq 0 ];then
    echo "OK..."
    echo "Now, you can run NetWorkTrafficApp"
else 
    echo "please check..." 
fi
