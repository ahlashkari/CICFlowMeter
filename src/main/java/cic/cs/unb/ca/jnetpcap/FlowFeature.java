package cic.cs.unb.ca.jnetpcap;


import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public enum FlowFeature {

    fid("Flow ID","FID",false),							//1 this index is for feature not for ordinal
    src_ip("Src IP","SIP",false),						//2
    src_port("Src Port","SPT"),					//3
    dst_ip("Dst IP","DIP",false),			//4
    dst_pot("Dst Port","DPT"),		//5
    prot("Protocol","PROT"),						//6
    tstp("Timestamp","TSTP",false),						//7
    fl_dur("Flow Duration","DUR"),				//8
    tot_fw_pkt("Tot Fwd Pkts","TFwP"),							//9
    tot_bw_pkt("Tot Bwd Pkts","TBwP"),				//10
    tot_l_fw_pkt("TotLen Fwd Pkts","TLFwP"),		//11
    tot_l_bw_pkt("TotLen Bwd Pkts","TLBwP"),		//12
    fw_pkt_l_max("Fwd Pkt Len Max","FwPLMA"),					//13
    fw_pkt_l_min("Fwd Pkt Len Min","FwPLMI"),					//14
    fw_pkt_l_avg("Fwd Pkt Len Mean","FwPLAG"),				//15
    fw_pkt_l_std("Fwd Pkt Len Std","FwPLSD"),					//16
    bw_pkt_l_max("Bwd Pkt Len Max","BwPLMA"),					//17
    bw_pkt_l_min("Bwd Pkt Len Min","BwPLMI"),					//18
    bw_pkt_l_avg("Bwd Pkt Len Mean","BwPLAG"),				//19
    bw_pkt_l_std("Bwd Pkt Len Std","BwPLSD"),					//20
    fl_byt_s("Flow Byts/s","FB/s"),								//21
    fl_pkt_s("Flow Pkts/s","FP/s"),							//22
    fl_iat_avg("Flow IAT Mean","FLIATAG"),									//23
    fl_iat_std("Flow IAT Std","FLIATSD"),									//24
    fl_iat_max("Flow IAT Max","FLIATMA"),									//25
    fl_iat_min("Flow IAT Min","FLIATMI"),									//26
    fw_iat_tot("Fwd IAT Tot","FwIATTO"),									//27
    fw_iat_avg("Fwd IAT Mean","FwIATAG"),									//28
    fw_iat_std("Fwd IAT Std","FwIATSD"),										//29
    fw_iat_max("Fwd IAT Max","FwIATMA"),										//30
    fw_iat_min("Fwd IAT Min","FwIATMI"),										//31
    bw_iat_tot("Bwd IAT Tot","BwIATTO"),									//32
    bw_iat_avg("Bwd IAT Mean","BwIATAG"),									//33
    bw_iat_std("Bwd IAT Std","BwIATSD"),										//34
    bw_iat_max("Bwd IAT Max","BwIATMA"),										//35
    bw_iat_min("Bwd IAT Min","BwIATMI"),										//36
    fw_psh_flag("Fwd PSH Flags","FwPSH"),									//37
    bw_psh_flag("Bwd PSH Flags","BwPSH"),									//38
    fw_urg_flag("Fwd URG Flags","FwURG"),									//39
    bw_urg_flag("Bwd URG Flags","BwURG"),									//40
    fw_hdr_len("Fwd Header Len","FwHL"),							//41
    bw_hdr_len("Bwd Header Len","BwHL"),							//42
    fw_pkt_s("Fwd Pkts/s","FwP/s"),							//43
    bw_pkt_s("Bwd Pkts/s","Bwp/s"),							//44
    pkt_len_min("Pkt Len Min","PLMI"),							//45
    pkt_len_max("Pkt Len Max","PLMA"),							//46
    pkt_len_avg("Pkt Len Mean","PLAG"),						//47
    pkt_len_std("Pkt Len Std","PLSD"),							//48
    pkt_len_var("Pkt Len Var","PLVA"),		//49
    fin_cnt("FIN Flag Cnt","FINCT"),				//50
    syn_cnt("SYN Flag Cnt","SYNCT"),				//51
    rst_cnt("RST Flag Cnt","RSTCT"),				//52
    pst_cnt("PSH Flag Cnt","PSHCT"),				//53
    ack_cnt("ACK Flag Cnt","ACKCT"),				//54
    urg_cnt("URG Flag Cnt","URGCT"),				//55
    cwe_cnt("CWE Flag Count","CWECT"),				//56
    ece_cnt("ECE Flag Cnt","ECECT"),				//57
    down_up_ratio("Down/Up Ratio","D/URO"),				//58
    pkt_size_avg("Pkt Size Avg","PSAG"),			//59
    fw_seg_avg("Fwd Seg Size Avg","FwSgAG"),		//60
    bw_seg_avg("Bwd Seg Size Avg","BwSgAG"),		//61
    fw_byt_blk_avg("Fwd Byts/b Avg","FwB/BAG"),			//63   62 is duplicated with 41,so has been deleted
    fw_pkt_blk_avg("Fwd Pkts/b Avg","FwP/BAG"),		//64
    fw_blk_rate_avg("Fwd Blk Rate Avg","FwBRAG"),			//65
    bw_byt_blk_avg("Bwd Byts/b Avg","BwB/BAG"),			//66
    bw_pkt_blk_avg("Bwd Pkts/b Avg","BwP/BAG"),		//67
    bw_blk_rate_avg("Bwd Blk Rate Avg","BwBRAG"),			//68
    subfl_fw_pkt("Subflow Fwd Pkts","SFFwP"),			//69
    subfl_fw_byt("Subflow Fwd Byts","SFFwB"),			//70
    subfl_bw_pkt("Subflow Bwd Pkts","SFBwP"),			//71
    subfl_bw_byt("Subflow Bwd Byts","SFBwB"),			//72
    fw_win_byt("Init Fwd Win Byts","FwWB"),		//73
    bw_win_byt("Init Bwd Win Byts","BwWB"),		//74
    Fw_act_pkt("Fwd Act Data Pkts","FwAP"),			//75
    fw_seg_min("Fwd Seg Size Min","FwSgMI"),		//76
    atv_avg("Active Mean","AcAG"),					//77
    atv_std("Active Std","AcSD"),					//78
    atv_max("Active Max","AcMA"),					//79
    atv_min("Active Min","AcMI"),					//80
    idl_avg("Idle Mean","IlAG"),					//81
    idl_std("Idle Std","IlSD"),					//82
    idl_max("Idle Max","IlMA"),					//83
    idl_min("Idle Min","IlMI"),					//84
	
	Label("Label","LBL",new String[]{"CIC"});					//85


	protected static final Logger logger = LoggerFactory.getLogger(FlowFeature.class);
	private static String HEADER;
	private String name;
	private String abbr;
	private boolean isNumeric;
	private String[] values;

    FlowFeature(String name,String abbr,boolean numeric) {
        this.name = name;
        this.abbr = abbr;
        isNumeric = numeric;
    }

	FlowFeature(String name, String abbr) {
        this.name = name;
        this.abbr = abbr;
        isNumeric = true;

    }

	FlowFeature(String name,String abbr,String[] values) {
		this.name = name;
        this.abbr = abbr;
        this.values = values;
        isNumeric = false;
    }

	public String getName() {
		return name;
	}

    public String getAbbr() {
        return abbr;
    }

    public boolean isNumeric(){
        return isNumeric;
    }

	public static FlowFeature getByName(String name) {
		for(FlowFeature feature: FlowFeature.values()) {
			if(feature.getName().equals(name)) {
				return feature;
			}
		}
		return null;
	}
	
	public static String getHeader() {
		
		if(HEADER ==null|| HEADER.length()==0) {
			StringBuilder header = new StringBuilder();
			
			for(FlowFeature feature: FlowFeature.values()) {
				header.append(feature.getName()).append(",");
			}
			header.deleteCharAt(header.length()-1);
			HEADER = header.toString();
		}
		return HEADER;
	}

	public static List<FlowFeature> getFeatureList() {
        List<FlowFeature> features = new ArrayList<>();
        features.add(prot);
        for(int i = fl_dur.ordinal(); i<= idl_min.ordinal(); i++) {
            features.add(FlowFeature.values()[i]);
        }
        return features;
    }

	public static List<FlowFeature> getLengthFeature(){
		List<FlowFeature> features = new ArrayList<>();
		features.add(tot_l_fw_pkt);
		features.add(tot_l_bw_pkt);
		features.add(fl_byt_s);
		features.add(fl_pkt_s);
		features.add(fw_hdr_len);
		features.add(bw_hdr_len);
		features.add(fw_pkt_s);
		features.add(bw_pkt_s);
		features.add(pkt_size_avg);
		features.add(fw_seg_avg);
		features.add(bw_seg_avg);
		return features;
	}


    public static String featureValue2String(FlowFeature feature, String value) {
        String ret = value;

        switch (feature) {
            case prot:
                try {
                    int number  = NumberUtils.createNumber(value).intValue();
                    if (number == 6) {
                        ret = "TCP";

                    } else if (number == 17) {
                        ret = "UDP";

                    } else {
                        ret = "Others";
                    }
                } catch (NumberFormatException e) {
                    logger.info("NumberFormatException {} value is {}",e.getMessage(),value);
                    ret = "Others";
                }
            break;
        }

        return ret;
    }

	@Override
	public String toString() {
		return name;
	}
	
}
