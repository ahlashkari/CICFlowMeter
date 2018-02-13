
#！/bin/bash
cp ./jnetpcap-1.3.0/libjnetpcap.so /usr/lib/
if [ $? -eq 0 ];then
    echo "OK..."
    echo "Now, you can run NetWorkTrafficApp"
else 
    echo "please check..." 
fi
