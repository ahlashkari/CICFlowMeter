#ifndef __MAC_ADDR_HDR__
#define __MAC_ADDR_HDR__

extern "C" int mac_addr_dlpi ( char *dev, u_char  *addr);
extern "C" int mac_addr_sys ( char *dev, u_char  *addr);

#endif
