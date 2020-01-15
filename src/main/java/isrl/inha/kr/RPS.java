package isrl.inha.kr;

import cic.cs.unb.ca.jnetpcap.BasicPacketInfo;

import java.util.Random;

public class RPS implements Sampler {
    private int sampling_interval;
    private int est;
    private int counter=0;
    Random random ;
    public RPS(int sampling_interval, long seed){
        this.sampling_interval = sampling_interval;
        random = new Random(seed);
        this.est = generateRandomIntWithinRange(1,2*sampling_interval-1);
    }

    public int generateRandomIntWithinRange(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public boolean is_sampled(BasicPacketInfo basicPacketInfo){
        counter++;
        if (counter==est) {
            counter=0;
            est = generateRandomIntWithinRange(1, 2 * sampling_interval - 1);
            return true;
        }
        return false;
    }
}
