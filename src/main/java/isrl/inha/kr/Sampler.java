package isrl.inha.kr;

import cic.cs.unb.ca.jnetpcap.BasicPacketInfo;

public interface Sampler {
    public boolean is_sampled(BasicPacketInfo basicPacketInfo);
}
