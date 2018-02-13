package cic.cs.unb.ca.jnetpcap;

import org.jnetpcap.PcapIf;

import java.util.ArrayList;
import java.util.List;

public class PcapIfWrapper {

    private PcapIf pcapIf;
    private String prompt;

    public PcapIfWrapper(PcapIf pcapIf) {
        this.pcapIf = pcapIf;
    }

    public PcapIfWrapper(String prompt) {
        this.prompt = prompt;
    }

    public static List<PcapIfWrapper> fromPcapIf(List<PcapIf> ifs){
        List<PcapIfWrapper> ifWrappers = new ArrayList<>();
        for(PcapIf pcapif:ifs){
            ifWrappers.add(new PcapIfWrapper(pcapif));
        }
        return ifWrappers;
    }

    public String name(){
        return pcapIf.getName();
    }

    @Override
    public String toString() {
        if(pcapIf == null){
            return prompt;
        }else{
            return String.format("%s (%s)",pcapIf.getName(),pcapIf.getDescription());
        }
    }
}
